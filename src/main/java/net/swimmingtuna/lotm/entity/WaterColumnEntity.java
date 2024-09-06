package net.swimmingtuna.lotm.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;

public class WaterColumnEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(WaterColumnEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_LIFE_COUNT = SynchedEntityData.defineId(WaterColumnEntity.class, EntityDataSerializers.INT);


    public WaterColumnEntity(EntityType<? extends WaterColumnEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public WaterColumnEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.WIND_BLADE_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }

    @Override
    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
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

        // Get current position and level
        Level level = this.level();
        int x = Mth.floor(this.getX());
        int y = Mth.floor(this.getY());
        int z = Mth.floor(this.getZ());
        BlockPos currentPos = new BlockPos(x, y, z);

        if (level.isClientSide()) {
            return;
        }
        // If the entity is below y-level 300, move it upwards
        if (y <= 300) {
            this.setDeltaMovement(0, 2, 0);
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }

        if (this.tickCount  % 20 == 0) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos abovePos = currentPos.offset(dx, 1, dz);
                    BlockState stateAbove = level.getBlockState(abovePos);
                    if (stateAbove.isAir()) {
                        level.setBlock(abovePos, Blocks.WATER.defaultBlockState(), 3);
                    }
                }
            }
        }
        if (this.tickCount >= 200) {
            this.discard();
        }
    }
}
