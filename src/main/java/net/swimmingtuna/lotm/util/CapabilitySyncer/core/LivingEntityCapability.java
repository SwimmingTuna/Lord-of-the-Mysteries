package net.swimmingtuna.lotm.util.CapabilitySyncer.core;

import net.minecraft.world.entity.LivingEntity;

public abstract class LivingEntityCapability extends EntityCapability {
    protected final LivingEntity livingEntity;

    protected LivingEntityCapability(LivingEntity entity) {
        super(entity);
        this.livingEntity = entity;
    }
}
