package net.swimmingtuna.lotm.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
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

        //REGULAR BLOCKS
        blockWithItem(BlockInit.MINDSCAPE_BLOCK);
        blockWithItem(BlockInit.MINDSCAPE_OUTSIDE);
        blockWithItem(BlockInit.CATHEDRAL_BLOCK);
        blockWithItem(BlockInit.VISIONARY_BARRIER_BLOCK);

        //PANE

        //STAIRS BLOCKS
        stairsBlock(((StairBlock) BlockInit.LOTM_DARKOAK_STAIRS.get()), blockTexture(Blocks.DARK_OAK_PLANKS));
        stairsBlock(((StairBlock) BlockInit.LOTM_DEEPSLATEBRICK_STAIRS.get()), blockTexture(Blocks.DEEPSLATE_BRICKS));
        stairsBlock(((StairBlock) BlockInit.LOTM_OAK_STAIRS.get()), blockTexture(Blocks.OAK_PLANKS));
        stairsBlock(((StairBlock) BlockInit.LOTM_QUARTZ_STAIRS.get()), blockTexture(Blocks.DARK_OAK_PLANKS));

        //CARPET

        //SLAB BLOCKS
        slabBlock(((SlabBlock) BlockInit.LOTM_QUARTZ_SLAB.get()), blockTexture(Blocks.DARK_OAK_PLANKS), blockTexture(Blocks.DARK_OAK_PLANKS));
        slabBlock(((SlabBlock) BlockInit.LOTM_DARKOAK_SLAB.get()), blockTexture(Blocks.DARK_OAK_PLANKS), blockTexture(Blocks.DARK_OAK_PLANKS));

        simpleBlockWithItem(BlockInit.POTION_CAULDRON.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/empty_potion_cauldron")));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
