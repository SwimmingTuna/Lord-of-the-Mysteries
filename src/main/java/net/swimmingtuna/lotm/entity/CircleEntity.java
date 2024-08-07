package net.swimmingtuna.lotm.entity;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class CircleEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AWE = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BATTLE_HYPNOTISM = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ENVISION_DEATH = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FRENZY = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MANIPULATE_EMOTION = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MANIPULATE_FONDNESS = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MANIPULATE_MOVEMENT = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MENTAL_PLAGUE = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PLAGUE_STORM = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PROPHESIZE_BLOCK = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PROPHESIZE_PLAYER = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EXTREME_COLDNESS = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HYPNOSIS = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> VIRTUAL_PERSONA = SynchedEntityData.defineId(CircleEntity.class, EntityDataSerializers.BOOLEAN);

    public CircleEntity(EntityType<? extends CircleEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CircleEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.CIRCLE_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }


    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        if (entity instanceof CircleEntity) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    public boolean isOnFire() {
        return false;
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide) {
            Vec3 hitPos = pResult.getLocation();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, (5.0f * scaleData.getScale() / 3), Level.ExplosionInteraction.TNT);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE.AMBIENT, 5.0F, 5.0F);
            if (pResult.getEntity() instanceof LivingEntity entity) {
                Explosion explosion = new Explosion(this.level(), this, hitPos.x, hitPos.y, hitPos.z, 30.0F, true, Explosion.BlockInteraction.DESTROY);
                DamageSource damageSource = damageSources().explosion(explosion);
                entity.hurt(damageSource, 30.0F);
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide) {
            Vec3 hitPos = pResult.getLocation();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, (5.0f * scaleData.getScale() / 3), Level.ExplosionInteraction.BLOCK);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE.AMBIENT, 5.0F, 5.0F);
            for (LivingEntity entity : this.getOwner().level().getEntitiesOfClass(LivingEntity.class, this.getOwner().getBoundingBox().inflate(50))) {
                Explosion explosion = new Explosion(this.level(), this, hitPos.x, hitPos.y, hitPos.z, 30.0F, true, Explosion.BlockInteraction.DESTROY);
                DamageSource damageSource = damageSources().explosion(explosion);
                entity.hurt(damageSource, 30.0F);
                entity.hurt(damageSource, 25.0F);
            }
            this.discard();
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
        ProjectileUtil.rotateTowardsMovement(this, 0.5f);
        this.xRotO = getXRot();
        this.yRotO = this.getYRot();
    }
}
