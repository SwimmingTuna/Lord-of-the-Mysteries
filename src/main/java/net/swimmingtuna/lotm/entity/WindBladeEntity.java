package net.swimmingtuna.lotm.entity;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

public class WindBladeEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(WindBladeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_LIFE_COUNT = SynchedEntityData.defineId(WindBladeEntity.class, EntityDataSerializers.INT);


    public WindBladeEntity(EntityType<? extends WindBladeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public WindBladeEntity(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityInit.WIND_BLADE_ENTITY.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }


    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }


    public boolean isOnFire() {
        return false;
    }


    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide()) {
            if (pResult.getEntity() instanceof LivingEntity entity) {
                if (this.getOwner() instanceof Player pPlayer) {
                    BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                        if (!entity.level().isClientSide() && !pPlayer.level().isClientSide()) {
                            int currentLifeCount = this.entityData.get(DATA_LIFE_COUNT);
                            int decrease = (tyrantSequence.getCurrentSequence() * 9) + 30;
                            currentLifeCount = currentLifeCount - decrease;
                            this.entityData.set(DATA_LIFE_COUNT, currentLifeCount - decrease);
                            if (currentLifeCount <= 0) {
                                this.discard();
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    private static final List<Block> EXCLUDED_BLOCKS = List.of(Blocks.BEDROCK, Blocks.OBSIDIAN);
    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide) {
            if (this.getOwner() instanceof Player pPlayer) {
                if (pResult != EXCLUDED_BLOCKS) {
                    BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                        if (!pPlayer.level().isClientSide()) {
                            int currentLifeCount = this.entityData.get(DATA_LIFE_COUNT);
                            int decrease = (tyrantSequence.getCurrentSequence() * 4) + 10;
                            currentLifeCount = currentLifeCount - decrease;
                            this.entityData.set(DATA_LIFE_COUNT, currentLifeCount - decrease);
                            if (currentLifeCount <= 0) {
                                this.discard();
                            }
                        }
                    });
                }
            } else {
                this.discard();
            }
        }
    }

    public boolean isPickable() {
        return false;
    }


    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
        this.entityData.define(DATA_LIFE_COUNT,400);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }


    protected boolean shouldBurn() {
        return false;
    }



    public static void summonEntityWithSpeed(Vec3 direction, Vec3 initialVelocity, Vec3 eyePosition, double x, double y, double z, Player pPlayer, float yRotation, float xRotation) {
        if (!pPlayer.level().isClientSide()) {
            WindBladeEntity windBladeEntity = new WindBladeEntity(pPlayer.level(), pPlayer, initialVelocity.x, initialVelocity.y, initialVelocity.z);
            windBladeEntity.setDeltaMovement(initialVelocity);
            Vec3 lightPosition = eyePosition.add(direction.scale(2.0));
            windBladeEntity.setPos(lightPosition);
            windBladeEntity.setOwner(pPlayer);
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (!pPlayer.level().isClientSide()) {
                    ScaleData scaleData = ScaleTypes.BASE.getScaleData(windBladeEntity);
                    scaleData.setTargetScale((7 - (tyrantSequence.getCurrentSequence())));
                    scaleData.markForSync(true);
                }
            });
            pPlayer.level().addFreshEntity(windBladeEntity);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 20 == 0) {
            if (this.tickCount >= 240) {
                this.discard();
            }
        }
    }
}
