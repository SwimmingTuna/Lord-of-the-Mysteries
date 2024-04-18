package net.swimmingtuna.lotm.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.AqueousLightEntity;
import net.swimmingtuna.lotm.entity.AqueousLightEntityPush;
import net.swimmingtuna.lotm.entity.AqueousLightEntityPull;
import net.swimmingtuna.lotm.entity.Meteor.MeteorEntity;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LOTM.MOD_ID);


    public static final RegistryObject<EntityType<AqueousLightEntity>> AQUEOUS_LIGHT_ENTITY =
            ENTITIES.register("aqueous_light", () -> EntityType.Builder.<AqueousLightEntity>of(AqueousLightEntity::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build(new ResourceLocation(LOTM.MOD_ID, "aqueous_light").toString()));
    public static final RegistryObject<EntityType<AqueousLightEntityPull>> AQUEOUS_LIGHT_ENTITY_PULL =
            ENTITIES.register("aqueous_light_pull", () -> EntityType.Builder.<AqueousLightEntityPull>of(AqueousLightEntityPull::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("aqueous_light_pull"));
    public static final RegistryObject<EntityType<AqueousLightEntityPush>> AQUEOUS_LIGHT_ENTITY_PUSH =
            ENTITIES.register("aqueous_light_push", () -> EntityType.Builder.<AqueousLightEntityPush>of(AqueousLightEntityPush::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("aqueous_light_push"));
    public static final RegistryObject<EntityType<MeteorEntity>> METEOR_ENTITY =
            ENTITIES.register("meteor", () -> EntityType.Builder.<MeteorEntity>of(MeteorEntity::new, MobCategory.MISC)
                    .sized(30.0f,30.0f).build("meteor"));


    public static void register(IEventBus bus) {ENTITIES.register(bus);}





}
