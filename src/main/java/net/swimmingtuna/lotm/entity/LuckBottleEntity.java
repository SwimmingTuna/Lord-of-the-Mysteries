package net.swimmingtuna.lotm.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;

public class LuckBottleEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Integer> LUCK_AMOUNT = SynchedEntityData.defineId(LuckBottleEntity.class, EntityDataSerializers.INT);

    public LuckBottleEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LuckBottleEntity(Level pLevel) {
        super(EntityInit.LUCK_BOTTLE_ENTITY.get(), pLevel);
    }

    public LuckBottleEntity(Level pLevel, LivingEntity livingEntity) {
        super(EntityInit.LUCK_BOTTLE_ENTITY.get(), livingEntity, pLevel);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("bottleLuck")) {
            this.setLuck(compound.getInt("bottleLuck"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("bottleLuck", this.getLuck());
    }

    @Override
    protected Item getDefaultItem() {
        return ItemInit.LUCKBOTTLEITEM.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide()) {
            int entitiesHit = 0;
            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5))) {
                entitiesHit++;
                double luck = livingEntity.getPersistentData().getDouble("luck");
                livingEntity.getPersistentData().putDouble("luck", luck + ((double) getLuck() / entitiesHit));

            }
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, pResult.getBlockPos(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL);
            }
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide()) {
            int entitiesHit = 0;
            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5))) {
                entitiesHit++;
                double luck = livingEntity.getPersistentData().getDouble("luck");
                livingEntity.getPersistentData().putDouble("luck", luck + ((double) getLuck() / entitiesHit));
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, pResult.getEntity().getOnPos(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL);
            }
            this.discard();
        }
    }

    public int getLuck() {
        return this.entityData.get(LUCK_AMOUNT);
    }

    public void setLuck(int luck) {
        this.entityData.set(LUCK_AMOUNT, luck);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LUCK_AMOUNT, 0);
    }
}
