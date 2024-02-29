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
    public static final RegistryObject<MobEffect> FRENZY = MOB_EFFECTS.register("frenzy",
            () -> new FrenzyEffect(MobEffectCategory.HARMFUL,3124687));
    public static final RegistryObject<MobEffect> BATTLEHYPNOTISM = MOB_EFFECTS.register("battlehypnotism",
            () -> new BattleHypnotismEffect(MobEffectCategory.HARMFUL,3124687));
    public static final RegistryObject<MobEffect> NIGHTMARE = MOB_EFFECTS.register("nightmare",
            () -> new NightmareEffect(MobEffectCategory.HARMFUL, 3124687));



    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
