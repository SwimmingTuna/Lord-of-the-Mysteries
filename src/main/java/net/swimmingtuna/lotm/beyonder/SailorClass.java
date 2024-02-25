package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.List;

public class SailorClass implements BeyonderClass {
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
                "lotm.beyonder.tyrant8",
                "lotm.beyonder.tyrant9"
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
        if (player.level().getGameTime() % 50 == 0) {
            if (sequenceLevel >= 0) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, -1));

                if (player.isCrouching()) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, -1));
                }
            }
        }
    }

    @Override
    public HashMultimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(0, ItemInit.Placate.get());
        return items;
    }


}
