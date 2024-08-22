package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MatterAccelerationSelf extends Item {

    public MatterAccelerationSelf(Properties pProperties) {
        super(pProperties);
    }

    @SubscribeEvent
    public static void blinkTimer(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.START) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (spectatorSequence.getCurrentSequence() == 0) {
                    int blinkDistance = pPlayer.getPersistentData().getInt("tyrantSelfAcceleration");
                    if (pPlayer.isShiftKeyDown() && pPlayer.getMainHandItem().getItem() instanceof MatterAccelerationSelf) {
                        blinkDistance = blinkDistance + 50;
                        pPlayer.sendSystemMessage(Component.literal("Matter Acceleration Distance is " + blinkDistance));
                    }
                    if (blinkDistance >= 1000) {
                        blinkDistance = 0;
                    }
                    pPlayer.getPersistentData().putInt("tyrantSelfAcceleration", blinkDistance);
                }
            });
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        int blinkDistance = pPlayer.getPersistentData().getInt("tyrantSelfAcceleration");
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
            if (holder.getSpirituality() < blinkDistance * 2) {
                pPlayer.displayClientMessage(Component.literal("You need "  + (blinkDistance * 8) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        }

        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder.isSpectatorClass() && !pPlayer.level().isClientSide() && spectatorSequence.getCurrentSequence() == 0 && spectatorSequence.useSpirituality(blinkDistance * 8)) {
                Vec3 lookVector = pPlayer.getLookAngle();
                BlockPos startPos = pPlayer.blockPosition();
                BlockPos endPos = new BlockPos(
                        (int) (pPlayer.getX() + blinkDistance * lookVector.x()),
                        (int) ((pPlayer.getY() + 1) + blinkDistance * lookVector.y()),
                        (int) (pPlayer.getZ() + blinkDistance * lookVector.z())
                );

                // Calculate the path between start and end positions
                BlockPos.betweenClosed(startPos, endPos).forEach(pos -> {
                    // Destroy blocks in a 10x10 area around each block in the path
                    for (int x = -5; x <= 5; x++) {
                        for (int y = -5; y <= 5; y++) {
                            for (int z = -5; z <= 5; z++) {
                                BlockPos targetPos = pos.offset(x, y, z);
                                BlockState blockState = level.getBlockState(targetPos);
                                if (!blockState.is(Blocks.BEDROCK)) {
                                    level.destroyBlock(targetPos, false);
                                }
                            }
                        }
                    }

                    // Deal damage to living entities in the 10x10 area
                    AABB boundingBox = new AABB(pos).inflate(5);
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox);
                    for (LivingEntity entity : entities) {
                        if (entity != pPlayer) {
                            entity.hurt(level.damageSources().magic(), 10.0f); // Adjust damage amount as needed
                        }
                    }
                });

                // Teleport the player
                pPlayer.teleportTo(endPos.getX(), endPos.getY(), endPos.getZ());

                if (!pPlayer.getAbilities().instabuild) {
                    pPlayer.getCooldowns().addCooldown(this, 100);
                }
            }
        });
        return super.use(level, pPlayer, hand);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, teleport in front of you\n" +
                    "Shift to Increase Blink Distance\n" +
                    "Left Click for Envision Weather\n" +
                    "Spirituality Used: 150\n" +
                    "Cooldown: 1 second"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof MatterAccelerationSelf) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionHealth.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof MatterAccelerationSelf) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionHealth.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}
