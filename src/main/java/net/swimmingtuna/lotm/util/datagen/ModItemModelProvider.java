package net.swimmingtuna.lotm.util.datagen;

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
        simpleItem(ItemInit.ApplyManipulation);
        simpleItem(ItemInit.DragonBreath);
        simpleItem(ItemInit.EnvisionDisasters);
        simpleItem(ItemInit.EnvisionKingdom);
        simpleItem(ItemInit.Awe);
        simpleItem(ItemInit.BattleHypnotism);
        simpleItem(ItemInit.ConsciousnessStroll);
        simpleItem(ItemInit.Discern);
        simpleItem(ItemInit.DreamIntoReality);
        simpleItem(ItemInit.DreamWalking);
        simpleItem(ItemInit.DreamWeaving);
        simpleItem(ItemInit.EnvisionBarrier);
        simpleItem(ItemInit.EnvisionDeath);
        simpleItem(ItemInit.EnvisionHealth);
        simpleItem(ItemInit.EnvisionLife);
        simpleItem(ItemInit.EnvisionLocation);
        simpleItem(ItemInit.EnvisionLocationBlink);
        simpleItem(ItemInit.EnvisionWeather);
        simpleItem(ItemInit.Frenzy);
        simpleItem(ItemInit.ManipulateEmotion);
        simpleItem(ItemInit.ManipulateFondness);
        simpleItem(ItemInit.ManipulateMovement);
        simpleItem(ItemInit.MentalPlague);
        simpleItem(ItemInit.MindReading);
        simpleItem(ItemInit.Nightmare);
        simpleItem(ItemInit.Placate);
        simpleItem(ItemInit.PlagueStorm);
        simpleItem(ItemInit.ProphesizeDemise);
        simpleItem(ItemInit.ProphesizeTeleportBlock);
        simpleItem(ItemInit.ProphesizeTeleportPlayer);
        simpleItem(ItemInit.PsychologicalInvisibility);
        simpleItem(ItemInit.SPECTATOR_9_POTION);
        simpleItem(ItemInit.SPECTATOR_8_POTION);
        simpleItem(ItemInit.SPECTATOR_7_POTION);
        simpleItem(ItemInit.SPECTATOR_6_POTION);
        simpleItem(ItemInit.SPECTATOR_5_POTION);
        simpleItem(ItemInit.SPECTATOR_4_POTION);
        simpleItem(ItemInit.SPECTATOR_3_POTION);
        simpleItem(ItemInit.SPECTATOR_2_POTION);
        simpleItem(ItemInit.SPECTATOR_1_POTION);
        simpleItem(ItemInit.SPECTATOR_0_POTION);



    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(LOTM.MOD_ID,"item/" + item.getId().getPath()));
    }
}
