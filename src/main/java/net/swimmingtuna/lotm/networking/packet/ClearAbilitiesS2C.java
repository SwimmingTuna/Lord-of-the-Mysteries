package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.util.ClientAbilitiesData;

import java.util.function.Supplier;

public class ClearAbilitiesS2C {

    public ClearAbilitiesS2C() {

    }

    public ClearAbilitiesS2C(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public static void handle(ClearAbilitiesS2C msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientAbilitiesData.clearAbilities();
        });
        context.setPacketHandled(true);
    }
}
