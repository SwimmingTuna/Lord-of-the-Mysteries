package net.swimmingtuna.lotm.entity;

import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.mixin.ExplosionNoKnockback;
import net.swimmingtuna.lotm.particle.SonicBoomParticle;
import net.swimmingtuna.lotm.util.NoKnockbackExplosion;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;
import virtuoel.pehkui.mixin.compat115minus.ExplosionMixin;

import java.util.Random;

public class RoarEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(RoarEntity.class, EntityDataSerializers.BOOLEAN);


    public RoarEntity(EntityType<? extends RoarEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public RoarEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.ROAR_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }

    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isNoGravity() {
        return true;
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleTypes.SONIC_BOOM;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide()) {
            Entity entity = pResult.getEntity();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);

            // Check if the entity is a player, projectile, or a mob with max health > 100
            if (entity instanceof Projectile) {
                float explosionRadius = 3 * scaleData.getScale();
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius, Level.ExplosionInteraction.TNT);
            }
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.hurt(livingEntity.damageSources().generic(), (int) (20 * scaleData.getScale()));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {

    }

    public boolean isPickable() {
        return false;
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        float explosionRadius = 3 * scaleData.getScale();

        for (int i = 0; i < 10; i++) {
            Random random = new Random();
            if (random.nextInt(3) == 1) {
                double offsetX = (this.random.nextDouble() - 0.5) * explosionRadius;
                double offsetY = (this.random.nextDouble() - 0.5) * explosionRadius;
                double offsetZ = (this.random.nextDouble() - 0.5) * explosionRadius;
                this.level().addParticle(ParticleTypes.EXPLOSION, x + offsetX, y + offsetY, z + offsetZ, 0, 0, 0);
            }
        }


        float damage = 10.0F * scaleData.getScale(); // Adjust the base damage as needed
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(explosionRadius))) {
            if (entity != this.getOwner()) {
                entity.hurt(this.damageSources().mobAttack((LivingEntity) this.getOwner()), damage);
            }
        }


        if (!this.level().isClientSide()) {
            if (this.tickCount % 5 == 0) {
                createNoKnockbackExplosion(10);
            }
            scaleData.setScale(scaleData.getScale() + 0.05f);
            if (this.tickCount >= 100) {
                this.discard();
            }
        }
    }
    private void createNoKnockbackExplosion(float radius) {
        if (!this.level().isClientSide()) {
            Explosion explosion = new Explosion(
                    this.level(),
                    this,
                    null,
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    radius,
                    false,
                    Explosion.BlockInteraction.DESTROY
            );

            // Set no knockback using the interface
            if (explosion instanceof NoKnockbackExplosion) {
                ((NoKnockbackExplosion) explosion).setNoKnockback(true);
            }

            explosion.explode();
            explosion.finalizeExplosion(true);
        }
    }
}
