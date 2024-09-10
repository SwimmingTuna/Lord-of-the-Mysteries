package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MindStorm extends Item {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public MindStorm(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.literal("Upon use on a living entity, damages, freezes, blinds, and confuses them\n" +
                    "Spirituality Used: 250\n" +
                    "Cooldown: 10 seconds").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (holder != null && itemStack.getItem() instanceof MindStorm) {
                if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                    player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                }
                if (holder.getSpirituality() < 250) {
                    player.displayClientMessage(Component.literal("You need 250 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                }
            }

            boolean x = !player.getCooldowns().isOnCooldown(itemStack.getItem());
            Entity targetEntity = event.getTarget();
            AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
            if (x && holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide && !targetEntity.level().isClientSide && itemStack.getItem() instanceof MindStorm && targetEntity instanceof LivingEntity && holder.getCurrentSequence() <= 3 && holder.useSpirituality(250)) {
                int sequence = holder.getCurrentSequence();
                int duration = 300 - (sequence * 25);
                int damage = 30 - (sequence * 2);
                if (dreamIntoReality.getValue() == 2) {
                    damage = 50 - (sequence * 2);
                }
                ((LivingEntity) targetEntity).addEffect(new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false));
                ((LivingEntity) targetEntity).addEffect(new MobEffectInstance(MobEffects.DARKNESS, duration, 1, false, false));
                ((LivingEntity) targetEntity).addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1, false, false));
                targetEntity.hurt(targetEntity.damageSources().magic(), damage);
                if (!player.getAbilities().instabuild) {
                    player.getCooldowns().addCooldown(itemStack.getItem(), 200);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
            if (x && holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide && !targetEntity.level().isClientSide && itemStack.getItem() instanceof Placate && targetEntity instanceof LivingEntity && holder.getCurrentSequence() <= 7 && holder.getCurrentSequence() > 4 && holder.useSpirituality(120)) {
                Placate.halfHarmfulEffects((LivingEntity) targetEntity);
                player.getCooldowns().addCooldown(itemStack.getItem(), 120 / (int) dreamIntoReality.getValue());
                event.setCanceled(true);
            }
            if (x && holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide && !targetEntity.level().isClientSide && itemStack.getItem() instanceof Placate && targetEntity instanceof LivingEntity && holder.getCurrentSequence() <= 4 && holder.useSpirituality(250)) {
                Placate.removeHarmfulEffects((LivingEntity) targetEntity);
                event.setCanceled(true);
                if (!player.getAbilities().mayfly) {
                    player.getCooldowns().addCooldown(itemStack.getItem(), 120);
                }
            }

        }
    }
}