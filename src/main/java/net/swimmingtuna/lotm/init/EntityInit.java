package net.swimmingtuna.lotm.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.*;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LOTM.MOD_ID);

    public static final RegistryObject<EntityType<AqueousLightEntity>> AQUEOUS_LIGHT_ENTITY =
            ENTITIES.register("aqueous_light", () -> EntityType.Builder.<AqueousLightEntity>of(AqueousLightEntity::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build(new ResourceLocation(LOTM.MOD_ID, "aqueous_light").toString()));
    public static final RegistryObject<EntityType<RoarEntity>> ROAR_ENTITY =
            ENTITIES.register("roar_entity", () -> EntityType.Builder.<RoarEntity>of(RoarEntity::new, MobCategory.MISC)
                    .sized(2.0f,2.0f).build(new ResourceLocation(LOTM.MOD_ID, "roar_entity").toString()));
    public static final RegistryObject<EntityType<AqueousLightEntityPull>> AQUEOUS_LIGHT_ENTITY_PULL =
            ENTITIES.register("aqueous_light_pull", () -> EntityType.Builder.<AqueousLightEntityPull>of(AqueousLightEntityPull::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("aqueous_light_pull"));
    public static final RegistryObject<EntityType<AqueousLightEntityPush>> AQUEOUS_LIGHT_ENTITY_PUSH =
            ENTITIES.register("aqueous_light_push", () -> EntityType.Builder.<AqueousLightEntityPush>of(AqueousLightEntityPush::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("aqueous_light_push"));
    public static final RegistryObject<EntityType<DragonBreathEntity>> DRAGON_BREATH_ENTITY =
            ENTITIES.register("dragon_breath", () -> EntityType.Builder.<DragonBreathEntity>of(DragonBreathEntity::new, MobCategory.MISC)
                    .sized(2.0f,2.0f).build("dragon_breath"));
    public static final RegistryObject<EntityType<MeteorEntity>> METEOR_ENTITY =
            ENTITIES.register("meteor", () -> EntityType.Builder.<MeteorEntity>of(MeteorEntity::new, MobCategory.MISC)
                    .sized(2.0f,2.0f).build("meteor"));
    public static final RegistryObject<EntityType<CircleEntity>> CIRCLE_ENTITY =
            ENTITIES.register("circle", () -> EntityType.Builder.<CircleEntity>of(CircleEntity::new, MobCategory.MISC)
                    .sized(1.0f,1.0f).build("circle"));
    public static final RegistryObject<EntityType<StoneEntity>> STONE_ENTITY =
            ENTITIES.register("stone", () -> EntityType.Builder.<StoneEntity>of(StoneEntity::new, MobCategory.MISC)
                    .sized(1.0f,1.0f).clientTrackingRange(4).updateInterval(20).build("stone"));
    public static final RegistryObject<EntityType<TornadoEntity>> TORNADO_ENTITY =
            ENTITIES.register("tornado", () -> EntityType.Builder.<TornadoEntity>of(TornadoEntity::new, MobCategory.MISC)
                    .sized(1.0f,1.0f).clientTrackingRange(4).updateInterval(20).build("tornado"));
    public static final RegistryObject<EntityType<MeteorNoLevelEntity>> METEOR_NO_LEVEL_ENTITY =
            ENTITIES.register("meteor_no_hurt", () -> EntityType.Builder.<MeteorNoLevelEntity>of(MeteorNoLevelEntity::new, MobCategory.MISC)
                    .sized(2.0f,2.0f).build("meteor_no_hurt"));
    public static final RegistryObject<EntityType<WindBladeEntity>> WIND_BLADE_ENTITY =
            ENTITIES.register("wind_blade", () -> EntityType.Builder.<WindBladeEntity>of(WindBladeEntity::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("wind_blade"));
    public static final RegistryObject<EntityType<WindCushionEntity>> WIND_CUSHION_ENTITY =
            ENTITIES.register("wind_cushion", () -> EntityType.Builder.<WindCushionEntity>of(WindCushionEntity::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("wind_cushion"));
    public static final RegistryObject<EntityType<LightningEntity>> LIGHTNING_ENTITY =
            ENTITIES.register("line_entity", () -> EntityType.Builder.<LightningEntity>of(LightningEntity::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("line_entity"));

    public static void register(IEventBus bus) {ENTITIES.register(bus);}
}
