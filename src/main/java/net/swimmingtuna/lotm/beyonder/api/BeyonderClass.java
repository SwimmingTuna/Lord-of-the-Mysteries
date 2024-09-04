package net.swimmingtuna.lotm.beyonder.api;

import com.google.common.collect.Multimap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.List;

public interface BeyonderClass {
    List<String> sequenceNames();

    List<Integer> spiritualityLevels();

    List<Integer> spiritualityRegen();

    List<Double> maxHealth();

    void tick(Player player, int sequence);

    Multimap<Integer, Item> getItems();

    default SimpleContainer getAbilityItemsContainer(int sequenceLevel) {
        SimpleContainer container = new SimpleContainer(27);
        for (int i = 9; i >= sequenceLevel; i--) {
            getItems().get(i).stream().map(Item::getDefaultInstance).forEach(container::addItem);
        }
        return container;
    }
}
