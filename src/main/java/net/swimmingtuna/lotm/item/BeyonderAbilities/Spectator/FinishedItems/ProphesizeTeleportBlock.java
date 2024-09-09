package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ProphesizeTeleportBlock extends Item {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public ProphesizeTeleportBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(pSlot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 200, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 200, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player pPlayer = pContext.getPlayer();
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 600) {
                pPlayer.displayClientMessage(Component.literal("You need 600 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
        }
        Level level = pPlayer.level();
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        BlockPos positionClicked = pContext.getClickedPos();
        if (!pContext.getLevel().isClientSide) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && spectatorSequence.getCurrentSequence() <= 1 &&  BeyonderHolderAttacher.getHolderUnwrap(pPlayer).useSpirituality(600)) {
                    teleportEntities(pPlayer, level, positionClicked, spectatorSequence.getCurrentSequence(), (int) dreamIntoReality.getValue());
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 300);
                    }
                }
            });
        }
        return InteractionResult.SUCCESS;
    }


    private void teleportEntities(Player pPlayer, Level level, BlockPos targetPos, int sequence, int dir) {
        double radius = (500 - sequence * 100) * dir;
        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
            if (entity != pPlayer && !entity.level().isClientSide()) {
                entity.teleportTo(targetPos.getX(), targetPos.getY() +1, targetPos.getZ());
                }}
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, makes all living entities around the user teleport to a clicked block\n" +
                    "Spirituality Used: 600\n" +
                    "Cooldown: 15 seconds").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof ProphesizeTeleportBlock) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.PROPHESIZE_TELEPORT_PLAYER.get()));
            heldItem.shrink(1);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ProphesizeTeleportBlock) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.PROPHESIZE_TELEPORT_PLAYER.get()));
            heldItem.shrink(1);
        }
    }
}
