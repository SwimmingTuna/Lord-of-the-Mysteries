package net.swimmingtuna.lotm.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class StoneEntity extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(StoneEntity.class, EntityDataSerializers.BOOLEAN);

    private int xxRot;
    private int yyRot;

    public StoneEntity(EntityType<? extends StoneEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public StoneEntity(Level pLevel, LivingEntity pShooter) {
        super(EntityInit.STONE_ENTITY.get(), pShooter, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("xxRot")) {
            this.setTickXRot(compound.getInt("xxRot"));
        }
        if (compound.contains("yyRot")) {
            this.setTickYRot(compound.getInt("yyRot"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("xxRot", this.xxRot);
        compound.putInt("yyRot", this.yyRot);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (this.level() != null && !this.level().isClientSide) {
            Vec3 hitPos = pResult.getLocation();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, (5.0f * scaleData.getScale() / 3), Level.ExplosionInteraction.TNT);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 5.0F, 5.0F);
            if (pResult.getEntity() instanceof LivingEntity entity) {
                Explosion explosion = new Explosion(this.level(), this, hitPos.x, hitPos.y, hitPos.z, 30.0F, true, Explosion.BlockInteraction.DESTROY);
                DamageSource damageSource = this.level().damageSources().explosion(explosion);
                entity.hurt(damageSource, 10.0F * scaleData.getScale());
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (this.level() != null && !this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.level().setBlock(blockPosition(), Blocks.STONE.defaultBlockState(), 3);
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
        this.setXRot((this.getXRot() + 2));
        this.setYRot((this.getYRot() + 2));
        this.setOldPosAndRot();

    }

    public void setTickXRot(int xRot) {
        this.xxRot = xRot;
    }

    public void setTickYRot(int yRot) {
        this.yyRot = yRot;
    }
}