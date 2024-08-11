package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.ExplosionUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;

import java.util.Random;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SonicBoom extends Item implements ReachChangeUUIDs {

    public SonicBoom(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 300) {
                pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 4 && sailorSequence.useSpirituality(300)) {
                    sonicBoom(pPlayer, holder.getCurrentSequence());

                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        return super.use(level, pPlayer, hand);
    }

    public static void sonicBoom(Player pPlayer, int sequence) {
        if (!pPlayer.level().isClientSide()) {
            Vec3 lookVec = pPlayer.getLookAngle().normalize().scale(100);
            pPlayer.hurtMarked = true;
            pPlayer.setDeltaMovement(lookVec.x(), lookVec.y(), lookVec.z());
            int radius = 20 - sequence;
            BlockPos playerPos = pPlayer.blockPosition();
            Level level = pPlayer.level();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = playerPos.offset(x, y, z);
                        if (pos.distSqr(playerPos) <= radius * radius) {
                            level.destroyBlock(pos, false);
                        }
                    }
                }
            }
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
}
