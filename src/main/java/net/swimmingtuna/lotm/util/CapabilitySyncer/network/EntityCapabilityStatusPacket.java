package net.swimmingtuna.lotm.util.CapabilitySyncer.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.util.CapabilitySyncer.core.ISyncableCapability;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class EntityCapabilityStatusPacket implements IPacket {
    private final int entityId;
    private final CompoundTag tag;

    protected EntityCapabilityStatusPacket(int entityId, CompoundTag tag) {
        this.entityId = entityId;
        this.tag = tag;
    }

    protected EntityCapabilityStatusPacket(int entityId, ISyncableCapability capability) {
        this(entityId, capability.serializeNBT(false));
    }

    protected static <T extends EntityCapabilityStatusPacket> void register(SimpleChannel channel, int id, Class<T> packetClass, Function<FriendlyByteBuf, T> readFunc) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, packetClass, readFunc);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeNbt(this.tag);
    }

    protected static <T extends EntityCapabilityStatusPacket> T read(FriendlyByteBuf buf, BiFunction<Integer, CompoundTag, T> function) {
        return function.apply(buf.readInt(), buf.readNbt());
    }

    public int getEntityId() {
        return entityId;
    }

    public CompoundTag getTag() {
        return tag;
    }
}
