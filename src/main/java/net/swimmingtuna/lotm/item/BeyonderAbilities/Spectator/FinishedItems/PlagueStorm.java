package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlagueStorm extends Item {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public PlagueStorm(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 80, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 80, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on a living entity, all living entities around it gain Darkness, Wither, Blindness, and Confusion, as well as take damage\n" +
                "Spirituality Used: 400\n" +
                "Cooldown: 8 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide() && player.getMainHandItem().getItem() instanceof PlagueStorm) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 400) {
                player.displayClientMessage(Component.literal("You need 400 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
        }
        ItemStack itemStack = player.getItemInHand(event.getHand());
        Entity targetEntity = event.getTarget();
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide && !targetEntity.level().isClientSide && itemStack.getItem() instanceof PlagueStorm && targetEntity instanceof LivingEntity && holder.getCurrentSequence() <= 3 && holder.useSpirituality(400)) {
            ((LivingEntity) targetEntity).addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 1, false, false));
            for (LivingEntity targetEntity1 : targetEntity.level().getEntitiesOfClass(LivingEntity.class, targetEntity.getBoundingBox().inflate(30 * dreamIntoReality.getValue()))) {
                if (targetEntity1 != player) {
                    if (targetEntity1 != targetEntity) {
                        targetEntity1.hurt(targetEntity1.damageSources().magic(), (float) ((20 - (holder.getCurrentSequence() * 3)) * dreamIntoReality.getValue()));
                    } else {
                        targetEntity1.hurt(targetEntity1.damageSources().magic(), (float) ((40 - (holder.getCurrentSequence() * 6)) * dreamIntoReality.getValue()));
                    }
                    targetEntity1.addEffect(new MobEffectInstance(MobEffects.DARKNESS,80,1,false,false));
                    targetEntity1.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 2, false, false));
                    targetEntity1.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 1, false, false));
                    targetEntity1.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 1, false, false));
                }
            }
            if (!player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(itemStack.getItem(), 160);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
