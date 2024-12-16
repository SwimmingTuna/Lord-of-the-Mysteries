package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatterAccelerationSelf extends SimpleAbilityItem {

    public MatterAccelerationSelf(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 0, 0, 300); //cooldown fix for all items that rely on smth
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        int matterAccelerationDistance = player.getPersistentData().getInt("tyrantSelfAcceleration");
        if (!checkAll(player, BeyonderClassInit.SAILOR.get(), 0, matterAccelerationDistance * 10)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player, matterAccelerationDistance * 10);
        matterAccelerationSelf(player);
        return InteractionResult.SUCCESS;
    }

    public void matterAccelerationSelf(Player player) {
        Level level = player.level();
        int blinkDistance = player.getPersistentData().getInt("tyrantSelfAcceleration");
        Vec3 lookVector = player.getLookAngle();
        BlockPos startPos = player.blockPosition();
        BlockPos endPos = new BlockPos(
                (int) (player.getX() + blinkDistance * lookVector.x()),
                (int) (player.getY() + 1 + blinkDistance * lookVector.y()),
                (int) (player.getZ() + blinkDistance * lookVector.z())
        );

        BlockPos blockPos = new BlockPos(endPos.getX(), endPos.getY(), endPos.getZ());
        double distance = startPos.getCenter().distanceTo(blockPos.getCenter());
        Vec3 direction = new Vec3(
                endPos.getX() - startPos.getX(),
                endPos.getY() - startPos.getY(),
                endPos.getZ() - startPos.getZ()
        ).normalize();

        Set<BlockPos> visitedPositions = new HashSet<>();

        for (double i = 0; i <= distance; i += 0.5) { // Adjust step size for smoother or coarser destruction
            BlockPos pos = new BlockPos(
                    (int) (startPos.getX() + i * direction.x),
                    (int) (startPos.getY() + i * direction.y),
                    (int) (startPos.getZ() + i * direction.z)
            );

            // Destroy blocks in a 5-block radius around the current position
            List<BlockPos> blockPositions = new ArrayList<>();
            for (BlockPos offsetedPos : BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5))) {
                if (visitedPositions.contains(offsetedPos)) continue;
                visitedPositions.add(offsetedPos);
                BlockState blockState = level.getBlockState(offsetedPos);
                blockPositions.add(offsetedPos);

                if (!blockState.isAir() && blockState.getBlock().defaultDestroyTime() != -1.0F) {
                    level.setBlock(offsetedPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }

            for (BlockPos blockPosToUpdate : blockPositions) {
                Block block = level.getBlockState(blockPosToUpdate).getBlock();
                level.blockUpdated(blockPosToUpdate, block);
            }

            AABB boundingBox = new AABB(pos).inflate(1); // Adjust size as needed
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox);
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    entity.hurt(level.damageSources().lightningBolt(), 60.0f); // Adjust damage amount as needed
                }
            }
        }
        // Teleport the player
        BlockHitResult blockHitResult = level.clip(new ClipContext(player.getEyePosition(), new Vec3(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        BlockPos teleportLocation = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
        player.teleportTo(teleportLocation.getX() + 0.5, teleportLocation.getY(), teleportLocation.getZ() + 0.5);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, accelerates you to the speed of light, instantly getting you to your destination and leaving behind destruction in your path"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("5 x Distance Traveled").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("15 seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(BeyonderClassInit.SAILOR.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(0, BeyonderClassInit.SAILOR.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
