package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class NoRegenerationEffect extends MobEffect {
    public NoRegenerationEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory,color);
    }


    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

        }
        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public static void preventRegeneration(LivingEntity entity) {
        if (entity.hasEffect(ModEffects.NOREGENERATION.get())) {
            int x = entity.getPersistentData().getInt("noRegenerationEffectHealth");
            if (entity.getHealth() < x) {
                entity.getPersistentData().putInt("noRegenerationEffectHealth", (int) entity.getHealth());
            }
            if (entity.getHealth() > x) {
                entity.setHealth(x);
            }
        }
    }

}

