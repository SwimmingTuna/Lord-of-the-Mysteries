package net.swimmingtuna.lotm.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;

public class ParticleInit {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LOTM.MOD_ID);

    public static final RegistryObject<SimpleParticleType> NULL_PARTICLE =
            PARTICLE_TYPES.register("null_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> METEOR_PARTICLE =
            PARTICLE_TYPES.register("meteor_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> ACIDRAIN_PARTICLE =
            PARTICLE_TYPES.register("acidrain_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> TORNADO_PARTICLE =
            PARTICLE_TYPES.register("tornado_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SONIC_BOOM_PARTICLE =
            PARTICLE_TYPES.register("sonic_boom_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> ATTACKER_POISONED_PARTICLE =
            PARTICLE_TYPES.register("attackerpoisoned", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BABY_ZOMBIE_PARTICLE =
            PARTICLE_TYPES.register("babyzombie", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BREEZE_PARTICLE =
            PARTICLE_TYPES.register("breeze", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> CANT_USE_ABILITY_PARTICLE =
            PARTICLE_TYPES.register("cantuseability", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DOUBLE_DAMAGE_PARTICLE =
            PARTICLE_TYPES.register("doubledamage", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> EXPLOSION_PARTICLE =
            PARTICLE_TYPES.register("explosion", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FALLING_STONE_PARTICLE =
            PARTICLE_TYPES.register("fallingstone", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> GOO_GAZE_PARTICLE =
            PARTICLE_TYPES.register("googaze", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> GROUND_TREMOR_PARTICLE =
            PARTICLE_TYPES.register("groundtremor", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> HALF_DAMAGE_PARTICLE =
            PARTICLE_TYPES.register("halfdamage", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> HEAT_WAVE_PARTICLE =
            PARTICLE_TYPES.register("heatwave", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> IGNORE_DAMAGE_PARTICLE =
            PARTICLE_TYPES.register("ignoredamage", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> IGNORE_MOBS_PARTICLE =
            PARTICLE_TYPES.register("ignoremobs", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> LIGHTNING_STORM_PARTICLE =
            PARTICLE_TYPES.register("lightningstorm", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> LOTM_LIGHTNING_PARTICLE =
            PARTICLE_TYPES.register("lotmlightning", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DIAMOND_PARTICLE =
            PARTICLE_TYPES.register("luckdiamond", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> UNDEAD_ARMY_PARTICLE =
            PARTICLE_TYPES.register("undeadarmy", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MC_LIGHTNING_PARTICLE =
            PARTICLE_TYPES.register("mclightning", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> TORNADO_CALAMITY_PARTICLE =
            PARTICLE_TYPES.register("tornado", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> TRIP_PARTICLE =
            PARTICLE_TYPES.register("tripoverstone", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> WARDEN_PARTICLE =
            PARTICLE_TYPES.register("warden", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> WIND_MOVE_PROJECTILES_PARTICLES =
            PARTICLE_TYPES.register("windmovingprojectiles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> WIND_UNEQUIP_ARMOR_PARTICLE =
            PARTICLE_TYPES.register("windunequiparmor", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> METEOR_CALAMITY_PARTICLE =
            PARTICLE_TYPES.register("meteor", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> POISON_PARTICLE =
            PARTICLE_TYPES.register("poison", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> REGENERATION_PARTICLE =
            PARTICLE_TYPES.register("regeneration", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}

