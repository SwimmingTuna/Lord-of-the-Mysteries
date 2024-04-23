package net.swimmingtuna.lotm.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.Model.DragonBreathModel;
import net.swimmingtuna.lotm.entity.Model.MeteorNoLevelModel;
import net.swimmingtuna.lotm.entity.Renderers.DragonBreathRenderer;
import net.swimmingtuna.lotm.entity.Renderers.MeteorEntityRenderer;
import net.swimmingtuna.lotm.entity.Model.MeteorModel;
import net.swimmingtuna.lotm.entity.Renderers.MeteorNoLevelEntityRenderer;
import net.swimmingtuna.lotm.init.EntityInit;


@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventsEntity {

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MeteorModel.METEOR_LOCATION, MeteorModel::createBodyLayer);
        event.registerLayerDefinition(MeteorNoLevelModel.METEOR_LOCATION, MeteorNoLevelModel::createBodyLayer);
        event.registerLayerDefinition(DragonBreathModel.LAYER, DragonBreathModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.METEOR_ENTITY.get(), MeteorEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.METEOR_NO_LEVEL_ENTITY.get(), MeteorNoLevelEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.DRAGON_BREATH_ENTITY.get(), DragonBreathRenderer::new);
    }
}
