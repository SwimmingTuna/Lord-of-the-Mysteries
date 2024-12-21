package net.swimmingtuna.lotm.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.DeathKnellBulletLocationS2C;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class DeathKnellBulletEntity extends AbstractHurtingProjectile {

    private static final EntityDataAccessor<Integer> DAMAGE = SynchedEntityData.defineId(DeathKnellBulletEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WEAKNESS = SynchedEntityData.defineId(DeathKnellBulletEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LETHAL = SynchedEntityData.defineId(DeathKnellBulletEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLAUGHTERING = SynchedEntityData.defineId(DeathKnellBulletEntity.class, EntityDataSerializers.BOOLEAN);

    public DeathKnellBulletEntity(EntityType<? extends DeathKnellBulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DeathKnellBulletEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.DEATH_KNELL_BULLET_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
    }


    @Override
    protected float getInertia() {
        return 0.99F;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide()) {
            Entity entity = pResult.getEntity();
            if (entity instanceof LivingEntity livingEntity) {
                if (getWeakness()) {
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.ARMOR_WEAKNESS.get(), 200, 1));
                } else if (getSlaughter()) {
                    if (livingEntity.getHealth() <= livingEntity.getMaxHealth() * 0.35f) {
                        if (this.getOwner() != null) {
                            livingEntity.hurt(BeyonderUtil.genericSource(this.getOwner()), getDamage() * 2);
                        } else {
                            livingEntity.hurt(livingEntity.damageSources().generic(), getDamage() * 2);
                        }
                    } else {
                        if (this.getOwner() != null) {
                            livingEntity.hurt(BeyonderUtil.genericSource(this.getOwner()), getDamage());
                        } else {
                            livingEntity.hurt(livingEntity.damageSources().generic(), getDamage());
                        }
                    }
                }
                if (!getSlaughter()) {
                    if (this.getOwner() != null) {
                        livingEntity.hurt(BeyonderUtil.genericSource(this.getOwner()), getDamage());
                    } else {
                        livingEntity.hurt(livingEntity.damageSources().generic(), getDamage());
                    }
                }
                this.discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (!this.level().isClientSide()) {
            BlockPos hitPos = pResult.getBlockPos();
            int radius = 0;
            if (getWeakness()) {
                radius = 1;
            } else if (getLethal()) {
                radius = 2;
            } else if (getSlaughter()) {
                radius = 3;
            }
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos targetPos = hitPos.offset(x, y, z);
                        BlockState state = this.level().getBlockState(targetPos);
                        if (state.getDestroySpeed(this.level(), targetPos) >= 0) {
                            this.level().destroyBlock(targetPos, true);
                        }
                    }
                }
            }
            for (LivingEntity livingEntity : level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius))) {
                if (this.getOwner() != null && this.getOwner() instanceof LivingEntity owner) {
                    livingEntity.hurt(BeyonderUtil.genericSource(owner), getDamage() / distanceTo(this));
                } else {
                    livingEntity.hurt(livingEntity.damageSources().generic(), getDamage() / distanceTo(this));
                }
            }
            this.discard();
        }
    }


    @Override
    public boolean isOnFire() {
        return false;
    }



    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        if (getWeakness()) {
            return new DustParticleOptions(new Vector3f(1.0F, 0.5F, 0.5F), 1.0F);
        } else if (getLethal()) {
            return new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F);
        } else if (getSlaughter()) {
            return new DustParticleOptions(new Vector3f(0.5F, 0.0F, 0.0F), 1.0F);
        }
        return ParticleInit.NULL_PARTICLE.get();
    }





    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {

        this.entityData.define(WEAKNESS, false);
        this.entityData.define(LETHAL, false);
        this.entityData.define(SLAUGHTERING, false);
        this.entityData.define(DAMAGE, 10);
    }




    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            Vec3 currentPos = this.position();

            // Send position to players
            for (ServerPlayer player : level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(100))) {
                LOTMNetworkHandler.sendToPlayer(new DeathKnellBulletLocationS2C(currentPos.x(), currentPos.y(), currentPos.z(), this.getId()), player);
            }
        }

        if (level() instanceof ServerLevel serverLevel) {
            Vec3 currentPos = this.position();
            Vec3 previousPos = this.getDeltaMovement().length() > 0 ? this.position().subtract(this.getDeltaMovement()) : currentPos;

            // Determine the particle color and size
            Vector3f color;
            int diameter;
            if (getWeakness()) {
                color = new Vector3f(1.0F, 0.5F, 0.5F);
                diameter = 1;
            } else if (getLethal()) {
                color = new Vector3f(1.0F, 0, 0);
                diameter = 2;
            } else if (getSlaughter()) {
                color = new Vector3f(0.5F, 0, 0);
                diameter = 3;
            } else {
                return;
            }

            DustParticleOptions particle = new DustParticleOptions(color, 1.0F);

            int steps = (int) Math.ceil(previousPos.distanceTo(currentPos));
            for (int i = 0; i <= steps; i++) {
                double t = i / (double) steps;
                double x = previousPos.x + (currentPos.x - previousPos.x) * t;
                double y = previousPos.y + (currentPos.y - previousPos.y) * t;
                double z = previousPos.z + (currentPos.z - previousPos.z) * t;

                // Spawn particles with diameter-dependent inflation
                serverLevel.sendParticles(particle, x, y, z, 0, diameter * 0.5, diameter * 0.5, diameter * 0.5, 0);
            }
        }
    }


    public boolean getWeakness() {
        return this.entityData.get(WEAKNESS);
    }

    public boolean getLethal() {
        return this.entityData.get(LETHAL);
    }

    public boolean getSlaughter() {
        return this.entityData.get(SLAUGHTERING);
    }

    public int getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setWeakness(boolean weakness) {
        this.entityData.set(WEAKNESS, weakness);
    }

    public void setLethal(boolean lethal) {
        this.entityData.set(LETHAL, lethal);
    }

    public void setSlaughter(boolean slaughter) {
        this.entityData.set(SLAUGHTERING, slaughter);
    }

    public void setDamage(int damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, pVelocity, pInaccuracy);
    }

    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * 0.017453292F) * Mth.cos(pX * 0.017453292F);
        float f1 = -Mth.sin((pX + pZ) * 0.017453292F);
        float f2 = Mth.cos(pY * 0.017453292F) * Mth.cos(pX * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, pVelocity, pInaccuracy);
        Vec3 vec3 = pShooter.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, pShooter.onGround() ? 0.0 : vec3.y, vec3.z));
    }
}
