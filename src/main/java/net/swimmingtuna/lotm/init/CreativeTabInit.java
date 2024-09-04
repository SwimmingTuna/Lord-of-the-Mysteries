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
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ItemInit.BEYONDER_ABILITY_USER.get());
                        pOutput.accept(ItemInit.MIND_READING.get());
                        pOutput.accept(ItemInit.AWE.get());
                        pOutput.accept(ItemInit.FRENZY.get());
                        pOutput.accept(ItemInit.PLACATE.get());
                        pOutput.accept(ItemInit.PSYCHOLOGICAL_INVISIBILITY.get());
                        pOutput.accept(ItemInit.BATTLE_HYPNOTISM.get());
                        pOutput.accept(ItemInit.GUIDANCE.get());
                        pOutput.accept(ItemInit.ALTERATION.get());
                        pOutput.accept(ItemInit.DREAM_WALKING.get());
                        pOutput.accept(ItemInit.NIGHTMARE.get());
                        pOutput.accept(ItemInit.ApplyManipulation.get());
                        pOutput.accept(ItemInit.MANIPULATE_EMOTION.get());
                        pOutput.accept(ItemInit.MANIPULATE_FONDNESS.get());
                        pOutput.accept(ItemInit.MANIPULATE_MOVEMENT.get());
                        pOutput.accept(ItemInit.MENTAL_PLAGUE.get());
                        pOutput.accept(ItemInit.MIND_STORM.get());
                        pOutput.accept(ItemInit.DRAGON_BREATH.get());
                        pOutput.accept(ItemInit.CONSCIOUSNESS_STROLL.get());
                        pOutput.accept(ItemInit.PLAGUE_STORM.get());
                        pOutput.accept(ItemInit.DREAM_WEAVING.get());
                        pOutput.accept(ItemInit.DISCERN.get());
                        pOutput.accept(ItemInit.DREAM_INTO_REALITY.get());
                        pOutput.accept(ItemInit.PROPHESIZE_DEMISE.get());
                        pOutput.accept(ItemInit.PROPHESIZE_TELEPORT_BLOCK.get());
                        pOutput.accept(ItemInit.PROPHESIZE_TELEPORT_PLAYER.get());
                        pOutput.accept(ItemInit.METEOR_SHOWER.get());
                        pOutput.accept(ItemInit.METEOR_NO_LEVEL_SHOWER.get());
                        pOutput.accept(ItemInit.ENVISION_BARRIER.get());
                        pOutput.accept(ItemInit.ENVISION_DEATH.get());
                        pOutput.accept(ItemInit.ENVISIONHEALTH.get());
                        pOutput.accept(ItemInit.ENVISION_KINGDOM.get());
                        pOutput.accept(ItemInit.ENVISION_LIFE.get());
                        pOutput.accept(ItemInit.ENVISION_LOCATION.get());
                        pOutput.accept(ItemInit.ENVISION_LOCATION_BLINK.get());
                        pOutput.accept(ItemInit.ENVISION_WEATHER.get());
                        pOutput.accept(ItemInit.RAGING_BLOWS.get());
                        pOutput.accept(ItemInit.ENABLE_OR_DISABLE_LIGHTNING.get());
                        pOutput.accept(ItemInit.AQUEOUS_LIGHT_DROWN.get());
                        pOutput.accept(ItemInit.AQUEOUS_LIGHT_PULL.get());
                        pOutput.accept(ItemInit.AQUEOUS_LIGHT_PUSH.get());
                        pOutput.accept(ItemInit.WIND_MANIPULATION_BLADE.get());
                        pOutput.accept(ItemInit.WIND_MANIPULATION_CUSHION.get());
                        pOutput.accept(ItemInit.WIND_MANIPULATION_FLIGHT.get());
                        pOutput.accept(ItemInit.WIND_MANIPULATION_SENSE.get());
                        pOutput.accept(ItemInit.SAILOR_LIGHTNING.get());
                        pOutput.accept(ItemInit.SIREN_SONG_HARM.get());
                        pOutput.accept(ItemInit.SIREN_SONG_STRENGTHEN.get());
                        pOutput.accept(ItemInit.SIREN_SONG_STUN.get());
                        pOutput.accept(ItemInit.SIREN_SONG_WEAKEN.get());
                        pOutput.accept(ItemInit.ACIDIC_RAIN.get());
                        pOutput.accept(ItemInit.WATER_SPHERE.get());
                        pOutput.accept(ItemInit.TSUNAMI.get());
                        pOutput.accept(ItemInit.TSUNAMI_SEAL.get());
                        pOutput.accept(ItemInit.HURRICANE.get());
                        pOutput.accept(ItemInit.TORNADO.get());
                        pOutput.accept(ItemInit.EARTHQUAKE.get());
                        pOutput.accept(ItemInit.ROAR.get());
                        pOutput.accept(ItemInit.AQUATIC_LIFE_MANIPULATION.get());
                        pOutput.accept(ItemInit.LIGHTNING_STORM.get());
                        pOutput.accept(ItemInit.LIGHTNING_BRANCH.get());
                        pOutput.accept(ItemInit.SONIC_BOOM.get());
                        pOutput.accept(ItemInit.THUNDER_CLAP.get());
                        pOutput.accept(ItemInit.RAIN_EYES.get());
                        pOutput.accept(ItemInit.CALAMITY_INCARNATION_TORNADO.get());
                        pOutput.accept(ItemInit.CALAMITY_INCARNATION_TSUNAMI.get());
                        pOutput.accept(ItemInit.EXTREME_COLDNESS.get());
                        pOutput.accept(ItemInit.VOLCANIC_ERUPTION.get());
                        pOutput.accept(ItemInit.LIGHTNING_BALL.get());
                        pOutput.accept(ItemInit.SAILOR_LIGHTNING_TRAVEL.get());
                        pOutput.accept(ItemInit.LIGHTNING_BALL_ABSORB.get());
                        pOutput.accept(ItemInit.STAR_OF_LIGHTNING.get());
                        pOutput.accept(ItemInit.STORM_SEAL.get());
                        pOutput.accept(ItemInit.WATER_COLUMN.get());
                        pOutput.accept(ItemInit.MATTER_ACCELERATION_BLOCKS.get());
                        pOutput.accept(ItemInit.MATTER_ACCELERATION_ENTITIES.get());
                        pOutput.accept(ItemInit.MATTER_ACCELERATION_SELF.get());
                        pOutput.accept(ItemInit.TYRANNY.get());



                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
