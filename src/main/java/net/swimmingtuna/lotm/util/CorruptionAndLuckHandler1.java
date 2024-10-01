package net.swimmingtuna.lotm.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class CorruptionAndLuckHandler1 {

    public static void corruptionAndLuckManagers(ServerLevel serverLevel, AttributeInstance misfortune, AttributeInstance corruption, Player player, AttributeInstance luck, BeyonderHolder holder, int sequence) {
        if (!player.level().isClientSide()) {
            if (corruption.getValue() >= 1 && player.tickCount % 200 == 0) {
                corruption.setBaseValue(corruption.getValue() - 1);
            }
            CompoundTag tag = player.getPersistentData();
            int meteor = tag.getInt("luckMeteor");
            int lotmLightning = tag.getInt("luckLightningLOTM");
            int paralysis = tag.getInt("luckParalysis");
            int unequipArmor = tag.getInt("luckUnequipArmor");
            int wardenSpawn = tag.getInt("luckWarden");
            int mcLightning = tag.getInt("luckLightningMC");
            int poison = tag.getInt("luckPoison");
            int tornadoInt = tag.getInt("luckTornado");
            int stone = tag.getInt("luckStone");
            int regeneration = tag.getInt("luckRegeneration");
            int diamondsDropped = tag.getInt("luckDiamonds");
            int windMovingProjectiles = tag.getInt("windMovingProjectilesCounter");
            Random random = new Random();
            double lotmLuckValue = luck.getValue();
            double lotmMisfortunateValue = misfortune.getValue();
            if (lotmMisfortunateValue >= 1) { //use different prime numbers for the tick count so misfortune is calculated at diff times
                if (player.tickCount % 397 == 0 && random.nextInt(300) <= lotmMisfortunateValue && meteor == 0) {
                    tag.putInt("luckMeteor", 40);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 40));
                }
                if (player.tickCount % 251 == 0 && random.nextInt(100) <= lotmMisfortunateValue && lotmLightning == 0) {
                    tag.putInt("luckLightningLOTM", 20);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 15));
                }
                if (player.tickCount % 151 == 0 && random.nextInt(50) <= lotmMisfortunateValue && paralysis == 0) {
                    tag.putInt("luckParalysis", 15);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 5));
                }
                if (player.tickCount % 149 == 0 && random.nextInt(75) <= lotmMisfortunateValue && unequipArmor == 0) {
                    tag.putInt("unequipArmor", 20);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 10));
                }
                if (player.tickCount % 349 == 0 && random.nextInt(320) <= lotmMisfortunateValue && wardenSpawn == 0) {
                    tag.putInt("luckWarden", 30);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 30));
                }
                if (player.tickCount % 41 == 0 && random.nextInt(50) <= lotmMisfortunateValue && mcLightning == 0) {
                    tag.putInt("luckLightningMC", 15);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 15));
                }
                if (player.tickCount % 199 == 0 && random.nextInt(150) <= lotmMisfortunateValue && !player.hasEffect(MobEffects.POISON)) {
                    tag.putInt("luckPoison", 15);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 8));
                }
                if (player.tickCount % 307 == 0 && random.nextInt(300) <= lotmMisfortunateValue && tornadoInt == 0) {
                    tag.putInt("luckTornado", 25);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 25));
                }
                if (player.tickCount % 127 == 0 && random.nextInt(100) <= lotmMisfortunateValue && stone == 0) {
                    tag.putInt("luckStone", 10);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 12));
                }
            }
            if (luck.getValue() >= 1) {
                if (player.tickCount % 29 == 0 && random.nextInt(100) <= lotmLuckValue && player.getHealth() <= 15 && !player.hasEffect(MobEffects.REGENERATION) && regeneration == 0) {
                    tag.putInt("luckRegeneration", 5);
                    luck.setBaseValue(Math.max(0, lotmLuckValue - 5));
                }
                if (player.tickCount % 907 == 0 && random.nextInt(300) <= lotmLuckValue && player.onGround() && diamondsDropped == 0) {
                    tag.putInt("luckDiamonds", 10);
                    luck.setBaseValue(Math.max(0, lotmLuckValue - 5));
                }
                if (player.tickCount % 11 == 0 && random.nextInt(100) <= lotmLuckValue) {
                    tag.putInt("windMovingProjectilesCounter", windMovingProjectiles + 1);
                    luck.setBaseValue(Math.max(0, lotmLuckValue - 10));
                }
            }
            if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
                if (player.tickCount % 500 == 0) {
                    SpamClass.sendMonsterMessage(player);
                }
                if (sequence == 7) {
                    if (player.tickCount % 400 == 0) {
                        luck.setBaseValue(Math.max(0, luck.getValue() + 1));
                    }
                }
                if (sequence == 6) {
                    if (player.tickCount % 360 == 0) {
                        luck.setBaseValue(Math.max(0, luck.getValue() + 1));
                    }
                }
                if (sequence == 5) {
                    if (player.tickCount % 310 == 0) {
                        luck.setBaseValue(Math.max(0, luck.getValue() + 1));
                    }
                }
                if (sequence == 4) {
                    if (player.tickCount % 220 == 0) {
                        luck.setBaseValue(Math.max(0, luck.getValue() + 1));
                    }
                }
                if (sequence == 3) {
                    if (player.tickCount % 140 == 0) {
                        luck.setBaseValue(Math.max(0, luck.getValue() + 1));
                    }
                }
                if (sequence == 2) {
                    if (player.tickCount % 100 == 0) {
                        luck.setBaseValue(Math.max(0, luck.getValue() + 1));
                    }
                }
                if (sequence == 1) {
                    if (player.tickCount % 70 == 0) {
                        luck.setBaseValue(Math.max(0, luck.getValue() + 1));
                    }
                }
                if (sequence == 0) {
                    if (player.tickCount % 40 == 0) {
                        luck.setBaseValue(Math.max(0, luck.getValue() + 1));
                    }
                }
            }
            if (player.tickCount % 20 == 0) {
                if (meteor >= 2) {
                    tag.putInt("luckMeteor", meteor - 1);
                }
                if (lotmLightning >= 2) {
                    tag.putInt("luckLightningLOTM", lotmLightning - 1);
                }
                if (paralysis >= 2) {
                    tag.putInt("luckParalysis", paralysis - 1);
                }
                if (unequipArmor >= 2) {
                    tag.putInt("luckUnequipArmor", unequipArmor - 1);
                }
                if (wardenSpawn >= 2) {
                    tag.putInt("luckWarden", wardenSpawn - 1);
                }
                if (mcLightning >= 2) {
                    tag.putInt("luckLightningMC", mcLightning - 1);
                }
                if (poison >= 2) {
                    tag.putInt("luckPoison", poison - 1);
                }
                if (tornadoInt >= 2) {
                    tag.putInt("luckTornado", tornadoInt - 1);
                }
                if (stone >= 2) {
                    tag.putInt("luckStone", stone - 1);
                }
                if (regeneration >= 2) {
                    tag.putInt("luckRegeneration", regeneration - 1);
                }
                if (diamondsDropped >= 2) {
                    tag.putInt("luckDiamonds", diamondsDropped - 1);
                }

                if (meteor == 1) {
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
                    tag.putInt("luckMeteor", 0);
                }
                if (lotmLightning == 1) {
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), serverLevel);
                    lightningEntity.setSpeed(6.0f);
                    lightningEntity.setTargetPos(player.getOnPos().getCenter());
                    lightningEntity.setMaxLength(15);
                    lightningEntity.teleportTo(player.getX() + (Math.random() * 60) - 30, player.getY() + 100, player.getZ() + (Math.random() * 60) - 30);
                    player.level().addFreshEntity(lightningEntity);
                    tag.putInt("luckLightningLOTM", 0);
                }
                if (paralysis == 1) {
                    player.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 10, 0, false, false));
                    player.sendSystemMessage(Component.literal("How unlucky, you tripped!").withStyle(ChatFormatting.BOLD));
                    tag.putInt("luckParalysis", 0);
                }
                if (unequipArmor == 1) {
                    List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                    List<EquipmentSlot> equippedArmor = armorSlots.stream()
                            .filter(slot -> !player.getItemBySlot(slot).isEmpty())
                            .toList();
                    if (!equippedArmor.isEmpty()) {
                        EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                        ItemStack armorPiece = player.getItemBySlot(randomArmorSlot);
                        player.spawnAtLocation(armorPiece);
                        player.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                    }
                    tag.putInt("luckUnequipArmor", 0);
                }
                if (wardenSpawn == 1 && player.onGround()) {
                    Warden warden = new Warden(EntityType.WARDEN, player.level());
                    warden.setAttackTarget(player);
                    warden.setLastHurtByPlayer(player);
                    AttributeInstance maxHP = warden.getAttribute(Attributes.MAX_HEALTH);
                    maxHP.setBaseValue(60);
                    player.level().addFreshEntity(warden);
                    tag.putInt("luckWarden", 0);
                }
                if (mcLightning == 1 && player.getHealth() <= 5) {
                    tag.putInt("luckLightningMC", 0);
                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                    lightningBolt.teleportTo(player.getX(), player.getY(), player.getZ());
                    lightningBolt.setDamage(10.0f);
                    player.level().addFreshEntity(lightningBolt);
                    tag.putInt("luckLightningMC", 0);
                }
                if (poison == 1 && !player.hasEffect(MobEffects.POISON)) {
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2, false, false));
                    tag.putInt("luckPoison", 0);
                }
                if (tornadoInt == 1) {
                    TornadoEntity tornado = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), player.level());
                    tornado.setTornadoLifecount(120);
                    tornado.setTornadoPickup(true);
                    tornado.setTornadoRandom(true);
                    tornado.setTornadoHeight(100);
                    tornado.setTornadoRadius(20);
                    player.level().addFreshEntity(tornado);
                    tag.putInt("luckTornado", 0);
                }
                if (stone == 1) {
                    StoneEntity stoneEntity = new StoneEntity(EntityInit.STONE_ENTITY.get(), player.level());
                    stoneEntity.teleportTo(player.getX(), player.getY() + 30, player.getZ());
                    stoneEntity.setDeltaMovement(0, -5, 0);
                    player.level().addFreshEntity(stoneEntity);
                    tag.putInt("luckStone", 0);
                }
                if (regeneration == 1 && !player.hasEffect(MobEffects.REGENERATION)) {
                    if (player.getEffect(MobEffects.REGENERATION).getAmplifier() <= 4) {
                        tag.putInt("luckRegeneration", 0);
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 4, false, false));
                    }
                }
                if (diamondsDropped == 1 && player.onGround()) {
                    player.addItem(Items.DIAMOND.getDefaultInstance());
                    player.addItem(Items.DIAMOND.getDefaultInstance());
                    player.addItem(Items.DIAMOND.getDefaultInstance());
                    player.displayClientMessage(Component.literal("How lucky! You found some diamonds on the ground"), true);
                    tag.putInt("luckDiamonds", 0);
                }
            }
        }
    }
}