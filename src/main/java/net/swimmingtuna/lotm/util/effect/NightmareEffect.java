package net.swimmingtuna.lotm.util.effect;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

public class NightmareEffect extends MobEffect {
    private final Random random = new Random();
    private int nightmareApplyCounter = 0;

    public NightmareEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            nightmareApplyCounter++;
            if (nightmareApplyCounter >= 50) {
                double x = pLivingEntity.getX();
                double y = pLivingEntity.getY();
                double z = pLivingEntity.getZ();


                SoundEvent randomSound = MOB_SOUNDS[random.nextInt(MOB_SOUNDS.length)];
                Player pPlayer = (Player) pLivingEntity;
                pLivingEntity.level().playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), randomSound, SoundSource.HOSTILE.AMBIENT, 1.0F, 1.0F);
                pLivingEntity.level().playLocalSound(x,y,z, randomSound,  SoundSource.HOSTILE.AMBIENT, 1.0F, 1.0F, false);
                nightmareApplyCounter = 0;
            }
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    private static final SoundEvent[] MOB_SOUNDS = {
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.SPIDER_STEP,
            SoundEvents.GENERIC_EXPLODE,
            SoundEvents.ELDER_GUARDIAN_CURSE,
            SoundEvents.ENDERMAN_STARE,
            SoundEvents.ZOMBIE_DEATH,
            SoundEvents.GHAST_SCREAM,
            SoundEvents.EVOKER_FANGS_ATTACK,
            SoundEvents.EVOKER_PREPARE_ATTACK,
            SoundEvents.STRAY_AMBIENT,
            SoundEvents.ZOMBIE_VILLAGER_CONVERTED,
            SoundEvents.SPIDER_AMBIENT,
            SoundEvents.SPIDER_HURT,
            SoundEvents.CAT_PURR,
            SoundEvents.ENDER_DRAGON_GROWL,
            SoundEvents.ENDERMAN_AMBIENT,
            SoundEvents.SKELETON_HORSE_AMBIENT,
            SoundEvents.SILVERFISH_STEP,
            SoundEvents.SKELETON_STEP,
            SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR,
            SoundEvents.VEX_CHARGE,
            SoundEvents.ZOGLIN_ANGRY,
            SoundEvents.ZOGLIN_STEP,
            SoundEvents.GUARDIAN_AMBIENT,
            SoundEvents.ENDERMITE_STEP,
            SoundEvents.ELDER_GUARDIAN_AMBIENT,
            SoundEvents.PHANTOM_DEATH,
    };
}
