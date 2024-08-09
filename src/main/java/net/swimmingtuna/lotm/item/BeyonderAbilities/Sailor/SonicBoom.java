package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.util.effect.ModEffects;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SonicBoom extends Item implements ReachChangeUUIDs {

    public SonicBoom(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
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
        }
        return super.use(level, pPlayer, hand);
    }

    public static void sonicBoom(Player pPlayer, int sequence) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.getPersistentData().putInt("sailorSonicBoom", 60);
            pPlayer.level().explode(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), 60 - (sequence * 8), Level.ExplosionInteraction.TNT);
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(40 - (sequence * 8)))) {
                if (entity != pPlayer) {
                    entity.getPersistentData().putInt("sailorSonicBoom", 5);
                    int duration = 100 - (sequence * 20);
                    int damage = 25 - (sequence * 5);
                    if (!(entity instanceof Player)) {
                        entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false)));
                        entity.hurt(entity.damageSources().magic(), damage);
                        pPlayer.sendSystemMessage(Component.literal("damage is " + damage));
                    } else if ((entity instanceof Player player)) {
                        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(player).orElse(null);
                        int pSequence = holder.getCurrentSequence();
                        int pDuration = duration - (50 - (pSequence * 5));
                        int pDamage = (int) (damage - (8 - (pSequence * 0.5)));
                        entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), pDuration, 1, false, false)));
                        entity.hurt(entity.damageSources().magic(), pDamage);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void sonicBoomCancel(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getPersistentData().getInt("sailorSonicBoom") >= 1) {
            DamageSource source = event.getSource();
            if ("explosion".equals(source.getMsgId()) || "explosion.player".equals(source.getMsgId())) {
                event.setCanceled(true);
                System.out.println("worked");
            }
        }
    }

    @SubscribeEvent
    public static void tickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity pPlayer = event.getEntity();
        if (!pPlayer.level().isClientSide()) {
            int x = pPlayer.getPersistentData().getInt("sailorSonicBoom");
            if (x >= 1) {
                pPlayer.getPersistentData().putInt("sailorSonicBoom", x - 1);
            }
        }
    }
}
