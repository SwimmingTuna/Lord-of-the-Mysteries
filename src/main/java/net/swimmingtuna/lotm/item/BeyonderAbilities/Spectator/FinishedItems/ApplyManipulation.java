package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ApplyManipulation extends Item implements ReachChangeUUIDs {
    private final LazyOptional<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = LazyOptional.of(() -> createAttributeMap());

    public ApplyManipulation(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND) {
            return lazyAttributeMap.orElseGet(() -> createAttributeMap());
        }
        return super.getDefaultAttributeModifiers(pSlot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BeyonderEntityReach, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BeyonderBlockReach, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use on a living entity, gives you the ability to manipulate them for 30 seconds\n" +
                    "Left Click for Manipulate Emotion\n" +
                    "Spirituality Used: 50\n" +
                    "Cooldown: None"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player pPlayer = event.getEntity();
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 50) {
                pPlayer.displayClientMessage(Component.literal("You need 50 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            ItemStack itemStack = pPlayer.getItemInHand(event.getHand());
            Entity targetEntity = event.getTarget();
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (!targetEntity.level().isClientSide && holder.isSpectatorClass() && itemStack.getItem() instanceof ApplyManipulation && targetEntity instanceof LivingEntity && spectatorSequence.getCurrentSequence() <= 4 && spectatorSequence.useSpirituality(50)) {
                    if (!((LivingEntity) targetEntity).hasEffect(ModEffects.MANIPULATION.get())) {
                        ((LivingEntity) targetEntity).addEffect(new MobEffectInstance(ModEffects.MANIPULATION.get(), 600, 1, false, false));
                        pPlayer.sendSystemMessage(Component.literal("Manipulating " + targetEntity.getName().getString()));
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                    }
                }
            });
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ApplyManipulation) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ManipulateEmotion.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ApplyManipulation) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ManipulateEmotion.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}