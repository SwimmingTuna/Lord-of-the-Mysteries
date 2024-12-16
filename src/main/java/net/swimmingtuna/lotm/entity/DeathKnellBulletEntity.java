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
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class DeathKnellBulletEntity extends AbstractHurtingProjectile {

    private static final EntityDataAccessor<Boolean> WEAKNESS = SynchedEntityData.defineId(DeathKnellBulletEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LETHAL = SynchedEntityData.defineId(DeathKnellBulletEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLAUGHTERING = SynchedEntityData.defineId(DeathKnellBulletEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(DeathKnellBulletEntity.class, EntityDataSerializers.BOOLEAN);

    public DeathKnellBulletEntity(EntityType<? extends DeathKnellBulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DeathKnellBulletEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.AQUEOUS_LIGHT_ENTITY_PULL.get(), shooter, offsetX, offsetY, offsetZ, level);
    }



    @Override
    protected float getInertia() {
        return this.isDangerous() ? 0.99F : super.getInertia();
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
        if (this.level().isClientSide() || !(result.getEntity() instanceof LivingEntity entity)) {
            return;
        } else if (result.getEntity() instanceof LivingEntity livingEntity) {
            CompoundTag tag = this.getPersistentData();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {

        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(WEAKNESS, false);
        this.entityData.define(LETHAL, false);
        this.entityData.define(SLAUGHTERING , false);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public static void summonEntityWithSpeed(Vec3 direction, Vec3 initialVelocity, Vec3 eyePosition, double x, double y, double z, Player player) {
        if (!player.level().isClientSide()) {
            DeathKnellBulletEntity aqueousLightEntity = new DeathKnellBulletEntity(player.level(), player, initialVelocity.x, initialVelocity.y, initialVelocity.z);
            aqueousLightEntity.setDeltaMovement(initialVelocity);
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(aqueousLightEntity);
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            scaleData.setScale(8.0f - sequence);
            Vec3 lightPosition = eyePosition.add(direction.scale(2.0));
            aqueousLightEntity.setPos(lightPosition);
            aqueousLightEntity.setOwner(player);
            player.level().addFreshEntity(aqueousLightEntity);
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
