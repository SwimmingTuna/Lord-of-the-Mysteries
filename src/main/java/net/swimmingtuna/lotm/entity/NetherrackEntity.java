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

public class NetherrackEntity extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_NETHERRACK_XROT = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_NETHERRACK_YROT = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_NETHERRACK_STAYATX = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_NETHERRACK_STAYATY = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_NETHERRACK_STAYATZ = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> REMOVE_AND_HURT = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SENT = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOULD_DAMAGE = SynchedEntityData.defineId(NetherrackEntity.class, EntityDataSerializers.BOOLEAN);


    public NetherrackEntity(EntityType<? extends NetherrackEntity> entityType, Level level) {
        super(entityType, level);
    }

    public NetherrackEntity(Level level, LivingEntity shooter) {
        super(EntityInit.NETHERRACK_ENTITY.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(REMOVE_AND_HURT, false);
        this.entityData.define(SENT, false);
        this.entityData.define(SHOULD_DAMAGE, true);
        this.entityData.define(DATA_NETHERRACK_XROT, 0);
        this.entityData.define(DATA_NETHERRACK_STAYATX, 0.0f);
        this.entityData.define(DATA_NETHERRACK_STAYATY, 0.0f);
        this.entityData.define(DATA_NETHERRACK_STAYATZ, 0.0f);
        this.entityData.define(DATA_NETHERRACK_YROT, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("xxRot")) {
            this.setNetherrackXRot(compound.getInt("xxRot"));
        }
        if (compound.contains("yyRot")) {
            this.setNetherrackYRot(compound.getInt("yyRot"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("xxRot", this.getNetherrackXRot());
        compound.putInt("yyRot", this.getNetherrackYRot());
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide && getShouldDamage()) {
            Vec3 hitPos = result.getLocation();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, (5.0f * scaleData.getScale() / 3), Level.ExplosionInteraction.TNT);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 5.0F, 5.0F);
            if (result.getEntity() instanceof LivingEntity entity) {
                entity.hurt(BeyonderUtil.genericSource(this), 10.0F * scaleData.getScale());
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide && !getRemoveAndHurt() && getShouldDamage()) {
            Random random = new Random();
            if (random.nextInt(10) == 1) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.level().setBlock(blockPosition(), Blocks.NETHERRACK.defaultBlockState(), 3);}
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
        int xRot = this.getNetherrackXRot();
        int yRot = this.getNetherrackXRot();
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
                    this.setDeltaMovement(this.getOwner().getX() - this.getX() + getNetherrackStayAtX(),this.getOwner().getY() - this.getY() + getNetherrackStayAtY(),this.getOwner().getZ() - this.getZ() + getNetherrackStayAtZ());
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
                        entity.hurt(BeyonderUtil.genericSource(this), 10);
                    }
                }
                if (this.tickCount >= 480) {
                    this.discard();
                }
            }
        }
    }


    public void setNetherrackXRot(int xRot) {
        this.entityData.set(DATA_NETHERRACK_XROT, xRot);
    }

    public void setNetherrackYRot(int yRot) {
        this.entityData.set(DATA_NETHERRACK_YROT, yRot);
    }

    public int getNetherrackXRot() {
        return this.entityData.get(DATA_NETHERRACK_XROT);
    }

    public int getNetherrackYRot() {
        return this.entityData.get(DATA_NETHERRACK_YROT);
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

    public void setShouldDamage(boolean shouldDamage) {
        this.entityData.set(SHOULD_DAMAGE, shouldDamage);
    }

    public boolean getShouldDamage() {
        return this.entityData.get(SHOULD_DAMAGE);
    }

    public float getNetherrackStayAtX() {
        return this.entityData.get(DATA_NETHERRACK_STAYATX);
    }

    public void setNetherrackStayAtX(float stayAtX) {
        this.entityData.set(DATA_NETHERRACK_STAYATX, stayAtX);
    }

    public float getNetherrackStayAtY() {
        return this.entityData.get(DATA_NETHERRACK_STAYATY);
    }

    public void setNetherrackStayAtY(float stayAtY) {
        this.entityData.set(DATA_NETHERRACK_STAYATY, stayAtY);
    }

    public float getNetherrackStayAtZ() {
        return this.entityData.get(DATA_NETHERRACK_STAYATZ);
    }

    public void setNetherrackStayAtZ(float stayAtZ) {
        this.entityData.set(DATA_NETHERRACK_STAYATZ, stayAtZ);
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }
}