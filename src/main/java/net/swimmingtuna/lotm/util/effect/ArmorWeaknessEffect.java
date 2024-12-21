package net.swimmingtuna.lotm.util.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;

public class ArmorWeaknessEffect extends MobEffect {
    public ArmorWeaknessEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }


    public ArmorWeaknessEffect() {
        super(MobEffectCategory.HARMFUL, 0xDC143C);
        this.addAttributeModifier(Attributes.ARMOR, "42b3787c-d536-47de-9d94-cc46c59e10c7", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "82f662cb-0a3a-4f7b-ab0d-b453f9e30708", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {

    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }
}

