package net.swimmingtuna.lotm.entity.EntityGoals;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Cow;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;

public class PlayerMobGoals {

    public static class PlayerMobSpawnCowGoals extends Goal {
    private final PlayerMobEntity playerMob;
    private int cooldown = 0;

    public PlayerMobSpawnCowGoals(PlayerMobEntity playerMob) {
        this.playerMob = playerMob;
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        return "spectator".equals(playerMob.getPathway()) && playerMob.getSequence() == 0;
    }

    @Override
    public void start() {
        Cow cow = EntityType.COW.create(playerMob.level());
        if (cow != null) {
            cow.moveTo(playerMob.getX(), playerMob.getY(), playerMob.getZ(), playerMob.getYRot(), 0.0F);
            playerMob.level().addFreshEntity(cow);
        }
        cooldown = 200;
    }
    }
}
