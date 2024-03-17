package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SpectatorDemiseEffect extends MobEffect {
    public SpectatorDemiseEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory,color);
        }

        @Override
        public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier){
            super.applyEffectTick(pLivingEntity, pAmplifier);
        }

        @Override
        public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
            return true;
        }
    }

