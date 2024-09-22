package net.swimmingtuna.lotm.util.ProjectileDangerSense;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public interface PreviewProvider {
    /**
     * Is called once before everything else
     */
    default void prepare() {
    }

    /**
     * @param shootable shootable or throwable item (ammo), selected in hotbar
     * @return preview entity class for appropriate ammo type
     */
    Class<? extends PreviewEntity<? extends Entity>> getPreviewEntityFor(Player player, Item shootable);
}

