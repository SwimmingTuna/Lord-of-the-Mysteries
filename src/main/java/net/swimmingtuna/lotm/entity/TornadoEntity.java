package net.swimmingtuna.lotm.entity;

import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.player.Player;
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

public class TornadoEntity extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TORNADO_RADIUS = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TORNADO_HEIGHT = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);


    public TornadoEntity(EntityType<? extends TornadoEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public TornadoEntity(Level pLevel, LivingEntity pShooter) {
        super(EntityInit.TORNADO_ENTITY.get(), pShooter, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(DATA_TORNADO_RADIUS, 4);
        this.entityData.define(DATA_TORNADO_HEIGHT, 20);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("TornadoRadius")) {
            this.setTornadoRadius(compound.getInt("TornadoRadius"));
        }
        if (compound.contains("TornadoHeight")) {
            this.setTornadoHeight(compound.getInt("TornadoHeight"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TornadoRadius", this.getTornadoRadius());
        compound.putInt("TornadoHeight", this.getTornadoHeight());
    }


    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (this.level() != null && !this.level().isClientSide) {

        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (this.level() != null && !this.level().isClientSide) {

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
        int tornadoRadius = getTornadoRadius();
        int tornadoHeight = getTornadoHeight();
        this.setXRot(this.getXRot() + 2);
        this.setYRot(this.getYRot() + 2);
        this.setOldPosAndRot();
        if (this.level().isClientSide) {
            double sizeFactor = (tornadoRadius + 1) * (tornadoHeight + 1) / 1000.0;
            int particleCount = Math.max(20, (int) (50 * sizeFactor));
            double baseX = this.getX();
            double baseY = this.getY();
            double baseZ = this.getZ();
            double height = tornadoHeight;
            double radius1 = (double) tornadoRadius / 8;
            double radius2 = tornadoRadius;
            double riseSpeed = 0.2;
            for (int i = 0; i < particleCount; i++) {
                double h = this.random.nextDouble() * height;
                double radiusRatio = h / height;
                double currentRadius = radius1 + (radius2 - radius1) * radiusRatio;
                double angle = this.random.nextDouble() * Math.PI * 2;
                double offsetX = currentRadius * Math.cos(angle);
                double offsetZ = currentRadius * Math.sin(angle);
                double particleX = baseX + offsetX;
                double particleY = baseY + h;
                double particleZ = baseZ + offsetZ;
                double velocityY = riseSpeed;
                this.level().addAlwaysVisibleParticle(
                        ParticleTypes.CLOUD, true,
                        particleX, particleY, particleZ,
                        0.0, velocityY, 0.0
                );
            }
        }
        if (!this.level().isClientSide() && this.tickCount >= 300) {
            this.discard();
        }
    }


    public void setTornadoRadius(int radius) {
        this.entityData.set(DATA_TORNADO_RADIUS, radius);
    }

    public void setTornadoHeight(int height) {
        this.entityData.set(DATA_TORNADO_HEIGHT, height);
    }
    public int getTornadoRadius() {
        return this.entityData.get(DATA_TORNADO_RADIUS);
    }

    public int getTornadoHeight() {
        return this.entityData.get(DATA_TORNADO_HEIGHT);
    }

}
