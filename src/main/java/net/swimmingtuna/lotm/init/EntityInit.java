package net.swimmingtuna.lotm.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.AqueousLightEntity;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LOTM.MOD_ID);


    public static final RegistryObject<EntityType<AqueousLightEntity>> AQUEOUS_LIGHT_ENTITY =
            ENTITIES.register("aqueous_light", () -> EntityType.Builder.<AqueousLightEntity>of(AqueousLightEntity::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("aqueous_light"));



    public static void register(IEventBus bus) {ENTITIES.register(bus);}





}
