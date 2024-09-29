package net.swimmingtuna.lotm.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
public class CorruptionAndLuckHandler {

    public static void corruptionAndLuckManagers(ServerLevel serverLevel, AttributeInstance misfortune, AttributeInstance corruption, Player player, AttributeInstance luck, BeyonderHolder holder, int sequence) {
        if (corruption.getValue() >= 1 && player.tickCount % 200 == 0) {
            corruption.setBaseValue(corruption.getValue() - 1);
        }
        Random random = new Random();
        double lotmLuckValue = luck.getValue();
        double lotmMisfortunateValue = misfortune.getValue();
        if (lotmMisfortunateValue >= 1) {
            if (player.tickCount % 400 == 0 && random.nextInt(300) >= lotmMisfortunateValue) {
                MeteorEntity meteorEntity = new MeteorEntity(EntityInit.METEOR_ENTITY.get(), serverLevel);
                meteorEntity.teleportTo(player.getX(), player.getY() + 150, player.getZ());
                ScaleData scaleData = ScaleTypes.BASE.getScaleData(meteorEntity);
                scaleData.setScale(6.0f);
                double dx = player.getX() - meteorEntity.getX();
                double dy = player.getY() - meteorEntity.getY();
                double dz = player.getZ() - meteorEntity.getZ();
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                dx /= distance;
                dy /= distance;
                dz /= distance;
                double speed = 2.0;
                meteorEntity.setDeltaMovement(dx * speed, dy * speed, dz * speed);
                player.level().addFreshEntity(meteorEntity);
                misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 40));
            }
            if (player.tickCount % 250 == 0 && random.nextInt(100) >= lotmMisfortunateValue) {
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), serverLevel);
                lightningEntity.setSpeed(6.0f);
                lightningEntity.setTargetPos(player.getOnPos().getCenter());
                lightningEntity.setMaxLength(15);
                lightningEntity.teleportTo(player.getX() + (Math.random() * 60) - 30, player.getY() + 100, player.getZ() + (Math.random() * 60) - 30);
                player.level().addFreshEntity(lightningEntity);
                misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 15));
            }
            if (player.tickCount % 150 == 0 && random.nextInt(50) >= lotmMisfortunateValue) {
                player.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 10, 0, false, false));
                player.sendSystemMessage(Component.literal("How unlucky, you tripped!").withStyle(ChatFormatting.BOLD));
                misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 5));
            }
            if (player.tickCount % 150 == 0 && random.nextInt(75) >= lotmMisfortunateValue) {
                List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                List<EquipmentSlot> equippedArmor = armorSlots.stream()
                        .filter(slot -> !player.getItemBySlot(slot).isEmpty())
                        .toList();
                if (!equippedArmor.isEmpty()) {
                    EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                    ItemStack armorPiece = player.getItemBySlot(randomArmorSlot);
                    player.spawnAtLocation(armorPiece);
                    player.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                    misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 10));
                }
            }
            if (player.tickCount % 350 == 0 && random.nextInt(320) >= lotmMisfortunateValue && player.onGround()) {
                Warden warden = new Warden(EntityType.WARDEN, player.level());
                warden.setAttackTarget(player);
                warden.setLastHurtByPlayer(player);
                player.level().addFreshEntity(warden);
                misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 30));
            }
            if (player.tickCount % 40 == 0 && random.nextInt(50) >= lotmMisfortunateValue && player.getHealth() <= 5) {
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                lightningBolt.teleportTo(player.getX(), player.getY(), player.getZ());
                lightningBolt.setDamage(10.0f);
                player.level().addFreshEntity(lightningBolt);
                misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 15));
            }
            if (player.tickCount % 200 == 0 && random.nextInt(150) >= lotmMisfortunateValue && !player.hasEffect(MobEffects.POISON)) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2, false,false));
                misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 8));
            }
            if (player.tickCount % 300 == 0 && random.nextInt(300) >= lotmMisfortunateValue) {
                TornadoEntity tornado = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), player.level());
                tornado.setTornadoLifecount(200);
                tornado.setTornadoPickup(true);
                tornado.setTornadoRandom(true);
                tornado.setTornadoHeight(100);
                tornado.setTornadoRadius(20);
                player.level().addFreshEntity(tornado);
                misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 25));
            }
            if (player.tickCount % 120 == 0 && random.nextInt(100) >= lotmMisfortunateValue) {
                StoneEntity stoneEntity = new StoneEntity(EntityInit.STONE_ENTITY.get(), player.level());
                stoneEntity.teleportTo(player.getX(), player.getY() + 30, player.getZ());
                stoneEntity.setDeltaMovement(0,-5,0);
                player.level().addFreshEntity(stoneEntity);
                misfortune.setBaseValue(Math.max(0,lotmMisfortunateValue - 12));
            }
        }
        if (luck.getValue() >= 1) {
            if (player.tickCount % 40 == 0 && random.nextInt(100) >= lotmLuckValue && player.getHealth() <= 10 && !player.hasEffect(MobEffects.REGENERATION)) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,40,4,false,false));
                luck.setBaseValue(Math.max(0,lotmLuckValue - 5));
            }
        }
        if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
            if (player.tickCount % 500 == 0) {
                SpamClass.sendMonsterMessage(player);
            }
            if (sequence == 7) {
                if (player.tickCount % 400 == 0) {
                    luck.setBaseValue(Math.max(0,luck.getValue() + 1));
                }
            }
            if (sequence == 6) {
                if (player.tickCount % 360 == 0) {
                    luck.setBaseValue(Math.max(0,luck.getValue() + 1));
                }
            }
            if (sequence == 5) {
                if (player.tickCount % 310 == 0) {
                    luck.setBaseValue(Math.max(0,luck.getValue() + 1));
                }
            }
            if (sequence == 4) {
                if (player.tickCount % 220 == 0) {
                    luck.setBaseValue(Math.max(0,luck.getValue() + 1));
                }
            }
            if (sequence == 3) {
                if (player.tickCount % 140 == 0) {
                    luck.setBaseValue(Math.max(0,luck.getValue() + 1));
                }
            }
            if (sequence == 2) {
                if (player.tickCount % 100 == 0) {
                    luck.setBaseValue(Math.max(0,luck.getValue() + 1));
                }
            }
            if (sequence == 1) {
                if (player.tickCount % 70 == 0) {
                    luck.setBaseValue(Math.max(0,luck.getValue() + 1));
                }
            }
            if (sequence == 0) {
                if (player.tickCount % 40 == 0) {
                    luck.setBaseValue(Math.max(0,luck.getValue() + 1));
                }
            }
        }
    }
}