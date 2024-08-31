package net.swimmingtuna.lotm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.EntityInit;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Random;

public class StormSealEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(StormSealEntity.class, EntityDataSerializers.BOOLEAN);


    public StormSealEntity(EntityType<? extends StormSealEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public StormSealEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.ROAR_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }

    protected float getInertia() {
        return this.isDangerous() ? 1.0F : super.getInertia();
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide()) {
            Entity entity = pResult.getEntity();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);

            // Check if the entity is a player, projectile, or a mob with max health > 100
            if (entity instanceof Projectile projectile) {
                float explosionRadius = 3 * scaleData.getScale();
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius, Level.ExplosionInteraction.TNT);
                projectile.discard();
            }
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.getPersistentData().putInt("inStormSeal", 3600);
                livingEntity.getPersistentData().putInt("stormSealX", (int) livingEntity.getX());
                livingEntity.getPersistentData().putInt("stormSealY", (int) livingEntity.getY());
                livingEntity.getPersistentData().putInt("stormSealZ", (int) livingEntity.getZ());

            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide()) {
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            float explosionRadius = 5 * scaleData.getScale();
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius, Level.ExplosionInteraction.TNT);
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


    @Override
    public void tick() {
        super.tick();
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
        float radius = 3 * scaleData.getScale();
        if (!this.level().isClientSide()) {
            for (int i = 0; i < 36; i++) {
                double angle = i * 10 * Math.PI / 180;
                double x = this.getX() + radius * Math.cos(angle);
                double z = this.getZ() + radius * Math.sin(angle);
                this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, x, this.getY(), z, 0, 0, 0);
            }

            // Spawn 4 lightning bolts around the entity
            for (int i = 0; i < 4; i++) {
                double angle = i * 90 * Math.PI / 180;
                double x = this.getX() + radius * Math.cos(angle);
                double z = this.getZ() + radius * Math.sin(angle);
                BlockPos strikePos = new BlockPos((int) x, (int) this.getY(), (int) z);
                EntityType.LIGHTNING_BOLT.spawn((ServerLevel) this.level(), (ItemStack) null, null, strikePos,
                        net.minecraft.world.entity.MobSpawnType.TRIGGERED, true, true);
            }
        }
    }
}
