package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CalamityEnhancementLeftClickC2S {
    public CalamityEnhancementLeftClickC2S() {

    }

    public CalamityEnhancementLeftClickC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null) return;
            CompoundTag tag = player.getPersistentData();
            int x = tag.getInt("calamityEnhancementItemValue");
            if (x <= 2) {
                tag.putInt("calamityEnhancementItemValue", x + 1);
            } else {
                tag.putInt("calamityEnhancementItemValue", 1);
            }
            });
        return true;
    }
}
