package net.swimmingtuna.lotm.entity;


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
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;

public class LightningBallEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SUMMONED = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> X_ROT = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ABSORB = SynchedEntityData.defineId(LightningBallEntity.class, EntityDataSerializers.BOOLEAN);


    public LightningBallEntity(EntityType<? extends LightningBallEntity> pEntityType, Level pLevel, boolean absorb) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.setAbsorbed(absorb);
    }

    public LightningBallEntity(EntityType<LightningBallEntity> lightningBallEntityEntityType, Level level) {
        super(lightningBallEntityEntityType, level);
    }

    public boolean isOnFire() {
        return false;
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
        this.entityData.define(DATA_DANGEROUS, false);
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

    @Override //make it so lightning entities's last pos is set to the lightning, every other tick, and then in the other tick, they're discarded
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
                        this.teleportTo(owner.getX(), owner.getY() + (this.tickCount * 0.3), owner.getZ());
                    }
                    if (this.tickCount == 41 ) {
                        this.setDeltaMovement(owner.getLookAngle().scale(3.0f));
                        this.hurtMarked = true;
                        owner.sendSystemMessage(Component.literal("worked"));
                    }
                }
            }
        }
        if (!this.level().isClientSide() && y && this.tickCount <= 40) {
            if (this.getOwner() != null) {
                this.getOwner().sendSystemMessage(Component.literal("wokring"));
            }
            for (Entity entity : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(100))) {
                if (entity instanceof LightningEntity lightningEntity) {
                    lightningEntity.setSpeed(30.0f);
                    lightningEntity.setNoUp(false);
                    if (this.getOwner() != null) {
                        lightningEntity.setOwner(this.getOwner());
                        lightningEntity.setOwner(this.getOwner());
                    }
                    lightningEntity.setTargetPos(this.getOnPos().getCenter());
                    lightningEntity.setMaxLength(lightningEntity.getMaxLength() + 10);
                    if (lightningEntity.distanceTo(this) <= 10) {

                        //+1 scale
                    }
                } else if (entity instanceof LightningBolt lightningBolt) {
                    lightningBolt.teleportTo(this.getX(), this.getY(), this.getZ());
                }
            }
        }
        this.xRotO = getXRot();
        this.yRotO = this.getYRot();
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
