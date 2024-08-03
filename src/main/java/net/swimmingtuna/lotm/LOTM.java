package net.swimmingtuna.lotm;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.client.ClientConfigs;
import net.swimmingtuna.lotm.entity.Renderers.AqueousLightEntityPullRenderer;
import net.swimmingtuna.lotm.entity.Renderers.AqueousLightEntityPushRenderer;
import net.swimmingtuna.lotm.entity.Renderers.AqueousLightEntityRenderer;
import net.swimmingtuna.lotm.entity.Renderers.LightningEntityRenderer;
import net.swimmingtuna.lotm.events.ClientEvents;
import net.swimmingtuna.lotm.init.*;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LOTM.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LOTM {

    public static final int NEW_STRUCTURE_SIZE = 512;
    public static Supplier<Boolean> fadeOut;
    public static Supplier<Integer> fadeTicks;
    public static Supplier<Double> maxBrightness;
    public static Supplier<Double> fadeRate = () -> maxBrightness.get() / fadeTicks.get();
    public static ResourceLocation modLoc(String name) {return new ResourceLocation(MOD_ID, name);}


    public static final String MOD_ID = "lotm";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation rl(String name)
    {
        return new ResourceLocation(LOTM.MOD_ID, name);
    }
    public LOTM()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
        ItemInit.register(modEventBus);
        BlockInit.register(modEventBus);
        ModEffects.register(modEventBus);
        ModAttributes.register(modEventBus);
        EntityInit.register(modEventBus);
        CommandInit.ARGUMENT_TYPES.register(modEventBus);
        BeyonderClassInit.BEYONDER_CLASS.register(modEventBus);
        ParticleInit.register(modEventBus);
        SoundInit.register(modEventBus);

        modEventBus.addListener(ClientEvents::onRegisterOverlays);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC, String.format("%s-client.toml", LOTM.MOD_ID));
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        BeyonderHolderAttacher.register();
        MinecraftForge.EVENT_BUS.addListener(CommandInit::onCommandRegistration);
    }



    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_BARRIER_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.VISIONARY_BLACK_STAINED_GLASS_PANE.get(), RenderType.cutout());
        LOTMNetworkHandler.register();
    }



    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.AQUEOUS_LIGHT_ENTITY.get(), AqueousLightEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.AQUEOUS_LIGHT_ENTITY_PUSH.get(), AqueousLightEntityPushRenderer::new);
        event.registerEntityRenderer(EntityInit.AQUEOUS_LIGHT_ENTITY_PULL.get(), AqueousLightEntityPullRenderer::new);
        event.registerEntityRenderer(EntityInit.LINE_ENTITY.get(), LightningEntityRenderer::new);


    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                event.accept(ItemInit.TestItem);
                event.accept(ItemInit.Earthquake);
                event.accept(ItemInit.SailorLightning);
                event.accept(ItemInit.MindReading);
                event.accept(ItemInit.Awe);
                event.accept(ItemInit.Frenzy);
                event.accept(ItemInit.Placate);
                event.accept(ItemInit.BattleHypnotism);
                event.accept(ItemInit.PsychologicalInvisibility);
                event.accept(ItemInit.Guidance);
                event.accept(ItemInit.DreamWalking);
                event.accept(ItemInit.Nightmare);
                event.accept(ItemInit.ManipulateMovement);
                event.accept(ItemInit.ManipulateEmotion);
                event.accept(ItemInit.ApplyManipulation);
                event.accept(ItemInit.MentalPlague);
                event.accept(ItemInit.MindStorm);
                event.accept(ItemInit.ManipulateFondness);
                event.accept(ItemInit.ConsciousnessStroll);
                event.accept(ItemInit.DragonBreath);
                event.accept(ItemInit.PlagueStorm);
                event.accept(ItemInit.DreamWeaving);
                event.accept(ItemInit.Discern);
                event.accept(ItemInit.DreamIntoReality);
                event.accept(ItemInit.ProphesizeTeleportBlock);
                event.accept(ItemInit.ProphesizeTeleportPlayer);
                event.accept(ItemInit.ProphesizeDemise);
                event.accept(ItemInit.EnvisionLife);
                event.accept(ItemInit.MeteorShower);
                event.accept(ItemInit.MeteorNoLevelShower);
                event.accept(ItemInit.EnvisionWeather);
                event.accept(ItemInit.EnvisionBarrier);
                event.accept(ItemInit.EnvisionDeath);
                event.accept(ItemInit.EnvisionKingdom);
                event.accept(ItemInit.EnvisionLocation);
                event.accept(ItemInit.EnvisionLocationBlink);
                event.accept(ItemInit.EnvisionHealth);
                event.accept(ItemInit.SPECTATOR_9_POTION);
                event.accept(ItemInit.SPECTATOR_8_POTION);
                event.accept(ItemInit.SPECTATOR_7_POTION);
                event.accept(ItemInit.SPECTATOR_6_POTION);
                event.accept(ItemInit.SPECTATOR_5_POTION);
                event.accept(ItemInit.SPECTATOR_4_POTION);
                event.accept(ItemInit.SPECTATOR_3_POTION);
                event.accept(ItemInit.SPECTATOR_2_POTION);
                event.accept(ItemInit.SPECTATOR_1_POTION);
                event.accept(ItemInit.SPECTATOR_0_POTION);
                event.accept(ItemInit.BEYONDER_RESET_POTION);
                event.accept(ItemInit.TYRANT_9_POTION);
                event.accept(ItemInit.TYRANT_8_POTION);
                event.accept(ItemInit.TYRANT_7_POTION);
                event.accept(ItemInit.TYRANT_6_POTION);
                event.accept(ItemInit.TYRANT_5_POTION);
                event.accept(ItemInit.TYRANT_4_POTION);
                event.accept(ItemInit.TYRANT_3_POTION);
                event.accept(ItemInit.TYRANT_2_POTION);
                event.accept(ItemInit.TYRANT_1_POTION);
                event.accept(ItemInit.TYRANT_0_POTION);
                event.accept(ItemInit.RagingBlows);
                event.accept(ItemInit.AqueousLightDrown);
                event.accept(ItemInit.EnableOrDisableLightning);
                event.accept(ItemInit.AqueousLightPull);
                event.accept(ItemInit.AqueousLightPush);
                event.accept(ItemInit.WindManipulationFlight);
                event.accept(ItemInit.WindManipulationBlade);
                event.accept(ItemInit.WindManipulationCushion);
                event.accept(ItemInit.WindManipulationSense);
                event.accept(ItemInit.AcidicRain);
                event.accept(ItemInit.SirenSongHarm);
                event.accept(ItemInit.SirenSongWeaken);
                event.accept(ItemInit.SirenSongStun);
                event.accept(ItemInit.SirenSongStrengthen);
        }
        if(event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            event.accept(BlockInit.VISIONARY_BARRIER_BLOCK);
            event.accept(BlockInit.CATHEDRAL_BLOCK);
            event.accept(BlockInit.MINDSCAPE_BLOCK);
            event.accept(BlockInit.MINDSCAPE_OUTSIDE);
            event.accept(BlockInit.VISIONARY_BLACK_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_WHITE_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_GRAY_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_LIGHT_GRAY_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_BROWN_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_PURPLE_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_CYAN_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_BLUE_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_LIGHT_BLUE_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_LIME_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_GREEN_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_YELLOW_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_RED_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_ORANGE_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_PINK_STAINED_GLASS_PANE);
            event.accept(BlockInit.VISIONARY_MAGENTA_STAINED_GLASS_PANE);

            event.accept(BlockInit.LOTM_DARKOAK_SLAB);
            event.accept(BlockInit.LOTM_QUARTZ_SLAB);
            event.accept(BlockInit.LOTM_DARKOAK_STAIRS);
            event.accept(BlockInit.LOTM_OAK_STAIRS);
            event.accept(BlockInit.LOTM_QUARTZ_STAIRS);
            event.accept(BlockInit.LOTM_DEEPSLATEBRICK_STAIRS);
        }
    }
}
