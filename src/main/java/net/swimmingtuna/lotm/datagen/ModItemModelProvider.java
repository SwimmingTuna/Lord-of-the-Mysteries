package net.swimmingtuna.lotm.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.ItemInit;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LOTM.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ItemInit.AcidicRain);
        simpleItem(ItemInit.ApplyManipulation);
        simpleItem(ItemInit.Awe);
        simpleItem(ItemInit.AqueousLightDrown);
        simpleItem(ItemInit.AqueousLightPull);
        simpleItem(ItemInit.AcidicRain);
        simpleItem(ItemInit.AqueousLightPush);
        simpleItem(ItemInit.BattleHypnotism);
        simpleItem(ItemInit.BEYONDER_RESET_POTION);
        simpleItem(ItemInit.ConsciousnessStroll);
        simpleItem(ItemInit.Discern);
        simpleItem(ItemInit.DragonBreath);
        simpleItem(ItemInit.DreamIntoReality);
        simpleItem(ItemInit.DreamWalking);
        simpleItem(ItemInit.DreamWeaving);
        simpleItem(ItemInit.EnvisionBarrier);
        simpleItem(ItemInit.EnableOrDisableLightning);
        simpleItem(ItemInit.EnvisionDeath);
        simpleItem(ItemInit.EnvisionHealth);
        simpleItem(ItemInit.EnvisionKingdom);
        simpleItem(ItemInit.EnvisionLife);
        simpleItem(ItemInit.EnvisionLocation);
        simpleItem(ItemInit.EnvisionLocationBlink);
        simpleItem(ItemInit.EnvisionWeather);
        simpleItem(ItemInit.Frenzy);
        simpleItem(ItemInit.Guidance);
        simpleItem(ItemInit.ManipulateEmotion);
        simpleItem(ItemInit.ManipulateFondness);
        simpleItem(ItemInit.ManipulateMovement);
        simpleItem(ItemInit.MentalPlague);
        simpleItem(ItemInit.MeteorShower);
        simpleItem(ItemInit.MeteorNoLevelShower);
        simpleItem(ItemInit.MindStorm);
        simpleItem(ItemInit.MindReading);
        simpleItem(ItemInit.Nightmare);
        simpleItem(ItemInit.Placate);
        simpleItem(ItemInit.PlagueStorm);
        simpleItem(ItemInit.ProphesizeDemise);
        simpleItem(ItemInit.PsychologicalInvisibility);
        simpleItem(ItemInit.ProphesizeTeleportBlock);
        simpleItem(ItemInit.ProphesizeTeleportPlayer);
        simpleItem(ItemInit.RagingBlows);
        simpleItem(ItemInit.SPECTATOR_0_POTION);
        simpleItem(ItemInit.SPECTATOR_1_POTION);
        simpleItem(ItemInit.SPECTATOR_2_POTION);
        simpleItem(ItemInit.SPECTATOR_3_POTION);
        simpleItem(ItemInit.SPECTATOR_4_POTION);
        simpleItem(ItemInit.SPECTATOR_5_POTION);
        simpleItem(ItemInit.SPECTATOR_6_POTION);
        simpleItem(ItemInit.SPECTATOR_7_POTION);
        simpleItem(ItemInit.SPECTATOR_8_POTION);
        simpleItem(ItemInit.SPECTATOR_9_POTION);
        simpleItem(ItemInit.TYRANT_9_POTION);
        simpleItem(ItemInit.TYRANT_8_POTION);
        simpleItem(ItemInit.TYRANT_7_POTION);
        simpleItem(ItemInit.TYRANT_6_POTION);
        simpleItem(ItemInit.TYRANT_5_POTION);
        simpleItem(ItemInit.TYRANT_4_POTION);
        simpleItem(ItemInit.TYRANT_3_POTION);
        simpleItem(ItemInit.TYRANT_2_POTION);
        simpleItem(ItemInit.TYRANT_1_POTION);
        simpleItem(ItemInit.TYRANT_0_POTION);
        simpleItem(ItemInit.WindManipulationBlade);
        simpleItem(ItemInit.WindManipulationCushion);
        simpleItem(ItemInit.WindManipulationSense);
        simpleItem(ItemInit.WindManipulationFlight);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(LOTM.MOD_ID, "item/" + item.getId().getPath()));
    }
}
