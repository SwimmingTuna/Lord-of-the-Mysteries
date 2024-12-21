package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedEffect extends MobEffect {
    public BleedEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory,color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide() && livingEntity.tickCount % 5 == 0) {
            livingEntity.hurt(livingEntity.damageSources().cramming(), amplifier);

        }
        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
