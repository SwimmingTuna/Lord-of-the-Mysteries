package net.swimmingtuna.lotm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TornadoEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TORNADO_RADIUS = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TORNADO_HEIGHT = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LIFECOUNT = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_TORNADO_X = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TORNADO_Y = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TORNADO_Z = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RANDOM_MOVEMENT = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PICK_UP_BLOCKS = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.BOOLEAN);


    public TornadoEntity(EntityType<? extends TornadoEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public TornadoEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.TORNADO_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(DATA_LIFECOUNT, 300);
        this.entityData.define(DATA_TORNADO_RADIUS, 4);
        this.entityData.define(DATA_TORNADO_HEIGHT, 20);
        this.entityData.define(DATA_TORNADO_X, 0.0f);
        this.entityData.define(DATA_TORNADO_Y, 0.0f);
        this.entityData.define(DATA_TORNADO_Z, 0.0f);
        this.entityData.define(RANDOM_MOVEMENT, false);
        this.entityData.define(PICK_UP_BLOCKS, true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("TornadoRandomMovement")) {
            this.setTornadoRandom(compound.getBoolean("TornadoRandomMovement"));
        }
        if (compound.contains("TornadoPickupBlocks")) {
            this.setTornadoPickup(compound.getBoolean("TornadoPickupBlocks"));
        }
        if (compound.contains("TornadoRadius")) {
            this.setTornadoRadius(compound.getInt("TornadoRadius"));
        }
        if (compound.contains("TornadoHeight")) {
            this.setTornadoHeight(compound.getInt("TornadoHeight"));
        }
        if (compound.contains("TornadoLifeCount")) {
            this.setTornadoLifecount(compound.getInt("TornadoLifeCount"));
        }
        if (compound.contains("TornadoX")) {
            this.setTornadoXMov(compound.getFloat("TornadoX"));
        }
        if (compound.contains("TornadoY")) {
            this.setTornadoYMov(compound.getFloat("TornadoY"));
        }
        if (compound.contains("TornadoZ")) {
            this.setTornadoZMov(compound.getFloat("TornadoZ"));
        }

    }

    public static void summonTornado(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            TornadoEntity tornado = new TornadoEntity(pPlayer.level(), pPlayer, 0, 0, 0);
            tornado.setTornadoHeight(100);
            tornado.setTornadoRadius(25);
            tornado.setTornadoXMov((float) pPlayer.getLookAngle().scale(0.5f).x());
            tornado.setTornadoYMov((float) pPlayer.getLookAngle().scale(0.5f).y());
            tornado.setTornadoZMov((float) pPlayer.getLookAngle().scale(0.5f).z());
            pPlayer.level().addFreshEntity(tornado);
        }
    }
    public static void summonTyrantTornado(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            TornadoEntity tornado = new TornadoEntity(pPlayer.level(), pPlayer, 0, 0, 0);
            tornado.setTornadoHeight(300);
            tornado.setTornadoRadius(200);
            tornado.setTornadoXMov((float) pPlayer.getLookAngle().scale(0.5f).x());
            tornado.setTornadoYMov((float) pPlayer.getLookAngle().scale(0.5f).y());
            tornado.setTornadoZMov((float) pPlayer.getLookAngle().scale(0.5f).z());
            pPlayer.level().addFreshEntity(tornado);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TornadoRadius", this.getTornadoRadius());
        compound.putInt("TornadoLifeCount", this.getTornadoLifecount());
        compound.putInt("TornadoHeight", this.getTornadoHeight());
        compound.putFloat("TornadoX", this.getTornadoXMov());
        compound.putFloat("TornadoY", this.getTornadoYMov());
        compound.putFloat("TornadoZ", this.getTornadoZMov());
    }


    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide()) {
            BlockPos hitPos = pResult.getBlockPos();
            BlockPos tornadoPos = this.blockPosition();
            if (hitPos.getY() == tornadoPos.getY() - 1 && hitPos.getX() == tornadoPos.getX() && hitPos.getZ() == tornadoPos.getZ()) {
                this.setPos(this.getX(), this.getY() + 1, this.getZ());
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    @Override
    public void tick() {
        super.tick();

        int tornadoRadius = getTornadoRadius();
        int tornadoHeight = getTornadoHeight();
        double minX = this.getX() - tornadoRadius;
        double minY = this.getY() - 10; // 10 blocks below the tornado
        double minZ = this.getZ() - tornadoRadius;
        double maxX = this.getX() + tornadoRadius;
        double maxY = this.getY() + tornadoHeight;
        double maxZ = this.getZ() + tornadoRadius;
        boolean pickup = getTornadoPickup();
        AABB boundingBox = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        float tornadoX = getTornadoXMov();
        float tornadoY = getTornadoYMov();
        float tornadoZ = getTornadoZMov();
        this.setXRot(this.getXRot() + 2);
        this.setYRot(this.getYRot() + 2);
        this.setOldPosAndRot();

        if (this.level().isClientSide) {
            double sizeFactor = (tornadoRadius + 1) * (tornadoHeight + 1) / 1000.0;
            int particleCount = Math.max(20, (int) (50 * sizeFactor));
            double baseX = this.getX();
            double baseY = this.getY();
            double baseZ = this.getZ();
            double height = tornadoHeight;
            double radius1 = (double) tornadoRadius / 8;
            double radius2 = tornadoRadius;
            double riseSpeed = 0.2;
            for (int i = 0; i < particleCount; i++) {
                double h = this.random.nextDouble() * height;
                double radiusRatio = h / height;
                double currentRadius = radius1 + (radius2 - radius1) * radiusRatio;
                double angle = this.random.nextDouble() * Math.PI * 2;
                double offsetX = currentRadius * Math.cos(angle);
                double offsetZ = currentRadius * Math.sin(angle);
                double particleX = baseX + offsetX;
                double particleY = baseY + h;
                double particleZ = baseZ + offsetZ;
                double velocityY = riseSpeed;
                this.level().addAlwaysVisibleParticle(
                        ParticleTypes.CLOUD, true,
                        particleX, particleY, particleZ,
                        this.getDeltaMovement().x(), this.getDeltaMovement().y() + velocityY, this.getDeltaMovement().z()
                );
            }
        } else {
            Vec3 pos = this.position();
            int blockRadius = tornadoRadius;
            int blockHeight = tornadoHeight;
            Random random = new Random();
            List<Entity> entities = this.level().getEntities(this, boundingBox);
            for (Entity entity : entities) {
                if (entity != this && !(entity instanceof TornadoEntity) && entity != this.getOwner()) {
                    double dx = entity.getX() - this.getX();
                    double dz = entity.getZ() - this.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    if (this.tickCount % 10 == 0) {
                        entity.hurt(damageSources().fall(), 5);
                    }
                    if (distance < blockRadius) {
                        double angle = Math.atan2(dz, dx) + Math.PI / 2; // Change angle to push outward
                        double orbitRadius = blockRadius * 0.9; // Radius slightly less than the tornado radius
                        double newX = this.getX() + orbitRadius * Math.cos(angle);
                        double newZ = this.getZ() + orbitRadius * Math.sin(angle);

                        double heightRatio = (entity.getY() - this.getY()) / tornadoHeight;
                        double baseMotionY = 0.2 + (1 - heightRatio) * 0.4; // Stronger upward force at the bottom

                        double motionY = baseMotionY;
                        double horizontalForce = 0.3; // Increased for stronger outward push

                        // Apply the movement away from the center of the tornado
                        double outwardX = (newX - entity.getX()) * horizontalForce;
                        double outwardZ = (newZ - entity.getZ()) * horizontalForce;

                        // Normalize outward force to ensure it's not too strong
                        double outwardDistance = Math.sqrt(outwardX * outwardX + outwardZ * outwardZ);
                        if (outwardDistance > 0.1) { // Avoid division by zero and too small movements
                            outwardX /= outwardDistance;
                            outwardZ /= outwardDistance;
                        }

                        entity.setDeltaMovement(
                                outwardX,
                                motionY,
                                outwardZ
                        );
                    }
                    entity.hurtMarked = true;
                }
            }
            if (pickup) {
                for (BlockPos blockPosition : BlockPos.betweenClosed((int) minX, (int) minY, (int) minZ, (int) maxX, (int) maxY, (int) maxZ)) {
                    BlockState state = this.level().getBlockState(blockPosition);
                    if (!state.isAir() && !state.is(BlockTags.TALL_FLOWERS)) {
                        if (random.nextInt(5000) == 1) {
                            // Spawn a falling block entity at the destroyed block's position
                            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(this.level(), blockPosition, state);
                            fallingBlock.time = 1;

                            // Set random motion for the falling block
                            double randomDirectionX = (random.nextDouble() - 0.5) * 2.0; // random between -1 and 1
                            double randomDirectionY = random.nextDouble() * 2.0; // random upward motion
                            double randomDirectionZ = (random.nextDouble() - 0.5) * 2.0; // random between -1 and 1
                            fallingBlock.setDeltaMovement(randomDirectionX, randomDirectionY, randomDirectionZ);

                            // Remove the block from the world and add the falling block entity
                            this.level().setBlock(blockPosition, Blocks.AIR.defaultBlockState(), 3);
                            this.level().addFreshEntity(fallingBlock);
                        }
                    }
                }
            }

            if (!this.level().isClientSide) {
                this.setDeltaMovement(tornadoX, tornadoY, tornadoZ);
                this.hurtMarked = true;
                boolean randomMovement = getTornadoRandomness();
                if (randomMovement) {
                    if (this.tickCount % 60 == 0) {
                        float newTornadoX = (float) (Math.random() * 2 - 1); // Random value between -1 and 1
                        float newTornadoZ = (float) (Math.random() * 2 - 1); // Random value between -1 and 1

                        // Set new random movement values
                        setTornadoXMov(newTornadoX);
                        setTornadoZMov(newTornadoZ);
                    }
                }
                if (this.tickCount >= getTornadoLifecount()) {
                    this.discard();
                }
                if (getTornadoHeight() >= 290) {
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), this.level());
                    lightningEntity.setSpeed(20.0f);
                    lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                    lightningEntity.setMaxLength(40);
                    lightningEntity.setNoUp(true);
                    double x1 = this.getX() + ((Math.random() * 200) - 100);
                    double y1 = this.getY() + 200;
                    double z1 = this.getZ() + ((Math.random() * 200) - 100);
                    lightningEntity.teleportTo(x1, y1, z1);
                    this.level().addFreshEntity(lightningEntity);
                    this.level().addFreshEntity(lightningEntity);
                    this.level().addFreshEntity(lightningEntity);
                    this.level().addFreshEntity(lightningEntity);
                    this.level().addFreshEntity(lightningEntity);
                    this.level().addFreshEntity(lightningEntity);
                    for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(getTornadoRadius() * 1.5))) {
                        if (entity != this.getOwner() && this.tickCount % 40 == 0) {
                            entity.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 10,1,false,false));
                        }
                    }
                }
                if (Objects.requireNonNull(this.getOwner()).getPersistentData().getInt("calamityIncarnationTornado") >= 0) {
                    this.teleportTo(this.getOwner().getX(), this.getOwner().getY(), this.getOwner().getZ());
                }
            }
        }
    }



    public boolean isOnFire() {
        return false;
    }


    public void setTornadoRadius(int radius) {
        this.entityData.set(DATA_TORNADO_RADIUS, radius);
    }

    public void setTornadoHeight(int height) {
        this.entityData.set(DATA_TORNADO_HEIGHT, height);
    }

    public int getTornadoRadius() {
        return this.entityData.get(DATA_TORNADO_RADIUS);
    }

    public int getTornadoHeight() {
        return this.entityData.get(DATA_TORNADO_HEIGHT);
    }

    public void setTornadoXMov(float xMov) {
        this.entityData.set(DATA_TORNADO_X, xMov);
    }

    public float getTornadoXMov() {
        return this.entityData.get(DATA_TORNADO_X);
    }

    public void setTornadoYMov(float yMov) {
        this.entityData.set(DATA_TORNADO_Y, yMov);
    }
    public int getTornadoLifecount() {
        return this.entityData.get(DATA_LIFECOUNT);
    }

    public void setTornadoLifecount(int lifecount) {
        this.entityData.set(DATA_LIFECOUNT, lifecount);
    }

    public float getTornadoYMov() {
        return this.entityData.get(DATA_TORNADO_Y);
    }

    public void setTornadoZMov(float zMov) {
        this.entityData.set(DATA_TORNADO_Z, zMov);
    }
    public float getTornadoZMov() {
        return this.entityData.get(DATA_TORNADO_Z);
    }
    public void setTornadoRandom(boolean pRandom) {
        this.entityData.set(RANDOM_MOVEMENT, pRandom);
    }

    public boolean getTornadoRandomness() {
        return this.entityData.get(RANDOM_MOVEMENT);
    }
    public void setTornadoPickup(boolean pPickup) {
        this.entityData.set(PICK_UP_BLOCKS, pPickup);
    }

    public boolean getTornadoPickup() {
        return this.entityData.get(PICK_UP_BLOCKS);
    }
}
