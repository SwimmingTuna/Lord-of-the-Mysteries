package net.swimmingtuna.lotm.events;


import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.particle.AcidRainParticle;
import net.swimmingtuna.lotm.particle.NullParticle;
import net.swimmingtuna.lotm.particle.SonicBoomParticle;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents2 {
    @SubscribeEvent
    public static void registerParticleProvidersEvent(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleInit.ACIDRAIN_PARTICLE.get(), AcidRainParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.NULL_PARTICLE.get(), NullParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.TORNADO_PARTICLE.get(), NullParticle.Provider::new);
        event.registerSpriteSet(ParticleInit.SONIC_BOOM_PARTICLE.get(), SonicBoomParticle.Provider::new);
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_BARRIER_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.LOTM_BLUE_STAINED_GLASS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.LOTM_WHITE_STAINED_GLASS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.LOTM_LIGHT_BLUE_STAINED_GLASS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_BLACK_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_CYAN_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_LIME_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_GREEN_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_LIGHT_BLUE_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_BLUE_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_WHITE_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_GRAY_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_LIGHT_GRAY_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_BROWN_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_PURPLE_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_MAGENTA_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_YELLOW_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_RED_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_PINK_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_ORANGE_STAINED_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_GLASS_PANE.get(), RenderType.translucent());
        LOTMNetworkHandler.register();
    }
}
