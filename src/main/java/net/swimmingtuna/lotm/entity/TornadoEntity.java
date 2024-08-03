package net.swimmingtuna.lotm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class TornadoEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TORNADO_RADIUS = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TORNADO_HEIGHT = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_TORNADO_X = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TORNADO_Y = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TORNADO_Z = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);

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
        this.entityData.define(DATA_TORNADO_RADIUS, 4);
        this.entityData.define(DATA_TORNADO_HEIGHT, 20);
        this.entityData.define(DATA_TORNADO_X, 0.0f);
        this.entityData.define(DATA_TORNADO_Y, 0.0f);
        this.entityData.define(DATA_TORNADO_Z, 0.0f);

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("TornadoRadius")) {
            this.setTornadoRadius(compound.getInt("TornadoRadius"));
        }
        if (compound.contains("TornadoHeight")) {
            this.setTornadoHeight(compound.getInt("TornadoHeight"));
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

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TornadoRadius", this.getTornadoRadius());
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
            List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().inflate(blockRadius));
            for (Entity entity : entities) {
                if (entity != this && !(entity instanceof TornadoEntity)) {
                    double dx = entity.getX() - this.getX();
                    double dz = entity.getZ() - this.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    if (distance < blockRadius) {
                        double angle = Math.atan2(dz, dx) + Math.PI / 40;
                        double radius = Math.max(1, distance);
                        double newX = this.getX() + radius * Math.cos(angle);
                        double newZ = this.getZ() + radius * Math.sin(angle);
                        double motionY1 = 0.1 + (blockHeight - entity.getY() + this.getY()) / blockHeight * 0.3; // Reduced from 0.2 and 0.5
                        double motionY = (Math.min(1 ,0.1 + (blockHeight - entity.getY() + this.getY()) / blockHeight * 0.3)); // Reduced from 0.2 and 0.5
                        if (entity.getY() >= this.getY() + tornadoHeight / 2) {
                            entity.setDeltaMovement(newX - entity.getX(), motionY1, newZ - entity.getZ()); // Reduced from 0.8
                        }
                        if (entity.getY() < this.getY() + tornadoHeight / 2) {
                            entity.setDeltaMovement(newX - entity.getX(), motionY, newZ - entity.getZ());
                        }
                    }
                    entity.hurtMarked = true;
                }
            }

            for (int x = -blockRadius; x <= blockRadius; x++) {
                for (int y = 0; y < blockHeight; y++) {
                    for (int z = -blockRadius; z <= blockRadius; z++) {
                        Vec3 blockPos = pos.add(x, y, z).subtract(0, 1, 0);
                        BlockState state = this.level().getBlockState(BlockPos.containing(blockPos));
                        if (!state.isAir() && !state.is(BlockTags.TALL_FLOWERS)) {
                            if (random.nextInt(1000) == 1) {
                                FallingBlockEntity fallingBlock = FallingBlockEntity.fall(this.level(), BlockPos.containing(blockPos), state);
                                fallingBlock.time = 1;
                                fallingBlock.setDeltaMovement(0, 2, 0);
                                this.level().setBlock(BlockPos.containing(blockPos), Blocks.AIR.defaultBlockState(), 3);
                                this.level().addFreshEntity(fallingBlock);
                            }
                        }
                    }
                }
            }
        }
        if (!this.level().isClientSide) {
            this.setDeltaMovement(tornadoX, tornadoY, tornadoZ);
            this.hurtMarked = true;
            if (this.tickCount >= 300) {
                this.discard();
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

    public float getTornadoYMov() {
        return this.entityData.get(DATA_TORNADO_Y);
    }

    public void setTornadoZMov(float zMov) {
        this.entityData.set(DATA_TORNADO_Z, zMov);
    }

    public float getTornadoZMov() {
        return this.entityData.get(DATA_TORNADO_Z);
    }
}
