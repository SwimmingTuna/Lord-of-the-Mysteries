package net.swimmingtuna.lotm.HELPWITHEVENT;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ProjectileEvent extends Event implements IModBusEvent {



    public static class ProjectileControlEvent extends ProjectileEvent{
        private  HitResult ray;
        private Projectile projectile;
        private ProjectileImpactEvent.ImpactResult result = ProjectileImpactEvent.ImpactResult.DEFAULT;

        public ProjectileControlEvent(Projectile projectile, HitResult ray) {
            super();
            this.ray = ray;
            this.projectile = projectile;
        }
        public Projectile getProjectile() {
            return projectile;
        }

        public HitResult getProjectileResult() {
            return ray;
        }
        public LivingEntity getOwner() {
            return (LivingEntity) projectile.getOwner();
        }

        public void setImpactResult(@NotNull ProjectileImpactEvent.ImpactResult newResult) {
            this.result = Objects.requireNonNull(newResult, "ImpactResult cannot be null");
        }

        public ProjectileImpactEvent.ImpactResult getImpactResult() {
            return result;
        }

        public void setMovement(Projectile projectile, double deltaMovementX, double deltaMovementY, double deltaMovementZ) {
            projectile.setDeltaMovement(deltaMovementX, deltaMovementY, deltaMovementZ);
        }

        public LivingEntity getTarget(double maxValue, double minValue) {
            LivingEntity closestEntity = null;
            double closestDistance = Double.MAX_VALUE;
            Vec3 projectilePos = projectile.position();
            double radius = maxValue;
            List<LivingEntity> nearbyEntities = projectile.level().getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(radius));

            for (LivingEntity entity : nearbyEntities) {
                if (entity != getOwner()) {
                    double distance = entity.distanceToSqr(projectilePos);
                    if (distance < maxValue && distance > minValue && distance < closestDistance) {
                        closestDistance = distance;
                        closestEntity = entity;
                    }
                }
            }

            return closestEntity;
        }
    }
}
