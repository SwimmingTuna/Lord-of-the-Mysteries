package net.swimmingtuna.lotm.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

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

    @Override
    public boolean canHitEntity(Entity entity) {
        if (entity instanceof MeteorEntity) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    public boolean isOnFire() {
        return false;
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    @Override
    public void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide()) {
            BlockPos hitPos = result.getBlockPos();
            this.level().playSound(null, this.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 30.0f, 1.0f);
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            float scale = scaleData.getScale();
            // Define the radius of the sphere
            double radius = scale * 4; // Adjust multiplier as needed

            // Loop through all blocks in the spherical area
            for (BlockPos pos : BlockPos.betweenClosed(
                    hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                    hitPos.offset((int) radius, (int) radius, (int) radius))) {
                if (pos.distSqr(hitPos) <= radius * radius) {
                    if (this.level().getBlockState(pos).getDestroySpeed(this.level(), pos) >= 0) {
                        this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
            List<Entity> entities = this.level().getEntities(this,
                    new AABB(hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                            hitPos.offset((int) radius, (int) radius, (int) radius)));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity) {
                    // Damage the entity if it's within the radius
                    livingEntity.hurt(damageSources().generic(), 10 * scale); // Adjust damage as needed
                }
            }
            this.discard();
        }
    }

    @Override
    public void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 30.0f, 1.0f);
            Entity hitEntity = result.getEntity();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            float scale = scaleData.getScale();
            if (hitEntity instanceof LivingEntity) {
                BlockPos hitPos = hitEntity.blockPosition();

                // Define the radius of the sphere
                double radius = scale * 4; // Adjust multiplier as needed

                // Loop through all blocks in the spherical area
                for (BlockPos pos : BlockPos.betweenClosed(
                        hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                        hitPos.offset((int) radius, (int) radius, (int) radius))) {
                    if (pos.distSqr(hitPos) <= radius * radius) {
                        // Destroy the block if it's within the radius
                        if (this.level().getBlockState(pos).getDestroySpeed(this.level(), pos) >= 0) {
                            this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }

                // Loop through all entities in the spherical area
                List<Entity> entities = this.level().getEntities(this,
                        new AABB(hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                                hitPos.offset((int) radius, (int) radius, (int) radius)));
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity livingEntity) {
                        // Damage the entity if it's within the radius
                        livingEntity.hurt(damageSources().generic(), 10 * scale); // Adjust damage as needed
                    }
                }
            }
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

    public static void summonMultipleMeteors(Vec3 direction, Vec3 eyePosition, double x, double y, double z, Player pPlayer, int scale) {
        if (!pPlayer.level().isClientSide()) {
            double randomXOffset = (Math.random() * 0.4) - 0.2;
            double randomZOffset = (Math.random() * 0.4) - 0.2;
            double randomAngle = Math.toRadians(Math.random() * 20 - 10); // Random angle between -10 and 10 degrees
            double randomVelocityX = -direction.x + randomXOffset * Math.cos(randomAngle);
            double randomVelocityZ = -direction.z + randomZOffset * Math.cos(randomAngle);
            Vec3 initialVelocity = new Vec3(randomVelocityX, -direction.y, randomVelocityZ);
            MeteorEntity meteorEntity = new MeteorEntity(pPlayer.level(), pPlayer, initialVelocity.x, initialVelocity.y, initialVelocity.z);
            meteorEntity.setDeltaMovement(initialVelocity);
            Vec3 lightPosition = eyePosition.add(direction.scale(2.0));
            meteorEntity.setPos(lightPosition);
            meteorEntity.setOwner(pPlayer);
            double randomX = Math.random() * 60 - 30;
            double randomZ = Math.random() * 60 - 30;
            meteorEntity.teleportTo(pPlayer.getX() + randomX, pPlayer.getY() + 50, pPlayer.getZ() + randomZ);
            double random = 0.5 + Math.random();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            int sequence = holder.getCurrentSequence();
            int scalecheck = 10 - sequence * 4;
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(meteorEntity);
            scaleData.setScale(scalecheck);
            scaleData.markForSync(true);
            pPlayer.level().addFreshEntity(meteorEntity);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ProjectileUtil.rotateTowardsMovement(this, 0.5f);
        this.xRotO = getXRot();
        this.yRotO = this.getYRot();
    }
}
