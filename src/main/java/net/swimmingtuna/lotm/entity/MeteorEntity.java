package net.swimmingtuna.lotm.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

public class MeteorEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(MeteorEntity.class, EntityDataSerializers.BOOLEAN);

    public MeteorEntity(EntityType<? extends MeteorEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MeteorEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.METEOR_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
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
            double radius = scale * 4;
            if (this.getOwner() != null && this.getOwner().getPersistentData().getInt("inMindscape") >= 1) {
                if (hitPos.getX() > this.getOwner().getX()) {
                    return;
                } else {
                    this.explodeMeteorBlock(hitPos,radius,scale);
                }
            }
                explodeMeteorBlock(hitPos, radius, scale);
            this.discard();
        }
    }


    @Override
    public void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide()) {
            Entity hitEntity = result.getEntity();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            float scale = scaleData.getScale();
            if (hitEntity instanceof LivingEntity livingEntity) {
                if (this.getOwner() != null && this.getOwner().getPersistentData().getInt("inMindscape") >= 1) {
                    if (hitEntity.getX() > this.getOwner().getX()) {
                        return;
                    } else {
                        this.explodeMeteor(livingEntity, scale);
                    }
                }
                explodeMeteor(livingEntity, scale);
                this.level().playSound(null, this.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 30.0f, 1.0f);
                this.discard();
            }
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
            Vec3 lookVec = player.getLookAngle().normalize().scale(100);
            Vec3 targetPos = player.getEyePosition().add(lookVec);
            randomX = Math.random() * scatterRadius * 2 - scatterRadius;
            randomY = Math.random() * scatterRadius * 2 - scatterRadius;
            randomZ = Math.random() * scatterRadius * 2 - scatterRadius;

            // Set the meteor spawn position
            BlockPos meteorSpawnPos = new BlockPos(
                    (int) (player.getX() + randomX),
                    (int) (player.getY() + 200),
                    (int) (player.getZ() + randomZ)
            );

            MeteorEntity meteorEntity = new MeteorEntity(EntityInit.METEOR_ENTITY.get(), player.level());
            meteorEntity.teleportTo(meteorSpawnPos.getX(), meteorSpawnPos.getY(), meteorSpawnPos.getZ());
            meteorEntity.setOwner(player);
            meteorEntity.noPhysics = true;

            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int scalecheck = 10 - holder.getCurrentSequence() * 4;
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(meteorEntity);
            scaleData.setScale(scalecheck);
            scaleData.markForSync(true);

            Vec3 randomizedTargetPos = targetPos.add(
                    (Math.random() * 140 - 70),
                    (Math.random() * 140 - 70),
                    (Math.random() * 140 - 70)
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

    public static void summonMeteorAtPosition(LivingEntity player, int x, int y, int z) {
        if (!player.level().isClientSide()) {
            BlockPos meteorSpawnPos = new BlockPos(x, y, z);
            MeteorEntity meteorEntity = new MeteorEntity(EntityInit.METEOR_ENTITY.get(), player.level());
            meteorEntity.teleportTo(meteorSpawnPos.getX(), meteorSpawnPos.getY(), meteorSpawnPos.getZ());
            meteorEntity.noPhysics = true;
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(meteorEntity);
            scaleData.setScale(6);
            scaleData.markForSync(true);
            Vec3 randomizedTargetPos = new Vec3(x, y, z);
            Vec3 meteorPos = meteorEntity.position();
            Vec3 directionToTarget = randomizedTargetPos.subtract(meteorPos).normalize();
            double speed = 3.0;
            meteorEntity.setDeltaMovement(directionToTarget.scale(speed));
            player.level().addFreshEntity(meteorEntity);
        }
    }

    public void explodeMeteor(LivingEntity hitEntity, float scale) {
        BlockPos hitPos = hitEntity.blockPosition();
        double radius = scale * 4;
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
                livingEntity.hurt(BeyonderUtil.getSource(this, DamageTypes.GENERIC), 16 * scale);
            }
        }
    }
    public void explodeMeteorBlock(BlockPos hitPos, double radius, float scale) {
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
                livingEntity.hurt(BeyonderUtil.getSource(this, DamageTypes.GENERIC), 16 * scale); // problem w/ damage sources
            }
        }
    }


    @Override
    public void tick() {
        super.tick();
        ProjectileUtil.rotateTowardsMovement(this, 0.5f);
        this.xRotO = getXRot();
        this.yRotO = this.getYRot();
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; i++) {
                double offsetX = (Math.random() - 0.5) * 6; // Random offset within [-3, 3]
                double offsetY = (Math.random() - 0.5) * 6; // Random offset within [-3, 3]
                double offsetZ = (Math.random() - 0.5) * 6; // Random offset within [-3, 3]
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        0, 0.0, 0.0,0,0);
            }

            // Spawn 20 fire particles randomly spread within a 10-block radius
            for (int i = 0; i < 20; i++) {
                double offsetX = (Math.random() - 0.5) * 20; // Random offset within [-10, 10]
                double offsetY = (Math.random() - 0.5) * 20; // Random offset within [-10, 10]
                double offsetZ = (Math.random() - 0.5) * 20; // Random offset within [-10, 10]
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        0, 0.0, 0.0, 0.0, 0);
            }
        }

    }
}
