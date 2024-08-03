package net.swimmingtuna.lotm.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.particle.AcidRainParticle;
import net.swimmingtuna.lotm.particle.NullParticle;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents2 {
    @SubscribeEvent
    public static void registerParticleProvidersEvent(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleInit.ACIDRAIN_PARTICLE.get(), AcidRainParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.NULL_PARTICLE.get(), NullParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.TORNADO_PARTICLE.get(), NullParticle.Provider::new);
    }
}
