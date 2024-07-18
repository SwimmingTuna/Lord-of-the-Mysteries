package net.swimmingtuna.lotm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LightningEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Integer> MAX_LENGTH = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);

    private List<Vec3> positions = new ArrayList<>();
    private List<AABB> boundingBoxes = new ArrayList<>();
    private Random random = new Random();
    private Vec3 startPos;
    private LivingEntity owner;
    private Entity targetEntity;
    private Vec3 targetPos;

    public LightningEntity(EntityType<? extends LightningEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LightningEntity(EntityType<? extends LightningEntity> entityType, double x, double y, double z, double dX, double dY, double dZ, Level level) {
        super(entityType, x, y, z, dX, dY, dZ, level);
        this.startPos = new Vec3(x, y, z);
    }

    public LightningEntity(EntityType<? extends LightningEntity> entityType, LivingEntity shooter, double dX, double dY, double dZ, Level level) {
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
        if (compound.contains("TargetEntity")) {
            UUID targetUUID = compound.getUUID("TargetUUID");
            Level level = this.level();
            if (targetUUID != null && level != null) {
                this.targetEntity = ((ServerLevel) level).getEntity(targetUUID);
            }
        }
        if (compound.contains("TargetPos")) {
            CompoundTag targetPosTag = compound.getCompound("TargetPos");
            this.targetPos = new Vec3(
                    targetPosTag.getDouble("tX"),
                    targetPosTag.getDouble("tY"),
                    targetPosTag.getDouble("tZ")
            );
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
        if (this.targetEntity != null) {
            compound.putUUID("TargetUUID", this.targetEntity.getUUID());
        }
        if (targetPos != null) {
            CompoundTag targetPosTag = new CompoundTag();
            targetPosTag.putDouble("tX", targetPos.x);
            targetPosTag.putDouble("tY", targetPos.y);
            targetPosTag.putDouble("tZ", targetPos.z);
            compound.put("TargetPos", targetPosTag);
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
        Vec3 targetVector = null;

        // Determine the target position
        if (this.targetEntity != null) {
            targetVector = this.targetEntity.position().subtract(lastPos).normalize();
        } else if (this.targetPos != null) {
            targetVector = this.targetPos.subtract(lastPos).normalize();
        }

        Vec3 newPos;
        if (targetVector != null) {
            newPos = lastPos.add(
                    targetVector.x * speed + random.nextGaussian() * 0.1 * speed,
                    targetVector.y * speed + random.nextGaussian() * 0.1 * speed,
                    targetVector.z * speed + random.nextGaussian() * 0.1 * speed
            );
        } else {
            newPos = lastPos.add(
                    this.getDeltaMovement().x * speed + random.nextGaussian() * 0.1 * speed,
                    this.getDeltaMovement().y * speed + random.nextGaussian() * 0.1 * speed,
                    this.getDeltaMovement().z * speed + random.nextGaussian() * 0.1 * speed
            );
        }
        if (targetPos != null) {
            if (lastPos.distanceToSqr(targetPos) < 1) {
                targetPos = null;
            }
        }

        if (this.positions.size() < this.getMaxLength()) {
            this.positions.add(newPos);
        }

        boolean hasExploded = false;

        for (int i = 0; i < this.positions.size() - 1 && !hasExploded; i++) {
            Vec3 pos1 = this.positions.get(i);
            Vec3 pos2 = this.positions.get(i + 1);
            double distance = pos1.distanceTo(pos2);
            Vec3 direction = pos2.subtract(pos1).normalize();
            for (double d = 0; d < distance && !hasExploded; d += 3.0) {
                Vec3 currentPos = pos1.add(direction.scale(d));
                AABB checkArea = createBoundingBox(currentPos);
                if (!this.level().isClientSide()) {
                    for (BlockPos blockPos : BlockPos.betweenClosed(new BlockPos((int) checkArea.minX, (int) checkArea.minY, (int) checkArea.minZ), new BlockPos((int) checkArea.maxX, (int) checkArea.maxY, (int) checkArea.maxZ))) {
                        if (!this.level().getBlockState(blockPos).isAir()) {
                            Vec3 hitPos = currentPos;
                            if (this.owner != null) {
                                if (this.owner instanceof Player pPlayer) {
                                    BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                                    int sequence = holder.getCurrentSequence();
                                    int radius = 20 - (sequence * 2);
                                    this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, radius, Level.ExplosionInteraction.BLOCK);
                                    AABB damageArea = new AABB(hitPos.x - radius, hitPos.y - radius, hitPos.z - radius,
                                            hitPos.x + radius, hitPos.y + radius, hitPos.z + radius);
                                    for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, damageArea)) {
                                        if (entity != this.owner) {
                                            double distance1 = Math.sqrt(entity.distanceToSqr(hitPos));
                                            float maxDamage = 50f;
                                            float minDamage = 10f;
                                            float damageFalloff = (float) (distance1 / radius);
                                            float damage = Math.max(minDamage, maxDamage * (1 - damageFalloff));
                                            damage -= (sequence * 2); // Reduce damage based on sequence
                                            entity.hurt(entity.damageSources().lightningBolt(), damage);
                                            pPlayer.sendSystemMessage(Component.literal("Damage dealt: " + damage + " at distance: " + distance1));
                                        }
                                    }
                                }
                            } else {
                                level().explode(this, hitPos.x(), hitPos.y, hitPos.z, 10, false, Level.ExplosionInteraction.TNT);
                                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, new AABB(hitPos.x - 4, hitPos.y - 4, hitPos.z - 4, hitPos.x + 4, hitPos.y + 4, hitPos.z + 4))) {
                                    entity.hurt(entity.damageSources().lightningBolt(), 10);
                                }
                            }
                            hasExploded = true;
                            this.discard();
                            break;
                        }
                    }
                }
                // Add particles at each AABB location with a random offset of ±2
                double offsetX = random.nextGaussian() * 1;
                double offsetY = random.nextGaussian() * 1;
                double offsetZ = random.nextGaussian() * 1;
                level().addParticle(ParticleTypes.ELECTRIC_SPARK, checkArea.minX + offsetX, checkArea.minY + offsetY, checkArea.minZ + offsetZ, 0, 0, 0);
                level().addParticle(ParticleTypes.ELECTRIC_SPARK, checkArea.maxX + offsetX, checkArea.maxY + offsetY, checkArea.maxZ + offsetZ, 0, 0, 0);
            }
            if (hasExploded) break;
        }

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
    public void setTargetPos(Vec3 targetPos) {
        this.targetPos = targetPos;
    }

    public void setTargetEntity(LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }
    public void setOwner (LivingEntity entity) {
        this.owner = entity;
    }
}

