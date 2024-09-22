package net.swimmingtuna.lotm.util.ProjectileDangerSense;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

/**
 * A preview for snowballs, eggs, ender pearls, experience bottles and potions
 */
public class ThrowablePreview extends Entity implements PreviewEntity<ThrowableItemProjectile> {
    public ThrowablePreview(Level level) {
        super(EntityType.SNOWBALL, level);
    }

    @Override
    public List<ThrowableItemProjectile> initializeEntities(Player player, ItemStack associatedItem, EquipmentSlot hand) {
        Item item = associatedItem.getItem();
        if (item instanceof SnowballItem) {
            Snowball snowball = new Snowball(level(), player);
            snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 0);
            return Collections.singletonList(snowball);
        } else if (item instanceof EggItem) {
            ThrownEgg egg = new ThrownEgg(level(), player);
            egg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 0);
            return Collections.singletonList(egg);
        } else if (item instanceof EnderpearlItem) {
            ThrownEnderpearl thrownEnderpearl = new ThrownEnderpearl(level(), player);
            thrownEnderpearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 0);
            return Collections.singletonList(thrownEnderpearl);
        } else if (item instanceof SplashPotionItem || item instanceof LingeringPotionItem) {
            ThrownPotion thrownPotion = new ThrownPotion(level(), player);
            thrownPotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20, 0.5f, 0);
            return Collections.singletonList(thrownPotion);
        } else if (item instanceof ExperienceBottleItem) {
            ThrownExperienceBottle experienceBottle = new ThrownExperienceBottle(level(), player);
            experienceBottle.shootFromRotation(player, player.getXRot(), player.getYRot(), -20, 0.7f, 0);
            return Collections.singletonList(experienceBottle);
        }
        return null;
    }

    @Override
    public void simulateShot(ThrowableItemProjectile simulatedEntity) {
        super.tick();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, entity -> !entity.isSpectator() && entity.isAlive() && entity.isPickable());
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) hitresult).getBlockPos();
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                discard();
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                discard();
            }
        }

        if (hitresult.getType() != HitResult.Type.MISS) {
            discard();
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        float f = 0.99f;
        if (this.isInWater()) {
            discard();
        }

        this.setDeltaMovement(vec3.scale(f));
        if (!this.isNoGravity()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, vec31.y - getGravity(simulatedEntity), vec31.z);
        }

        this.setPos(d2, d0, d1);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    private float getGravity(Entity simulated) {
        if (simulated instanceof ThrownExperienceBottle)
            return 0.07f;
        else if (simulated instanceof ThrownPotion)
            return 0.05f;
        return 0.03f;
    }

    @Override
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> fluid, double p_204033_) {
        return false;
    }
}
