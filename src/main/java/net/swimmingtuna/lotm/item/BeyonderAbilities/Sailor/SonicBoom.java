package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
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
import net.swimmingtuna.lotm.util.ExplosionUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SonicBoom extends Item {

    public SonicBoom(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 600) {
                pPlayer.displayClientMessage(Component.literal("You need 600 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && sailorSequence.getCurrentSequence() <= 3 && sailorSequence.useSpirituality(600)) {
                    sonicBoom(pPlayer, holder.getCurrentSequence());

                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 30);
                }
            });
        return super.use(level, pPlayer, hand);
    }

    public static void sonicBoom(Player pPlayer, int sequence) {
        if (!pPlayer.level().isClientSide()) {
            Vec3 lookVec = pPlayer.getLookAngle().normalize().scale(100);
            pPlayer.hurtMarked = true;
            pPlayer.setDeltaMovement(lookVec.x(), lookVec.y(), lookVec.z());
            pPlayer.level().playSound(null, pPlayer.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 30.0f, 1.0f);
            ExplosionUtil.createNoKnockbackExplosion(pPlayer.level(), pPlayer, 40 - (sequence * 5), false);
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(30 - (sequence * 5)))) {
                if (entity != pPlayer) {
                    int duration = 100 - (sequence * 20);
                    int damage = 25 - (sequence * 5);
                    if (!(entity instanceof Player)) {
                        entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false)));
                        entity.hurt(entity.damageSources().generic(), damage);
                    } else if ((entity instanceof Player player)) {
                        BeyonderHolder holder1 = BeyonderHolderAttacher.getHolder(player).orElse(null);
                        int pSequence = holder1.getCurrentSequence();
                        int pDuration = duration - (50 - (pSequence * 5));
                        int pDamage = (int) (damage - (8 - (pSequence * 0.5)));
                        entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), pDuration, 1, false, false)));
                        entity.hurt(entity.damageSources().generic(), pDamage);
                    }
                }
            }
            RandomSource random = RandomSource.create();
            for (int i = 0; i < 100; i++) {
                double x = pPlayer.getX() + (random.nextDouble() * 20) - 10;
                double y = pPlayer.getY() + (random.nextDouble() * 20) - 10;
                double z = pPlayer.getZ() + (random.nextDouble() * 20) - 10;
                pPlayer.level().addParticle(ParticleTypes.EXPLOSION, x, y, z, 0, 0, 0);
            }
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, compresses air and releases it in order to create a sonic boom, causing an explosion that propels you in the direction you're looking\n" +
                    "Spirituality Used: 600\n" +
                    "Cooldown: 1.5 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}
