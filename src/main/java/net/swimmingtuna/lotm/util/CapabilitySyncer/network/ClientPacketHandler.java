package net.swimmingtuna.lotm.util.CapabilitySyncer.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.util.CapabilitySyncer.core.ISyncableCapability;

import java.util.function.Function;

class ClientPacketHandler {
    static <T extends Entity> void handleEntityCapabilityStatus(EntityCapabilityStatusPacket packet, Function<T, ISyncableCapability> capabilityRetriever) {
        ISyncableCapability capability = capabilityRetriever.apply(getEntity(packet.getEntityId()));
        if (capability != null)
            capability.deserializeNBT(packet.getTag(), false);
    }

    static void handleLevelCapabilityStatus(LevelCapabilityStatusPacket packet, Function<Level, ISyncableCapability> capabilityRetriever) {
        ISyncableCapability capability = capabilityRetriever.apply(Minecraft.getInstance().level);
        if (capability != null)
            capability.deserializeNBT(packet.getTag(), false);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> T getEntity(int entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return null;
        Entity entity = level.getEntity(entityId);
        return (T) entity;
    }
}
