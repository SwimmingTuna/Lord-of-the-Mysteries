package net.swimmingtuna.lotm.blocks.spectator_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

public class LOTMStainedGlass extends StainedGlassBlock {
    public LOTMStainedGlass(DyeColor color, Properties properties) {
        super(color, properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return 1.0F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }
}
