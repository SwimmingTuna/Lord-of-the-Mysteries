package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WindTrapEffect extends MobEffect {
    public WindTrapEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory,color);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            Double x = pLivingEntity.getX();
            Double y = pLivingEntity.getY();
            Double z = pLivingEntity.getZ();
            pLivingEntity.teleportTo(x, y, z);
            pLivingEntity.setDeltaMovement(0,0,0);
            pLivingEntity.getPersistentData().putInt("windManipulationTrap", 1);
            if (pLivingEntity.getPersistentData().getInt("windManipulationTrap") > 10) {
                pLivingEntity.hurt(pLivingEntity.damageSources().fall(), 4);
            }
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
