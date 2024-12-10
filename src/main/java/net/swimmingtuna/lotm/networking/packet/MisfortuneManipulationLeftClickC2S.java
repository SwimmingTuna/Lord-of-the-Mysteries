package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;

import java.util.function.Supplier;

public class MisfortuneManipulationLeftClickC2S {
    public MisfortuneManipulationLeftClickC2S() {

    }

    public MisfortuneManipulationLeftClickC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null) return;
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            CompoundTag tag = player.getPersistentData();
            int x = tag.getInt("misfortuneManipulationItem");
            if (sequence != 0) {
                if (x <= 8) {
                    tag.putInt("misfortuneManipulationItem", x + 1);
                } else {
                    tag.putInt("misfortuneManipulationItem", 1);
                }
            } else {
                if (x <= 11) {
                    tag.putInt("misfortuneManipulationItem", x + 1);
                } else {
                    tag.putInt("misfortuneManipulationItem", 1);
                }
            }
            });
        return true;
    }
}