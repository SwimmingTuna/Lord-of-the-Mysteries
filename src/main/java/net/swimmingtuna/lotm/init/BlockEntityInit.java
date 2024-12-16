package net.swimmingtuna.lotm.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.blocks.MonsterDomainBlockEntity;
import net.swimmingtuna.lotm.blocks.PotionCauldronBlockEntity;
import net.swimmingtuna.lotm.blocks.spectator_blocks.CathedralBlockEntity;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LOTM.MOD_ID);

    public static final RegistryObject<BlockEntityType<CathedralBlockEntity>> CATHEDRAL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("cathedral_block_entity",
                    () -> BlockEntityType.Builder.of(CathedralBlockEntity::new, BlockInit.CATHEDRAL_BLOCK.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<PotionCauldronBlockEntity>> POTION_CAULDRON_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("potion_cauldron_entity",
                    () -> BlockEntityType.Builder.of(PotionCauldronBlockEntity::new, BlockInit.POTION_CAULDRON.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<MonsterDomainBlockEntity>> MONSTER_DOMAIN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("monster_domain_entity",
                    () -> BlockEntityType.Builder.of(MonsterDomainBlockEntity::new, BlockInit.MONSTER_DOMAIN_BLOCK.get())
                            .build(null));



    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
