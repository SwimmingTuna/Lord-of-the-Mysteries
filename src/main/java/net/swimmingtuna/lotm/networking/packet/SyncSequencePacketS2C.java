package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.util.ClientSequenceData;

import java.util.function.Supplier;

public class SyncSequencePacketS2C {
    private final int sequence;

    public SyncSequencePacketS2C(int sequence) {
        this.sequence = sequence;
    }

    public SyncSequencePacketS2C(FriendlyByteBuf buf) {
        this.sequence = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.sequence);
    }

    public int getCurrentSequence() {
        return sequence;
    }


    public static void handle(SyncSequencePacketS2C msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Update the sequence on the client-side
            ClientSequenceData.setCurrentSequence(msg.getCurrentSequence());
        });
        context.setPacketHandled(true);
    }
}
