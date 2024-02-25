package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FrenzyEffect extends MobEffect {
    public FrenzyEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory,color);
        }

        @Override
        public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
            if (!pLivingEntity.level().isClientSide()) {
                    //Ignore for now
                    Double x = pLivingEntity.getX() + Math.random() * 0.2 - 0.1;
                    Double y = pLivingEntity.getY();
                    Double z = pLivingEntity.getZ() + Math.random() * 0.2 - 0.1;

                double deltaX = Math.random() * 0.25 + (Math.random() - 1.072) * 0.25;
                double deltaY = 0; // Keep the y-direction movement as 0
                double deltaZ = Math.random() * 0.25 + (Math.random() - 1.055) * 0.25;

                AABB targetBoundingBox = new AABB(x, y, z, x + pLivingEntity.getBbWidth(), y + pLivingEntity.getBbHeight(), z + pLivingEntity.getBbWidth());
                if (!pLivingEntity.level().noCollision(pLivingEntity, targetBoundingBox)) {
                    Vec3 newPos = pLivingEntity.position().add(deltaX,deltaY,deltaZ);
                if (pLivingEntity.level().noCollision(pLivingEntity, pLivingEntity.getBoundingBox().move(newPos))) {
                    x = newPos.x;
                    y = newPos.y;
                    z = newPos.z;
                }
                }
                    pLivingEntity.setDeltaMovement(pLivingEntity.getDeltaMovement().add(Math.random() * 0.25 + (Math.random() -1.072) * 0.25,0,Math.random() * 0.25 + (Math.random() -1.055) * 0.25));
                    pLivingEntity.setSprinting(true);
                    pLivingEntity.hurtMarked = true;
                if (pLivingEntity instanceof Player && pLivingEntity.getRandom().nextFloat() > 0.5f && pLivingEntity.onGround()) {
                    ((Player) pLivingEntity).jumpFromGround();
                }

            }
            super.applyEffectTick(pLivingEntity, pAmplifier);
        }

        @Override
        public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
            return true;
        }
    }

