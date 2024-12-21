package net.swimmingtuna.lotm.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.util.EntityUtil.BeamEntity;
import net.swimmingtuna.lotm.util.RotationUtil;

public class DragonBreathEntity extends BeamEntity {

    public static final double RANGE = 64.0D;
    public static final int CHARGE = 20;

    public DragonBreathEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
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

    public static void shootDragonBreath(Player player, int power, double x, double y, double z) {
        DragonBreathEntity dragonBreath = new DragonBreathEntity(player, power);
        dragonBreath.teleportTo(x,y+1,z);
        dragonBreath.setDamage(power * 0.5f);
        player.level().addFreshEntity(dragonBreath);
    }

}
