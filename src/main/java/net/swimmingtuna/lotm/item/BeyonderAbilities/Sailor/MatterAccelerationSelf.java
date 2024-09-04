package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MatterAccelerationSelf extends Item {

    public MatterAccelerationSelf(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            int matterAccelerationDistance = pPlayer.getPersistentData().getInt("tyrantSelfAcceleration");
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < matterAccelerationDistance * 10) {
                pPlayer.displayClientMessage(Component.literal("You need " + matterAccelerationDistance * 10 + "spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && sailorSequence.useSpirituality(matterAccelerationDistance * 10) && sailorSequence.getCurrentSequence() == 0) {
                    useItem(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 300);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }


    public static void useItem(Player pPlayer) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        int sequence = holder.getCurrentSequence();
        Level level = pPlayer.level();
        int blinkDistance = pPlayer.getPersistentData().getInt("tyrantSelfAcceleration");
        if (holder.getSpirituality() < blinkDistance * 10) {
            pPlayer.displayClientMessage(Component.literal("You need " + (blinkDistance * 10) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
        }
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
            if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && !pPlayer.level().isClientSide() && sailorSequence.getCurrentSequence() == 0 && sailorSequence.useSpirituality(blinkDistance * 2)) {

                Vec3 lookVector = pPlayer.getLookAngle();
                BlockPos startPos = pPlayer.blockPosition();
                BlockPos endPos = new BlockPos(
                        (int) (pPlayer.getX() + blinkDistance * lookVector.x()),
                        (int) (pPlayer.getY() + 1 + blinkDistance * lookVector.y()),
                        (int) (pPlayer.getZ() + blinkDistance * lookVector.z())
                );

                BlockPos blockPos = new BlockPos(endPos.getX(), endPos.getY(), endPos.getZ());
                double distance = startPos.distSqr(blockPos);
                Vec3 direction = new Vec3(
                        endPos.getX() - startPos.getX(),
                        endPos.getY() - startPos.getY(),
                        endPos.getZ() - startPos.getZ()
                ).normalize();

                for (double i = 0; i <= distance; i += 0.5) { // Adjust step size for smoother or coarser destruction
                    BlockPos pos = new BlockPos(
                            (int) (startPos.getX() + i * direction.x),
                            (int) (startPos.getY() + i * direction.y),
                            (int) (startPos.getZ() + i * direction.z)
                    );

                    // Destroy blocks in a 5-block radius around the current position
                    for (int x = -5; x <= 5; x++) {
                        for (int y = -5; y <= 5; y++) {
                            for (int z = -5; z <= 5; z++) {
                                BlockPos nearbyPos = pos.offset(x, y, z);
                                BlockState blockState = level.getBlockState(nearbyPos);
                                if (!blockState.is(Blocks.BEDROCK)) {
                                    level.setBlock(nearbyPos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }

                    AABB boundingBox = new AABB(pos).inflate(1); // Adjust size as needed
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox);
                    for (LivingEntity entity : entities) {
                        if (entity != pPlayer) {
                            entity.hurt(level.damageSources().magic(), 10.0f); // Adjust damage amount as needed
                        }
                    }
                }
                // Teleport the player
                pPlayer.teleportTo(endPos.getX(), endPos.getY(), endPos.getZ());
            }
        });
    }


    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, moves you at an inhuman speed, instantly getting you to your destination and leaving behind destruction in your path\n" +
                    "Spirituality Used: 2500\n" +
                    "Cooldown: 15 seconds").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof MatterAccelerationSelf) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MATTER_ACCELERATION_BLOCKS.get()));
            heldItem.shrink(1);
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof MatterAccelerationSelf) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MATTER_ACCELERATION_BLOCKS.get()));
            heldItem.shrink(1);
        }
    }
}
