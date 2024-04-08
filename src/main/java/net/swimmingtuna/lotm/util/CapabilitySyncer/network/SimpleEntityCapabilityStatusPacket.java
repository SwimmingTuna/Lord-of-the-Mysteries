package net.swimmingtuna.lotm.util.CapabilitySyncer.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.util.CapabilitySyncer.core.ISyncableCapability;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleEntityCapabilityStatusPacket extends EntityCapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<? extends Entity, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleEntityCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, CompoundTag tag) {
        super(entityId, tag);
        this.capabilityId = capabilityId;
    }

    public SimpleEntityCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, ISyncableCapability capability) {
        super(entityId, capability);
        this.capabilityId = capabilityId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeResourceLocation(this.capabilityId);
    }

    /**
     * @deprecated Use {@link #register(SimpleChannel, int)} once for the network channel and {@link #registerRetriever(ResourceLocation, Function)} for each capability.
     */
    @Deprecated(since = "3.0.2", forRemoval = true)
    public static <T extends Entity> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        registerRetriever(capabilityId, capabilityRetriever);
        register(channel, id);
    }

    public static void register(SimpleChannel channel, int id) {
        register(channel, id, SimpleEntityCapabilityStatusPacket.class, buf -> read(buf, (entityId, tag) -> new SimpleEntityCapabilityStatusPacket(entityId, buf.readResourceLocation(), tag)));
    }

    public static <T extends Entity> void registerRetriever(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever) {
        capRetrievers.put(capabilityId, capabilityRetriever);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleEntityCapabilityStatus(this, capRetrievers.get(this.capabilityId)));
    }
}
