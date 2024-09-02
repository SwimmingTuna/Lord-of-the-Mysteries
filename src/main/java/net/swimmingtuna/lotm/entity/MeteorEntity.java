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
        return 1.0F;
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


    public static void summonMultipleMeteors(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            double scatterRadius = 100.0;
            double randomX, randomY, randomZ;

            // Calculate the target position based on the player's look angle
            Vec3 lookVec = pPlayer.getLookAngle().normalize().scale(100);
            Vec3 targetPos = pPlayer.getEyePosition().add(lookVec);

            // Randomize the position within the scatter radius
            randomX = Math.random() * scatterRadius * 2 - scatterRadius;
            randomY = Math.random() * scatterRadius * 2 - scatterRadius;
            randomZ = Math.random() * scatterRadius * 2 - scatterRadius;

            // Set the meteor spawn position
            BlockPos meteorSpawnPos = new BlockPos(
                    (int) (pPlayer.getX() + randomX),
                    (int) (pPlayer.getY() + 100),
                    (int) (pPlayer.getZ() + randomZ)
            );

            // Create and configure the meteor entity
            MeteorEntity meteorEntity = new MeteorEntity(EntityInit.METEOR_ENTITY.get(), pPlayer.level());
            meteorEntity.teleportTo(meteorSpawnPos.getX(), meteorSpawnPos.getY(), meteorSpawnPos.getZ());
            meteorEntity.setOwner(pPlayer);
            meteorEntity.noPhysics = true;

            // Set the scale of the meteor based on the player's sequence
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            int scalecheck = 10 - holder.getCurrentSequence() * 4;
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(meteorEntity);
            scaleData.setScale(scalecheck);
            scaleData.markForSync(true);

            // Adjust the target position by adding some randomness within a 70-block radius
            Vec3 randomizedTargetPos = targetPos.add(
                    (Math.random() * 140 - 70),  // Random X offset within -70 to +70
                    (Math.random() * 140 - 70),  // Random Y offset within -70 to +70
                    (Math.random() * 140 - 70)   // Random Z offset within -70 to +70
            );

            // Calculate the direction and set the meteor's movement speed
            Vec3 meteorPos = meteorEntity.position();
            Vec3 directionToTarget = randomizedTargetPos.subtract(meteorPos).normalize();
            double speed = 4.0;
            meteorEntity.setDeltaMovement(directionToTarget.scale(speed));

            // Spawn the meteor entity in the world
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
