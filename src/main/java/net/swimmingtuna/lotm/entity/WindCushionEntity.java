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


    public WindCushionEntity(EntityType<? extends WindCushionEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public final AnimationState animationState = new AnimationState();
    private int animationStateTimeout = 0;

    public WindCushionEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.WIND_CUSHION_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }


    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }


    public boolean isOnFire() {
        return false;
    }


    protected void onHitEntity(EntityHitResult pResult) {
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        this.discard();
    }

    public boolean isPickable() {
        return false;
    }



    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(DATA_LIFE_COUNT,0);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }


    protected boolean shouldBurn() {
        return false;
    }

    public static void summonWindCushion(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            Vec3 lookVector = pPlayer.getLookAngle();
            WindCushionEntity windCushion = new WindCushionEntity(pPlayer.level(), pPlayer, 0,0,0);
                pPlayer.getPersistentData().putInt("windCushionXRot", (int) pPlayer.getXRot());
                pPlayer.getPersistentData().putInt("windCushionYRot", (int) pPlayer.getYRot());
                pPlayer.getPersistentData().putInt("windCushion", 1);
                windCushion.setDeltaMovement(pPlayer.getDeltaMovement().x * 2, pPlayer.getDeltaMovement().y * 1.1, pPlayer.getDeltaMovement().z * 2);
                windCushion.setOwner(pPlayer);
                windCushion.hurtMarked = true;
                ScaleData scaleData = ScaleTypes.BASE.getScaleData(windCushion);
                scaleData.setTargetScale(2);
                scaleData.markForSync(true);
                pPlayer.level().addFreshEntity(windCushion);
                pPlayer.sendSystemMessage(Component.literal("rotation is" + windCushion.getXRot() + "and" + windCushion.getYRot()));
        }
    }
    @Override
    public void tick() {
        super.tick();
        ProjectileUtil.rotateTowardsMovement(this,0.0F);
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
