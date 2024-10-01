package net.swimmingtuna.lotm.util.CapabilitySyncer.core;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public interface INBTSavable<T extends Tag> extends INBTSerializable<T> {

    @Deprecated
    @Override
    default T serializeNBT() {
        return serializeNBT(false);
    }

    T serializeNBT(boolean savingToDisk);

    /**
     * @deprecated Use {@link #deserializeNBT(Tag, boolean)}
     */
    @Deprecated
    @Override
    default void deserializeNBT(T nbt) {
        deserializeNBT(nbt, false);
    }

    void deserializeNBT(T nbt, boolean readingFromDisk);
}
