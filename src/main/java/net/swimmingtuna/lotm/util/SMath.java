package net.swimmingtuna.lotm.util;

import net.minecraft.world.phys.Vec3;

public class SMath
{
    public static double getHorizontalDistanceSqr(Vec3 pVector)
    {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }
}