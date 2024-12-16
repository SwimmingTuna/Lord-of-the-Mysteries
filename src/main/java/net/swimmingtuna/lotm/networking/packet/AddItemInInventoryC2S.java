package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddItemInInventoryC2S {
    private final ItemStack newItem;

    public AddItemInInventoryC2S( ItemStack newItem) {
        this.newItem = newItem;
    }

    public AddItemInInventoryC2S(FriendlyByteBuf buf) {
        this.newItem = buf.readItem();
    }

    public void toByte(FriendlyByteBuf buf) {
        buf.writeItem(newItem);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getInventory().setItem(player.getInventory().getSuitableHotbarSlot(), newItem);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

