package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.util.ClientAbilitiesData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncAbilitiesS2C {
    private final Map<String, String> abilities;

    public SyncAbilitiesS2C(Map<String, String> abilities) {
        this.abilities = abilities;
    }

    public SyncAbilitiesS2C(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.abilities = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String combination = buf.readUtf();
            String abilityName = buf.readUtf();
            this.abilities.put(combination, abilityName);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(abilities.size());
        for (Map.Entry<String, String> entry : abilities.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeUtf(entry.getValue());
        }
    }

    public static void handle(SyncAbilitiesS2C msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            for (Map.Entry<String, String> entry : msg.abilities.entrySet()) {
                ClientAbilitiesData.setAbilities(entry.getKey(), entry.getValue());
            }
        });
        context.setPacketHandled(true);
    }
}