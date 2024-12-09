package net.swimmingtuna.lotm.world.worlddata;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class WorldMisfortuneData extends SavedData {
    private static final String DATA_NAME = "worldMisfortuneData";
    private int worldMisfortune = 1;

    public static WorldMisfortuneData create() {
        return new WorldMisfortuneData();
    }

    public static WorldMisfortuneData load(CompoundTag nbt) {
        WorldMisfortuneData data = new WorldMisfortuneData();
        data.worldMisfortune = nbt.getInt("worldMisfortuneData");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("worldMisfortuneData", worldMisfortune);
        return nbt;
    }

    public int getWorldMisfortune() {
        return worldMisfortune;
    }

    public void setWorldMisfortune(int value) {
        this.worldMisfortune = value;
        setDirty();
    }

    public static WorldMisfortuneData getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WorldMisfortuneData::load,
                WorldMisfortuneData::create,
                DATA_NAME
        );
    }
}
