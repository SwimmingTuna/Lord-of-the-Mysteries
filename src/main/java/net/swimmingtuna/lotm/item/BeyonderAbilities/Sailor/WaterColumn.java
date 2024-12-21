package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.swimmingtuna.lotm.entity.WaterColumnEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WaterColumn extends SimpleAbilityItem {

    public WaterColumn(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 0, 2000, 500);
    }


    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        waterColumn(player);
        return InteractionResult.SUCCESS;
    }

    private static void waterColumn(Player player) {
        if (!player.level().isClientSide()) {
            Level level = player.level();
            BlockPos playerPos = player.blockPosition();
            int radius = 200;
            Random random = new Random();
            List<BlockPos> validWaterPositions = new ArrayList<>();
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.offset(x, 0, z);
                    if (pos.distSqr(playerPos) <= radius * radius) {
                        // Find the highest water block at this x and z coordinate
                        BlockPos highestWaterPos = null;
                        for (int y = level.getMaxBuildHeight() - 1; y >= level.getMinBuildHeight(); y--) {
                            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
                            BlockState state = level.getBlockState(checkPos);
                            BlockState stateAbove = level.getBlockState(checkPos.above());
                            if (state.is(Blocks.WATER) && state.getFluidState().isSource() && stateAbove.isAir()) {
                                highestWaterPos = checkPos;
                                break;
                            }
                        }
                        if (highestWaterPos != null) {
                            validWaterPositions.add(highestWaterPos);
                        }
                    }
                }
            }

            // Shuffle the list of valid positions
            Collections.shuffle(validWaterPositions, random);

            // Limit to 100 positions
            int spawnLimit = Math.min(50, validWaterPositions.size());

            // Spawn a zombie at each selected position
            for (int i = 0; i < spawnLimit; i++) {
                BlockPos spawnPos = validWaterPositions.get(i);
                WaterColumnEntity zombie = new WaterColumnEntity(EntityInit.WATER_COLUMN_ENTITY.get(), level);
                zombie.setPos(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5);
                level.addFreshEntity(zombie);
            }
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, raises all water sources around you gradually, leaving pillars of water that extend into the sky"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("25 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

}
