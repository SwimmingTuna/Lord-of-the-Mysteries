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
                    .displayItems((parameters, output) -> {
                        output.accept(ItemInit.BEYONDER_ABILITY_USER.get());
                        output.accept(ItemInit.MIND_READING.get());
                        output.accept(ItemInit.AWE.get());
                        output.accept(ItemInit.FRENZY.get());
                        output.accept(ItemInit.PLACATE.get());
                        output.accept(ItemInit.PSYCHOLOGICAL_INVISIBILITY.get());
                        output.accept(ItemInit.BATTLE_HYPNOTISM.get());
                        output.accept(ItemInit.GUIDANCE.get());
                        output.accept(ItemInit.ALTERATION.get());
                        output.accept(ItemInit.DREAM_WALKING.get());
                        output.accept(ItemInit.NIGHTMARE.get());
                        output.accept(ItemInit.APPLY_MANIPULATION.get());
                        output.accept(ItemInit.MANIPULATE_EMOTION.get());
                        output.accept(ItemInit.MANIPULATE_FONDNESS.get());
                        output.accept(ItemInit.MANIPULATE_MOVEMENT.get());
                        output.accept(ItemInit.MENTAL_PLAGUE.get());
                        output.accept(ItemInit.MIND_STORM.get());
                        output.accept(ItemInit.DRAGON_BREATH.get());
                        output.accept(ItemInit.CONSCIOUSNESS_STROLL.get());
                        output.accept(ItemInit.PLAGUE_STORM.get());
                        output.accept(ItemInit.DREAM_WEAVING.get());
                        output.accept(ItemInit.DISCERN.get());
                        output.accept(ItemInit.DREAM_INTO_REALITY.get());
                        output.accept(ItemInit.PROPHESIZE_DEMISE.get());
                        output.accept(ItemInit.PROPHESIZE_TELEPORT_BLOCK.get());
                        output.accept(ItemInit.PROPHESIZE_TELEPORT_PLAYER.get());
                        output.accept(ItemInit.METEOR_SHOWER.get());
                        output.accept(ItemInit.METEOR_NO_LEVEL_SHOWER.get());
                        output.accept(ItemInit.ENVISION_BARRIER.get());
                        output.accept(ItemInit.ENVISION_DEATH.get());
                        output.accept(ItemInit.ENVISIONHEALTH.get());
                        output.accept(ItemInit.ENVISION_KINGDOM.get());
                        output.accept(ItemInit.ENVISION_LIFE.get());
                        output.accept(ItemInit.ENVISION_LOCATION.get());
                        output.accept(ItemInit.ENVISION_LOCATION_BLINK.get());
                        output.accept(ItemInit.ENVISION_WEATHER.get());
                        output.accept(ItemInit.RAGING_BLOWS.get());
                        output.accept(ItemInit.ENABLE_OR_DISABLE_LIGHTNING.get());
                        output.accept(ItemInit.AQUEOUS_LIGHT_DROWN.get());
                        output.accept(ItemInit.AQUEOUS_LIGHT_PULL.get());
                        output.accept(ItemInit.AQUEOUS_LIGHT_PUSH.get());
                        output.accept(ItemInit.WIND_MANIPULATION_BLADE.get());
                        output.accept(ItemInit.WIND_MANIPULATION_CUSHION.get());
                        output.accept(ItemInit.WIND_MANIPULATION_FLIGHT.get());
                        output.accept(ItemInit.WIND_MANIPULATION_SENSE.get());
                        output.accept(ItemInit.SAILOR_LIGHTNING.get());
                        output.accept(ItemInit.SIREN_SONG_HARM.get());
                        output.accept(ItemInit.SIREN_SONG_STRENGTHEN.get());
                        output.accept(ItemInit.SIREN_SONG_STUN.get());
                        output.accept(ItemInit.SIREN_SONG_WEAKEN.get());
                        output.accept(ItemInit.ACIDIC_RAIN.get());
                        output.accept(ItemInit.WATER_SPHERE.get());
                        output.accept(ItemInit.TSUNAMI.get());
                        output.accept(ItemInit.TSUNAMI_SEAL.get());
                        output.accept(ItemInit.HURRICANE.get());
                        output.accept(ItemInit.TORNADO.get());
                        output.accept(ItemInit.EARTHQUAKE.get());
                        output.accept(ItemInit.SAILORPROJECTILECTONROL.get());
                        output.accept(ItemInit.ROAR.get());
                        output.accept(ItemInit.AQUATIC_LIFE_MANIPULATION.get());
                        output.accept(ItemInit.LIGHTNING_STORM.get());
                        output.accept(ItemInit.LIGHTNING_BRANCH.get());
                        output.accept(ItemInit.SONIC_BOOM.get());
                        output.accept(ItemInit.THUNDER_CLAP.get());
                        output.accept(ItemInit.RAIN_EYES.get());
                        output.accept(ItemInit.CALAMITY_INCARNATION_TORNADO.get());
                        output.accept(ItemInit.CALAMITY_INCARNATION_TSUNAMI.get());
                        output.accept(ItemInit.EXTREME_COLDNESS.get());
                        output.accept(ItemInit.VOLCANIC_ERUPTION.get());
                        output.accept(ItemInit.LIGHTNING_BALL.get());
                        output.accept(ItemInit.SAILOR_LIGHTNING_TRAVEL.get());
                        output.accept(ItemInit.LIGHTNING_BALL_ABSORB.get());
                        output.accept(ItemInit.STAR_OF_LIGHTNING.get());
                        output.accept(ItemInit.STORM_SEAL.get());
                        output.accept(ItemInit.WATER_COLUMN.get());
                        output.accept(ItemInit.MATTER_ACCELERATION_BLOCKS.get());
                        output.accept(ItemInit.MATTER_ACCELERATION_ENTITIES.get());
                        output.accept(ItemInit.MATTER_ACCELERATION_SELF.get());
                        output.accept(ItemInit.TYRANNY.get());



                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
