package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.custom.BeyonderAbilities.PsychologicalInvisibility;

import java.util.List;

public class SpectatorClass implements BeyonderClass {
    @Override
    public List<String> sequenceNames() {
        return List.of(
                "lotm.beyonder.spectator0",
                "lotm.beyonder.spectator1",
                "lotm.beyonder.spectator2",
                "lotm.beyonder.spectator3",
                "lotm.beyonder.spectator4",
                "lotm.beyonder.spectator5",
                "lotm.beyonder.spectator6",
                "lotm.beyonder.spectator7",
                "lotm.beyonder.spectator8",
                "lotm.beyonder.spectator9"
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
    public void tick(Player player, int sequenceLevel) {
        if (player.level().getGameTime() % 80 == 0) {
            if (sequenceLevel >= 0) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, -1,false,false));
                if (player.isCrouching()) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, -1,false,false));
                }
            }

            if (sequenceLevel == 6) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 5) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 4) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 3) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 2) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 1) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 0, false, false));

            }
            if (sequenceLevel == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 0, false, false));
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
        return items;
    }


}
