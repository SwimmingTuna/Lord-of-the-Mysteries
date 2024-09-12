package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.REQUEST_FILES.BeyonderAbilityUser;
import net.swimmingtuna.lotm.REQUEST_FILES.BeyonderUtil;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.LightningStorm;

import java.util.function.Supplier;

public class SpiritVisionC2S {
    public SpiritVisionC2S() {

    }

    public SpiritVisionC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer pPlayer = context.getSender();
        context.enqueueWork(() -> {
            if (pPlayer.getPersistentData().getBoolean("spiritVision")) {
                pPlayer.getPersistentData().putBoolean("spiritVision", false);
                pPlayer.sendSystemMessage(Component.literal("is false"));
            } else if (!pPlayer.getPersistentData().getBoolean("spiritVision")) {
                pPlayer.getPersistentData().putBoolean("spiritVision", true);
                pPlayer.sendSystemMessage(Component.literal("is true"));
            }
        });
        return true;
    }
}
