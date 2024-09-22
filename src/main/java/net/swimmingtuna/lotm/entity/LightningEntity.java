package net.swimmingtuna.lotm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LightningEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Integer> MAX_LENGTH = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FALL_DOWN = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BRANCH_OUT = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> NO_UP = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SYNCHED_MOVEMENT = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.BOOLEAN);

    private List<Vec3> positions = new ArrayList<>();
    private List<AABB> boundingBoxes = new ArrayList<>();
    private Random random = new Random();
    private Vec3 startPos;
    private LivingEntity owner;
    private Entity targetEntity;
    private Vec3 targetPos;
    private Vec3 lastPos; // New field for last position

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
        this.entityData.define(FALL_DOWN, false);
        this.entityData.define(BRANCH_OUT, false);
        this.entityData.define(NO_UP, false);
        this.entityData.define(SYNCHED_MOVEMENT, false);
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
        if (compound.contains("NoUp")) {
            this.setNoUp(compound.getBoolean("NoUp"));
        }
        if (compound.contains("SynchedMovement")) {
            this.setSynchedMovement(compound.getBoolean("SynchedMovement"));
        }
        if (compound.contains("fallDown")) {
            this.setFallDown(compound.getBoolean("fallDown"));
        }
        if (compound.contains("Speed")) {
            this.setSpeed(compound.getFloat("Speed"));
        }
        if (compound.contains("BranchOut")) {
            this.setBranchOut(compound.getBoolean("BranchOut"));
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

        this.lastPos = this.positions.get(this.positions.size() - 1);
        Vec3 targetVector = null;

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
            Vec3 movement = this.getDeltaMovement().scale(speed);
            if (this.getFallDown()) {
                movement = movement.add(0, -0.5, 0);
            }
            newPos = lastPos.add(movement.add(new Vec3(
                    random.nextGaussian() * 0.1 * speed,
                    random.nextGaussian() * 0.1 * speed,
                    random.nextGaussian() * 0.1 * speed
            )));
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
                        if (!this.level().getBlockState(blockPos).isAir() && !this.level().getBlockState(blockPos).getBlock().equals(Blocks.WATER)) {
                            Vec3 hitPos = currentPos;
                            if (this.owner != null) {
                                if (this.owner instanceof Player player) {
                                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                                    int sequence = holder.getCurrentSequence();
                                    int radius = 20 - (sequence * 2);
                                    this.level().explode(this, hitPos.x, hitPos.y, hitPos.z, radius, Level.ExplosionInteraction.BLOCK);
                                    AABB damageArea = new AABB(hitPos.x - radius, hitPos.y - radius, hitPos.z - radius,
                                            hitPos.x + radius, hitPos.y + radius, hitPos.z + radius);
                                    for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, damageArea)) {
                                        if (entity != this.owner || entity != this.getOwner()) {
                                            double distance1 = Math.sqrt(entity.distanceToSqr(hitPos));
                                            float maxDamage = 50f;
                                            float minDamage = 10f;
                                            float damageFalloff = (float) (distance1 / radius);
                                            float damage = Math.max(minDamage, maxDamage * (1 - damageFalloff));
                                            damage -= (sequence * 2);
                                            entity.hurt(entity.damageSources().lightningBolt(), damage);
                                        }
                                    }
                                }
                            } else {
                                level().explode(this, hitPos.x(), hitPos.y(), hitPos.z(), 10, false, Level.ExplosionInteraction.TNT);
                                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, new AABB(hitPos.x - 4, hitPos.y - 4, hitPos.z - 4, hitPos.x + 4, hitPos.y + 4, hitPos.z + 4))) {
                                    if (entity != this.getOwner() || entity != this.owner) {
                                        entity.hurt(entity.damageSources().lightningBolt(), 10);
                                    }
                                }
                            }
                            hasExploded = true;
                            this.discard();
                            break;
                        }
                    }
                }
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
        boolean branchOut = getBranchOut();
        boolean noUp = getNoUp();
        boolean synchedMovement = getSynchedMovement();
        if (!this.level().isClientSide()) {
            if (synchedMovement) {
                if (this.tickCount >= 2) {
                    this.setDeltaMovement(this.getPersistentData().getDouble("lightningBranchDMX"), this.getPersistentData().getDouble("lightningBranchDMY"), this.getPersistentData().getDouble("lightningBranchDMZ"));
                }
            }
            if (branchOut) {
                if (this.tickCount == getMaxLength()) {
                    this.level().explode(this, lastPos.x(), lastPos.y(), lastPos.z(), 10, Level.ExplosionInteraction.TNT);
                }
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), this.level());
                lightningEntity.setSpeed(8.0f);
                lightningEntity.setDeltaMovement(this.getPersistentData().getDouble("sailorLightningDMX") + (Math.random() * 0.5) - 0.25, this.getPersistentData().getDouble("sailorLightningDMY") + (Math.random() * 0.5) - 0.25, this.getPersistentData().getDouble("sailorLightningDMZ") + (Math.random() * 0.5) - 0.25);
                lightningEntity.setMaxLength(100);
                lightningEntity.teleportTo(lastPos.x(), lastPos.y(), lastPos.z());
                lightningEntity.setSynchedMovement(true);
                lightningEntity.getPersistentData().putDouble("lightningBranchDMY", this.getPersistentData().getDouble("sailorLightningDMY") + (Math.random() * 0.8) - 0.4);
                lightningEntity.getPersistentData().putDouble("lightningBranchDMX", this.getPersistentData().getDouble("sailorLightningDMX") + (Math.random() * 0.8) - 0.4);
                lightningEntity.getPersistentData().putDouble("lightningBranchDMZ", this.getPersistentData().getDouble("sailorLightningDMZ") + (Math.random() * 0.8) - 0.4);
                this.level().addFreshEntity(lightningEntity);
                if (this.tickCount == 1) {
                    this.getPersistentData().putDouble("sailorLightningDMY", this.getDeltaMovement().y());
                    this.getPersistentData().putDouble("sailorLightningDMX", this.getDeltaMovement().x());
                    this.getPersistentData().putDouble("sailorLightningDMZ", this.getDeltaMovement().z());
                }
                this.setDeltaMovement(this.getPersistentData().getDouble("sailorLightningDMX"), this.getPersistentData().getDouble("sailorLightningDMY"), this.getPersistentData().getDouble("sailorLightningDMZ"));
            }
            if (noUp) {
                if (this.getDeltaMovement().y() >= -0.5f) {
                    this.discard();
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), this.level());
                    lightningEntity.setSpeed(5.0f);
                    lightningEntity.setDeltaMovement((Math.random() * 0.6) - 0.3, -2, (Math.random() * 0.6) - 0.3);
                    lightningEntity.setMaxLength(100);
                    lightningEntity.teleportTo(lastPos.x(), lastPos.y(), lastPos.z());
                    lightningEntity.setBranchOut(true);
                    this.level().addFreshEntity(lightningEntity);
                }
            }
        }
        if (!this.level().isClientSide() && owner != null) {
            CompoundTag tag = owner.getPersistentData();
            if (tag.getInt("sailorLightningTravel") >= 1 && lastPos != null) {
                owner.teleportTo(lastPos.x(), lastPos.y, lastPos.z);
                owner.getPersistentData().putInt("sailorLightningTravel", 10);
                Vec3 lookVec = owner.getLookAngle();
                this.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            }
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
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
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide()) {
            if (result.getEntity() instanceof LivingEntity entity) {
                entity.hurt(damageSources().fall(), 5);
                this.discard();
            }
        }
        super.onHitEntity(result);
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

    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public void setOwner(LivingEntity entity) {
        this.owner = entity;
    }

    public boolean getFallDown() {
        return this.entityData.get(FALL_DOWN);
    }

    public void setFallDown(boolean fallDown) {
        this.entityData.set(FALL_DOWN, fallDown);
    }

    public boolean getBranchOut() {
        return this.entityData.get(BRANCH_OUT);
    }

    public void setBranchOut(boolean branchOut) {
        this.entityData.set(BRANCH_OUT, branchOut);
    }

    public boolean getNoUp() {
        return this.entityData.get(NO_UP);
    }

    public void setNoUp(boolean noUp) {
        this.entityData.set(NO_UP, noUp);
    }

    public boolean getSynchedMovement() {
        return this.entityData.get(SYNCHED_MOVEMENT);
    }

    public void setSynchedMovement(boolean synchedMovement) {
        this.entityData.set(SYNCHED_MOVEMENT, synchedMovement);
    }
    public Vec3 getLastPos() {
        return this.lastPos;
    }
}