package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.List;

public class SpectatorClass implements BeyonderClass {
    @Override
    public List<String> sequenceNames() {
        return List.of(
                "Visionary",
                "Author",
                "Discerner",
                "Dream Weaver",
                "Manipulator",
                "Dreamwalker",
                "Hypnotist",
                "Psychiatrist",
                "Telepathist",
                "Spectator"
        );
    }

    @Override
    public List<Integer> spiritualityLevels() {
        return List.of(10000, 5000, 3000, 1800, 1200, 700, 450, 300, 175, 125);
    }

    @Override
    public List<Integer> mentalStrength() {return List.of(600,400,290,230,190,145,110,95,70,45);}

    @Override
    public List<Integer> spiritualityRegen() {
        return List.of(34, 22, 16, 12, 10, 8, 6, 5, 3, 2);
    }

    @Override
    public List<Double> maxHealth() {
        return List.of(350.0, 250.0, 186.0, 136.0, 96.0, 66.0, 54.0, 48.0, 28.0, 22.0);
    }

    @Override
    public void tick(Player pPlayer, int sequenceLevel) {
        if (pPlayer.level().getGameTime() % 80 == 0) {
            if (sequenceLevel >= 0) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 30 * 20, -1,false,false));
                if (pPlayer.isCrouching()) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, -1,false,false));
                }
            }

            if (sequenceLevel == 6) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 5) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 4) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 3) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 2) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 1) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 0) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 0, false, false));
            }
        }
    }
    @Override
    public HashMultimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(8, ItemInit.MindReading.get());
        items.put(7, ItemInit.Awe.get());
        items.put(7, ItemInit.Frenzy.get());
        items.put(7, ItemInit.Placate.get());
        items.put(6, ItemInit.BattleHypnotism.get());
        items.put(6, ItemInit.PsychologicalInvisibility.get());
        items.put(5, ItemInit.DreamWalking.get());
        items.put(5, ItemInit.Nightmare.get());
        items.put(4, ItemInit.ApplyManipulation.get());
        items.put(4, ItemInit.ManipulateEmotion.get());
        items.put(4, ItemInit.ManipulateFondness.get());
        items.put(4, ItemInit.ManipulateMovement.get());
        items.put(4, ItemInit.DragonBreath.get());
        items.put(4, ItemInit.MentalPlague.get());
        items.put(3, ItemInit.MindStorm.get());
        items.put(3, ItemInit.PlagueStorm.get());
        items.put(3, ItemInit.ConsciousnessStroll.get());
        items.put(3, ItemInit.DreamWeaving.get());
        items.put(2, ItemInit.Discern.get());
        items.put(2, ItemInit.DreamIntoReality.get());
        items.put(1, ItemInit.ProphesizeDemise.get());
        items.put(1, ItemInit.ProphesizeTeleportBlock.get());
        items.put(1, ItemInit.ProphesizeTeleportPlayer.get());
        items.put(1, ItemInit.MeteorShower.get());
        items.put(1, ItemInit.MeteorNoLevelShower.get());
        items.put(0, ItemInit.EnvisionBarrier.get());
        items.put(0, ItemInit.EnvisionLife.get());
        items.put(0, ItemInit.EnvisionDeath.get());
        items.put(0, ItemInit.EnvisionHealth.get());
        items.put(0, ItemInit.EnvisionLocation.get());
        items.put(0, ItemInit.EnvisionLocationBlink.get());
        items.put(0, ItemInit.EnvisionWeather.get());
        items.put(0, ItemInit.EnvisionKingdom.get());

        return items;
    }


}
