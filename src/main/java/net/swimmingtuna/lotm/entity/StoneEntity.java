package net.swimmingtuna.lotm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Random;

public class StoneEntity extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_STONE_XROT = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STONE_YROT = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_STONE_STAYATX = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_STONE_STAYATY = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_STONE_STAYATZ = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> REMOVE_AND_HURT = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SENT = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOULDNT_DAMAGE = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.BOOLEAN);

    public StoneEntity(EntityType<? extends StoneEntity> entityType, Level level) {
        super(entityType, level);
    }

    public StoneEntity(Level level, LivingEntity shooter) {
        super(EntityInit.STONE_ENTITY.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(REMOVE_AND_HURT, false);
        this.entityData.define(SENT, false);
        this.entityData.define(SHOULDNT_DAMAGE, false);
        this.entityData.define(DATA_STONE_XROT, 0);
        this.entityData.define(DATA_STONE_STAYATX, 0.0f);
        this.entityData.define(DATA_STONE_STAYATY, 0.0f);
        this.entityData.define(DATA_STONE_STAYATZ, 0.0f);
        this.entityData.define(DATA_STONE_YROT, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("xxRot")) {
            this.setStoneXRot(compound.getInt("xxRot"));
        }
        if (compound.contains("yyRot")) {
            this.setStoneYRot(compound.getInt("yyRot"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("xxRot", this.getStoneXRot());
        compound.putInt("yyRot", this.getStoneYRot());
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level() != null && !this.level().isClientSide && !(result.getEntity() instanceof StoneEntity) && !(result.getEntity() instanceof LavaEntity) && !getShouldntDamage()) {
            Vec3 hitPos = result.getLocation();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, (5.0f * scaleData.getScale() / 3), Level.ExplosionInteraction.TNT);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 5.0F, 5.0F);
            if (result.getEntity() instanceof LivingEntity entity) {
                entity.hurt(BeyonderUtil.explosionSource(this), 10.0F * scaleData.getScale());
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (this.level() != null && !this.level().isClientSide && !getRemoveAndHurt() && !getShouldntDamage()) {
            Random random = new Random();
            if (random.nextInt(10) == 1) {
                this.level().broadcastEntityEvent(this, (byte) 3);
                this.level().setBlock(blockPosition(), Blocks.STONE.defaultBlockState(), 3);
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
        this.setOldPosAndRot();
        int xRot = this.getStoneXRot();
        int yRot = this.getStoneXRot();
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
                    this.hurtMarked = true;
                    this.setDeltaMovement(this.getOwner().getX() - this.getX() + getStoneStayAtX(), this.getOwner().getY() - this.getY() + getStoneStayAtY(), this.getOwner().getZ() - this.getZ() + getStoneStayAtX());
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
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5))) {
                    if (entity != this.getOwner()) {
                        entity.hurt(BeyonderUtil.explosionSource(entity), 10);
                        this.discard();
                    }
                }
                if (this.tickCount >= 480) {
                    this.discard();
                }
            }
        }
    }


    public void setStoneXRot(int xRot) {
        this.entityData.set(DATA_STONE_XROT, xRot);
    }

    public void setStoneYRot(int yRot) {
        this.entityData.set(DATA_STONE_YROT, yRot);
    }

    public int getStoneXRot() {
        return this.entityData.get(DATA_STONE_XROT);
    }

    public float getStoneStayAtX() {
        return this.entityData.get(DATA_STONE_STAYATX);
    }

    public float getStoneStayAtY() {
        return this.entityData.get(DATA_STONE_STAYATY);
    }

    public float getStoneStayAtZ() {
        return this.entityData.get(DATA_STONE_STAYATZ);
    }

    public void setStoneStayAtX(float stayAtX) {
        this.entityData.set(DATA_STONE_STAYATX, stayAtX);
    }

    public void setStoneStayAtY(float stayAtY) {
        this.entityData.set(DATA_STONE_STAYATY, stayAtY);
    }

    public void setStoneStayAtZ(float stayAtZ) {
        this.entityData.set(DATA_STONE_STAYATZ, stayAtZ);
    }


    public int getStoneYRot() {
        return this.entityData.get(DATA_STONE_YROT);
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
    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }


}