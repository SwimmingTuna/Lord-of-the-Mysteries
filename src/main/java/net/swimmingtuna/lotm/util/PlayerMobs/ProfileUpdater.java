package net.swimmingtuna.lotm.util.PlayerMobs;

import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;

public class ProfileUpdater {

    private static final Queue<PlayerMobEntity> entities = new ArrayDeque<>();
    @Nullable
    private static Thread thread;

    public static void updateProfile(PlayerMobEntity entity) {
        entities.add(entity);

        if (thread == null || thread.getState() == Thread.State.TERMINATED) {
            thread = new Thread(() -> {
                while (!entities.isEmpty()) {
                    PlayerMobEntity mob = entities.remove();
                    if (mob != null) {
                        SkullBlockEntity.updateGameprofile(mob.getProfile(), mob::setProfile);
                    }
                }
            });
            thread.start();
        }
    }
}

