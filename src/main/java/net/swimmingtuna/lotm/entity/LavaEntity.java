package net.swimmingtuna.lotm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Random;

public class LavaEntity extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(LavaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_LAVA_XROT = SynchedEntityData.defineId(LavaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LAVA_YROT = SynchedEntityData.defineId(LavaEntity.class, EntityDataSerializers.INT);

    public LavaEntity(EntityType<? extends LavaEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LavaEntity(Level level, LivingEntity shooter) {
        super(EntityInit.LAVA_ENTITY.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(DATA_LAVA_XROT, 0);
        this.entityData.define(DATA_LAVA_YROT, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("xxRot")) {
            this.setLavaXRot(compound.getInt("xxRot"));
        }
        if (compound.contains("yyRot")) {
            this.setLavaYRot(compound.getInt("yyRot"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("xxRot", this.getLavaXRot());
        compound.putInt("yyRot", this.getLavaYRot());
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide && !(result.getEntity() instanceof LavaEntity) && !(result.getEntity() instanceof StoneEntity)) {
            Vec3 hitPos = result.getLocation();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            int scale = (int) scaleData.getScale();
            this.level().setBlock(BlockPos.containing(hitPos), Blocks.LAVA.defaultBlockState(), 3);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (this.level() != null && !this.level().isClientSide) {
            Random random = new Random();
            if (random.nextInt(10) == 1) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.level().setBlock(blockPosition(), Blocks.LAVA.defaultBlockState(), 3);}
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
        int xRot = this.getLavaXRot();
        int yRot = this.getLavaXRot();
        this.setXRot(this.getXRot() + xRot);
        this.setYRot(this.getYRot() + yRot);
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        if (!this.level().isClientSide() && this.tickCount > 140) {
            this.discard();
        }
    }


    public void setLavaXRot(int xRot) {
        this.entityData.set(DATA_LAVA_XROT, xRot);
    }

    public void setLavaYRot(int yRot) {
        this.entityData.set(DATA_LAVA_YROT, yRot);
    }

    public int getLavaXRot() {
        return this.entityData.get(DATA_LAVA_XROT);
    }

    public int getLavaYRot() {
        return this.entityData.get(DATA_LAVA_YROT);
    }

}