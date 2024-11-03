package net.swimmingtuna.lotm.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.*;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Earthquake;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class PlayerMobCorruptionAndLuckHandler {

    public static void corruptionAndLuckManagers(ServerLevel serverLevel, AttributeInstance misfortune, AttributeInstance corruption, PlayerMobEntity player, AttributeInstance luck, int sequence) {
        if (!player.level().isClientSide()) {
            if (corruption.getValue() >= 1 && player.tickCount % 200 == 0) {
                corruption.setBaseValue(corruption.getValue() - 1);
            }
            Supplier<? extends BeyonderClass> pathway = player.getCurrentPathway();
            CompoundTag tag = player.getPersistentData();
            int meteor = tag.getInt("luckMeteor");
            int lotmLightning = tag.getInt("luckLightningLOTM");
            int paralysis = tag.getInt("luckParalysis");
            int unequipArmor = tag.getInt("luckUnequipArmor");
            int wardenSpawn = tag.getInt("luckWarden");
            int mcLightning = tag.getInt("luckLightningMC");
            int poison = tag.getInt("luckPoison");
            int attackerPoisoned = tag.getInt("luckAttackerPoisoned");
            int tornadoInt = tag.getInt("luckTornado");
            int stone = tag.getInt("luckStone");
            int luckIgnoreMobs = tag.getInt("luckIgnoreMobs");
            int regeneration = tag.getInt("luckRegeneration");
            int diamondsDropped = tag.getInt("luckDiamonds");
            int windMovingProjectiles = tag.getInt("windMovingProjectilesCounter");
            int lotmLightningDamage = tag.getInt("luckLightningLOTMDamage");
            int meteorDamage = tag.getInt("luckMeteorDamage");
            int MCLightingDamage = tag.getInt("luckLightningMCDamage");
            int stoneDamage = tag.getInt("luckStoneDamage");
            int ignoreAbilityUse = tag.getInt("luckIgnoreAbility");
            int doubleDamage = tag.getInt("luckDoubleDamage");
            int ignoreDamage = tag.getInt("luckIgnoreDamage");
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
                if (player.tickCount % 701 == 0 && random.nextInt(250) <= lotmMisfortunateValue) {
                    tag.putInt("luckIgnoreAbility", ignoreAbilityUse + 1);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 15));
                }
                if (player.tickCount % 263 == 0 && random.nextInt(150) <= lotmMisfortunateValue) {
                    tag.putInt("luckDoubleDamage", doubleDamage + 1);
                    misfortune.setBaseValue(Math.max(0, lotmMisfortunateValue - 5));
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
                if (player.tickCount % 317 == 0 && random.nextInt(150) <= lotmLuckValue) {
                    tag.putInt("luckHalveDamage", tag.getInt("luckHalveDamage") + 1);
                    luck.setBaseValue(Math.max(0, lotmLuckValue - 7));
                }
                if (pathway == BeyonderClassInit.MONSTER && player.tickCount % 51 == 0 && random.nextInt(70) <= lotmLuckValue) {
                    tag.putInt("luckIgnoreMobs", luckIgnoreMobs + 1);
                    luck.setBaseValue(Math.max(0, lotmLuckValue - 3));
                }
                if (pathway == BeyonderClassInit.MONSTER && sequence <= 5 && player.tickCount % 383 == 0 && random.nextInt(200) <= lotmLuckValue) {
                    tag.putInt("luckAttackerPoisoned", attackerPoisoned + 1);
                    luck.setBaseValue(Math.max(0, lotmLuckValue - 15));
                }
                if (player.tickCount % 503 == 0 && random.nextInt(225) <= lotmLuckValue && pathway == BeyonderClassInit.MONSTER && sequence <= 5) {
                    tag.putInt("luckIgnoreDamage", ignoreDamage + 1);
                    luck.setBaseValue(Math.max(0, lotmLuckValue - 13));
                }
            }
            if (pathway == BeyonderClassInit.MONSTER) {
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


                if (sequence <= 6 && tag.getBoolean("monsterCalamityAttraction") && player.tickCount % 100 == 0) {
                    int calamityMeteor = tag.getInt("calamityMeteor");
                    int calamityLightningStorm = tag.getInt("calamityLightningStorm");
                    int calamityLightningBolt = tag.getInt("calamityLightningBolt");
                    int calamityGroundTremor = tag.getInt("calamityGroundTremor");
                    int calamityGaze = tag.getInt("calamityGaze");
                    int calamityUndeadArmy = tag.getInt("calamityUndeadArmy");
                    int calamityBabyZombie = tag.getInt("calamityBabyZombie");
                    int calamityWindArmorRemoval = tag.getInt("calamityWindArmorRemoval");
                    int calamityBreeze = tag.getInt("calamityBreeze");
                    int calamityWave = tag.getInt("calamityWave");
                    int calamityExplosion = tag.getInt("calamityExplosion");
                    int calamityTornado = tag.getInt("calamityTornado");
                    Random randomInt = new Random();
                    if (calamityMeteor == 0 && randomInt.nextInt(1000) == 1) {
                        tag.putInt("calamityMeteor", (int) Math.max(15, Math.random() * 70));
                    }
                    if (calamityTornado == 0 && randomInt.nextInt(750) == 1) {
                        tag.putInt("calamityTornado", (int) Math.max(15, Math.random() * 70));
                    }
                    if (calamityExplosion == 0 && randomInt.nextInt(500) == 1) {
                        tag.putInt("calamityExplosion", (int) Math.max(10, Math.random() * 60));
                    }
                    if (calamityWave == 0 && randomInt.nextInt(250) == 1) {
                        tag.putInt("calamityWave", (int) Math.max(10, Math.random() * 25));
                    }
                    if (calamityBreeze == 0 && randomInt.nextInt(250) == 1) {
                        tag.putInt("calamityBreeze", (int) Math.max(10, Math.random() * 25));
                    }
                    if (calamityWindArmorRemoval == 0 && randomInt.nextInt(400) == 1) {
                        tag.putInt("calamityWindArmorRemoval", (int) Math.max(10, Math.random() * 40));
                    }
                    if (calamityBabyZombie == 0 && randomInt.nextInt(200) == 1) {
                        tag.putInt("calamityBabyZombie", (int) Math.max(5, Math.random() * 20));
                    }
                    if (calamityUndeadArmy == 0 && randomInt.nextInt(250) == 1) {
                        tag.putInt("calamityUndeadArmy", (int) Math.max(5, Math.random() * 20));
                    }
                    if (calamityGaze == 0 && randomInt.nextInt(450) == 1) {
                        tag.putInt("calamityGaze", (int) Math.max(10, Math.random() * 50));
                    }
                    if (calamityGroundTremor == 0 && randomInt.nextInt(1000) == 1) {
                        tag.putInt("calamityGroundTremor", (int) Math.max(10, Math.random() * 40));
                    }
                    if (calamityLightningBolt == 0 && randomInt.nextInt(150) == 1) {
                        tag.putInt("calamityLightningBolt", (int) Math.max(5, Math.random() * 10));
                    }
                    if (calamityLightningStorm == 0 && randomInt.nextInt(600) == 1) {
                        tag.putInt("calamityLightningStorm", (int) Math.max(15, Math.random() * 50));
                    }
                }


                if (sequence <= 6 && player.tickCount % 20 == 0 && !player.level().isClientSide()) {
                    int calamityMeteor = tag.getInt("calamityMeteor");
                    int calamityLightningStorm = tag.getInt("calamityLightningStorm");
                    int calamityLightningBolt = tag.getInt("calamityLightningBolt");
                    int calamityGroundTremor = tag.getInt("calamityGroundTremor");
                    int calamityGaze = tag.getInt("calamityGaze");
                    int calamityUndeadArmy = tag.getInt("calamityUndeadArmy");
                    int calamityBabyZombie = tag.getInt("calamityBabyZombie");
                    int calamityWindArmorRemoval = tag.getInt("calamityWindArmorRemoval");
                    int calamityBreeze = tag.getInt("calamityBreeze");
                    int calamityWave = tag.getInt("calamityWave");
                    int calamityExplosion = tag.getInt("calamityExplosion");
                    int calamityTornado = tag.getInt("calamityTornado");
                    if (calamityMeteor == 16) {
                        tag.putInt("calamityMeteorX", (int) player.getX());
                        tag.putInt("calamityMeteorY", (int) player.getY());
                        tag.putInt("calamityMeteorZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A meteor will start falling to your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityLightningStorm == 16) {
                        tag.putInt("calamityLightningStormX", (int) player.getX());
                        tag.putInt("calamityLightningStormY", (int) player.getY());
                        tag.putInt("calamityLightningStormZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A lightning storm will appear at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityTornado == 16) {
                        tag.putInt("calamityTornadoX", (int) player.getX());
                        tag.putInt("calamityTornadoY", (int) player.getY());
                        tag.putInt("calamityTornadoZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A tornado will appear at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityWave == 11) {
                        tag.putInt("calamityWaveX", (int) player.getX());
                        tag.putInt("calamityWaveY", (int) player.getY());
                        tag.putInt("calamityWaveZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A heat wave will pass through at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityBreeze == 11) {
                        tag.putInt("calamityBreezeX", (int) player.getX());
                        tag.putInt("calamityBreezeY", (int) player.getY());
                        tag.putInt("calamityBreezeZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("An ice cold breeze will pass through at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityGroundTremor == 11) {
                        tag.putInt("calamityGroundTremorX", (int) player.getX());
                        tag.putInt("calamityGroundTremorY", (int) player.getY());
                        tag.putInt("calamityGroundTremorZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("The ground will tremor at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + "), causing a pulse that damages all players and mobs in the ground and sending stone flying, in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityGaze == 11) {
                        tag.putInt("calamityGazeX", (int) player.getX());
                        tag.putInt("calamityGazeY", (int) player.getY());
                        tag.putInt("calamityGazeZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("An outer deity will focus it's gaze at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + "), causing corruption, in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityWindArmorRemoval == 11) {
                        tag.putInt("calamityWindArmorRemovalX", (int) player.getX());
                        tag.putInt("calamityWindArmorRemovalY", (int) player.getY());
                        tag.putInt("calamityWindArmorRemovalZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A gust of wind will take armor off all players and mobs at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityExplosion == 11) {
                        tag.putInt("calamityExplosionX", (int) player.getX());
                        tag.putInt("calamityExplosionY", (int) player.getY());
                        tag.putInt("calamityExplosionZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("An accumulation of gas will explode at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityLightningBolt == 6) {
                        tag.putInt("calamityLightningBoltX", (int) player.getX());
                        tag.putInt("calamityLightningBoltY", (int) player.getY());
                        tag.putInt("calamityLightningBoltZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A lightning bolt will strike your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityUndeadArmy == 6) {
                        tag.putInt("calamityUndeadArmyX", (int) player.getX());
                        tag.putInt("calamityUndeadArmyY", (int) player.getY());
                        tag.putInt("calamityUndeadArmyZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("An undead army will appear at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityBabyZombie == 6) {
                        tag.putInt("calamityBabyZombieX", (int) player.getX());
                        tag.putInt("calamityBabyZombieY", (int) player.getY());
                        tag.putInt("calamityBabyZombieZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A strengthened baby zombie will appear at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                    }
                    if (calamityMeteor >= 1) {
                        tag.putInt("calamityMeteor", calamityMeteor - 1);
                    }
                    if (calamityTornado >= 1) {
                        tag.putInt("calamityTornado", calamityTornado - 1);
                    }
                    if (calamityLightningStorm >= 1) {
                        tag.putInt("calamityLightningStorm", calamityLightningStorm - 1);
                    }
                    if (calamityWave >= 1) {
                        tag.putInt("calamityWave", calamityWave - 1);
                    }
                    if (calamityBreeze >= 1) {
                        tag.putInt("calamityBreeze", calamityBreeze - 1);
                    }
                    if (calamityGaze >= 1) {
                        tag.putInt("calamityGaze", calamityGaze - 1);
                    }
                    if (calamityWindArmorRemoval >= 1) {
                        tag.putInt("calamityWindArmorRemoval", calamityWindArmorRemoval - 1);
                    }
                    if (calamityGroundTremor >= 1) {
                        tag.putInt("calamityGroundTremor", calamityGroundTremor - 1);
                    }
                    if (calamityExplosion >= 1) {
                        player.sendSystemMessage(Component.literal("explosion val is " + calamityExplosion));
                        tag.putInt("calamityExplosion", calamityExplosion - 1);
                    }
                    if (calamityBabyZombie >= 1) {
                        tag.putInt("calamityBabyZombie", calamityBabyZombie - 1);
                    }
                    if (calamityUndeadArmy >= 1) {
                        player.sendSystemMessage(Component.literal(calamityUndeadArmy + " value"));
                        tag.putInt("calamityUndeadArmy", calamityUndeadArmy - 1);
                    }
                    if (calamityLightningBolt >= 1) {
                        tag.putInt("calamityLightningBolt", calamityLightningBolt - 1);
                    }


                    if (calamityMeteor == 1) {
                        int meteorX = tag.getInt("calamityMeteorX");
                        int meteorY = tag.getInt("calamityMeteorY");
                        int meteorZ = tag.getInt("calamityMeteorZ");
                        int subtractX = meteorX - (int) player.getX();
                        int subtractY = meteorY - (int) player.getY();
                        int subtractZ = meteorZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(40))) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                    pPlayer.getPersistentData().putInt("calamityMeteorImmunity", 5);
                                }
                            }
                            if (entity == player) {
                                if (sequence <= 3) {
                                    player.getPersistentData().putInt("calamityMeteorImmunity",5);
                                }
                            }
                        }
                        tag.putInt("luckMeteorDamage", 6);
                        MeteorEntity.summonMeteorAtPosition(player, meteorX, meteorY, meteorZ);
                    }
                    if (calamityTornado == 1) {
                        int tornadoX = tag.getInt("calamityTornadoX");
                        int tornadoY = tag.getInt("calamityTornadoY");
                        int tornadoZ = tag.getInt("calamityTornadoZ");
                        int subtractX = tornadoX - (int) player.getX();
                        int subtractY = tornadoY - (int) player.getY();
                        int subtractZ = tornadoZ - (int) player.getZ();
                        TornadoEntity tornadoEntity = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), player.level());
                        tornadoEntity.setTornadoHeight(80);
                        tornadoEntity.setTornadoRadius(30);
                        tornadoEntity.setOwner(player);
                        tornadoEntity.setTornadoLifecount(100);
                        tornadoEntity.teleportTo(tornadoX, tornadoY, tornadoZ);
                        tornadoEntity.setTornadoRandom(true);
                        tornadoEntity.setDeltaMovement((Math.random() * 2) - 1, 0, (Math.random() * 2) - 1);
                        tag.putInt("luckTornadoResistance", 5);
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(40))) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                    pPlayer.getPersistentData().putInt("luckTornadoImmunity", 6);
                                }
                            }
                        }
                    }
                    if (calamityLightningStorm == 1) {
                        tag.putInt("luckLightningLOTMDamage", 5);
                        tag.putInt("calamityLightningStormSummon", 20);
                        if (sequence >= 3) {
                            tag.putInt("calamityLightningStormResistance", 23);
                        }
                    }
                    if (calamityWave == 1) {
                        int waveX = tag.getInt("calamityWaveX");
                        int waveY = tag.getInt("calamityWaveY");
                        int waveZ = tag.getInt("calamityWaveZ");
                        int subtractX = waveX - (int) player.getX();
                        int subtractY = waveY - (int) player.getY();
                        int subtractZ = waveZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                            if (entity != player) {
                                if (entity instanceof Player pPlayer) {
                                    BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                    if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                        return;
                                    } else pPlayer.hurt(pPlayer.damageSources().lava(), 12);
                                    pPlayer.setSecondsOnFire(6);
                                } else entity.hurt(entity.damageSources().lava(), 12);
                                entity.setSecondsOnFire(6);
                            } else if (sequence <= 3) {
                                return;
                            } else player.hurt(player.damageSources().lava(), 6);
                            player.setSecondsOnFire(3);
                        }
                    }
                    if (calamityBreeze == 1) {
                        int breezeX = tag.getInt("calamityBreezeX");
                        int breezeY = tag.getInt("calamityBreezeY");
                        int breezeZ = tag.getInt("calamityBreezeZ");
                        int subtractX = breezeX - (int) player.getX();
                        int subtractY = breezeY - (int) player.getY();
                        int subtractZ = breezeZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                            if (entity != player) {
                                if (entity instanceof Player pPlayer) {
                                    BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                    if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                        return;
                                    } else
                                        pPlayer.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60, 1, false, false));
                                    pPlayer.setTicksFrozen(60);
                                } else
                                    entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60, 1, false, false));
                                entity.setTicksFrozen(60);
                            } else if (sequence <= 3) {
                                return;
                            } else
                                player.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60, 1, false, false));
                            player.setTicksFrozen(60);
                        }
                    }
                    if (calamityGaze == 1) {
                        int gazeX = tag.getInt("calamityGazeX");
                        int gazeY = tag.getInt("calamityGazeY");
                        int gazeZ = tag.getInt("calamityGazeZ");
                        int subtractX = gazeX - (int) player.getX();
                        int subtractY = gazeY - (int) player.getY();
                        int subtractZ = gazeZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                            if (entity != player) {
                                if (entity instanceof Player pPlayer) {
                                    BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                    AttributeInstance pPlayerCorruption = pPlayer.getAttribute(ModAttributes.CORRUPTION.get());
                                    double corruptionAmount = pPlayerCorruption.getBaseValue();
                                    if (holder1.getCurrentSequence() <= 3) {
                                        return;
                                    } else pPlayerCorruption.setBaseValue(corruptionAmount + 45);
                                } else entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 3, false, false));
                                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3, false, false));
                            } else if (sequence <= 3) {
                                return;
                            } else corruption.setBaseValue(corruption.getBaseValue() + 15);
                        }
                    }
                    if (calamityWindArmorRemoval == 1) {
                        int windArmorX = tag.getInt("calamityWindArmorRemovalX");
                        int windArmorY = tag.getInt("calamityWindArmorRemovalY");
                        int windArmorZ = tag.getInt("calamityWindArmorRemovalZ");
                        int subtractX = windArmorX - (int) player.getX();
                        int subtractY = windArmorY - (int) player.getY();
                        int subtractZ = windArmorZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                                List<EquipmentSlot> equippedArmor = armorSlots.stream()
                                        .filter(slot -> !pPlayer.getItemBySlot(slot).isEmpty())
                                        .toList();
                                if (!equippedArmor.isEmpty()) {
                                    EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                                    ItemStack armorPiece = pPlayer.getItemBySlot(randomArmorSlot);
                                    if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                        return;
                                    } else if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 6 && holder1.getCurrentSequence() >= 4) {
                                        if (random.nextInt(2) == 1) {
                                            pPlayer.spawnAtLocation(armorPiece);
                                            pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                        }
                                    } else pPlayer.spawnAtLocation(armorPiece);
                                    pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                    if (sequence >= 3) {
                                        return;
                                    } else if (random.nextInt(2) == 1) {
                                        player.spawnAtLocation(armorPiece);
                                        player.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                    }
                                }
                            }
                        }
                    }
                    if (calamityGroundTremor == 1) {
                        int groundTremorX = tag.getInt("calamityGroundTremorX");
                        int groundTremorY = tag.getInt("calamityGroundTremorY");
                        int groundTremorZ = tag.getInt("calamityGroundTremorZ");
                        int subtractX = groundTremorX - (int) player.getX();
                        int subtractY = groundTremorY - (int) player.getY();
                        int subtractZ = groundTremorZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(50))) {
                            if (entity instanceof Player pPlayer && pPlayer.onGround()) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                    return;
                                } else if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() > 4) {
                                    entity.hurt(entity.damageSources().generic(), 12);
                                } else entity.hurt(entity.damageSources().generic(), 6);
                            } else if (entity == player) {
                                if (sequence <= 3) {
                                    return;
                                } else player.hurt(player.damageSources().generic(),6);
                            } else if (entity.onGround()) {
                                entity.hurt(entity.damageSources().generic(), 12);
                            }
                        }
                        AABB checkArea = player.getBoundingBox().inflate(50);
                        for (BlockPos blockPos : BlockPos.betweenClosedStream(checkArea).toList()) {
                            if (!player.level().getBlockState(blockPos).isAir() && Earthquake.isOnSurface(player.level(), blockPos)) {
                                if (random.nextInt(200) == 1) { // 50% chance to destroy a block
                                    player.level().destroyBlock(blockPos, false);
                                } else if (random.nextInt(200) == 2) { // 10% chance to spawn a stone entity
                                    StoneEntity stoneEntity = new StoneEntity(player.level(), player);
                                    ScaleData scaleData = ScaleTypes.BASE.getScaleData(stoneEntity);
                                    stoneEntity.teleportTo(blockPos.getX(), blockPos.getY() + 3, blockPos.getZ());
                                    stoneEntity.setDeltaMovement(0, 3 + Math.random() * 2, 0);
                                    stoneEntity.setStoneYRot((int) (Math.random() * 18));
                                    stoneEntity.setStoneXRot((int) (Math.random() * 18));
                                    scaleData.setScale((float) (1 + Math.random() * 2.0f));
                                    player.level().addFreshEntity(stoneEntity);
                                }
                            }
                        }
                    }
                    if (calamityExplosion == 2) {
                        tag.putInt("calamityExplosionOccurrence", 2);
                    }
                    if (calamityBabyZombie == 1) {
                        int babyZombieX = tag.getInt("calamityBabyZombieX");
                        int babyZombieY = tag.getInt("calamityBabyZombieY");
                        int babyZombieZ = tag.getInt("calamityBabyZombieZ");
                        int subtractX = babyZombieX - (int) player.getX();
                        int subtractY = babyZombieY - (int) player.getY();
                        int subtractZ = babyZombieZ - (int) player.getZ();
                        Zombie zombie = new Zombie(EntityType.ZOMBIE, player.level());
                        zombie.setBaby(true);
                        zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100000, 3, true, true));
                        zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100000, 3, false, false));
                        zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100000, 2, false, false));
                        zombie.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                        zombie.teleportTo(babyZombieX, babyZombieY, babyZombieZ);
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(10))) {
                            if (sequence <= 3) {
                                if (entity != null) {
                                    zombie.setTarget(entity);
                                }
                            }
                            player.level().addFreshEntity(zombie);
                        }
                    }
                    if (calamityUndeadArmy == 1) {
                        player.getPersistentData().putInt("calamityUndeadArmyCounter", 20);
                    }
                    if (calamityLightningBolt == 1) {
                        int lightningBoltX = tag.getInt("calamityLightningBoltX");
                        int lightningBoltY = tag.getInt("calamityLightningBoltY");
                        int lightningBoltZ = tag.getInt("calamityLightningBoltZ");
                        int subtractX = lightningBoltX - (int) player.getX();
                        int subtractY = lightningBoltY - (int) player.getY();
                        int subtractZ = lightningBoltZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                                    pPlayer.getPersistentData().putInt("calamityLightningBoltImmunity", 10);
                                }
                            } else if (entity == player) {
                                if (sequence <= 3) {
                                    player.getPersistentData().putInt("calamityLightningBoltImmunity", 10);
                                }
                            }
                        }
                        LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
                        lightningEntity.setSpeed(6);
                        lightningEntity.setNoUp(true);
                        lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                        lightningEntity.teleportTo(lightningBoltX, lightningBoltY + 60, lightningBoltZ);
                        BlockPos pos = new BlockPos(lightningBoltX, lightningBoltY, lightningBoltZ);
                        lightningEntity.setTargetPos(pos.getCenter());
                        lightningEntity.setMaxLength(60);
                        player.level().addFreshEntity(lightningEntity);
                        tag.putInt("calamityLightningBoltMonsterResistance", 5);
                    }
                }
            }


            //LUCK EVENTS
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
                    if (pathway == BeyonderClassInit.MONSTER && sequence <= 6) {
                        tag.putInt("luckMeteorDamage", 6);
                    }
                    for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50))) {
                        if (entity instanceof Player pPlayer) {
                            BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                            if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                tag.putInt("calamityMeteorImmunity", 6);
                            }
                        }

                    }
                }
                if (lotmLightning == 1) {
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), serverLevel);
                    lightningEntity.setSpeed(6.0f);
                    lightningEntity.setTargetPos(player.getOnPos().getCenter());
                    lightningEntity.setMaxLength(15);
                    lightningEntity.teleportTo(player.getX() + (Math.random() * 60) - 30, player.getY() + 100, player.getZ() + (Math.random() * 60) - 30);
                    player.level().addFreshEntity(lightningEntity);
                    if (pathway == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckLightningLOTMDamage", 5);
                    }
                    for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20))) {
                        if (entity instanceof Player pPlayer) {
                            BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                            if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                tag.putInt("calamityLOTMLightningImmunity", 6);
                            }
                        }
                        if (entity == player) {
                            if (sequence <= 3) {
                                player.getPersistentData().putInt("calamityLOTMLightningImmunity",6);
                            }
                        }
                    }
                    tag.putInt("luckLightningLOTM", 0);
                }
                if (paralysis == 1) {
                    if (pathway == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        player.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 5, 0, false, false));
                        player.sendSystemMessage(Component.literal("How unlucky, you tripped!").withStyle(ChatFormatting.BOLD));
                    } else if (pathway == BeyonderClassInit.MONSTER && sequence <= 3) {
                        return;
                    } else player.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 10, 0, false, false));
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
                        if (pathway == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                            if (random.nextInt(2) == 1) {
                                player.spawnAtLocation(armorPiece);
                                player.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                            }
                        } else if (pathway == BeyonderClassInit.MONSTER && sequence <= 3) {
                            return;
                        } else player.spawnAtLocation(armorPiece);
                        player.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                    }
                    tag.putInt("luckUnequipArmor", 0);
                }
                if (wardenSpawn == 1 && player.onGround()) {
                    Warden warden = new Warden(EntityType.WARDEN, player.level());
                    warden.setAttackTarget(player);
                    warden.setTarget(player);
                    AttributeInstance maxHP = warden.getAttribute(Attributes.MAX_HEALTH);
                    maxHP.setBaseValue(60);
                    if (pathway == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        Ravager ravager = new Ravager(EntityType.RAVAGER, player.level());
                        ravager.setTarget(player);
                        player.level().addFreshEntity(ravager);
                    } else if (pathway == BeyonderClassInit.MONSTER && sequence <= 3) {
                        return;
                    } else player.level().addFreshEntity(warden);
                    tag.putInt("luckWarden", 0);
                }
                if (mcLightning == 1 && player.getHealth() <= 5) {
                    tag.putInt("luckLightningMC", 0);
                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                    lightningBolt.teleportTo(player.getX(), player.getY(), player.getZ());
                    lightningBolt.setDamage(10.0f);
                    player.level().addFreshEntity(lightningBolt);
                    if (pathway == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckLightningMCDamage", 2);
                    } else if (pathway == BeyonderClassInit.MONSTER && sequence <= 3) {
                        tag.putInt("luckMCLightningImmunity", 2);
                    }
                }
                if (poison == 1 && !player.hasEffect(MobEffects.POISON)) {
                    if (pathway == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2, false, false));
                    } else if (pathway == BeyonderClassInit.MONSTER && sequence <= 3) {
                        return;
                    } else player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2, false, false));
                    tag.putInt("luckPoison", 0);
                }
                if (tornadoInt == 1) {
                    TornadoEntity tornado = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), player.level());
                    tornado.setTornadoLifecount(120);
                    tornado.setTornadoPickup(true);
                    tornado.setTornadoRandom(true);
                    if (pathway == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckTornadoResistance", 6);
                    } else if (pathway == BeyonderClassInit.MONSTER && sequence <= 3) {
                        tag.putInt("luckTornadoImmunity", 6);
                    }
                    player.level().addFreshEntity(tornado);
                    tag.putInt("luckTornado", 0);
                }
                if (stone == 1) {
                    StoneEntity stoneEntity = new StoneEntity(EntityInit.STONE_ENTITY.get(), player.level());
                    stoneEntity.teleportTo(player.getX(), player.getY() + 30, player.getZ());
                    stoneEntity.setDeltaMovement(0, -5, 0);
                    player.level().addFreshEntity(stoneEntity);
                    tag.putInt("luckStone", 0);
                    if (pathway == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckStoneDamage", 5);
                    } else if (pathway == BeyonderClassInit.MONSTER && sequence <= 3) {
                        tag.putInt("luckStoneDamageImmunity", 5);
                    }
                }
                if (regeneration == 1) {
                    if (player.hasEffect(MobEffects.REGENERATION)) {
                        if (player.getEffect(MobEffects.REGENERATION).getAmplifier() <= 4) {
                            tag.putInt("luckRegeneration", 0);
                            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 4, false, false));
                        }
                    } else player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 4, false, false));
                }
                if (diamondsDropped == 1 && player.onGround()) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,40,5,false,false));
                    tag.putInt("luckDiamonds", 0);
                }
                int stoneImmunity = tag.getInt("luckStoneDamageImmunity");
                int tornadoResistance = tag.getInt("luckTornadoResistance");
                int tornadoImmunity = tag.getInt("luckTornadoImmunity");
                int mcLightningImmunity = tag.getInt("luckMCLightningImmunity");
                int lotmLightningImmunity = tag.getInt("calamityLOTMLightningImmunity");
                int meteorImmunity = tag.getInt("calamityMeteorImmunity");
                int lightningBoltResistance = tag.getInt("calamityLightningBoltMonsterResistance");
                int lightningStormImmunity = tag.getInt("calamityLightningStormImmunity");
                int lightningStormResistance = tag.getInt("calamityLightningStormResistance");
                if (lightningStormResistance >= 1) {
                    tag.putInt("calamityLightningStormResistance", lightningStormResistance - 1);
                }
                if (stoneImmunity >= 1) {
                    tag.putInt("luckStoneDamageImmunity", stoneImmunity - 1);
                }
                if (tornadoResistance >= 1) {
                    tag.putInt("luckTornadoResistance", tornadoResistance - 1);
                }
                if (tornadoImmunity >= 1) {
                    tag.putInt("luckTornadoImmunity", tornadoImmunity - 1);
                }
                if (mcLightningImmunity >= 1) {
                    tag.putInt("luckMCLightningImmunity", mcLightningImmunity - 1);
                }
                if (lotmLightningImmunity >= 1) {
                    tag.putInt("calamityLOTMLightningImmunity", lotmLightningImmunity - 1);
                }
                if (meteorImmunity >= 1) {
                    tag.putInt("calamityMeteorImmunity", meteorImmunity - 1);
                }
                if (lightningBoltResistance >= 1) {
                    tag.putInt("calamityLightningBoltMonsterResistance", lightningBoltResistance - 1);
                }
                if (lightningStormImmunity >= 1) {
                    tag.putInt("calamityLightningStormImmunity", lightningStormImmunity - 1);
                }
                if (lotmLightningDamage >= 1) {
                    tag.putInt("luckLightningLOTMDamage", lotmLightningDamage - 1);
                }
                if (MCLightingDamage >= 1) {
                    tag.putInt("luckLightningMCDamage", MCLightingDamage - 1);
                }
                if (stoneDamage >= 1) {
                    tag.putInt("luckStoneDamage", stoneDamage - 1);
                }
                if (meteorDamage >= 1) {
                    tag.putInt("luckMeteorDamage", meteorDamage - 1);
                }
            }
        }
    }
}