package net.swimmingtuna.lotm.events.ability_events;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.DomainOfDecay;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.DomainOfProvidence;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;

public class Monster {
    protected static void monsterDomainIntHandler(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            int maxRadius = 250 - (holder.getCurrentSequence() * 45);
            int radius = tag.getInt("monsterDomainRadius");
            if (player.tickCount % 500 == 0) {
                tag.putInt("monsterDomainMaxRadius", maxRadius);
            }
            if (player.isShiftKeyDown() && (player.getMainHandItem().getItem() instanceof DomainOfDecay || player.getMainHandItem().getItem() instanceof DomainOfProvidence)) {
                player.displayClientMessage(Component.literal("Current Domain Radius is " + radius).withStyle(BeyonderUtil.getStyle(player)),true);
                tag.putInt("monsterDomainRadius", radius + 5);
            } if (radius > maxRadius) {
                player.displayClientMessage(Component.literal("Current Domain Radius is 0").withStyle(BeyonderUtil.getStyle(player)),true);
                tag.putInt("monsterDomainRadius", 0);
            }
        }
    }

    protected static void monsterDangerSense(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //WIND MANIPULATION SENSE
        boolean monsterDangerSense = playerPersistentData.getBoolean("monsterDangerSense");
        if (!monsterDangerSense) {
            return;
        }
        if (!holder.useSpirituality(2)) return;
        double radius = 150 - (holder.getCurrentSequence() * 15);
        for (Player otherPlayer : player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(radius))) {
            if (otherPlayer == player) {
                continue;
            }
            Vec3 directionToPlayer = otherPlayer.position().subtract(player.position()).normalize();
            Vec3 lookAngle = player.getLookAngle();
            double horizontalAngle = Math.atan2(directionToPlayer.x, directionToPlayer.z) - Math.atan2(lookAngle.x, lookAngle.z);

            String message = AbilityEventsUtil.getString(otherPlayer, horizontalAngle, directionToPlayer);
            if (player.tickCount % 200 == 0) {
                player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE));
            }
        }
    }

    protected static void monsterLuckPoisonAttacker(Player pPlayer) {
        if (pPlayer.tickCount % 100 == 0) {
            if (pPlayer.getPersistentData().getInt("luckAttackerPoisoned") >= 1) {
                for (Player player : pPlayer.level().getEntitiesOfClass(Player.class, pPlayer.getBoundingBox().inflate(50))) {
                    if (player.getPersistentData().getInt("attackedMonster") >= 1) {
                        player.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60, 1, false, false));
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 3, false, false));
                        pPlayer.getPersistentData().putInt("luckAttackerPoisoned", pPlayer.getPersistentData().getInt("luckAttackerPoisoned") - 1);
                    }
                }
            }
        }
    }

    protected static void monsterLuckIgnoreMobs(Player pPlayer) {
        if (pPlayer.tickCount % 40 == 0) {
            if (pPlayer.getPersistentData().getInt("luckIgnoreMobs") >= 1) {
                for (Mob mob : pPlayer.level().getEntitiesOfClass(Mob.class, pPlayer.getBoundingBox().inflate(20))) {
                    if (mob.getTarget() == pPlayer) {
                        for (LivingEntity livingEntity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(50))) {
                            if (livingEntity != null) {
                                mob.setTarget(livingEntity);
                            } else
                                mob.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60, 1, false, false));
                        }
                        pPlayer.getPersistentData().putInt("luckIgnoreMobs", pPlayer.getPersistentData().getInt("luckIgnoreMobs") - 1);
                    }
                }
            }
        }
    }
}
