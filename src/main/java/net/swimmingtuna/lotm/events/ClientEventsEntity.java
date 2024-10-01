package net.swimmingtuna.lotm.events;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.Model.*;
import net.swimmingtuna.lotm.entity.Renderers.*;
import net.swimmingtuna.lotm.entity.Renderers.PlayerMobRenderer.PlayerMobRenderer;
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
        event.registerLayerDefinition(StoneEntityModel.STONE_MODEL_LOCATION, StoneEntityModel::createBodyLayer);
        event.registerLayerDefinition(EndstoneEntityModel.ENDSTONE_MODEL_LOCATION, EndstoneEntityModel::createBodyLayer);
        event.registerLayerDefinition(NetherrackEntityModel.NETHERRACK_MODEL_LOCATION, NetherrackEntityModel::createBodyLayer);
        event.registerLayerDefinition(LavaEntityModel.LAVA_ENTITY_LOCATION, LavaEntityModel::createBodyLayer);
        event.registerLayerDefinition(LightningBallModel.LIGHTNING_BALL_LOCATION, LightningBallModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.METEOR_ENTITY.get(), MeteorEntityRenderer::new);
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
        event.registerEntityRenderer(EntityInit.STORM_SEAL_ENTITY.get(), StormSealEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.WATER_COLUMN_ENTITY.get(), WaterColumnEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.DRAGON_BREATH_ENTITY.get(), DragonBreathRenderer::new);
        event.registerEntityRenderer(EntityInit.WIND_BLADE_ENTITY.get(), WindBladeRenderer::new);
        event.registerEntityRenderer(EntityInit.WIND_CUSHION_ENTITY.get(), WindCushionRenderer::new);
    }
}