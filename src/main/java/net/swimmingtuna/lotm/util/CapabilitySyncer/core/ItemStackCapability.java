package net.swimmingtuna.lotm.util.CapabilitySyncer.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackCapability implements INBTSavable<CompoundTag> {
    protected final ItemStack itemStack;

    protected ItemStackCapability(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
