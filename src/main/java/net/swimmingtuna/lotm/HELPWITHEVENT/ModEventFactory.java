package net.swimmingtuna.lotm.HELPWITHEVENT;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;

public class ModEventFactory {
    public static <T extends Event & IModBusEvent> void fireModEvent(T event) {
        ModLoader.get().postEvent(event);
    }

    public static void onSailorShootProjectile(Projectile projectile, HitResult ray) {
        ProjectileEvent.ProjectileControlEvent projectileEvent = new ProjectileEvent.ProjectileControlEvent(projectile,ray);
        MinecraftForge.EVENT_BUS.post(projectileEvent);
    }
}
