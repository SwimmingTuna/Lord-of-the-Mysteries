package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BattleHypnotismEffect extends MobEffect {
    public BattleHypnotismEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory,color);
    }
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide) {
            double radius = 5.0 + amplifier;
            for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(radius))) {
                if (entity instanceof Player && entity != livingEntity) {
                    entity.setLastHurtByMob(livingEntity);
                } else if (entity instanceof LivingEntity && entity != livingEntity) {
                    entity.setLastHurtByMob(livingEntity);
                }
            }
            if (!(livingEntity instanceof Player)) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,1,2));
            }
            super.applyEffectTick(livingEntity, amplifier);
        }
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier){return true;}
}

