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
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.Awe.get()))
                    .title(Component.translatable("creativetab.abilities_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ItemInit.BeyonderAbilityUser.get());
                        pOutput.accept(ItemInit.MindReading.get());
                        pOutput.accept(ItemInit.Awe.get());
                        pOutput.accept(ItemInit.Frenzy.get());
                        pOutput.accept(ItemInit.Placate.get());
                        pOutput.accept(ItemInit.PsychologicalInvisibility.get());
                        pOutput.accept(ItemInit.BattleHypnotism.get());
                        pOutput.accept(ItemInit.Guidance.get());
                        pOutput.accept(ItemInit.Alteration.get());
                        pOutput.accept(ItemInit.DreamWalking.get());
                        pOutput.accept(ItemInit.Nightmare.get());
                        pOutput.accept(ItemInit.ApplyManipulation.get());
                        pOutput.accept(ItemInit.ManipulateEmotion.get());
                        pOutput.accept(ItemInit.ManipulateFondness.get());
                        pOutput.accept(ItemInit.ManipulateMovement.get());
                        pOutput.accept(ItemInit.MentalPlague.get());
                        pOutput.accept(ItemInit.MindStorm.get());
                        pOutput.accept(ItemInit.DragonBreath.get());
                        pOutput.accept(ItemInit.ConsciousnessStroll.get());
                        pOutput.accept(ItemInit.PlagueStorm.get());
                        pOutput.accept(ItemInit.DreamWeaving.get());
                        pOutput.accept(ItemInit.Discern.get());
                        pOutput.accept(ItemInit.DreamIntoReality.get());
                        pOutput.accept(ItemInit.ProphesizeDemise.get());
                        pOutput.accept(ItemInit.ProphesizeTeleportBlock.get());
                        pOutput.accept(ItemInit.ProphesizeTeleportPlayer.get());
                        pOutput.accept(ItemInit.MeteorShower.get());
                        pOutput.accept(ItemInit.MeteorNoLevelShower.get());
                        pOutput.accept(ItemInit.EnvisionBarrier.get());
                        pOutput.accept(ItemInit.EnvisionDeath.get());
                        pOutput.accept(ItemInit.EnvisionHealth.get());
                        pOutput.accept(ItemInit.EnvisionKingdom.get());
                        pOutput.accept(ItemInit.EnvisionLife.get());
                        pOutput.accept(ItemInit.EnvisionLocation.get());
                        pOutput.accept(ItemInit.EnvisionLocationBlink.get());
                        pOutput.accept(ItemInit.EnvisionWeather.get());
                        pOutput.accept(ItemInit.RagingBlows.get());
                        pOutput.accept(ItemInit.EnableOrDisableLightning.get());
                        pOutput.accept(ItemInit.AqueousLightDrown.get());
                        pOutput.accept(ItemInit.AqueousLightPull.get());
                        pOutput.accept(ItemInit.AqueousLightPush.get());
                        pOutput.accept(ItemInit.WindManipulationBlade.get());
                        pOutput.accept(ItemInit.WindManipulationCushion.get());
                        pOutput.accept(ItemInit.WindManipulationFlight.get());
                        pOutput.accept(ItemInit.WindManipulationSense.get());
                        pOutput.accept(ItemInit.SailorLightning.get());
                        pOutput.accept(ItemInit.SirenSongHarm.get());
                        pOutput.accept(ItemInit.SirenSongStrengthen.get());
                        pOutput.accept(ItemInit.SirenSongStun.get());
                        pOutput.accept(ItemInit.SirenSongWeaken.get());
                        pOutput.accept(ItemInit.AcidicRain.get());
                        pOutput.accept(ItemInit.WaterSphere.get());
                        pOutput.accept(ItemInit.Tsunami.get());
                        pOutput.accept(ItemInit.TsunamiSeal.get());
                        pOutput.accept(ItemInit.Hurricane.get());
                        pOutput.accept(ItemInit.Tornado.get());
                        pOutput.accept(ItemInit.Earthquake.get());
                        pOutput.accept(ItemInit.Roar.get());
                        pOutput.accept(ItemInit.AquaticLifeManipulation.get());
                        pOutput.accept(ItemInit.LightningStorm.get());
                        pOutput.accept(ItemInit.LightningBranch.get());
                        pOutput.accept(ItemInit.SonicBoom.get());
                        pOutput.accept(ItemInit.ThunderClap.get());
                        pOutput.accept(ItemInit.RainEyes.get());
                        pOutput.accept(ItemInit.CalamityIncarnationTornado.get());
                        pOutput.accept(ItemInit.CalamityIncarnationTsunami.get());
                        pOutput.accept(ItemInit.ExtremeColdness.get());
                        pOutput.accept(ItemInit.VolcanicEruption.get());
                        pOutput.accept(ItemInit.LightningBall.get());
                        pOutput.accept(ItemInit.SailorLightningTravel.get());
                        pOutput.accept(ItemInit.LightningBallAbsorb.get());
                        pOutput.accept(ItemInit.StarOfLightning.get());
                        pOutput.accept(ItemInit.StormSeal.get());
                        pOutput.accept(ItemInit.WaterColumn.get());
                        pOutput.accept(ItemInit.MatterAccelerationBlocks.get());
                        pOutput.accept(ItemInit.MatterAccelerationEntities.get());
                        pOutput.accept(ItemInit.MatterAccelerationSelf.get());
                        pOutput.accept(ItemInit.Tyranny.get());
                        pOutput.accept(ItemInit.TyrantTornado.get());



                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
