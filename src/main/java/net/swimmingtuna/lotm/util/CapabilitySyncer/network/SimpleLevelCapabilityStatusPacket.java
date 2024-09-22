package net.swimmingtuna.lotm.util.CapabilitySyncer.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.util.CapabilitySyncer.core.ISyncableCapability;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleLevelCapabilityStatusPacket extends LevelCapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<Level, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleLevelCapabilityStatusPacket(ResourceLocation capabilityId, CompoundTag tag) {
        super(tag);
        this.capabilityId = capabilityId;
    }

    public SimpleLevelCapabilityStatusPacket(ResourceLocation capabilityId, ISyncableCapability capability) {
        super(capability);
        this.capabilityId = capabilityId;
    }

    /**
     * @deprecated Use {@link #register(SimpleChannel, int)} once for the network channel and {@link #registerRetriever(ResourceLocation, Function)} for each capability.
     */
    @Deprecated(since = "3.0.2", forRemoval = true)
    public static <T extends Level> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        registerRetriever(capabilityId, capabilityRetriever);
        register(channel, id);
    }

    public static void register(SimpleChannel channel, int id) {
        register(channel, id, SimpleLevelCapabilityStatusPacket.class, buf -> read(buf, tag -> new SimpleLevelCapabilityStatusPacket(buf.readResourceLocation(), tag)));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Level> void registerRetriever(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever) {
        capRetrievers.put(capabilityId, (Function<Level, ISyncableCapability>) capabilityRetriever);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeResourceLocation(this.capabilityId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleLevelCapabilityStatus(this, capRetrievers.get(this.capabilityId)));
    }
}
