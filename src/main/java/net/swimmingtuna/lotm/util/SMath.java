package net.swimmingtuna.lotm.util;

import net.minecraft.world.phys.Vec3;

public class SMath {
    public static double getHorizontalDistanceSqr(Vec3 vec) {
        return vec.x * vec.x + vec.z * vec.z;
    }
}