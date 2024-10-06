package net.swimmingtuna.lotm.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

public class MeteorNoLevelEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(MeteorNoLevelEntity.class, EntityDataSerializers.BOOLEAN);

    public MeteorNoLevelEntity(EntityType<? extends MeteorNoLevelEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MeteorNoLevelEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.METEOR_NO_LEVEL_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
    }


    protected float getInertia() {
        return 1.0F;
    }


    public boolean isOnFire() {
        return false;
    }


    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 30.0f, 1.0f);
            Entity hitEntity = result.getEntity();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            float scale = scaleData.getScale();
            if (hitEntity instanceof LivingEntity) {
                BlockPos hitPos = hitEntity.blockPosition();
                double radius = scale * 4;
                List<Entity> entities = this.level().getEntities(this,
                        new AABB(hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                                hitPos.offset((int) radius, (int) radius, (int) radius)));
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.hurt(damageSources().generic(), 10 * scale); // Adjust damage as needed
                    }
                }
            }
            this.discard();
        }
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide()) {
            BlockPos hitPos = result.getBlockPos();
            this.level().playSound(null, this.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 30.0f, 1.0f);
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            float scale = scaleData.getScale();
            double radius = scale * 4;
            List<Entity> entities = this.level().getEntities(this,
                    new AABB(hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                            hitPos.offset((int) radius, (int) radius, (int) radius)));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.hurt(damageSources().generic(), 10 * scale); // Adjust damage as needed
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

    public static void summonMultipleMeteors(Player player) {
        if (!player.level().isClientSide()) {
            double scatterRadius = 100.0;
            double randomX, randomY, randomZ;

            // Calculate the target position based on the player's look angle
            Vec3 lookVec = player.getLookAngle().normalize().scale(100);
            Vec3 targetPos = player.getEyePosition().add(lookVec);

            // Randomize the position within the scatter radius
            randomX = Math.random() * scatterRadius * 2 - scatterRadius;
            randomY = Math.random() * scatterRadius * 2 - scatterRadius;
            randomZ = Math.random() * scatterRadius * 2 - scatterRadius;

            // Set the meteor spawn position
            BlockPos meteorSpawnPos = new BlockPos(
                    (int) (player.getX() + randomX),
                    (int) (player.getY() + 100),
                    (int) (player.getZ() + randomZ)
            );

            // Create and configure the meteor entity
            MeteorNoLevelEntity meteorEntity = new MeteorNoLevelEntity(EntityInit.METEOR_NO_LEVEL_ENTITY.get(), player.level());
            meteorEntity.teleportTo(meteorSpawnPos.getX(), meteorSpawnPos.getY(), meteorSpawnPos.getZ());
            meteorEntity.setOwner(player);
            meteorEntity.noPhysics = true;

            // Set the scale of the meteor based on the player's sequence
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
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
            player.level().addFreshEntity(meteorEntity);
        }
    }
}
