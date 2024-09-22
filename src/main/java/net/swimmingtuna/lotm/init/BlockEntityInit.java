package net.swimmingtuna.lotm.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.blocks.spectator_blocks.CathedralBlockEntity;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LOTM.MOD_ID);

    public static final RegistryObject<BlockEntityType<CathedralBlockEntity>> CATHEDRAL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("cathedral_block_entity",
                    () -> BlockEntityType.Builder.of(CathedralBlockEntity::new, BlockInit.CATHEDRAL_BLOCK.get())
                            .build(null));
}
