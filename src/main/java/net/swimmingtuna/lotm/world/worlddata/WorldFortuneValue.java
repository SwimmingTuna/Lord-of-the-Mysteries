package net.swimmingtuna.lotm.world.worlddata;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class WorldFortuneValue extends SavedData {
    private static final String DATA_NAME = "worldFortuneData";
    private int worldFortune = 1;

    public static WorldFortuneValue create() {
        return new WorldFortuneValue();
    }

    public static WorldFortuneValue load(CompoundTag nbt) {
        WorldFortuneValue data = new WorldFortuneValue();
        data.worldFortune = nbt.getInt("worldFortuneData");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("worldFortuneData", worldFortune);
        return nbt;
    }

    public int getWorldFortune() {
        return worldFortune;
    }

    public void setWorldFortune(int value) {
        this.worldFortune = value;
        setDirty();
    }

    public static WorldFortuneValue getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WorldFortuneValue::load,
                WorldFortuneValue::create,
                DATA_NAME
        );
    }
}
