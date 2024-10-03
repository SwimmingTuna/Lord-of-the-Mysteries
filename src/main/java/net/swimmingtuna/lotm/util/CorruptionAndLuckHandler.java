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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
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

public class CorruptionAndLuckHandler {

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
                if (player.tickCount % 317 == 0 && random.nextInt(225) <= lotmLuckValue) {
                    tag.putInt("luckIgnoreDamage", ignoreDamage + 1);
                    luck.setBaseValue(Math.max(0, lotmLuckValue - 13));
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
                if (sequence <= 6 && tag.getBoolean("monsterCalamityAttraction") && player.tickCount % 100 == 0) {
                    int calamityMeteor = tag.getInt("calamityMeteor");
                    int lightningStorm = tag.getInt("calamityLightningStorm");
                    int lightningBolt = tag.getInt("calamityLightningBolt");
                    int groundTremor = tag.getInt("calamityGroundTremor");
                    int gaze = tag.getInt("calamityGaze");
                    int undeadArmy = tag.getInt("calamityUndeadArmy");
                    int babyZombie = tag.getInt("calamityBabyZombie");
                    int windArmor = tag.getInt("calamityWindArmorRemoval");
                    int breeze = tag.getInt("calamityBreeze");
                    int wave = tag.getInt("calamityWave");
                    int explosion = tag.getInt("calamityExplosion");
                    int tornado = tag.getInt("calamityTornado");
                    Random randomInt = new Random();
                    if (calamityMeteor == 0 && randomInt.nextInt(1000) == 1) {
                        tag.putInt("calamityMeteor", (int) Math.max(15, Math.random() * 70));
                    }
                    if (tornado == 0 && randomInt.nextInt(750) == 1) {
                        tag.putInt("calamityTornado", (int) Math.max(15, Math.random() * 70));
                    }
                    if (explosion == 0 && randomInt.nextInt(500) == 1) {
                        tag.putInt("calamityExplosion", (int) Math.max(10, Math.random() * 60));
                    }
                    if (wave == 0 && randomInt.nextInt(250) == 1) {
                        tag.putInt("calamityWave", (int) Math.max(10, Math.random() * 25));
                    }
                    if (breeze == 0 && randomInt.nextInt(250) == 1) {
                        tag.putInt("calamityBreeze", (int) Math.max(10, Math.random() * 25));
                    }
                    if (windArmor == 0 && randomInt.nextInt(400) == 1) {
                        tag.putInt("calamityWindArmorRemoval", (int) Math.max(10, Math.random() * 40));
                    }
                    if (babyZombie == 0 && randomInt.nextInt(200) == 1) {
                        tag.putInt("calamityBabyZombie", (int) Math.max(5, Math.random() * 20));
                    }
                    if (undeadArmy == 0 && randomInt.nextInt(250) == 1) {
                        tag.putInt("calamityUndeadArmy", (int) Math.max(5, Math.random() * 20));
                    }
                    if (gaze == 0 && randomInt.nextInt(450) == 1) {
                        tag.putInt("calamityGaze", (int) Math.max(10, Math.random() * 50));
                    }
                    if (groundTremor == 0 && randomInt.nextInt(1000) == 1) {
                        tag.putInt("calamityGroundTremor", (int) Math.max(10, Math.random() * 40));
                    }
                    if (lightningBolt == 0 && randomInt.nextInt(150) == 1) {
                        tag.putInt("calamityLightningBolt", (int) Math.max(5, Math.random() * 10));
                    }
                    if (lightningStorm == 0 && randomInt.nextInt(600) == 1) {
                        tag.putInt("calamityLightningStorm", (int) Math.max(15, Math.random() * 50));
                    }
                }
                if (sequence <= 6 && player.tickCount % 20 == 0 && !player.level().isClientSide()) {
                    int calamityMeteor = tag.getInt("calamityMeteor");
                    int lightningStorm = tag.getInt("calamityLightningStorm");
                    int lightningBolt = tag.getInt("calamityLightningBolt");
                    int groundTremor = tag.getInt("calamityGroundTremor");
                    int gaze = tag.getInt("calamityGaze");
                    int undeadArmy = tag.getInt("calamityUndeadArmy");
                    int babyZombie = tag.getInt("calamityBabyZombie");
                    int windArmor = tag.getInt("calamityWindArmorRemoval");
                    int breeze = tag.getInt("calamityBreeze");
                    int wave = tag.getInt("calamityWave");
                    int explosion = tag.getInt("calamityExplosion");
                    int tornado = tag.getInt("calamityTornado");
                    if (calamityMeteor == 16) {
                        tag.putInt("calamityMeteorX", (int) player.getX());
                        tag.putInt("calamityMeteorY", (int) player.getY());
                        tag.putInt("calamityMeteorZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A meteor will start falling to your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (lightningStorm == 16) {
                        tag.putInt("calamityLightningStormX", (int) player.getX());
                        tag.putInt("calamityLightningStormY", (int) player.getY());
                        tag.putInt("calamityLightningStormZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A lightning storm will appear at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (tornado == 16) {
                        tag.putInt("calamityTornadoX", (int) player.getX());
                        tag.putInt("calamityTornadoY", (int) player.getY());
                        tag.putInt("calamityTornadoZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A tornado will appear at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (wave == 11) {
                        tag.putInt("calamityWaveX", (int) player.getX());
                        tag.putInt("calamityWaveY", (int) player.getY());
                        tag.putInt("calamityWaveZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A heat wave will pass through at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (breeze == 11) {
                        tag.putInt("calamityBreezeX", (int) player.getX());
                        tag.putInt("calamityBreezeY", (int) player.getY());
                        tag.putInt("calamityBreezeZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("An ice cold breeze will pass through at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (groundTremor == 11) {
                        tag.putInt("calamityGroundTremorX", (int) player.getX());
                        tag.putInt("calamityGroundTremorY", (int) player.getY());
                        tag.putInt("calamityGroundTremorZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("The ground will tremor at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + "), causing a pulse that damages all players and mobs in the ground and sending stone flying, in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (gaze == 11) {
                        tag.putInt("calamityGazeX", (int) player.getX());
                        tag.putInt("calamityGazeY", (int) player.getY());
                        tag.putInt("calamityGazeZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("An outer deity will focus it's gaze at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + "), causing corruption, in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (windArmor == 11) {
                        tag.putInt("calamityWindArmorRemovalX", (int) player.getX());
                        tag.putInt("calamityWindArmorRemovalY", (int) player.getY());
                        tag.putInt("calamityWindArmorRemovalZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A gust of wind will take armor off all players and mobs at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (explosion == 11) {
                        tag.putInt("calamityExplosionX", (int) player.getX());
                        tag.putInt("calamityExplosionY", (int) player.getY());
                        tag.putInt("calamityExplosionZ", (int) player.getZ());
                        tag.putInt("calamityExplosionResistance", 13);
                        player.sendSystemMessage(Component.literal("An accumulation of gas will explode at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (lightningBolt == 6) {
                        tag.putInt("calamityLightningBoltX", (int) player.getX());
                        tag.putInt("calamityLightningBoltY", (int) player.getY());
                        tag.putInt("calamityLightningBoltZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A lightning bolt will strike your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                    }
                    if (undeadArmy == 6) {
                        tag.putInt("calamityUndeadArmyX", (int) player.getX());
                        tag.putInt("calamityUndeadArmyY", (int) player.getY());
                        tag.putInt("calamityUndeadArmyZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("An undead army will appear at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                    }
                    if (babyZombie == 6) {
                        tag.putInt("calamityBabyZombieX", (int) player.getX());
                        tag.putInt("calamityBabyZombieY", (int) player.getY());
                        tag.putInt("calamityBabyZombieZ", (int) player.getZ());
                        player.sendSystemMessage(Component.literal("A strengthened baby zombie will appear at your current location (" + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                    }

                    if (calamityMeteor >= 1) {
                        tag.putInt("calamityMeteor", calamityMeteor - 1);
                    }
                    if (tornado >= 1) {
                        tag.putInt("calamityTornado", tornado - 1);
                    }
                    if (lightningStorm >= 1) {
                        tag.putInt("calamityLightningStorm", lightningStorm - 1);
                    }
                    if (wave >= 1) {
                        tag.putInt("calamityWave", wave - 1);
                    }
                    if (breeze >= 1) {
                        tag.putInt("calamityBreeze", breeze - 1);
                    }
                    if (gaze >= 1) {
                        tag.putInt("calamityGaze", gaze - 1);
                    }
                    if (windArmor >= 1) {
                        tag.putInt("calamityWindArmorRemoval", windArmor - 1);
                    }
                    if (groundTremor >= 1) {
                        tag.putInt("calamityGroundTremor", groundTremor - 1);
                    }
                    if (explosion >= 1) {
                        tag.putInt("calamityExplosion", explosion - 1);
                    }
                    if (babyZombie >= 1) {
                        tag.putInt("calamityBabyZombie", babyZombie - 1);
                    }
                    if (undeadArmy >= 1) {
                        player.sendSystemMessage(Component.literal(undeadArmy + " value"));
                        tag.putInt("calamityUndeadArmy", undeadArmy - 1);
                    }
                    if (lightningBolt >= 1) {
                        tag.putInt("calamityLightningBolt", lightningBolt - 1);
                    }
                    if (calamityMeteor == 1) {
                        int meteorX = tag.getInt("calamityMeteorX");
                        int meteorY = tag.getInt("calamityMeteorY");
                        int meteorZ = tag.getInt("calamityMeteorZ");
                        MeteorEntity.summonMeteorAtPosition(player, meteorX, meteorY, meteorZ);
                    }
                    if (tornado == 1) {
                        int tornadoX = tag.getInt("calamityTornadoX");
                        int tornadoY = tag.getInt("calamityTornadoY");
                        int tornadoZ = tag.getInt("calamityTornadoZ");
                        TornadoEntity tornadoEntity = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), player.level());
                        tornadoEntity.setTornadoHeight(80);
                        tornadoEntity.setTornadoRadius(30);
                        tornadoEntity.setOwner(player);
                        tornadoEntity.teleportTo(tornadoX, tornadoY, tornadoZ);
                        tornadoEntity.setTornadoRandom(true);
                        tornadoEntity.setDeltaMovement((Math.random() * 2) - 1, 0, (Math.random() * 2) - 1);
                    }
                    if (lightningStorm == 1) {
                        tag.putInt("calamityLightningStormSummon", 20);
                    }
                    if (wave == 1) {
                        int waveX = tag.getInt("calamityWaveX");
                        int waveY = tag.getInt("calamityWaveY");
                        int waveZ = tag.getInt("calamityWaveZ");
                        int subtractX = waveX - (int) player.getX();
                        int subtractY = waveY - (int) player.getY();
                        int subtractZ = waveZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 6) {
                                    entity.setSecondsOnFire(3);
                                    entity.hurt(entity.damageSources().lava(), 10);
                                } else entity.setSecondsOnFire(5);
                                entity.hurt(entity.damageSources().lava(), 15);
                            } else entity.setSecondsOnFire(5);
                            entity.hurt(entity.damageSources().lava(), 15);
                        }
                    }
                    if (breeze == 1) {
                        int breezeX = tag.getInt("calamityBreezeX");
                        int breezeY = tag.getInt("calamityBreezeY");
                        int breezeZ = tag.getInt("calamityBreezeZ");
                        int subtractX = breezeX - (int) player.getX();
                        int subtractY = breezeY - (int) player.getY();
                        int subtractZ = breezeZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(15))) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 6) {
                                    entity.setTicksFrozen(20);
                                    entity.hurt(entity.damageSources().freeze(), 5);
                                    entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 20, 1, false, false));
                                } else pPlayer.setTicksFrozen(40);
                                pPlayer.hurt(entity.damageSources().freeze(), 5);
                                pPlayer.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 40, 1, false, false));
                            } else entity.setTicksFrozen(40);
                            entity.hurt(entity.damageSources().freeze(), 5);
                            entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 40, 1, false, false));
                        }
                    }
                    if (gaze == 1) {
                        int gazeX = tag.getInt("calamityGazeX");
                        int gazeY = tag.getInt("calamityGazeY");
                        int gazeZ = tag.getInt("calamityGazeZ");
                        int subtractX = gazeX - (int) player.getX();
                        int subtractY = gazeY - (int) player.getY();
                        int subtractZ = gazeZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(30))) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                int addCorrutpion = 90 - (holder1.getCurrentSequence() * 10);
                                if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 6) {
                                    entity.getAttribute(ModAttributes.CORRUPTION.get()).setBaseValue(entity.getAttribute(ModAttributes.CORRUPTION.get()).getValue() + (addCorrutpion / 2));
                                } else
                                    entity.getAttribute(ModAttributes.CORRUPTION.get()).setBaseValue(entity.getAttribute(ModAttributes.CORRUPTION.get()).getValue() + addCorrutpion);
                            } else
                                entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 40, 1, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 3, false, false));
                        }
                    }
                    if (windArmor == 1) {
                        int windArmorX = tag.getInt("calamityWindArmorRemovalX");
                        int windArmorY = tag.getInt("calamityWindArmorRemovalY");
                        int windArmorZ = tag.getInt("calamityWindArmorRemovalZ");
                        int subtractX = windArmorX - (int) player.getX();
                        int subtractY = windArmorY - (int) player.getY();
                        int subtractZ = windArmorZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                            if (entity instanceof Player pPlayer) {
                                List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                                List<EquipmentSlot> equippedArmor = armorSlots.stream()
                                        .filter(slot -> !pPlayer.getItemBySlot(slot).isEmpty())
                                        .toList();
                                if (!equippedArmor.isEmpty()) {
                                    EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                                    ItemStack armorPiece = pPlayer.getItemBySlot(randomArmorSlot);
                                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                                        if (random.nextInt(2) == 1) {
                                            pPlayer.spawnAtLocation(armorPiece);
                                            pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                        }
                                    } else pPlayer.spawnAtLocation(armorPiece);
                                    pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                }
                            }
                        }
                    }
                    if (groundTremor == 1) {
                        int groundTremorX = tag.getInt("calamityGroundTremorX");
                        int groundTremorY = tag.getInt("calamityGroundTremorY");
                        int groundTremorZ = tag.getInt("calamityGroundTremorZ");
                        int subtractX = groundTremorX - (int) player.getX();
                        int subtractY = groundTremorY - (int) player.getY();
                        int subtractZ = groundTremorZ - (int) player.getZ();
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(50))) {
                            if (entity.onGround()) {
                                if (entity != player) {
                                    entity.hurt(entity.damageSources().generic(), 12);
                                } else entity.hurt(entity.damageSources().generic(), 6);
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
                    if (explosion == 2) {
                        tag.putInt("calamityExplosionOccurrence", 2);
                    }
                    if (babyZombie == 1) {
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
                            if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
                                if (entity != null) {
                                    zombie.setTarget(entity);
                                }
                            }
                            player.level().addFreshEntity(zombie);
                        }
                        if (undeadArmy == 1) {
                            player.getPersistentData().putInt("calamityUndeadArmyCounter", 20);
                        }
                        if (lightningBolt == 1) {
                            int lightningBoltX = tag.getInt("calamityLightningBoltX");
                            int lightningBoltY = tag.getInt("calamityLightningBoltY");
                            int lightningBoltZ = tag.getInt("calamityLightningBoltZ");
                            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
                            lightningEntity.setSpeed(6);
                            lightningEntity.setNoUp(true);
                            lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                            lightningEntity.teleportTo(lightningBoltX, lightningBoltY + 60, lightningBoltZ);
                            BlockPos pos = new BlockPos(lightningBoltX, lightningBoltY, lightningBoltZ);
                            lightningEntity.setTargetPos(pos.getCenter());
                            lightningEntity.setMaxLength(60);
                            player.level().addFreshEntity(lightningEntity);
                            tag.putInt("calamityLightningBoltMonsterResistance",5);
                        }
                    }
                }
                if (player.tickCount % 20 == 0) {
                    if (meteor >= 2) {
                        tag.putInt("luckMeteor", meteor - 1);
                    }
                    if (tag.getInt("calamityLightningBoltMonsterResistance") >= 1) {
                        tag.putInt("calamityLightningBoltMonsterResistance", tag.getInt("calamityLightningBoltMonsterResistance") - 1);
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
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                            tag.putInt("luckMeteorDamage", 6);
                        }
                    }
                    if (lotmLightning == 1) {
                        LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), serverLevel);
                        lightningEntity.setSpeed(6.0f);
                        lightningEntity.setTargetPos(player.getOnPos().getCenter());
                        lightningEntity.setMaxLength(15);
                        lightningEntity.teleportTo(player.getX() + (Math.random() * 60) - 30, player.getY() + 100, player.getZ() + (Math.random() * 60) - 30);
                        player.level().addFreshEntity(lightningEntity);
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                            tag.putInt("luckLightningLOTMDamage", 5);
                        }
                        tag.putInt("luckLightningLOTM", 0);
                    }
                    if (paralysis == 1) {
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                            player.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 5, 0, false, false));
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
                            if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                                if (random.nextInt(2) == 1) {
                                    player.spawnAtLocation(armorPiece);
                                    player.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                }
                            } else player.spawnAtLocation(armorPiece);
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
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                            Ravager ravager = new Ravager(EntityType.RAVAGER, player.level());
                            ravager.setLastHurtByPlayer(player);
                            player.level().addFreshEntity(ravager);
                        } else player.level().addFreshEntity(warden);
                        tag.putInt("luckWarden", 0);
                    }
                    if (mcLightning == 1 && player.getHealth() <= 5) {
                        tag.putInt("luckLightningMC", 0);
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                        lightningBolt.teleportTo(player.getX(), player.getY(), player.getZ());
                        lightningBolt.setDamage(10.0f);
                        player.level().addFreshEntity(lightningBolt);
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                            tag.putInt("luckLightningMCDamage", 2);
                        }
                    }
                    if (poison == 1 && !player.hasEffect(MobEffects.POISON)) {
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                            player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2, false, false));
                        } else player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2, false, false));
                        tag.putInt("luckPoison", 0);
                    }
                    if (tornadoInt == 1) {
                        TornadoEntity tornado = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), player.level());
                        tornado.setTornadoLifecount(120);
                        tornado.setTornadoPickup(true);
                        tornado.setTornadoRandom(true);
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                            tornado.setTornadoHeight(50);
                            tornado.setTornadoRadius(10);
                        } else tornado.setTornadoHeight(100);
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
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                            tag.putInt("luckStoneDamage", 5);
                        }
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
                    if (lotmLightningDamage >= 1) {
                        tag.putInt("luckLightningLOTMDamage", lotmLightningDamage - 1);
                    }
                    if (MCLightingDamage >= 1) {
                        tag.putInt("luckLightningMCDamage", MCLightingDamage - 1);
                    }
                    if (stoneDamage >= 1) {
                        tag.putInt("luckStoneDamage", stoneDamage - 1);
                    }
                    if (lotmLightningDamage >= 1) {
                        tag.putInt("luckMeteorDamage", meteorDamage - 1);
                    }
                }
            }
        }
    }
}