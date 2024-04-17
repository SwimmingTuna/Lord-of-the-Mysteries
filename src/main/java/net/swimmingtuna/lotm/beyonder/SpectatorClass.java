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
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, -1,false,false));
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
        items.put(8, ItemInit.RagingBlows.get());
        items.put(7, ItemInit.AqueousLightDrown.get());
        return items;
    }


}
