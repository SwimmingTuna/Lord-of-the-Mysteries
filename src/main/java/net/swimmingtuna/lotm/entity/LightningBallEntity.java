package net.swimmingtuna.lotm.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class LightningBallEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SUMMONED = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> X_ROT = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ABSORB = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.BOOLEAN);


    public LightningBallEntity(EntityType<? extends LightningBallEntity> pEntityType, Level pLevel, boolean absorb) {
        super(pEntityType, pLevel);
        this.setAbsorbed(absorb);
    }

    public LightningBallEntity(EntityType<LightningBallEntity> lightningBallEntityEntityType, Level level) {
        super(lightningBallEntityEntityType, level);
    }

    public boolean isOnFire() {
        return false;
    }

    protected float getInertia() {
        return this.isDangerous() ? 1.0F : super.getInertia();
    }


    @Override
    public void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide()) {

        }
    }

    @Override
    public void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide()) {

        }
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Summoned", this.getSummoned());
        compound.putBoolean("Absorbed", this.getAbsorb());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSummoned(compound.getBoolean("Summoned"));
        this.setAbsorbed(compound.getBoolean("Absorbed"));
        this.setBallXRot(compound.contains("xxRot") ? compound.getFloat("xxRot") : 0.0f);
        this.setBallYRot(compound.contains("yyRot") ? compound.getFloat("yyRot") : 0.0f);
    }


    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, true);
        this.entityData.define(ABSORB, false);
        this.entityData.define(SUMMONED, false);
        this.entityData.define(X_ROT, 0.0f);
        this.entityData.define(Y_ROT, 0.0f);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }


    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        if (entity == this.getOwner()){
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        super.tick();
        boolean x = getSummoned();
        boolean y = getAbsorb();
        LivingEntity owner = (LivingEntity) this.getOwner();
        if (x) {
            this.setXRot(this.getXRot() + getBallXRot());
            this.setYRot(this.getYRot() + getBallYRot());
            if (!this.level().isClientSide()) {
                if (owner != null) {
                    if (this.tickCount <= 40) {
                        ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
                        this.teleportTo(owner.getX(), owner.getY() + scaleData.getScale() * 2, owner.getZ());
                    }
                    if (this.tickCount == 41 ) {
                        this.setDeltaMovement(owner.getLookAngle().scale(3.0f));
                        this.hurtMarked = true;
                    }
                }
            }
        }
        if (!this.level().isClientSide() && y && this.tickCount <= 40 && owner != null) {
            Vec3 playerPos = new Vec3(owner.getX(), owner.getY(), owner.getZ());
            for (Entity entity : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(100))) {
                if (entity instanceof LightningEntity lightningEntity) {
                    if (lightningEntity.getSpeed() != 10.5f && lightningEntity.getLastPos() != null) {
                        Vec3 direction = this.position().subtract(lightningEntity.getLastPos());
                        lightningEntity.discard();
                        LightningEntity lightning = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), this.level());
                        BlockPos pos = BlockPos.containing(lightningEntity.getLastPos());
                        lightning.teleportTo(pos.getX(), pos.getY(), pos.getZ());
                        direction = direction.normalize();
                        lightning.setDeltaMovement(direction);
                        lightning.hurtMarked = true;
                        lightning.setSpeed(10.5f);
                        lightning.setOwner(owner);
                        lightning.setOwner(owner);
                        lightning.setMaxLength(30);
                        this.level().addFreshEntity(lightning);
                    }
                    if (lightningEntity.getLastPos() != null && lightningEntity.getLastPos().distanceToSqr(this.getX(), this.getY(), this.getZ()) <= 250) {
                        lightningEntity.discard();
                        ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
                        if (scaleData.getScale() <= 50) {
                        scaleData.setScale(scaleData.getScale() + 1);
                        }
                    }
                }
                if (entity instanceof LightningBolt lightningBolt) {
                    lightningBolt.discard();
                    LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, this.level());
                    lightning.teleportTo(this.getX(), this.getY(), this.getZ());
                    ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
                    if (scaleData.getScale() <= 50) {
                        scaleData.setScale(scaleData.getScale() + 1.5f);
                        scaleData.markForSync(true);
                    }
                }
            }
        }
        this.xRotO = getXRot();
        this.yRotO = this.getYRot();
        if (!this.level().isClientSide() && this.tickCount >= 200) {
            this.discard();
        }
    }
    public boolean getSummoned() {
        return this.entityData.get(SUMMONED);
    }

    public float getBallXRot() {
        return this.entityData.get(X_ROT);
    }

    public float getBallYRot() {
        return this.entityData.get(Y_ROT);
    }

    public void setSummoned(boolean summoned) {
        this.entityData.set(SUMMONED, summoned);
    }
    public void setBallXRot(float xRot) {
        this.entityData.set(X_ROT, xRot);
    }

    public void setBallYRot(float yRot) {
        this.entityData.set(Y_ROT, yRot);
    }
    public boolean getAbsorb() {
        return this.entityData.get(ABSORB);
    }

    public void setAbsorbed(boolean absorbed) {
        this.entityData.set(ABSORB, absorbed);
    }
}
