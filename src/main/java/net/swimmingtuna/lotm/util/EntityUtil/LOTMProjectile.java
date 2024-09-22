package net.swimmingtuna.lotm.util.EntityUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class LOTMProjectile extends Projectile {
    private static final int MAX_DURATION = 10 * 20;

    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(LOTMProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_POWER = SynchedEntityData.defineId(LOTMProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_DOMAIN = SynchedEntityData.defineId(LOTMProjectile.class, EntityDataSerializers.BOOLEAN);

    public LOTMProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public LOTMProjectile(EntityType<? extends Projectile> entityType, Level level, Entity shooter) {
        super(entityType, level);

        this.setOwner(shooter);
    }

    public LOTMProjectile(EntityType<? extends Projectile> entityType, Level level, Entity shooter, float power) {
        this(entityType, level, shooter);

        this.setPower(power);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 0);
        this.entityData.define(DATA_POWER, 0.0F);
        this.entityData.define(DATA_DOMAIN, false);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    protected void setPower(float power) {
        this.entityData.set(DATA_POWER, power);
    }

    public float getPower() {
        return this.entityData.get(DATA_POWER);
    }

    public boolean isDomain() {
        return this.entityData.get(DATA_DOMAIN);
    }

    public void setDomain(boolean domain) {
        this.entityData.set(DATA_DOMAIN, domain);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putInt("time", this.getTime());
        compound.putFloat("power", this.getPower());
        compound.putBoolean("domain", this.isDomain());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.setTime(compound.getInt("time"));
        this.setPower(compound.getFloat("power"));
        this.setDomain(compound.getBoolean("domain"));
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    protected boolean isProjectile() {
        return true;
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        Entity owner = this.getOwner();

        if (!this.level().isClientSide && (this.getTime() >= MAX_DURATION || (owner == null || owner.isRemoved() || !owner.isAlive()))) {
            this.discard();
            return;
        }
        super.tick();

        if (this.isProjectile()) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

            if (hit.getType() != HitResult.Type.MISS) {
                this.onHit(hit);
            }

            this.checkInsideBlocks();

            Vec3 movement = this.getDeltaMovement();
            double d0 = this.getX() + movement.x;
            double d1 = this.getY() + movement.y;
            double d2 = this.getZ() + movement.z;
            this.setPos(d0, d1, d2);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;

        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 *= 64.0D * getViewScale();
        return distance < d0 * d0;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(),
                this.getXRot(), this.getYRot(), this.getType(), i, this.getDeltaMovement(), 0.0D);
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);

        this.moveTo(packet.getX(), packet.getY(), packet.getZ(), packet.getYRot(), packet.getXRot());
        this.setDeltaMovement(packet.getXa(), packet.getYa(), packet.getZa());
    }
}
