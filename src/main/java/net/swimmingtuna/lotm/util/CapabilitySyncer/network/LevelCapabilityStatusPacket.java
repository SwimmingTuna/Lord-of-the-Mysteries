package net.swimmingtuna.lotm.util.CapabilitySyncer.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.util.CapabilitySyncer.core.ISyncableCapability;

import java.util.function.Function;

public abstract class LevelCapabilityStatusPacket implements IPacket {
    private final CompoundTag tag;

    protected LevelCapabilityStatusPacket(CompoundTag tag) {
        this.tag = tag;
    }

    protected LevelCapabilityStatusPacket(ISyncableCapability capability) {
        this(capability.serializeNBT(false));
    }

    protected static <T extends LevelCapabilityStatusPacket> void register(SimpleChannel channel, int id, Class<T> packetClass, Function<FriendlyByteBuf, T> readFunc) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, packetClass, readFunc);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    protected static <T extends LevelCapabilityStatusPacket> T read(FriendlyByteBuf buf, Function<CompoundTag, T> function) {
        return function.apply(buf.readNbt());
    }

    public CompoundTag getTag() {
        return tag;
    }
}
