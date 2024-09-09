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
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.client.ClientConfigs;
import net.swimmingtuna.lotm.client.Configs;
import net.swimmingtuna.lotm.entity.Renderers.AqueousLightEntityPullRenderer;
import net.swimmingtuna.lotm.entity.Renderers.AqueousLightEntityPushRenderer;
import net.swimmingtuna.lotm.entity.Renderers.AqueousLightEntityRenderer;
import net.swimmingtuna.lotm.entity.Renderers.LightningEntityRenderer;
import net.swimmingtuna.lotm.events.ClientEvents;
import net.swimmingtuna.lotm.init.*;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.PlayerMobs.NameManager;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.worldgen.biome.BiomeModifierRegistry;
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

    public static final String MOD_ID = "lotm";

    private static final Logger LOGGER = LogUtils.getLogger();

    public LOTM() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
        BeyonderClassInit.BEYONDER_CLASS.register(modEventBus);
        BeyonderHolderAttacher.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.commonSpec);
        BlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
        CreativeTabInit.register(modEventBus);
        ItemInit.register(modEventBus);
        BlockInit.register(modEventBus);
        ModEffects.register(modEventBus);
        ModAttributes.register(modEventBus);
        EntityInit.register(modEventBus);
        modEventBus.addListener(EntityInit::registerEntityAttributes);
        CommandInit.ARGUMENT_TYPES.register(modEventBus);
        ParticleInit.register(modEventBus);
        SoundInit.register(modEventBus);

        modEventBus.addListener(ClientEvents::onRegisterOverlays);
        BiomeModifierRegistry.BIOME_MODIFIER_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC, String.format("%s-client.toml", LOTM.MOD_ID));
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.addListener(CommandInit::onCommandRegistration);

    }

    private void serverAboutToStart(ServerAboutToStartEvent event) {
        NameManager.INSTANCE.init();
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


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.AQUEOUS_LIGHT_ENTITY.get(), AqueousLightEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.AQUEOUS_LIGHT_ENTITY_PUSH.get(), AqueousLightEntityPushRenderer::new);
        event.registerEntityRenderer(EntityInit.AQUEOUS_LIGHT_ENTITY_PULL.get(), AqueousLightEntityPullRenderer::new);
        event.registerEntityRenderer(EntityInit.LIGHTNING_ENTITY.get(), LightningEntityRenderer::new);


    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ItemInit.TEST_ITEM);
            event.accept(ItemInit.LIGHTNING_STORM);
            event.accept(ItemInit.ROAR);
            event.accept(ItemInit.CALAMITY_INCARNATION_TSUNAMI);
            event.accept(ItemInit.CALAMITY_INCARNATION_TORNADO);
            event.accept(ItemInit.LIGHTNING_BALL);
            event.accept(ItemInit.LIGHTNING_BALL_ABSORB);
            event.accept(ItemInit.MATTER_ACCELERATION_BLOCKS);
            event.accept(ItemInit.MATTER_ACCELERATION_ENTITIES);
            event.accept(ItemInit.MATTER_ACCELERATION_SELF);
            event.accept(ItemInit.STORM_SEAL);
            event.accept(ItemInit.SAILOR_LIGHTNING_TRAVEL);
            event.accept(ItemInit.STORM_SEAL);
            event.accept(ItemInit.VOLCANIC_ERUPTION);
            event.accept(ItemInit.EXTREME_COLDNESS);
            event.accept(ItemInit.LIGHTNING_BRANCH);
            event.accept(ItemInit.EARTHQUAKE);
            event.accept(ItemInit.STAR_OF_LIGHTNING);
            event.accept(ItemInit.RAIN_EYES);
            event.accept(ItemInit.SONIC_BOOM);
            event.accept(ItemInit.SAILOR_LIGHTNING);
            event.accept(ItemInit.WATER_SPHERE);
            event.accept(ItemInit.THUNDER_CLAP);
            event.accept(ItemInit.TYRANNY);
            event.accept(ItemInit.WATER_COLUMN);
            event.accept(ItemInit.HURRICANE);
            event.accept(ItemInit.TORNADO);
            event.accept(ItemInit.MIND_READING);
            event.accept(ItemInit.AWE);
            event.accept(ItemInit.FRENZY);
            event.accept(ItemInit.PLACATE);
            event.accept(ItemInit.BATTLE_HYPNOTISM);
            event.accept(ItemInit.PSYCHOLOGICAL_INVISIBILITY);
            event.accept(ItemInit.GUIDANCE);
            event.accept(ItemInit.BEYONDER_ABILITY_USER);
            event.accept(ItemInit.ALTERATION);
            event.accept(ItemInit.DREAM_WALKING);
            event.accept(ItemInit.NIGHTMARE);
            event.accept(ItemInit.MANIPULATE_MOVEMENT);
            event.accept(ItemInit.MANIPULATE_EMOTION);
            event.accept(ItemInit.APPLY_MANIPULATION);
            event.accept(ItemInit.MENTAL_PLAGUE);
            event.accept(ItemInit.MIND_STORM);
            event.accept(ItemInit.MANIPULATE_FONDNESS);
            event.accept(ItemInit.CONSCIOUSNESS_STROLL);
            event.accept(ItemInit.DRAGON_BREATH);
            event.accept(ItemInit.PLAGUE_STORM);
            event.accept(ItemInit.DREAM_WEAVING);
            event.accept(ItemInit.DISCERN);
            event.accept(ItemInit.TSUNAMI);
            event.accept(ItemInit.DREAM_INTO_REALITY);
            event.accept(ItemInit.PROPHESIZE_TELEPORT_BLOCK);
            event.accept(ItemInit.PROPHESIZE_TELEPORT_PLAYER);
            event.accept(ItemInit.PROPHESIZE_DEMISE);
            event.accept(ItemInit.ENVISION_LIFE);
            event.accept(ItemInit.METEOR_SHOWER);
            event.accept(ItemInit.METEOR_NO_LEVEL_SHOWER);
            event.accept(ItemInit.ENVISION_WEATHER);
            event.accept(ItemInit.ENVISION_BARRIER);
            event.accept(ItemInit.ENVISION_DEATH);
            event.accept(ItemInit.ENVISION_KINGDOM);
            event.accept(ItemInit.ENVISION_LOCATION);
            event.accept(ItemInit.ENVISION_LOCATION_BLINK);
            event.accept(ItemInit.ENVISIONHEALTH);
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
            event.accept(ItemInit.RAGING_BLOWS);
            event.accept(ItemInit.AQUEOUS_LIGHT_DROWN);
            event.accept(ItemInit.ENABLE_OR_DISABLE_LIGHTNING);
            event.accept(ItemInit.AQUEOUS_LIGHT_PULL);
            event.accept(ItemInit.AQUEOUS_LIGHT_PUSH);
            event.accept(ItemInit.WIND_MANIPULATION_FLIGHT);
            event.accept(ItemInit.WIND_MANIPULATION_BLADE);
            event.accept(ItemInit.WIND_MANIPULATION_CUSHION);
            event.accept(ItemInit.WIND_MANIPULATION_SENSE);
            event.accept(ItemInit.ACIDIC_RAIN);
            event.accept(ItemInit.AQUATIC_LIFE_MANIPULATION);
            event.accept(ItemInit.TSUNAMI_SEAL);
            event.accept(ItemInit.SIREN_SONG_HARM);
            event.accept(ItemInit.SIREN_SONG_WEAKEN);
            event.accept(ItemInit.SIREN_SONG_STUN);
            event.accept(ItemInit.SIREN_SONG_STRENGTHEN);
        }
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            event.accept(BlockInit.VISIONARY_BARRIER_BLOCK);
            event.accept(BlockInit.VISIONARY_GLASS_PANE);
            event.accept(BlockInit.LOTM_LIGHT_BLUE_STAINED_GLASS);
            event.accept(BlockInit.LOTM_RED_NETHER_BRICKS);
            event.accept(BlockInit.LOTM_WHITE_STAINED_GLASS);
            event.accept(BlockInit.LOTM_BLUE_STAINED_GLASS);
            event.accept(BlockInit.CATHEDRAL_BLOCK);
            event.accept(BlockInit.MINDSCAPE_BLOCK);
            event.accept(BlockInit.MINDSCAPE_OUTSIDE);
            event.accept(BlockInit.LOTM_BOOKSHELF);
            event.accept(BlockInit.LOTM_DEEPSLATE_BRICKS);
            event.accept(BlockInit.LOTM_REDSTONE_BLOCK);
            event.accept(BlockInit.LOTM_SANDSTONE);
            event.accept(BlockInit.MINDSCAPE_OUTSIDE);
            event.accept(BlockInit.LOTM_POLISHED_DIORITE);
            event.accept(BlockInit.LOTM_DARK_OAK_PLANKS);
            event.accept(BlockInit.LOTM_QUARTZ);
            event.accept(BlockInit.LOTM_CHISELED_STONE_BRICKS);
            event.accept(BlockInit.LOTM_MANGROVE_PLANKS);
            event.accept(BlockInit.LOTM_SPRUCE_PLANKS);
            event.accept(BlockInit.LOTM_SPRUCE_LOG);
            event.accept(BlockInit.LOTM_OAK_PLANKS);
            event.accept(BlockInit.LOTM_BIRCH_PLANKS);
            event.accept(BlockInit.LOTM_BLACK_CONCRETE);
            event.accept(BlockInit.LOTM_STONE);
            event.accept(BlockInit.LOTM_STONE_BRICKS);
            event.accept(BlockInit.LOTM_CRACKED_STONE_BRICKS);
            event.accept(BlockInit.LOTM_LIGHT_BLUE_CONCRETE);
            event.accept(BlockInit.LOTM_BLUE_CONCRETE);
            event.accept(BlockInit.LOTM_BLACKSTONE);
            event.accept(BlockInit.LOTM_WHITE_CONCRETE);
            event.accept(BlockInit.LOTM_POLISHED_ANDESITE);
            event.accept(BlockInit.LOTM_POLISHED_BLACKSTONE);
            event.accept(BlockInit.LOTM_SEA_LANTERN);
            event.accept(BlockInit.LOTM_OAK_LOG);

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
            event.accept(BlockInit.LOTM_LIGHT_BLUE_CARPET);
            event.accept(BlockInit.LOTM_CHAIN);
            event.accept(BlockInit.LOTM_LANTERN);

            event.accept(BlockInit.LOTM_DARKOAK_SLAB);
            event.accept(BlockInit.LOTM_QUARTZ_SLAB);
            event.accept(BlockInit.LOTM_DARKOAK_STAIRS);
            event.accept(BlockInit.LOTM_OAK_STAIRS);
            event.accept(BlockInit.LOTM_QUARTZ_STAIRS);
            event.accept(BlockInit.LOTM_DEEPSLATEBRICK_STAIRS);
        }
    }
}
