package net.swimmingtuna.lotm.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplosionUtil {
    public static void createNoKnockbackExplosion(Level level, Entity source, float radius, boolean fire) {
        Explosion explosion = new Explosion(level, null, source.getX(), source.getY(), source.getZ(), radius, fire, Explosion.BlockInteraction.DESTROY);
        if (explosion instanceof NoKnockbackExplosion) {
            ((NoKnockbackExplosion) explosion).lordOfTheMysteries$setNoKnockback(true);
        }
        explosion.explode();
        explosion.finalizeExplosion(true);
    }
}
