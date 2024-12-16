package net.swimmingtuna.lotm.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.Model.*;
import net.swimmingtuna.lotm.entity.Renderers.*;
import net.swimmingtuna.lotm.entity.Renderers.PlayerMobRenderer.PlayerMobRenderer;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.particle.*;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents2 {
    @SubscribeEvent
    public static void registerParticleProvidersEvent(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleInit.ACIDRAIN_PARTICLE.get(), AcidRainParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.NULL_PARTICLE.get(), NullParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.METEOR_PARTICLE.get(), MeteorParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.TORNADO_PARTICLE.get(), NullParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.SONIC_BOOM_PARTICLE.get(), SonicBoomParticle.Provider::new);

        event.registerSpriteSet(ParticleInit.ATTACKER_POISONED_PARTICLE.get(), AttackerPoisonedParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.BABY_ZOMBIE_PARTICLE.get(), BabyZombieParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.BREEZE_PARTICLE.get(), BreezeParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.CANT_USE_ABILITY_PARTICLE.get(), CantUseAbilityParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.DOUBLE_DAMAGE_PARTICLE.get(), DoubleDamageParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.EXPLOSION_PARTICLE.get(), ExplosionParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.FALLING_STONE_PARTICLE.get(), FallingStoneParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.GOO_GAZE_PARTICLE.get(), GOOGazeParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.GROUND_TREMOR_PARTICLE.get(), GroundTremorParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.HALF_DAMAGE_PARTICLE.get(), HalfDamageParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.HEAT_WAVE_PARTICLE.get(), HeatWaveParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.IGNORE_DAMAGE_PARTICLE.get(), IgnoreDamageParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.IGNORE_MOBS_PARTICLE.get(), IgnoreMobsParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.LIGHTNING_STORM_PARTICLE.get(), LightningStormParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.LOTM_LIGHTNING_PARTICLE.get(), LOTMLightningParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.DIAMOND_PARTICLE.get(), LuckDiamondParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.MC_LIGHTNING_PARTICLE.get(), MCLightningParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.METEOR_CALAMITY_PARTICLE.get(), MeteorCalamityParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.POISON_PARTICLE.get(), PoisonParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.REGENERATION_PARTICLE.get(), RegenerationParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.TORNADO_CALAMITY_PARTICLE.get(), TornadoCalamityParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.TRIP_PARTICLE.get(), TripParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.UNDEAD_ARMY_PARTICLE.get(), UndeadArmyParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.WARDEN_PARTICLE.get(), WardenParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.WIND_MOVE_PROJECTILES_PARTICLES.get(), WindProjectilesParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.WIND_UNEQUIP_ARMOR_PARTICLE.get(), WindArmorParticle.Provider::new);
    }
    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MeteorModel.METEOR_LOCATION, MeteorModel::createBodyLayer);
        event.registerLayerDefinition(MeteorNoLevelModel.METEOR_LOCATION, MeteorNoLevelModel::createBodyLayer);
        event.registerLayerDefinition(DragonBreathModel.LAYER, DragonBreathModel::createBodyLayer);
        event.registerLayerDefinition(WindBladeModel.WIND_BLADE_LOCATION, WindBladeModel::createBodyLayer);
        event.registerLayerDefinition(WindCushionModel.WIND_CUSHION_LOCATION, WindCushionModel::createBodyLayer);
        event.registerLayerDefinition(StoneEntityModel.STONE_MODEL_LOCATION, StoneEntityModel::createBodyLayer);
        event.registerLayerDefinition(EndstoneEntityModel.ENDSTONE_MODEL_LOCATION, EndstoneEntityModel::createBodyLayer);
        event.registerLayerDefinition(NetherrackEntityModel.NETHERRACK_MODEL_LOCATION, NetherrackEntityModel::createBodyLayer);
        event.registerLayerDefinition(LavaEntityModel.LAVA_ENTITY_LOCATION, LavaEntityModel::createBodyLayer);
        event.registerLayerDefinition(LightningBallModel.LIGHTNING_BALL_LOCATION, LightningBallModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.METEOR_ENTITY.get(), MeteorEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.METEOR_TRAIL_ENTITY.get(), MeteorTrailRenderer::new);
        event.registerEntityRenderer(EntityInit.CIRCLE_ENTITY.get(), CircleEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.ENDSTONE_ENTITY.get(), EndstoneEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.PLAYER_MOB_ENTITY.get(), PlayerMobRenderer::new);
        event.registerEntityRenderer(EntityInit.NETHERRACK_ENTITY.get(), NetherrackEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.STONE_ENTITY.get(), StoneEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.LIGHTNING_BALL.get(), LightningBallRenderer::new);
        event.registerEntityRenderer(EntityInit.LAVA_ENTITY.get(), LavaEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.MC_LIGHTNING_BOLT.get(), MCLightningBoltEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.TORNADO_ENTITY.get(), TornadoEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.METEOR_NO_LEVEL_ENTITY.get(), MeteorNoLevelEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.ROAR_ENTITY.get(), RoarEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.WHISPERS_OF_CORRUPTION_ENTITY.get(), WhisperOfCorruptionEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.STORM_SEAL_ENTITY.get(), StormSealEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.WATER_COLUMN_ENTITY.get(), WaterColumnEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.DRAGON_BREATH_ENTITY.get(), DragonBreathRenderer::new);
        event.registerEntityRenderer(EntityInit.WIND_BLADE_ENTITY.get(), WindBladeRenderer::new);
        event.registerEntityRenderer(EntityInit.WIND_CUSHION_ENTITY.get(), WindCushionRenderer::new);
    }
}
