package net.swimmingtuna.lotm.entity;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class WhisperOfCorruptionEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(WhisperOfCorruptionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(WhisperOfCorruptionEntity.class, EntityDataSerializers.INT);

    public WhisperOfCorruptionEntity(EntityType<? extends WhisperOfCorruptionEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }


    public boolean isPickable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LIFETIME, 150);
        this.entityData.define(DATA_DANGEROUS, false);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            float scale = scaleData.getScale();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            for (int i = 0; i < scale * 200; i++) {
                double theta = 2 * Math.PI * Math.random();
                double phi = Math.acos(2 * Math.random() - 1);
                double particleX = x + scale * Math.sin(phi) * Math.cos(theta);
                double particleY = y + scale * Math.sin(phi) * Math.sin(theta);
                double particleZ = z + scale * Math.cos(phi);
                double random = (Math.random() * scale) - (scale / 2);
                if (this.level() instanceof ServerLevel serverLevel) {
                   serverLevel.sendParticles(ParticleTypes.ENCHANT, particleX, particleY, particleZ,0, random, random, random,0);
                }
            }
            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(scale * 1.5))) {
                if (livingEntity.getPersistentData().getInt("corruptionWhisperCooldown") == 0) {
                    if (livingEntity instanceof Player player) {
                        AttributeInstance corruption = player.getAttribute(ModAttributes.CORRUPTION.get());
                        corruption.setBaseValue(corruption.getBaseValue() + scale);
                    } else {
                        livingEntity.hurt(livingEntity.damageSources().magic(), scale);
                    }
                    livingEntity.getPersistentData().putInt("corruptionWhisperCooldown", 10);
                }
            }
            if (this.tickCount >= getLifetime()) {
                this.discard();
            }
        }
    }

    public static void summonWhispersInLookVec(LivingEntity livingEntity, int sequence) {
        if (!livingEntity.level().isClientSide()) {
            Vec3 lookVec = livingEntity.getLookAngle().scale(2.5);
            WhisperOfCorruptionEntity whisper = new WhisperOfCorruptionEntity(EntityInit.WHISPERS_OF_CORRUPTION_ENTITY.get(), livingEntity.level());
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(whisper);
            whisper.setOwner(livingEntity);
            whisper.setDeltaMovement(lookVec.x(), lookVec.y(), lookVec.z());
            whisper.hurtMarked = true;
            whisper.hasImpulse = true;
            whisper.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            scaleData.setScale(10 - (sequence * 2));
            scaleData.markForSync(true);
            whisper.setLifetime(300 - (sequence * 50));
            livingEntity.level().addFreshEntity(whisper);
        }
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int lifetime) {
        this.entityData.set(LIFETIME, lifetime);
    }

    public static void decrementWhisper(CompoundTag tag) {
        if (tag.getInt("corruptionWhisperCooldown") >= 1) {
            tag.putInt("corruptionWhisperCooldonw",tag.getInt("corruptionWhisperCooldown") - 1 );
        }
    }
}
