package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FrenzyEffect extends MobEffect {
    public FrenzyEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            //Ignore for now
            double x = livingEntity.getX() + Math.random() * 0.2 - 0.1;
            double y = livingEntity.getY();
            double z = livingEntity.getZ() + Math.random() * 0.2 - 0.1;

            double deltaX = Math.random() * 0.25 + (Math.random() - 1.072) * 0.25;
            double deltaY = 0; // Keep the y-direction movement as 0
            double deltaZ = Math.random() * 0.25 + (Math.random() - 1.055) * 0.25;

            AABB targetBoundingBox = new AABB(x, y, z, x + livingEntity.getBbWidth(), y + livingEntity.getBbHeight(), z + livingEntity.getBbWidth());
            if (!livingEntity.level().noCollision(livingEntity, targetBoundingBox)) {
                Vec3 newPos = livingEntity.position().add(deltaX, deltaY, deltaZ);
                if (livingEntity.level().noCollision(livingEntity, livingEntity.getBoundingBox().move(newPos))) {
                    x = newPos.x;
                    y = newPos.y;
                    z = newPos.z;
                }
            }
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(Math.random() * 0.25 + (Math.random() - 1.072) * 0.25, 0, Math.random() * 0.25 + (Math.random() - 1.055) * 0.25));
            livingEntity.setSprinting(true);
            livingEntity.hurtMarked = true;
            if (livingEntity instanceof Player player && livingEntity.getRandom().nextFloat() > 0.5f && livingEntity.onGround()) {
                player.jumpFromGround();
            }

        }
        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

}

