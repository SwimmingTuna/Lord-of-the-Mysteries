package net.swimmingtuna.lotm.entity;

import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
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
                float explosionRadius = 3 * scaleData.getScale();
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius, Level.ExplosionInteraction.TNT);
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide()) {

        }
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
        float radius = 3 * scaleData.getScale();
        if (!this.level().isClientSide()) {
            BlockPos center = new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ());
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-(int) radius, -(int) radius, -(int) radius), center.offset((int) radius, (int) radius, (int) radius))) {
                Vec3i vec = new Vec3i(pos.getX(), pos.getY(), pos.getZ());
                if (pos.distSqr(vec) <= radius * radius) {
                    BlockState state = this.level().getBlockState(pos);
                    Block block = state.getBlock();
                    float blockStrength = block.defaultDestroyTime();
                    float obsidianStrength = Blocks.OBSIDIAN.defaultDestroyTime();

                    if (blockStrength <= obsidianStrength) {
                        this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
            if (this.getOwner() instanceof Player pPlayer) {
                if (this.tickCount == 1) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                    int sequence = holder.getCurrentSequence();
                    if (sequence <= 4) {
                        scaleData.setTargetScale(7 - (sequence * 1.0f));
                        scaleData.markForSync(true);
                    }
                }
            }
            if (this.tickCount % 5 == 0) {
                int numParticles = (int) Math.min(100, 20 * scaleData.getScale());
                Random random = new Random();
                for (int i = 0; i < numParticles; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double distance = random.nextDouble() * radius;
                    double offsetX = distance * Math.cos(angle);
                    double offsetZ = distance * Math.sin(angle);
                    double offsetY = random.nextDouble() * radius * 2 - radius;
                    this.level().addParticle(ParticleTypes.EXPLOSION, this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, 0.0, 0.0, 0.0);
                }
            }
            float damage = 10.0F * scaleData.getScale();
            float explosionRadius = 3 * scaleData.getScale();
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(explosionRadius))) {
                if (entity != this.getOwner()) {
                    entity.hurt(this.damageSources().mobAttack((LivingEntity) this.getOwner()), damage);
                }
            }

            if (this.tickCount >= 100) {
                this.discard();
            }
        }
    }

}
