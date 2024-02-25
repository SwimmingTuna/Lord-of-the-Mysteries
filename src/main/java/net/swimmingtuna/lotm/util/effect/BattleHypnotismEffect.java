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
    public void applyEffectTick(LivingEntity pLivingEntity, int amplifier) {
        if (!pLivingEntity.level().isClientSide) {
            double radius = 5.0 + amplifier;
            for (LivingEntity entity : pLivingEntity.level().getEntitiesOfClass(LivingEntity.class, pLivingEntity.getBoundingBox().inflate(radius))) {
                if (entity instanceof Player && entity != pLivingEntity) {
                    entity.setLastHurtByMob(pLivingEntity);
                } else if (entity instanceof LivingEntity && entity != pLivingEntity) {
                    entity.setLastHurtByMob(pLivingEntity);
                }
            }
            if (!(pLivingEntity instanceof Player)) {
                pLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,1,2));
            }
            super.applyEffectTick(pLivingEntity, amplifier);
        }
    }
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier){return true;}
}

