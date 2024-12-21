package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FalseProphecyLeftClickC2S {
    public FalseProphecyLeftClickC2S() {

    }

    public FalseProphecyLeftClickC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null) return;
            CompoundTag tag = player.getPersistentData();
            int x = tag.getInt("falseProphecyItem");
            if (x <= 9) {
                tag.putInt("falseProphecyItem", x + 1);
            } else {
                tag.putInt("falseProphecyItem", 1);
            }
            });
        return true;
    }
}
