package net.swimmingtuna.lotm.util.CapabilitySyncer.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.IPacket;

public interface ISyncableCapability extends INBTSavable<CompoundTag> {
    void updateTracking();

    /**
     * Copies the capability data from another capability into this one.
     * It is generally assumed that the other capability instance is
     * the same type as this one.
     *
     * @param otherCap the other capability instance
     * @param isDeath {@code true} if the capability is being copied due to a player death, {@code false} otherwise
     */
    default void copyFrom(ISyncableCapability otherCap, boolean isDeath) {
        this.deserializeNBT(otherCap.serializeNBT(false), false);
    }

    IPacket createUpdatePacket();

    default void sendUpdatePacketToPlayer(ServerPlayer serverPlayer) {
        getNetworkChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), this.createUpdatePacket());
    }

    SimpleChannel getNetworkChannel();
}

