package net.swimmingtuna.lotm.util;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class ExplosionUtil {
    public static void createNoKnockbackExplosion(Level level, Entity source, double x, double y, double z, float radius) {
        Explosion explosion = new Explosion(level, null, source.getX(), source.getY(), source.getZ(), radius, true, Explosion.BlockInteraction.DESTROY);
        if (explosion instanceof NoKnockbackExplosion) {
            ((NoKnockbackExplosion) explosion).setNoKnockback(true);
        }
        explosion.explode();
        explosion.clearToBlow(); // This prevents block destruction
        explosion.finalizeExplosion(true);
    }
}
