package net.swimmingtuna.lotm.util.CapabilitySyncer.core;

import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.LevelCapabilityStatusPacket;

public abstract class LevelCapability implements ISyncableCapability {
    protected final Level level;

    protected LevelCapability(Level level) {
        this.level = level;
    }

    @Override
    public void updateTracking() {
        if (this.level.isClientSide)
            return;
        getNetworkChannel().send(PacketDistributor.DIMENSION.with(this.level::dimension), this.createUpdatePacket());
    }

    @Override
    public abstract LevelCapabilityStatusPacket createUpdatePacket();
}
