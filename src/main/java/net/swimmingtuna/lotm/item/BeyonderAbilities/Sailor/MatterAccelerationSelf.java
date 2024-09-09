package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Ability;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MatterAccelerationSelf extends Item implements Ability {

    public MatterAccelerationSelf(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            int matterAccelerationDistance = player.getPersistentData().getInt("tyrantSelfAcceleration");
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (holder == null) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            if (!holder.useSpirituality(matterAccelerationDistance * 10)) {
                player.displayClientMessage(Component.literal("You need " + matterAccelerationDistance * 10 + " spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            if (holder.getCurrentSequence() > 0) {
                player.displayClientMessage(Component.literal("You need to be sequence 0 or lower to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            useItem(player);
            if (!player.isCreative()) {
                player.getCooldowns().addCooldown(this, 300);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void useAbility(Level level, Player player, InteractionHand hand) {
        useItem(player);
    }

    public static void useItem(Player player) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder == null) return;
        int sequence = holder.getCurrentSequence();
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
                    entity.hurt(level.damageSources().magic(), 10.0f); // Adjust damage amount as needed
                }
            }
        }
        // Teleport the player
        BlockHitResult blockHitResult = level.clip(new ClipContext(player.getEyePosition(), new Vec3(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        BlockPos teleportLocation = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
        player.teleportTo(teleportLocation.getX() + 0.5, teleportLocation.getY(), teleportLocation.getZ() + 0.5);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, moves you at an inhuman speed, instantly getting you to your destination and leaving behind destruction in your path"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2500").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("15 seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(BeyonderClassInit.SAILOR.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(0, BeyonderClassInit.SAILOR.get()));
        super.appendHoverText(pStack, level, tooltipComponents, tooltipFlag);
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
