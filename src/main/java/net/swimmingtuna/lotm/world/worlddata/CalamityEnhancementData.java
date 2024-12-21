package net.swimmingtuna.lotm.world.worlddata;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class CalamityEnhancementData extends SavedData {
    private static final String DATA_NAME = "calamity_enhancement";
    private int calamityEnhancement = 1;

    public static CalamityEnhancementData create() {
        return new CalamityEnhancementData();
    }

    public static CalamityEnhancementData load(CompoundTag nbt) {
        CalamityEnhancementData data = new CalamityEnhancementData();
        data.calamityEnhancement = nbt.getInt("calamityEnhancement");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("calamityEnhancement", calamityEnhancement);
        return nbt;
    }

    public int getCalamityEnhancement() {
        return calamityEnhancement;
    }

    public void setCalamityEnhancement(int value) {
        this.calamityEnhancement = value;
        setDirty();
    }

    public static CalamityEnhancementData getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                CalamityEnhancementData::load,
                CalamityEnhancementData::create,
                DATA_NAME
        );
    }
}
