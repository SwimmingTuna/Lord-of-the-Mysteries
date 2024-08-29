package net.swimmingtuna.lotm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Random;

public class EndStoneEntity extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ENDSTONE_XROT = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ENDSTONE_YROT = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ENDSTONE_STAYATX = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ENDSTONE_STAYATY = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ENDSTONE_STAYATZ = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> REMOVE_AND_HURT = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SENT = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOULDNT_DAMAGE = SynchedEntityData.defineId(EndStoneEntity.class, EntityDataSerializers.BOOLEAN);


    public EndStoneEntity(EntityType<? extends EndStoneEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EndStoneEntity(Level pLevel, LivingEntity pShooter) {
        super(EntityInit.ENDSTONE_ENTITY.get(), pShooter, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(REMOVE_AND_HURT, false);
        this.entityData.define(SENT, false);
        this.entityData.define(SHOULDNT_DAMAGE, false);
        this.entityData.define(DATA_ENDSTONE_XROT, 0);
        this.entityData.define(DATA_ENDSTONE_STAYATX, 0.0f);
        this.entityData.define(DATA_ENDSTONE_STAYATY, 0.0f);
        this.entityData.define(DATA_ENDSTONE_STAYATZ, 0.0f);
        this.entityData.define(DATA_ENDSTONE_YROT, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("xxRot")) {
            this.setEndstoneXRot(compound.getInt("xxRot"));
        }
        if (compound.contains("yyRot")) {
            this.setEndstoneYRot(compound.getInt("yyRot"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("xxRot", this.getEndstoneXRot());
        compound.putInt("yyRot", this.getEndstoneYRot());
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (this.level() != null && !this.level().isClientSide() && !getShouldntDamage()) {
            Vec3 hitPos = pResult.getLocation();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, (5.0f * scaleData.getScale() / 3), Level.ExplosionInteraction.TNT);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 5.0F, 5.0F);
            if (pResult.getEntity() instanceof LivingEntity entity) {
                Explosion explosion = new Explosion(this.level(), this, hitPos.x, hitPos.y, hitPos.z, 10.0F, true, Explosion.BlockInteraction.DESTROY);
                DamageSource damageSource = this.level().damageSources().explosion(explosion);
                entity.hurt(damageSource, 10.0F * scaleData.getScale());
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (this.level() != null && !this.level().isClientSide && !getRemoveAndHurt() && !getShouldntDamage()) {
            Random random = new Random();
            if (random.nextInt(10) == 1) {
                this.level().broadcastEntityEvent(this, (byte) 3);
                this.level().setBlock(blockPosition(), Blocks.END_STONE.defaultBlockState(), 3);
            }
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }


    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    @Override
    public void tick() {
        super.tick();
        int xRot = this.getEndstoneXRot();
        int yRot = this.getEndstoneXRot();
        this.setXRot(this.getXRot() + xRot);
        this.setYRot(this.getYRot() + yRot);
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        if (!this.level().isClientSide() && this.tickCount > 140 && !getRemoveAndHurt()) {
            this.discard();
        }
        if (!this.level().isClientSide()) {
            if (getRemoveAndHurt()) {
                if (!getSent() && this.getOwner() != null) {
                this.setDeltaMovement(this.getOwner().getX() - this.getX() + getEndstoneStayAtX(),this.getOwner().getY() - this.getY() + getEndstoneStayAtY(),this.getOwner().getZ() - this.getZ() + getEndstoneStayAtZ());
                }
                BlockPos entityPos = this.blockPosition();
                for (int x = -2; x <= 2; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -2; z <= 2; z++) {
                            BlockPos pos = entityPos.offset(x, y, z);
                            BlockState state = this.level().getBlockState(pos);
                            Block block = state.getBlock();
                            float blockStrength = block.defaultDestroyTime();
                            float obsidianStrength = Blocks.OBSIDIAN.defaultDestroyTime();
                            if (blockStrength <= obsidianStrength) {
                                this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            }
                            if (blockStrength >= obsidianStrength) {
                                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 8, Level.ExplosionInteraction.TNT);
                            }
                        }
                    }
                }
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3))) {
                    if (entity != this.getOwner()) {
                        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 8, Level.ExplosionInteraction.TNT);
                    }
                }
                if (this.tickCount >= 480) {
                    this.discard();
                }
            }
        }
    }


    public void setEndstoneXRot(int xRot) {
        this.entityData.set(DATA_ENDSTONE_XROT, xRot);
    }

    public void setEndstoneYRot(int yRot) {
        this.entityData.set(DATA_ENDSTONE_YROT, yRot);
    }

    public int getEndstoneXRot() {
        return this.entityData.get(DATA_ENDSTONE_XROT);
    }

    public int getEndstoneYRot() {
        return this.entityData.get(DATA_ENDSTONE_YROT);
    }

    public void setRemoveAndHurt(boolean removeAndHurt) {
        this.entityData.set(REMOVE_AND_HURT, removeAndHurt);
    }

    public boolean getRemoveAndHurt() {
        return this.entityData.get(REMOVE_AND_HURT);
    }

    public void setSent(boolean sent) {
        this.entityData.set(SENT, sent);
    }

    public boolean getSent() {
        return this.entityData.get(SENT);
    }
    public void setShouldntDamage(boolean shouldntDamage) {
        this.entityData.set(SHOULDNT_DAMAGE, shouldntDamage);
    }

    public boolean getShouldntDamage() {
        return this.entityData.get(SHOULDNT_DAMAGE);
    }
    public float getEndstoneStayAtX() {
        return this.entityData.get(DATA_ENDSTONE_STAYATX);
    }
    public float getEndstoneStayAtY() {
        return this.entityData.get(DATA_ENDSTONE_STAYATY);
    }
    public float getEndstoneStayAtZ() {
        return this.entityData.get(DATA_ENDSTONE_STAYATZ);
    }
    public void setEndstoneStayAtX(float stayAtX) {
        this.entityData.set(DATA_ENDSTONE_STAYATX, stayAtX);
    }
    public void setEndstoneStayAtY(float stayAtY) {
        this.entityData.set(DATA_ENDSTONE_STAYATY, stayAtY);
    }
    public void setEndstoneStayAtZ(float stayAtZ) {
        this.entityData.set(DATA_ENDSTONE_STAYATZ, stayAtZ);
    }
    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }
}