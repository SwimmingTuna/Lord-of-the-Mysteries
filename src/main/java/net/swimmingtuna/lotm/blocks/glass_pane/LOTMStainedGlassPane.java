package net.swimmingtuna.lotm.blocks.glass_pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class LOTMStainedGlassPane extends StainedGlassPaneBlock implements BeaconBeamBlock {

    public LOTMStainedGlassPane(DyeColor color, Properties properties) {
        super(color, properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    @ParametersAreNonnullByDefault
    @NotNull
    public VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @SuppressWarnings("deprecation")
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
