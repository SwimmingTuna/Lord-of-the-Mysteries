package net.swimmingtuna.lotm.entity;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
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
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class WindCushionEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(WindCushionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_LIFE_COUNT = SynchedEntityData.defineId(WindCushionEntity.class, EntityDataSerializers.INT);
    public final AnimationState animationState = new AnimationState();
    private int animationStateTimeout = 0;

    public WindCushionEntity(EntityType<? extends WindCushionEntity> entityType, Level level) {
        super(entityType, level);
    }

    public WindCushionEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.WIND_CUSHION_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
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
    protected void onHitEntity(EntityHitResult result) {
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(DATA_LIFE_COUNT,0);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }


    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public static void summonWindCushion(Player player) {
        if (!player.level().isClientSide()) {
            Vec3 lookVector = player.getLookAngle();
            WindCushionEntity windCushion = new WindCushionEntity(player.level(), player, 0, 0, 0);
            player.getPersistentData().putInt("windCushionXRot", (int) player.getXRot());
            player.getPersistentData().putInt("windCushionYRot", (int) player.getYRot());
            player.getPersistentData().putInt("windCushion", 1);
            windCushion.setDeltaMovement(player.getDeltaMovement().x * 2, player.getDeltaMovement().y * 1.1, player.getDeltaMovement().z * 2);
            windCushion.setOwner(player);
            windCushion.hurtMarked = true;
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(windCushion);
            scaleData.setTargetScale(2);
            scaleData.markForSync(true);
            player.level().addFreshEntity(windCushion);
            player.sendSystemMessage(Component.literal("rotation is" + windCushion.getXRot() + "and" + windCushion.getYRot()));
        }
    }

    @Override
    public void tick() {
        super.tick();
        ProjectileUtil.rotateTowardsMovement(this, 0);
        this.xRotO = getXRot();
        this.yRotO = this.getYRot();
        int currentLifeCount = this.entityData.get(DATA_LIFE_COUNT);
        if (currentLifeCount >= 20) {
            this.discard();
        } if (currentLifeCount >= 0) {
            this.entityData.set(DATA_LIFE_COUNT, currentLifeCount + 1);
        }
        if (this.level().isClientSide()) {
            if (animationStateTimeout == 0) {
                setupAnimationStates();
            }
        }
        if (!this.level().isClientSide()) {
            animationStateTimeout++;
        }
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    private void setupAnimationStates() {
        this.animationState.start(this.tickCount);
    }
}
