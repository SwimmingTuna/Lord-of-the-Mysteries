package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateItemInHandC2S {
    private final int activeSlot;
    private final ItemStack newItem;

    public UpdateItemInHandC2S(int activeSlot, ItemStack newItem) {
        this.activeSlot = activeSlot;
        this.newItem = newItem;
    }

    public UpdateItemInHandC2S(FriendlyByteBuf buf) {
        this.activeSlot = buf.readInt();
        this.newItem = buf.readItem();
    }

    public void toByte(FriendlyByteBuf buf) {
        buf.writeInt(activeSlot);
        buf.writeItem(newItem);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getInventory().setItem(activeSlot, newItem);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

