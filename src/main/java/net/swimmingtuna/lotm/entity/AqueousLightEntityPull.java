package net.swimmingtuna.lotm.entity;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;

public class AqueousLightEntityPull extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(AqueousLightEntityPull.class, EntityDataSerializers.BOOLEAN);

    public AqueousLightEntityPull(EntityType<? extends AqueousLightEntityPull> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AqueousLightEntityPull(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.AQUEOUS_LIGHT_ENTITY_PULL.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }


    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }

    /**
     * Returns {@code true} if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isOnFire() {
        return false;
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide()) {
            if (pResult.getEntity() instanceof LivingEntity entity) {
                LivingEntity owner = (LivingEntity) this.getOwner();
                double x = owner.getX() - entity.getX();
                double y = owner.getY() - entity.getY();
                double z = owner.getZ() - entity.getZ();
                entity.setDeltaMovement(x * 0.3,y * 0.3,z * 0.3);
                CompoundTag ownerTag = owner.getPersistentData();
                boolean sailorLightning = ownerTag.getBoolean("SailorLightning");
                if (owner instanceof Player pPlayer) {
                    BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                        if (!entity.level().isClientSide() && !owner.level().isClientSide()) {
                            int damage = 15 - (tyrantSequence.getCurrentSequence() * 2);
                        entity.hurt(damageSources().fall(), damage);
                        if (tyrantSequence.getCurrentSequence() <= 7) {
                            double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5)); // Decrease chance by 12.5% for each level below 9
                            if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                lightningBolt.moveTo(entity.getX(), entity.getY(), entity.getZ());
                                entity.level().addFreshEntity(lightningBolt);
                            }
                            }
                        }
                    });
                }
                this.discard();
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
            AqueousLightEntityPull aqueousLightEntity = new AqueousLightEntityPull(pPlayer.level(), pPlayer, initialVelocity.x, initialVelocity.y, initialVelocity.z);
            aqueousLightEntity.setDeltaMovement(initialVelocity);
            Vec3 lightPosition = eyePosition.add(direction.scale(2.0));
            aqueousLightEntity.setPos(lightPosition);
            aqueousLightEntity.setOwner(pPlayer);
            pPlayer.level().addFreshEntity(aqueousLightEntity);
        }
    }

    public static void summonEntityWhip(Player pPlayer, LivingEntity pEntity, boolean x) {
        if (!pPlayer.level().isClientSide()) {
            Vec3 direction = pPlayer.getViewVector(1.0f);
            Vec3 initialVelocity = direction.scale(2.0);
            AqueousLightEntityPull aqueousLightEntity = new AqueousLightEntityPull(pPlayer.level(), pPlayer, initialVelocity.x, initialVelocity.y, initialVelocity.z);
            CompoundTag tag = aqueousLightEntity.getPersistentData();
            x = tag.getBoolean("waterManipulationPull");
            Vec3 eyePosition = pPlayer.getEyePosition(1.0f);
            summonEntityWithSpeed(direction, initialVelocity, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 20 == 0) {
            if (this.tickCount >= 100) {
                this.discard();
            }
        }
    }
}
