package net.swimmingtuna.lotm.util.ProjectileDangerSense;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Trajectory preview works by creating appropriate projectile entities on the client and simulating their logic.
 * An implementation must extend Entity class and have a constructor with a Level parameter. Additionally, it
 * should have empty {@link Entity#defineSynchedData()}, {@link Entity#addAdditionalSaveData(CompoundTag)} and
 * {@link Entity#readAdditionalSaveData(CompoundTag)} methods. Return false in
 * {@link Entity#updateFluidHeightAndDoFluidPushing(TagKey, double)} to prevent appearance of fluid splashes, and
 * {@link net.minecraft.network.protocol.game.ClientboundAddEntityPacket} from {@link Entity#getAddEntityPacket()}
 *
 * @param <E>
 */
public interface PreviewEntity<E extends Entity> {
    /**
     * Called before simulation; create target entities here. Do not spawn any into world
     *
     * @param associatedItem item held in main hand
     * @param hand
     * @return entities to be projected
     */
    List<E> initializeEntities(Player player, ItemStack associatedItem, EquipmentSlot hand);
    /**
     * Simulate a projected entity's tick here - generally, copy-paste relevant code from {@link Entity#tick()} method.
     * This method will be called until the entity is discarded - discarding should be done when the entity hits
     * something
     *
     * @param simulatedEntity projectile
     */
    void simulateShot(E simulatedEntity);
}

