package net.swimmingtuna.lotm;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.client.ClientConfigs;
import net.swimmingtuna.lotm.events.ClientEvents;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.CommandInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LOTM.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LOTM {

    public static Supplier<Boolean> fadeOut;
    public static Supplier<Integer> fadeTicks;
    public static Supplier<Double> maxBrightness;
    public static Supplier<Double> fadeRate = () -> maxBrightness.get() / fadeTicks.get();

    public static ResourceLocation modLoc(String name) {return new ResourceLocation(MOD_ID, name);}


    public static final String MOD_ID = "lotm";

    private static final Logger LOGGER = LogUtils.getLogger();
    public LOTM()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.register(modEventBus);
        ModEffects.register(modEventBus);
        ModAttributes.register(modEventBus);
        CommandInit.ARGUMENT_TYPES.register(modEventBus);
        BeyonderClassInit.BEYONDER_CLASS.register(modEventBus);

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
        LOTMNetworkHandler.register();
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                event.accept(ItemInit.MindReading);
                event.accept(ItemInit.Awe);
                event.accept(ItemInit.Frenzy);
                event.accept(ItemInit.Placate);
                event.accept(ItemInit.BattleHypnotism);
                event.accept(ItemInit.PsychologicalInvisibility);
                event.accept(ItemInit.Guidance);
                event.accept(ItemInit.Manipulation);
                event.accept(ItemInit.MentalPlague);
                event.accept(ItemInit.MindStorm);
                event.accept(ItemInit.ConsciousnessStroll);
                event.accept(ItemInit.DragonBreath);
                event.accept(ItemInit.PlagueStorm);
                event.accept(ItemInit.DreamWeaving);
                event.accept(ItemInit.Discern);
                event.accept(ItemInit.DreamIntoReality);
                event.accept(ItemInit.Prophesize);
                event.accept(ItemInit.EnvisionLife);
                event.accept(ItemInit.EnvisionNature);
                event.accept(ItemInit.EnvisionWeather);
                event.accept(ItemInit.EnvisionBarrier);
                event.accept(ItemInit.EnvisionDeath);
                event.accept(ItemInit.EnvisionKingdom);
                event.accept(ItemInit.EnvisionLocation);
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
        }
    }
}
