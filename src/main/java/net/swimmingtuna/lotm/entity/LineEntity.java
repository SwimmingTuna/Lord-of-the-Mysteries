package net.swimmingtuna.lotm.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LineEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Integer> MAX_LENGTH = SynchedEntityData.defineId(LineEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(LineEntity.class, EntityDataSerializers.FLOAT);

    private List<Vec3> positions = new ArrayList<>();
    private List<AABB> boundingBoxes = new ArrayList<>();
    private Random random = new Random();
    private Vec3 startPos;
    private LivingEntity owner;

    public LineEntity(EntityType<? extends LineEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LineEntity(EntityType<? extends LineEntity> entityType, double x, double y, double z, double dX, double dY, double dZ, Level level) {
        super(entityType, x, y, z, dX, dY, dZ, level);
        this.startPos = new Vec3(x, y, z);
    }

    public LineEntity(EntityType<? extends LineEntity> entityType, LivingEntity shooter, double dX, double dY, double dZ, Level level) {
        super(entityType, shooter, dX, dY, dZ, level);
        this.startPos = shooter.position();
        this.owner = shooter;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MAX_LENGTH, 100);
        this.entityData.define(SPEED, 1.0f);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("MaxLength")) {
            this.setMaxLength(compound.getInt("MaxLength"));
        }
        if (compound.contains("Speed")) {
            this.setSpeed(compound.getFloat("Speed"));
        }

        if (compound.contains("Positions")) {
            ListTag posList = compound.getList("Positions", 10);
            this.positions.clear();
            for (int i = 0; i < posList.size(); i++) {
                CompoundTag posTag = posList.getCompound(i);
                this.positions.add(new Vec3(posTag.getDouble("X"), posTag.getDouble("Y"), posTag.getDouble("Z")));
            }
        }
        if (compound.contains("StartPos")) {
            CompoundTag startPosTag = compound.getCompound("StartPos");
            this.startPos = new Vec3(
                    startPosTag.getDouble("sX"),
                    startPosTag.getDouble("sY"),
                    startPosTag.getDouble("sZ")
            );
        }
        // Restore the owner from saved data if necessary
        if (compound.contains("OwnerUUID")) {
            UUID ownerUUID = compound.getUUID("OwnerUUID");
            Level level = this.level();
            if (ownerUUID != null && level != null) {
                this.owner = (LivingEntity) ((ServerLevel) level).getEntity(ownerUUID);
            }
        }
    }


    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MaxLength", this.getMaxLength());
        compound.putFloat("Speed", this.getSpeed());
        ListTag posList = new ListTag();
        for (Vec3 pos : this.positions) {
            CompoundTag posTag = new CompoundTag();
            posTag.putDouble("X", pos.x);
            posTag.putDouble("Y", pos.y);
            posTag.putDouble("Z", pos.z);
            posList.add(posTag);
        }
        compound.put("Positions", posList);
        if (startPos != null) {
            CompoundTag startPosTag = new CompoundTag();
            startPosTag.putDouble("sX", startPos.x);
            startPosTag.putDouble("sY", startPos.y);
            startPosTag.putDouble("sZ", startPos.z);
            compound.put("StartPos", startPosTag);
        }
        // Save the owner's UUID
        if (this.owner != null) {
            compound.putUUID("OwnerUUID", this.owner.getUUID());
        }
    }

    @Override
    public void tick() {
        super.tick();

        float speed = this.getSpeed();

        if (startPos == null) {
            startPos = this.position();
        }

        if (this.positions.isEmpty()) {
            this.positions.add(startPos);
        }

        Vec3 lastPos = this.positions.get(this.positions.size() - 1);
        Vec3 newPos = lastPos.add(
                this.getDeltaMovement().x * speed + random.nextGaussian() * 0.1 * speed,
                this.getDeltaMovement().y * speed + random.nextGaussian() * 0.1 * speed,
                this.getDeltaMovement().z * speed + random.nextGaussian() * 0.1 * speed
        );

        if (this.positions.size() < this.getMaxLength()) {
            this.positions.add(newPos);
        }

        boundingBoxes.add(createBoundingBox(newPos));

        this.setPos(startPos.x, startPos.y, startPos.z);
        this.setBoundingBox(createBoundingBox(newPos));

        for (Vec3 pos : this.positions) {
            this.level().addParticle(ParticleInit.NULL_PARTICLE.get(),
                    pos.x, pos.y, pos.z,
                    0, 0, 0);
        }

        if (this.tickCount > this.getMaxLength()) {
            this.discard();
        }
    }

    private AABB createBoundingBox(Vec3 position) {
        double boxSize = 0.2;
        return new AABB(
                position.x - boxSize, position.y - boxSize, position.z - boxSize,
                position.x + boxSize, position.y + boxSize, position.z + boxSize
        );
    }

    public void setNewStartPos(Vec3 newStartPos) {
        this.startPos = newStartPos;
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide()) {
            if (pResult.getEntity() instanceof LivingEntity entity) {
                entity.hurt(damageSources().fall(), 5);
                this.discard();
            }
        }
        super.onHitEntity(pResult);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide) {
            Vec3 hitPos = pResult.getLocation();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, (5.0f * scaleData.getScale() / 3), Level.ExplosionInteraction.BLOCK);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE.AMBIENT, 5.0F, 5.0F);
            if (this.owner != null) {  // Null check to avoid NullPointerException
                for (LivingEntity entity : this.owner.level().getEntitiesOfClass(LivingEntity.class, this.owner.getBoundingBox().inflate(50))) {
                    Explosion explosion = new Explosion(this.level(), this, hitPos.x, hitPos.y, hitPos.z, 30.0F, true, Explosion.BlockInteraction.DESTROY);
                    DamageSource damageSource = damageSources().explosion(explosion);
                    entity.hurt(damageSource, 30.0F);
                    entity.hurt(damageSource, 25.0F);
                }
            }
            this.discard();
        }
    }

    public int getMaxLength() {
        return this.entityData.get(MAX_LENGTH);
    }

    public void setMaxLength(int length) {
        this.entityData.set(MAX_LENGTH, length);
    }

    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    public void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    public List<Vec3> getPositions() {
        return positions;
    }

    public List<AABB> getBoundingBoxes() {
        return boundingBoxes;
    }

    @Override
    public LivingEntity getOwner() {
        return this.owner;
    }

    public Level getLevel() {
        return this.level();
    }
}

