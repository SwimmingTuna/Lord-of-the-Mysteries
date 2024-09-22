package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WindTrapEffect extends MobEffect {
    public WindTrapEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            double x = livingEntity.getX();
            double y = livingEntity.getY();
            double z = livingEntity.getZ();
            livingEntity.teleportTo(x, y, z);
            livingEntity.setDeltaMovement(0,0,0);
            livingEntity.getPersistentData().putInt("windManipulationTrap", 1);
            if (livingEntity.getPersistentData().getInt("windManipulationTrap") > 10) {
                livingEntity.hurt(livingEntity.damageSources().fall(), 4);
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
