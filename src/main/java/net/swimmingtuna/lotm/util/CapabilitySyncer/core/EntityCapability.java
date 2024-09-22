package net.swimmingtuna.lotm.util.CapabilitySyncer.core;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.EntityCapabilityStatusPacket;

public abstract class EntityCapability implements ISyncableCapability {
    protected final Entity entity;

    protected EntityCapability(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void updateTracking() {
        if (this.entity.level().isClientSide)
            return;
        getNetworkChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), this.createUpdatePacket());
    }

    @Override
    public abstract EntityCapabilityStatusPacket createUpdatePacket();
}

