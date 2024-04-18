package net.swimmingtuna.lotm.entity.Meteor;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;

public class MeteorEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(MeteorEntity.class, EntityDataSerializers.BOOLEAN);

    public MeteorEntity(EntityType<? extends MeteorEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MeteorEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.METEOR_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }


    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }


    public boolean isOnFire() {
        return false;
    }


    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide()) {
            if (pResult.getEntity() instanceof LivingEntity entity) {
                entity.hurt(damageSources().fall(), 30);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, ((byte) 3));
            this.level().setBlock(blockPosition(), Blocks.WATER.defaultBlockState(), 3);
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

    public static void summonEntityWithSpeed(Vec3 direction, Vec3 initialVelocity, Vec3 eyePosition, double x, double y, double z, Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            MeteorEntity meteorEntity = new MeteorEntity(pPlayer.level(), pPlayer, initialVelocity.x, initialVelocity.y, initialVelocity.z);
            meteorEntity.setDeltaMovement(initialVelocity);
            Vec3 lightPosition = eyePosition.add(direction.scale(2.0));
            meteorEntity.setPos(lightPosition);
            meteorEntity.setOwner(pPlayer);
            meteorEntity.teleportTo(pPlayer.getX(), pPlayer.getY() + 50, pPlayer.getZ());
            pPlayer.level().addFreshEntity(meteorEntity);
        }
    }

    public static void summonEntityWhip(Player pPlayer, LivingEntity pEntity, boolean x) {
        if (!pPlayer.level().isClientSide()) {
            Vec3 direction = pPlayer.getViewVector(1.0f);
            Vec3 initialVelocity = direction.scale(2.0);
            MeteorEntity meteorEntity = new MeteorEntity(pPlayer.level(), pPlayer, initialVelocity.x, initialVelocity.y, initialVelocity.z);
            CompoundTag tag = meteorEntity.getPersistentData();
            x = tag.getBoolean("waterManipulationPull");
            Vec3 eyePosition = pPlayer.getEyePosition(1.0f);
            summonEntityWithSpeed(direction, initialVelocity, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 20 == 0) {
            if (this.tickCount >= 60) {
                this.discard();
            }
        }
    }
}
