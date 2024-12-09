package net.swimmingtuna.lotm.item.OtherItems;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class LuckyGoldCoin extends Item {

    public LuckyGoldCoin(Properties pProperties) {
        super(pProperties);
    }

    public static void setUUID(ItemStack stack, UUID ownerUUID) {
        stack.getOrCreateTag().putUUID("coinOwner", ownerUUID);
    }

    @Nullable
    public static UUID getUUID(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("coinOwner") ? stack.getTag().getUUID("coinOwner") : null;
    }

}
