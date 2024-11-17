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
import net.swimmingtuna.lotm.particle.AcidRainParticle;
import net.swimmingtuna.lotm.particle.MeteorParticle;
import net.swimmingtuna.lotm.particle.NullParticle;
import net.swimmingtuna.lotm.particle.SonicBoomParticle;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents2 {
    @SubscribeEvent
    public static void registerParticleProvidersEvent(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleInit.ACIDRAIN_PARTICLE.get(), AcidRainParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.NULL_PARTICLE.get(), NullParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.METEOR_PARTICLE.get(), MeteorParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.TORNADO_PARTICLE.get(), NullParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.SONIC_BOOM_PARTICLE.get(), SonicBoomParticle.Provider::new);
    }
    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        ClientEventsEntity.registerLayerDefinition(event);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.METEOR_TRAIL_ENTITY.get(), MeteorTrailRenderer::new);
        ClientEventsEntity.onRegisterRenderers(event);
    }
}
