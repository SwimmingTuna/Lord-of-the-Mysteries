package net.swimmingtuna.lotm.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.Model.*;
import net.swimmingtuna.lotm.entity.Renderers.*;
import net.swimmingtuna.lotm.init.EntityInit;


@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventsEntity {

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MeteorModel.METEOR_LOCATION, MeteorModel::createBodyLayer);
        event.registerLayerDefinition(MeteorNoLevelModel.METEOR_LOCATION, MeteorNoLevelModel::createBodyLayer);
        event.registerLayerDefinition(DragonBreathModel.LAYER, DragonBreathModel::createBodyLayer);
        event.registerLayerDefinition(WindBladeModel.WIND_BLADE_LOCATION, WindBladeModel::createBodyLayer);
        event.registerLayerDefinition(WindCushionModel.WIND_CUSHION_LOCATION, WindCushionModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.METEOR_ENTITY.get(), MeteorEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.METEOR_NO_LEVEL_ENTITY.get(), MeteorNoLevelEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.DRAGON_BREATH_ENTITY.get(), DragonBreathRenderer::new);
        event.registerEntityRenderer(EntityInit.WIND_BLADE_ENTITY.get(), WindBladeRenderer::new);
        event.registerEntityRenderer(EntityInit.WIND_CUSHION_ENTITY.get(), WindCushionRenderer::new);
    }
}
