package net.swimmingtuna.lotm.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
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

public class MeteorNoLevelEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(MeteorNoLevelEntity.class, EntityDataSerializers.BOOLEAN);

    public MeteorNoLevelEntity(EntityType<? extends MeteorNoLevelEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MeteorNoLevelEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.METEOR_NO_LEVEL_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }


    protected float getInertia() {
        return this.isDangerous() ? 1.0F : super.getInertia();
    }


    public boolean isOnFire() {
        return false;
    }


    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide) {
            Vec3 hitPos = pResult.getLocation();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE.AMBIENT, 5.0F, 5.0F);
            if (pResult.getEntity() instanceof LivingEntity entity) {
                Explosion explosion = new Explosion(this.level(), this, hitPos.x, hitPos.y, hitPos.z, 30.0F, true, Explosion.BlockInteraction.DESTROY);
                DamageSource damageSource = damageSources().explosion(explosion);
                entity.hurt(damageSource, 30.0F);
            }
            this.discard();
        }
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide) {
            Vec3 hitPos = pResult.getLocation();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE.AMBIENT, 30.0F, 1.0F);
            for (LivingEntity entity : this.getOwner().level().getEntitiesOfClass(LivingEntity.class, this.getOwner().getBoundingBox().inflate(50))) {
                Explosion explosion = new Explosion(this.level(), this, hitPos.x, hitPos.y, hitPos.z, 30.0F, true, Explosion.BlockInteraction.DESTROY);
                DamageSource damageSource = damageSources().explosion(explosion);
                entity.hurt(damageSource, 30.0F);
                entity.hurt(damageSource, 25.0F);
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
            MeteorNoLevelEntity meteorEntity = new MeteorNoLevelEntity(EntityInit.METEOR_NO_LEVEL_ENTITY.get(), pPlayer.level());
            meteorEntity.teleportTo(meteorSpawnPos.getX(), meteorSpawnPos.getY(), meteorSpawnPos.getZ());
            meteorEntity.setOwner(pPlayer);
            meteorEntity.noPhysics = true;

            // Set the scale of the meteor based on the player's sequence
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
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
}
