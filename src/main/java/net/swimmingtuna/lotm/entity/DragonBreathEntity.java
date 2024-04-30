package net.swimmingtuna.lotm.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.util.EntityUtil.BeamEntity;
import net.swimmingtuna.lotm.util.RotationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class DragonBreathEntity extends BeamEntity {

    public static double RANGE = 64.0D;
    public static final int CHARGE = 20;

    public DragonBreathEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DragonBreathEntity(LivingEntity owner, float power) {
        super(EntityInit.DRAGON_BREATH_ENTITY.get(), owner, power);
    }

    @Override
    public int getFrames() {
        return 16;
    }

    @Override
    public float getScale() {
        return 1.0F;
    }

    @Override
    protected double getRange() {
        return RANGE;
    }

    @Override
    protected float getDamage() {
        return 20.0F;
    }

    @Override
    protected boolean breaksBlocks() {
        return true;
    }


    @Override
    public int getDuration() {
        return 4;
    }

    @Override
    public int getCharge() {
        return CHARGE;
    }

    @Override
    protected boolean causesFire() {
        return true;
    }

    @Override
    protected Vec3 calculateSpawnPos(LivingEntity owner) {
        return new Vec3(owner.getX(), owner.getY() + (owner.getBbHeight() * 0.75F) - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(RotationUtil.getTargetAdjustedLookAngle(owner));
    }

    public static void shootDragonBreath(Player pPlayer, int power, double x, double y, double z) {
        DragonBreathEntity dragonBreath = new DragonBreathEntity(pPlayer, power);
        dragonBreath.teleportTo(x,y+1,z);
        pPlayer.level().addFreshEntity(dragonBreath);
    }

}
