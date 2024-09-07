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

public class LeftClickC2S {
    public LeftClickC2S() {

    }

    public LeftClickC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer pPlayer = context.getSender();
        context.enqueueWork(() -> {
            ItemStack heldItem = pPlayer.getMainHandItem();
            pPlayer.sendSystemMessage(Component.literal("working"));
            int firstKeyClicked = pPlayer.getPersistentData().getInt("firstKeyClicked");
            int secondKeyClicked = pPlayer.getPersistentData().getInt("secondKeyClicked");
            int thirdKeyClicked = pPlayer.getPersistentData().getInt("thirdKeyClicked");
            int fourthKeyClicked = pPlayer.getPersistentData().getInt("fourthKeyClicked");
            int fifthKeyClicked = pPlayer.getPersistentData().getInt("fifthKeyClicked");

            if (!heldItem.isEmpty() && heldItem.getItem() instanceof BeyonderAbilityUser) {
                if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("firstKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("secondKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("thirdKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("fourthKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("fifthKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
            }
            if (pPlayer.getMainHandItem().getItem() instanceof LightningStorm) {
                CompoundTag tag = pPlayer.getPersistentData();
                double distance = tag.getDouble("sailorLightningStormDistance");
                tag.putDouble("sailorLightningStormDistance", (int) (distance + 30));
                pPlayer.sendSystemMessage(Component.literal("Storm Radius Is " + distance).withStyle(BeyonderUtil.getStyle(pPlayer)));
                if (distance > 300) {
                    tag.putDouble("sailorLightningStormDistance", 0);
                }
            }
        });
        return true;
    }
}
