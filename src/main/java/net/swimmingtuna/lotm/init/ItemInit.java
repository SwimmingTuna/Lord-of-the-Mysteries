package net.swimmingtuna.lotm.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.item.BeyonderAbilities.BeyonderAbilityUser;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.*;
import net.swimmingtuna.lotm.item.BeyonderPotions.BeyonderPotion;
import net.swimmingtuna.lotm.item.BeyonderPotions.BeyonderResetPotion;
import net.swimmingtuna.lotm.item.TestItem;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, LOTM.MOD_ID);


    //ABILITIES


        //MONSTER
    public static final RegistryObject<Item> MONSTERDANGERSENSE = ITEMS.register("monsterdangersense",
            () -> new MonsterDangerSense(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MONSTERCALAMITYATTRACTION = ITEMS.register("monstercalamityattraction",
            () -> new MonsterDisableEnableCalamities(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LUCKPERCEPTION = ITEMS.register("luckperception",
            () -> new LuckPerception(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PSYCHESTORM = ITEMS.register("psychestorm",
            () -> new PsycheStorm(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SPIRITVISION = ITEMS.register("spiritvision",
            () -> new SpiritVision(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MONSTERPROJECTILECONTROL = ITEMS.register("monsterprojectilecontrol",
            () -> new MonsterProjectileControl(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LUCK_MANIPULATION = ITEMS.register("luckmanipulation",
            () -> new LuckManipulation(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LUCKGIFTING = ITEMS.register("luckgifting",
            () -> new LuckGifting(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LUCKDEPRIVATION = ITEMS.register("luckdeprivation",
            () -> new LuckDeprivation(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MISFORTUNEBESTOWAL = ITEMS.register("misfortunebestowal",
            () -> new MisfortuneBestowal(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LUCKFUTURETELLING = ITEMS.register("luckfuturetelling",
            () -> new LuckFutureTelling(new Item.Properties().stacksTo(1)));

        //SAILOR
    public static final RegistryObject<Item> TSUNAMI = ITEMS.register("tsunami",
            () -> new Tsunami(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EARTHQUAKE = ITEMS.register("earthquake",
            () -> new Earthquake(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAILORPROJECTILECTONROL = ITEMS.register("sailorprojectilecontrol",
            () -> new SailorProjectileControl(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STAR_OF_LIGHTNING = ITEMS.register("staroflightning",
            () -> new StarOfLightning(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LIGHTNING_REDIRECTION = ITEMS.register("lightningredirection",
            () -> new LightningRedirection(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RAIN_EYES = ITEMS.register("raineyes",
            () -> new RainEyes(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SONIC_BOOM = ITEMS.register("sonicboom",
            () -> new SonicBoom(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("testitem",
            () -> new TestItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LIGHTNING_STORM = ITEMS.register("sailorlightningstorm",
            () -> new LightningStorm(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ROAR = ITEMS.register("roar",
            () -> new Roar(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CALAMITY_INCARNATION_TORNADO = ITEMS.register("calamityincarnationtornado",
            () -> new CalamityIncarnationTornado(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CALAMITY_INCARNATION_TSUNAMI = ITEMS.register("calamityincarnationtsunami",
            () -> new CalamityIncarnationTsunami(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LIGHTNING_BALL = ITEMS.register("lightningball",
            () -> new LightningBall(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LIGHTNING_BALL_ABSORB = ITEMS.register("lightningballabsorb",
            () -> new LightningBallAbsorb(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MATTER_ACCELERATION_BLOCKS = ITEMS.register("matteraccelerationblocks",
            () -> new MatterAccelerationBlocks(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MATTER_ACCELERATION_ENTITIES = ITEMS.register("matteraccelerationentities",
            () -> new MatterAccelerationEntities(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MATTER_ACCELERATION_SELF = ITEMS.register("matteraccelerationself",
            () -> new MatterAccelerationSelf(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAILOR_LIGHTNING_TRAVEL = ITEMS.register("sailorlightningtravel",
            () -> new SailorLightningTravel(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STORM_SEAL = ITEMS.register("stormseal",
            () -> new StormSeal(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> VOLCANIC_ERUPTION = ITEMS.register("volcaniceruption",
            () -> new VolcanicEruption(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EXTREME_COLDNESS = ITEMS.register("extremecoldness",
            () -> new ExtremeColdness(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LIGHTNING_BRANCH = ITEMS.register("lightningbranch",
            () -> new LightningBranch(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAILOR_LIGHTNING = ITEMS.register("sailorlightning",
            () -> new SailorLightning(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WATER_SPHERE = ITEMS.register("watersphere",
            () -> new WaterSphere(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> THUNDER_CLAP = ITEMS.register("thunderclap",
            () -> new ThunderClap(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WATER_COLUMN = ITEMS.register("watercolumn",
            () -> new WaterColumn(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TYRANNY = ITEMS.register("tyranny",
            () -> new Tyranny(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HURRICANE = ITEMS.register("hurricane",
            () -> new Hurricane(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TORNADO = ITEMS.register("tornado",
            () -> new Tornado(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RAGING_BLOWS = ITEMS.register("ragingblows",
            () -> new RagingBlows(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> AQUEOUS_LIGHT_DROWN = ITEMS.register("aqueouslightdrown",
            () -> new AqueousLightDrown(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> AQUEOUS_LIGHT_PUSH = ITEMS.register("aqueouslightpush",
            () -> new AqueousLightPush(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> AQUEOUS_LIGHT_PULL = ITEMS.register("aqueouslightpull",
            () -> new AqueousLightPull(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENABLE_OR_DISABLE_LIGHTNING = ITEMS.register("enableordisablelightning",
            () -> new EnableOrDisableLightning(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WIND_MANIPULATION_FLIGHT = ITEMS.register("windmanipulationflight",
            () -> new WindManipulationFlight(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WIND_MANIPULATION_BLADE = ITEMS.register("windmanipulationblade",
            () -> new WindManipulationBlade(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WIND_MANIPULATION_CUSHION = ITEMS.register("windmanipulationcushion",
            () -> new WindManipulationCushion(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WIND_MANIPULATION_SENSE = ITEMS.register("windmanipulationsense",
            () -> new WindManipulationSense(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ACIDIC_RAIN = ITEMS.register("acidicrain",
            () -> new AcidicRain(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> AQUATIC_LIFE_MANIPULATION = ITEMS.register("aquaticlifemanipulation",
            () -> new AquaticLifeManipulation(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TSUNAMI_SEAL = ITEMS.register("tsunamiseal",
            () -> new TsunamiSeal(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SIREN_SONG_HARM = ITEMS.register("siren_song_harm",
            () -> new SirenSongHarm(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SIREN_SONG_STRENGTHEN = ITEMS.register("siren_song_strengthen",
            () -> new SirenSongStrengthen(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SIREN_SONG_STUN = ITEMS.register("siren_song_stun",
            () -> new SirenSongStun(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SIREN_SONG_WEAKEN = ITEMS.register("siren_song_weaken",
            () -> new SirenSongWeaken(new Item.Properties().stacksTo(1)));

        //SPECTATOR
    public static final RegistryObject<Item> MIND_READING = ITEMS.register("mindreading",
            () -> new MindReading(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> AWE = ITEMS.register("awe",
            () -> new Awe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FRENZY = ITEMS.register("frenzy",
            () -> new Frenzy(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PLACATE = ITEMS.register("placate",
            () -> new Placate(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BATTLE_HYPNOTISM = ITEMS.register("battlehypnotism",
            () -> new BattleHypnotism(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PSYCHOLOGICAL_INVISIBILITY = ITEMS.register("psychologicalinvisibility",
            () -> new PsychologicalInvisibility(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GUIDANCE = ITEMS.register("guidance",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DREAM_WALKING = ITEMS.register("dreamwalking",
            () -> new DreamWalking(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NIGHTMARE = ITEMS.register("nightmare",
            () -> new Nightmare(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MANIPULATE_MOVEMENT = ITEMS.register("manipulatemovement",
            () -> new ManipulateMovement(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MANIPULATE_EMOTION = ITEMS.register("manipulateemotion",
            () -> new ManipulateEmotion(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MANIPULATE_FONDNESS = ITEMS.register("manipulatefondness",
            () -> new ManipulateFondness(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> APPLY_MANIPULATION = ITEMS.register("applymanipulation",
            () -> new ApplyManipulation(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MENTAL_PLAGUE = ITEMS.register("mentalplague",
            () -> new MentalPlague(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MIND_STORM = ITEMS.register("mindstorm",
            () -> new MindStorm(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CONSCIOUSNESS_STROLL = ITEMS.register("consciousnessstroll",
            () -> new ConsciousnessStroll(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DRAGON_BREATH = ITEMS.register("dragonbreath",
            () -> new DragonBreath(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PLAGUE_STORM = ITEMS.register("plaguestorm",
            () -> new PlagueStorm(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DREAM_WEAVING = ITEMS.register("dreamweaving",
            () -> new DreamWeaving(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DISCERN = ITEMS.register("discern",
            () -> new Discern(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DREAM_INTO_REALITY = ITEMS.register("dreamintoreality",
            () -> new DreamIntoReality(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PROPHESIZE_TELEPORT_BLOCK = ITEMS.register("prophesizeblock",
            () -> new ProphesizeTeleportBlock(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PROPHESIZE_TELEPORT_PLAYER = ITEMS.register("prophesizeplayer",
            () -> new ProphesizeTeleportPlayer(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PROPHESIZE_DEMISE = ITEMS.register("prophesizedemise",
            () -> new ProphesizeDemise(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENVISION_LIFE = ITEMS.register("envisionlife",
            () -> new EnvisionLife(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> METEOR_SHOWER = ITEMS.register("meteorshower",
            () -> new MeteorShower(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> METEOR_NO_LEVEL_SHOWER = ITEMS.register("meteorshowernoblockdestruction",
            () -> new MeteorNoLevelShower(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENVISION_WEATHER = ITEMS.register("envisionweather",
            () -> new EnvisionWeather(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENVISION_BARRIER = ITEMS.register("envisionbarrier",
            () -> new EnvisionBarrier(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENVISION_DEATH = ITEMS.register("envisiondeath",
            () -> new EnvisionDeath(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENVISION_KINGDOM = ITEMS.register("envisionkingdom",
            () -> new EnvisionKingdom(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENVISION_LOCATION = ITEMS.register("envisionlocation",
            () -> new EnvisionLocation(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENVISION_LOCATION_BLINK = ITEMS.register("envisionlocationblink",
            () -> new EnvisionLocationBlink(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ALTERATION = ITEMS.register("alteration",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENVISIONHEALTH = ITEMS.register("envisionhealth",
            () -> new EnvisionHealth(new Item.Properties().stacksTo(1)));


    //INGREDIENTS
    public static final RegistryObject<Item> SPIRIT_EATER_STOMACH_POUCH = ITEMS.register("spirit_eater_stomach_pouch",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> DEEP_SEA_MARLINS_BLOOD = ITEMS.register("deep_sea_marlins_blood",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> HORNBEAM_ESSENTIALS_OIL = ITEMS.register("hornbeam_essentials_oil",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> STRING_GRASS_POWDER = ITEMS.register("string_grass_powder",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> RED_CHESTNUT_FLOWER = ITEMS.register("red_chestnut_flower",
            () -> new Item(new Item.Properties().stacksTo(16)));



    //UTIL
    public static final RegistryObject<Item> BEYONDER_ABILITY_USER = ITEMS.register("beyonderabilityuser",
            () -> new BeyonderAbilityUser(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ICONITEM = ITEMS.register("zqdsndnkawdnsalnkw",
            () -> new Item(new Item.Properties().stacksTo(1)));



    //POTIONS
    public static final RegistryObject<Item> SPECTATOR_9_POTION = ITEMS.register("spectator_9_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 9));
    public static final RegistryObject<Item> SPECTATOR_8_POTION = ITEMS.register("spectator_8_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 8));
    public static final RegistryObject<Item> SPECTATOR_7_POTION = ITEMS.register("spectator_7_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 7));
    public static final RegistryObject<Item> SPECTATOR_6_POTION = ITEMS.register("spectator_6_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 6));
    public static final RegistryObject<Item> SPECTATOR_5_POTION = ITEMS.register("spectator_5_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 5));
    public static final RegistryObject<Item> SPECTATOR_4_POTION = ITEMS.register("spectator_4_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 4));
    public static final RegistryObject<Item> SPECTATOR_3_POTION = ITEMS.register("spectator_3_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 3));
    public static final RegistryObject<Item> SPECTATOR_2_POTION = ITEMS.register("spectator_2_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 2));
    public static final RegistryObject<Item> SPECTATOR_1_POTION = ITEMS.register("spectator_1_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 1));
    public static final RegistryObject<Item> SPECTATOR_0_POTION = ITEMS.register("spectator_0_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SPECTATOR, 0));
    public static final RegistryObject<Item> TYRANT_9_POTION = ITEMS.register("tyrant_9_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_8_POTION = ITEMS.register("tyrant_8_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_7_POTION = ITEMS.register("tyrant_7_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_6_POTION = ITEMS.register("tyrant_6_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_5_POTION = ITEMS.register("tyrant_5_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_4_POTION = ITEMS.register("tyrant_4_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_3_POTION = ITEMS.register("tyrant_3_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_2_POTION = ITEMS.register("tyrant_2_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_1_POTION = ITEMS.register("tyrant_1_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> TYRANT_0_POTION = ITEMS.register("tyrant_0_potion",
            () -> new BeyonderPotion(new Item.Properties().stacksTo(1), BeyonderClassInit.SAILOR, 1));
    public static final RegistryObject<Item> BEYONDER_RESET_POTION = ITEMS.register("beyonder_reset_potion",
            () -> new BeyonderResetPotion(new Item.Properties().stacksTo(1)));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
