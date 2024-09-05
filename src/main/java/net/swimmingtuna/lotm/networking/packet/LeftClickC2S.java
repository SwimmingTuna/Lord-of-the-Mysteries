package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.REQUEST_FILES.BeyonderAbilityUser;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.LightningStorm;
import net.swimmingtuna.lotm.REQUEST_FILES.BeyonderUtil;

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
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            ItemStack heldItem = player.getMainHandItem();
            byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");
            int firstKeyClicked = keysClicked[0];
            int secondKeyClicked = keysClicked[1];
            int thirdKeyClicked = keysClicked[2];
            int fourthKeyClicked = keysClicked[3];
            int fifthKeyClicked = keysClicked[4];

            if (!heldItem.isEmpty() && heldItem.getItem() instanceof BeyonderAbilityUser) {
                if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    keysClicked[0] = 1;
                    player.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    keysClicked[1] = 1;
                    player.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    keysClicked[2] = 1;
                    player.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    keysClicked[3] = 1;
                    player.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                    keysClicked[4] = 1;
                    player.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
            }
            if (player.getMainHandItem().getItem() instanceof LightningStorm) {
                CompoundTag tag = player.getPersistentData();
                double distance = tag.getDouble("sailorLightningStormDistance");
                tag.putDouble("sailorLightningStormDistance", (int) (distance + 30));
                player.sendSystemMessage(Component.literal("Storm Radius Is " + distance).withStyle(BeyonderUtil.getStyle(player)));
                if (distance > 300) {
                    tag.putDouble("sailorLightningStormDistance", 0);
                }
            }
        });
        return true;
    }
}
