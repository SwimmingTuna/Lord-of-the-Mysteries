package net.swimmingtuna.lotm.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;

public class CreativeTabInit {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LOTM.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ABILITIES_TAB = CREATIVE_MODE_TABS.register("abilities_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.AWE.get()))
                    .title(Component.translatable("creativetab.abilities_tab"))
                    .icon(() -> new ItemStack(ItemInit.ICONITEM.get()))
                    .displayItems((parameters, event) -> {
                        event.accept(ItemInit.BEYONDER_ABILITY_USER.get());
                        event.accept(ItemInit.MIND_READING.get());
                        event.accept(ItemInit.AWE.get());
                        event.accept(ItemInit.FRENZY.get());
                        event.accept(ItemInit.PLACATE.get());
                        event.accept(ItemInit.PSYCHOLOGICAL_INVISIBILITY.get());
                        event.accept(ItemInit.BATTLE_HYPNOTISM.get());
                        event.accept(ItemInit.GUIDANCE.get());
                        event.accept(ItemInit.ALTERATION.get());
                        event.accept(ItemInit.DREAM_WALKING.get());
                        event.accept(ItemInit.NIGHTMARE.get());
                        event.accept(ItemInit.APPLY_MANIPULATION.get());
                        event.accept(ItemInit.MANIPULATE_EMOTION.get());
                        event.accept(ItemInit.MANIPULATE_FONDNESS.get());
                        event.accept(ItemInit.MANIPULATE_MOVEMENT.get());
                        event.accept(ItemInit.MENTAL_PLAGUE.get());
                        event.accept(ItemInit.MIND_STORM.get());
                        event.accept(ItemInit.DRAGON_BREATH.get());
                        event.accept(ItemInit.CONSCIOUSNESS_STROLL.get());
                        event.accept(ItemInit.PLAGUE_STORM.get());
                        event.accept(ItemInit.DREAM_WEAVING.get());
                        event.accept(ItemInit.DISCERN.get());
                        event.accept(ItemInit.DREAM_INTO_REALITY.get());
                        event.accept(ItemInit.PROPHESIZE_DEMISE.get());
                        event.accept(ItemInit.PROPHESIZE_TELEPORT_BLOCK.get());
                        event.accept(ItemInit.PROPHESIZE_TELEPORT_PLAYER.get());
                        event.accept(ItemInit.METEOR_SHOWER.get());
                        event.accept(ItemInit.METEOR_NO_LEVEL_SHOWER.get());
                        event.accept(ItemInit.ENVISION_BARRIER.get());
                        event.accept(ItemInit.ENVISION_DEATH.get());
                        event.accept(ItemInit.ENVISIONHEALTH.get());
                        event.accept(ItemInit.ENVISION_KINGDOM.get());
                        event.accept(ItemInit.ENVISION_LIFE.get());
                        event.accept(ItemInit.ENVISION_LOCATION.get());
                        event.accept(ItemInit.ENVISION_LOCATION_BLINK.get());
                        event.accept(ItemInit.ENVISION_WEATHER.get());
                        event.accept(ItemInit.RAGING_BLOWS.get());
                        event.accept(ItemInit.ENABLE_OR_DISABLE_LIGHTNING.get());
                        event.accept(ItemInit.AQUEOUS_LIGHT_DROWN.get());
                        event.accept(ItemInit.AQUEOUS_LIGHT_PULL.get());
                        event.accept(ItemInit.AQUEOUS_LIGHT_PUSH.get());
                        event.accept(ItemInit.WIND_MANIPULATION_BLADE.get());
                        event.accept(ItemInit.WIND_MANIPULATION_CUSHION.get());
                        event.accept(ItemInit.WIND_MANIPULATION_FLIGHT.get());
                        event.accept(ItemInit.WIND_MANIPULATION_SENSE.get());
                        event.accept(ItemInit.SAILOR_LIGHTNING.get());
                        event.accept(ItemInit.SIREN_SONG_HARM.get());
                        event.accept(ItemInit.SIREN_SONG_STRENGTHEN.get());
                        event.accept(ItemInit.SIREN_SONG_STUN.get());
                        event.accept(ItemInit.SIREN_SONG_WEAKEN.get());
                        event.accept(ItemInit.ACIDIC_RAIN.get());
                        event.accept(ItemInit.WATER_SPHERE.get());
                        event.accept(ItemInit.TSUNAMI.get());
                        event.accept(ItemInit.TSUNAMI_SEAL.get());
                        event.accept(ItemInit.HURRICANE.get());
                        event.accept(ItemInit.TORNADO.get());
                        event.accept(ItemInit.EARTHQUAKE.get());
                        event.accept(ItemInit.SAILORPROJECTILECTONROL.get());
                        event.accept(ItemInit.ROAR.get());
                        event.accept(ItemInit.AQUATIC_LIFE_MANIPULATION.get());
                        event.accept(ItemInit.LIGHTNING_STORM.get());
                        event.accept(ItemInit.LIGHTNING_BRANCH.get());
                        event.accept(ItemInit.SONIC_BOOM.get());
                        event.accept(ItemInit.THUNDER_CLAP.get());
                        event.accept(ItemInit.RAIN_EYES.get());
                        event.accept(ItemInit.CALAMITY_INCARNATION_TORNADO.get());
                        event.accept(ItemInit.CALAMITY_INCARNATION_TSUNAMI.get());
                        event.accept(ItemInit.EXTREME_COLDNESS.get());
                        event.accept(ItemInit.VOLCANIC_ERUPTION.get());
                        event.accept(ItemInit.LIGHTNING_BALL.get());
                        event.accept(ItemInit.SAILOR_LIGHTNING_TRAVEL.get());
                        event.accept(ItemInit.LIGHTNING_BALL_ABSORB.get());
                        event.accept(ItemInit.STAR_OF_LIGHTNING.get());
                        event.accept(ItemInit.STORM_SEAL.get());
                        event.accept(ItemInit.WATER_COLUMN.get());
                        event.accept(ItemInit.MATTER_ACCELERATION_BLOCKS.get());
                        event.accept(ItemInit.MATTER_ACCELERATION_ENTITIES.get());
                        event.accept(ItemInit.MATTER_ACCELERATION_SELF.get());
                        event.accept(ItemInit.TYRANNY.get());

                        event.accept(ItemInit.LUCK_MANIPULATION.get());
                        event.accept(ItemInit.MONSTERDANGERSENSE.get());
                        event.accept(ItemInit.MONSTERPROJECTILECONTROL.get());
                        event.accept(ItemInit.LUCKPERCEPTION.get());
                        event.accept(ItemInit.PSYCHESTORM.get());
                        event.accept(ItemInit.SPIRITVISION.get());
                        event.accept(ItemInit.MONSTERCALAMITYATTRACTION.get());
                        event.accept(ItemInit.PROVIDENCEDOMAIN.get());
                        event.accept(ItemInit.DECAYDOMAIN.get());
                        event.accept(ItemInit.MONSTERDOMAINTELEPORATION.get());
                        event.accept(ItemInit.LUCKGIFTING.get());
                        event.accept(ItemInit.LUCKDEPRIVATION.get());
                        event.accept(ItemInit.MISFORTUNEBESTOWAL.get());
                        event.accept(ItemInit.LUCKCHANNELING.get());
                        event.accept(ItemInit.MISFORTUNEMANIPULATION.get());
                        event.accept(ItemInit.CALAMITYINCARNATION.get());
                        event.accept(ItemInit.ENABLEDISABLERIPPLE.get());
                        event.accept(ItemInit.AURAOFCHAOS.get());
                        event.accept(ItemInit.LUCKDENIAL.get());
                        event.accept(ItemInit.MISFORTUNEREDIRECTION.get());
                        event.accept(ItemInit.LUCKABSORPTION.get());
                        event.accept(ItemInit.FALSEPROPHECY.get());
                        event.accept(ItemInit.MONSTERREBOOT.get());
                        event.accept(ItemInit.FATEREINCARNATION.get());
                        event.accept(ItemInit.CYCLEOFFATE.get());
                        event.accept(ItemInit.CHAOSAMPLIFICATION.get());
                        event.accept(ItemInit.FATEDCONNECTION.get());
                        event.accept(ItemInit.REALMOFFORTUNE.get());
                        event.accept(ItemInit.PROBABILITYBODY.get());
                        event.accept(ItemInit.REALMOFPROBABILITY.get());
                        event.accept(ItemInit.PROBABILITYINCREASEDECREASE.get());
                        event.accept(ItemInit.PROBABILITYWIPE.get());
                        event.accept(ItemInit.PROBABILITYEFFECT.get());
                        event.accept(ItemInit.PROBABILITYMISFORTUNE.get());
                        event.accept(ItemInit.PROBABILITYFORTUNE.get());
                        event.accept(ItemInit.WHISPEROFCORRUPTION.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> INGREDIENTS_TAB = CREATIVE_MODE_TABS.register("ingredients_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.AWE.get()))
                    .title(Component.translatable("creativetab.abilities_tab"))
                    .icon(() -> new ItemStack(ItemInit.RED_CHESTNUT_FLOWER.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ItemInit.SPIRIT_EATER_STOMACH_POUCH.get());
                        output.accept(ItemInit.DEEP_SEA_MARLINS_BLOOD.get());
                        output.accept(ItemInit.HORNBEAM_ESSENTIALS_OIL.get());
                        output.accept(ItemInit.STRING_GRASS_POWDER.get());
                        output.accept(ItemInit.RED_CHESTNUT_FLOWER.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
