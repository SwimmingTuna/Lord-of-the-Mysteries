package net.swimmingtuna.lotm.blocks.glass_pane;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LOTMYellowStainedGlassPane extends IronBarsBlock implements BeaconBeamBlock {
    private final DyeColor color;

    public LOTMYellowStainedGlassPane(DyeColor pColor, Properties pProperties) {
        super(Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)
                .strength(15)
                .destroyTime(2));
                this.color = DyeColor.YELLOW;
                this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(WATERLOGGED, false));
    }

    public DyeColor getColor() {
        return DyeColor.YELLOW;
    }
}