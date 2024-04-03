package net.swimmingtuna.lotm.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.*;
import net.swimmingtuna.lotm.item.BeyonderPotions.BeyonderPotion;
import net.swimmingtuna.lotm.item.BeyonderPotions.BeyonderResetPotion;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, LOTM.MOD_ID);

    public static final RegistryObject<Item> MindReading = ITEMS.register("mindreading",
            () -> new MindReading(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> Awe = ITEMS.register("awe",
            () -> new Awe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> Frenzy = ITEMS.register("frenzy",
            () -> new Frenzy(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> Placate = ITEMS.register("placate",
            () -> new Placate(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BattleHypnotism = ITEMS.register("battlehypnotism",
            () -> new BattleHypnotism(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PsychologicalInvisibility = ITEMS.register("psychologicalinvisibility",
            () -> new PsychologicalInvisibility(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> Guidance = ITEMS.register("guidance",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DreamWalking = ITEMS.register("dreamwalking",
            () -> new DreamWalking(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> Nightmare = ITEMS.register("nightmare",
            () -> new Nightmare(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ManipulateMovement = ITEMS.register("manipulateemotion",
            () -> new ManipulateEmotion(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ManipulateEmotion = ITEMS.register("manipulatemovement",
            () -> new ManipulateMovement(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ManipulateFondness = ITEMS.register("manipulatefondness",
            () -> new ManipulateFondness(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ApplyManipulation = ITEMS.register("applymanipulation",
            () -> new ApplyManipulation(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MentalPlague = ITEMS.register("mentalplague",
            () -> new MentalPlague(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MindStorm = ITEMS.register("mindstorm",
            () -> new MindStorm(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ConsciousnessStroll = ITEMS.register("consciousnessstroll",
            () -> new ConciousnessStroll(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DragonBreath = ITEMS.register("dragonbreath",
            () -> new DragonBreath(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PlagueStorm = ITEMS.register("plaguestorm",
            () -> new PlagueStorm(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DreamWeaving = ITEMS.register("dreamweaving",
            () -> new DreamWeaving(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> Discern = ITEMS.register("discern",
            () -> new Discern(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DreamIntoReality = ITEMS.register("dreamintoreality",
            () -> new DreamIntoReality(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ProphesizeTeleportBlock = ITEMS.register("prophesize",
            () -> new ProphesizeTeleportBlock(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ProphesizeTeleportPlayer = ITEMS.register("prophesizeplayer",
            () -> new ProphesizeTeleportPlayer(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ProphesizeDemise = ITEMS.register("prophesizedemise",
            () -> new ProphesizeDemise(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionLife = ITEMS.register("envisionlife",
            () -> new EnvisionLife(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionDisasters = ITEMS.register("envisionnature",
            () -> new EnvisionDisasters(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionWeather = ITEMS.register("envisionweather",
            () -> new EnvisionWeather(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionBarrier = ITEMS.register("envisionbarrier",
            () -> new EnvisionBarrier(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionDeath = ITEMS.register("envisiondeath",
            () -> new EnvisionDeath(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionKingdom = ITEMS.register("envisionkingdom",
            () -> new EnvisionKingdom(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionLocation = ITEMS.register("envisionlocation",
            () -> new EnvisionLocation(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionLocationBlink = ITEMS.register("envisionlocationblink",
            () -> new EnvisionLocationBlink(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EnvisionHealth = ITEMS.register("envisionhealth",
            () -> new EnvisionHealth(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SPECTATOR_9_POTION = ITEMS.register("spectator_9_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), ()->BeyonderClassInit.SPECTATOR.get(),9));
    public static final RegistryObject<Item> SPECTATOR_8_POTION = ITEMS.register("spectator_8_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),8));
    public static final RegistryObject<Item> SPECTATOR_7_POTION = ITEMS.register("spectator_7_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),7));
    public static final RegistryObject<Item> SPECTATOR_6_POTION = ITEMS.register("spectator_6_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),6));
    public static final RegistryObject<Item> SPECTATOR_5_POTION = ITEMS.register("spectator_5_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),5));
    public static final RegistryObject<Item> SPECTATOR_4_POTION = ITEMS.register("spectator_4_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),4));
    public static final RegistryObject<Item> SPECTATOR_3_POTION = ITEMS.register("spectator_3_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),3));
    public static final RegistryObject<Item> SPECTATOR_2_POTION = ITEMS.register("spectator_2_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),2));
    public static final RegistryObject<Item> SPECTATOR_1_POTION = ITEMS.register("spectator_1_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),1));
    public static final RegistryObject<Item> TYRANT_9_POTION = ITEMS.register("tyrant_1_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.TYRANT.get(),1));
    public static final RegistryObject<Item> SPECTATOR_0_POTION = ITEMS.register("spectator_0_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1),  ()->BeyonderClassInit.SPECTATOR.get(),0));
    public static final RegistryObject<Item> BEYONDER_RESET_POTION = ITEMS.register("beyonder_reset_potion",
            () -> new BeyonderResetPotion(new Item.Properties().stacksTo(1)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
