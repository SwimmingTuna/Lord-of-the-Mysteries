package net.swimmingtuna.lotm.blocks.spectator_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class MindscapeOutsideBlock extends AbstractGlassBlock {
    public MindscapeOutsideBlock(Properties properties) {
        super(properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    @NotNull
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
