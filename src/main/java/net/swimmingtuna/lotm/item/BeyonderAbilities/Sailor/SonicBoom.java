package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.ExplosionUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SonicBoom extends SimpleAbilityItem {

    public SonicBoom(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 3, 600, 40);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        sonicBoom(player);
        return InteractionResult.SUCCESS;
    }

    public static void sonicBoom(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int sequence = holder.getCurrentSequence();
        Vec3 lookVec = player.getLookAngle().scale(100);
        player.hurtMarked = true;
        player.setDeltaMovement(lookVec.x(), lookVec.y(), lookVec.z());
        player.level().playSound(null, player.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 30.0f, 1.0f);
        ExplosionUtil.createNoKnockbackExplosion(player.level(), player, 40 - (sequence * 5), false);
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(30 - (sequence * 5)))) {
            if (entity == player) {
                continue;
            }
            int duration = 100 - (sequence * 20);
            int damage = 25 - (sequence * 5);
            if (!(entity instanceof Player)) {
                entity.addEffect(new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false));
                entity.hurt(entity.damageSources().generic(), damage);
            } else {

                int sequence2 = holder.getCurrentSequence();
                int duration2 = duration - (50 - (sequence2 * 5));
                int damage2 = (int) (damage - (8 - (sequence2 * 0.5)));
                entity.addEffect(new MobEffectInstance(ModEffects.AWE.get(), duration2, 1, false, false));
                entity.hurt(entity.damageSources().generic(), damage2);
            }
        }
        RandomSource random = RandomSource.create();
        for (int i = 0; i < 100; i++) {
            double x = player.getX() + (random.nextDouble() * 20) - 10;
            double y = player.getY() + (random.nextDouble() * 20) - 10;
            double z = player.getZ() + (random.nextDouble() * 20) - 10;
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 0, 0, 0, 0, 0);
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, compresses air and releases it in order to create a sonic boom, causing an explosion that propels you in the direction you're looking\n" +
                "Spirituality Used: 600\n" +
                "Cooldown: 1.5 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
