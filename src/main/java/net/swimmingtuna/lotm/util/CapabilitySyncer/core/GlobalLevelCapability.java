package net.swimmingtuna.lotm.util.CapabilitySyncer.core;

import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.LevelCapabilityStatusPacket;

public abstract class GlobalLevelCapability extends LevelCapability {
    protected GlobalLevelCapability(Level level) {
        super(level);
    }

    @Override
    public void updateTracking() {
        if (this.level.isClientSide)
            return;

        getNetworkChannel().send(PacketDistributor.ALL.noArg(), this.createUpdatePacket());
    }

    @Override
    public abstract LevelCapabilityStatusPacket createUpdatePacket();
}
