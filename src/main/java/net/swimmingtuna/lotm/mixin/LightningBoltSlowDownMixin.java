package net.swimmingtuna.lotm.mixin;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltSlowDownMixin extends Entity {

    @Shadow private int life;

    public LightningBoltSlowDownMixin(EntityType<?> entityType, Level level) {
        super(entityType,level);
    }
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void slowDownLightning(CallbackInfo ci) {
        if (this.tickCount < this.life - 1) {
            this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y * 0.5, this.getDeltaMovement().x());
            ci.cancel();
        }
    }
}
