package net.swimmingtuna.lotm.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.NonVisibleS2C;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class MeteorTrailEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(MeteorTrailEntity.class, EntityDataSerializers.BOOLEAN);
    private Entity trailedEntity = null;

    public MeteorTrailEntity(EntityType<? extends MeteorTrailEntity> entityType, Level level, Entity trailedEntity) {
        super(entityType, level);
        this.trailedEntity = trailedEntity;
    }

    public MeteorTrailEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.METEOR_TRAIL_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
    }

    public MeteorTrailEntity(EntityType<MeteorTrailEntity> meteorTrailEntityEntityType, Level level) {
        super(meteorTrailEntityEntityType,level);
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
    protected void onHitEntity(EntityHitResult result) {

    }

    @Override
    protected void onHitBlock(BlockHitResult result) {

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
        if (this.level().isClientSide()) {
            this.level().addAlwaysVisibleParticle(
                    ParticleInit.METEOR_PARTICLE.get(), true,
                    this.getX(), this.getY(), this.getZ(),
                    0,0,0);
        }
        if (this.trailedEntity != null) {
            this.teleportTo(trailedEntity.getX(), trailedEntity.getY(), trailedEntity.getZ());
            ScaleData scaleDataTrail = ScaleTypes.BASE.getScaleData(trailedEntity);
            ScaleData scaleDataThis = ScaleTypes.BASE.getScaleData(this);
            scaleDataThis.setScale(scaleDataTrail.getScale());
            float x = scaleDataThis.getScale();
            double y = (Math.random() * x) - x / 2;
            if (!this.level().isClientSide()) {
                this.setDeltaMovement(trailedEntity.getDeltaMovement());
            }
        }
    }
}
