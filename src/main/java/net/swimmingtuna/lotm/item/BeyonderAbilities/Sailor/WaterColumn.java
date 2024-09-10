package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.WaterColumnEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WaterColumn extends Item {

    private static final Queue<BlockPos> waterBlocks = new LinkedList<>();

    public WaterColumn(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            // Your existing checks
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                return super.use(level, player, hand);
            }
            if (holder.getSpirituality() < 2000) {
                player.displayClientMessage(Component.literal("You need 2000 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                return super.use(level, player, hand);
            }
            summonColumns(player);
            if (!player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(this, 500);
            }
        }
        return super.use(level, player, hand);
    }

    private static void summonColumns(Player player) {
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
        tooltipComponents.add(Component.literal("Upon use, cause the water to intersect with the sky\n" +
                "Spirituality Used: 2000\n" +
                "Cooldown: 25 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
