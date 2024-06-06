package net.swimmingtuna.lotm.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.blocks.*;

import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, LOTM.MOD_ID);

    public static final RegistryObject<Block> VISIONARY_BARRIER_BLOCK = registerBlock("visionary_barrier_block",
            () -> new VisionaryBB(BlockBehaviour.Properties.of().sound(SoundType.GLASS).noLootTable()));

    public static final RegistryObject<Block> CATHEDRAL_BLOCK = registerBlock("cathedral_block",
            () -> new CathedralBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).noLootTable()));

    public static final RegistryObject<Block> MINDSCAPE_BLOCK = registerBlock("mindscape_block",
            () -> new MindscapeBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).noLootTable()));

    public static final RegistryObject<Block> MINDSCAPE_OUTSIDE = registerBlock("mindscape_outside",
            () -> new MindscapeOutsideBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).noLootTable()));

    public static final RegistryObject<Block> VISIONARY_BLACK_STAINED_GLASS = registerBlock("visionary_black_stained_glass",
            () -> new VisionaryBlackStainedGlass(DyeColor.BLACK, BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).noLootTable()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
