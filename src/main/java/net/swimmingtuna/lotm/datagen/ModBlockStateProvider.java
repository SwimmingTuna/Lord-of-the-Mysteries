package net.swimmingtuna.lotm.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.BlockInit;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, LOTM.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(BlockInit.CATHEDRAL_BLOCK);
        blockWithItem(BlockInit.MINDSCAPE_BLOCK);
        blockWithItem(BlockInit.VISIONARY_BARRIER_BLOCK);
        blockWithItem(BlockInit.MINDSCAPE_OUTSIDE);
        blockWithItem(BlockInit.VISIONARY_BLACK_STAINED_GLASS);
    }
    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
