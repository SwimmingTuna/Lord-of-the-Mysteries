package net.swimmingtuna.lotm.entity;


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
        return this.isDangerous() ? 0.73F : super.getInertia();
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

    public static void summonMultipleMeteors(Vec3 direction, Vec3 eyePosition, double x, double y, double z, Player pPlayer, int scale) {
        if (!pPlayer.level().isClientSide()) {
            double randomXOffset = (Math.random() * 0.4) - 0.2;
            double randomZOffset = (Math.random() * 0.4) - 0.2;
            double randomAngle = Math.toRadians(Math.random() * 20 - 10); // Random angle between -10 and 10 degrees
            double randomVelocityX = -direction.x + randomXOffset * Math.cos(randomAngle);
            double randomVelocityZ = -direction.z + randomZOffset * Math.cos(randomAngle);
            Vec3 initialVelocity = new Vec3(randomVelocityX, -direction.y, randomVelocityZ);
            MeteorNoLevelEntity meteorEntity = new MeteorNoLevelEntity(pPlayer.level(), pPlayer, initialVelocity.x, initialVelocity.y, initialVelocity.z);
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
}
