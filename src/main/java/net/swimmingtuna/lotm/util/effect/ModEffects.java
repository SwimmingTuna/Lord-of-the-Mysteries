package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, LOTM.MOD_ID);

    public static final RegistryObject<MobEffect> AWE = MOB_EFFECTS.register("awe",
            () -> new AweEffect(MobEffectCategory.HARMFUL,3124687));
    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding",
            () -> new BleedEffect(MobEffectCategory.HARMFUL,3124687));
    public static final RegistryObject<MobEffect> STUN = MOB_EFFECTS.register("stun",
            () -> new StunEffect(MobEffectCategory.HARMFUL,3124687));
    public static final RegistryObject<MobEffect> FRENZY = MOB_EFFECTS.register("frenzy",
            () -> new FrenzyEffect(MobEffectCategory.HARMFUL,3124687));
    public static final RegistryObject<MobEffect> BATTLEHYPNOTISM = MOB_EFFECTS.register("battlehypnotism",
            () -> new BattleHypnotismEffect(MobEffectCategory.HARMFUL,3124687));
    public static final RegistryObject<MobEffect> NIGHTMARE = MOB_EFFECTS.register("nightmare",
            () -> new NightmareEffect(MobEffectCategory.HARMFUL, 3124687));
    public static final RegistryObject<MobEffect> MANIPULATION = MOB_EFFECTS.register("manipulation",
            () -> new ManipulationEffect(MobEffectCategory.HARMFUL, 3124687));
    public static final RegistryObject<MobEffect> MENTALPLAGUE = MOB_EFFECTS.register("mentalplague",
            () -> new MentalPlagueEffect(MobEffectCategory.HARMFUL, 3124687));
    public static final RegistryObject<MobEffect> SPECTATORDEMISE = MOB_EFFECTS.register("demise",
            () -> new SpectatorDemiseEffect(MobEffectCategory.HARMFUL, 3124687));
    public static final RegistryObject<MobEffect> LOTMGLOWING = MOB_EFFECTS.register("lotmglowing",
            () -> new LOTMGlowingEffect(MobEffectCategory.HARMFUL, 3124687));
    public static final RegistryObject<MobEffect> PARALYSIS = MOB_EFFECTS.register("paralysis",
            () -> new ParalysisEffect(MobEffectCategory.HARMFUL, 3124687));
    public static final RegistryObject<MobEffect> NOREGENERATION = MOB_EFFECTS.register("noregeneration",
            () -> new NoRegenerationEffect(MobEffectCategory.HARMFUL, 3124687));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
