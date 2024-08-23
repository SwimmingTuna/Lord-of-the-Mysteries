package net.swimmingtuna.lotm.mixin;

import net.minecraft.world.level.Explosion;
import net.swimmingtuna.lotm.util.NoKnockbackExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionNoKnockback implements NoKnockbackExplosion {

    private boolean noKnockback = false;


    public void setNoKnockback(boolean value) {
        this.noKnockback = value;
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"), cancellable = true)
    private void onApplyKnockback(CallbackInfo ci) {
        if (this.noKnockback) {
            ci.cancel();
        }
    }
}