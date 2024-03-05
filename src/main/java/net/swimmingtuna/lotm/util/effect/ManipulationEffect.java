package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ManipulationEffect extends MobEffect {
    public ManipulationEffect(MobEffectCategory mobEffectCategory, int color) {
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

