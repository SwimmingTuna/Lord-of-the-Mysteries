package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DeathKnellLeftClickC2S {
    public DeathKnellLeftClickC2S() {

    }

    public DeathKnellLeftClickC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null) return;
            CompoundTag tag = player.getPersistentData();
            int x = tag.getInt("deathKnell");
            if (x <= 2) {
                tag.putInt("deathKnell", x + 1);
            } else {
                tag.putInt("deathKnell", 1);
            }
            });
        return true;
    }
}
