package net.swimmingtuna.lotm.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.*;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Earthquake;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import net.swimmingtuna.lotm.world.worlddata.WorldFortuneValue;
import net.swimmingtuna.lotm.world.worlddata.WorldMisfortuneData;
import org.joml.Vector3f;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CorruptionAndLuckHandler {

    public static void corruptionAndLuckManagers(ServerLevel serverLevel, LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            CompoundTag tag = livingEntity.getPersistentData();
            double corruption = tag.getDouble("corruption");
            double lotmLuckValue = tag.getDouble("luck");
            double lotmMisfortuneValue = tag.getDouble("misfortune");
            if (corruption >= 1 && livingEntity.tickCount % 200 == 0) {
                tag.putDouble("corruption", corruption - 1);
            }
            if (livingEntity.tickCount % 20 == 0) {
                if (corruption >= 60) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 2, false, false));
                }
                if (corruption >= 70) {
                    if (Math.random() <= 0.10 && livingEntity.tickCount % 100 == 0) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 2, false, false));
                    }
                    if (Math.random() <= 0.40 && livingEntity.tickCount % 60 == 0) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 2, false, false));
                    }
                }
                if (corruption >= 80 && livingEntity.tickCount % 200 == 0) {
                    if (Math.random() <= .50) {
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.FRENZY.get(), 40, 2, false, false));
                    } else {
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 40, 2, false, false));
                    }
                }
                if (corruption >= 90) {
                    if (Math.random() >= 0.15) {
                        if (livingEntity instanceof Player player) {
                            int sequence = BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence();
                            float maxHp = player.getMaxHealth();
                            if (sequence == 9 || sequence == 8) {
                                WitherSkeleton witherSkeleton = new WitherSkeleton(EntityType.WITHER_SKELETON, player.level());
                                witherSkeleton.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                witherSkeleton.setHealth(maxHp);
                                witherSkeleton.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                witherSkeleton.getPersistentData().putBoolean("corruptedEntityLow", true);
                                BeyonderUtil.setTargetToHighestHP(witherSkeleton, 30);
                                player.level().addFreshEntity(witherSkeleton);

                            } else if (sequence == 7 || sequence == 6 || sequence == 5) {
                                SkeletonHorse skeletonHorse = new SkeletonHorse(EntityType.SKELETON_HORSE, player.level());
                                skeletonHorse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                skeletonHorse.setHealth(maxHp);
                                skeletonHorse.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                skeletonHorse.getPersistentData().putBoolean("corruptedEntityMid", true);
                                BeyonderUtil.setTargetToHighestHP(skeletonHorse, 50);
                                Skeleton skeleton = new Skeleton(EntityType.SKELETON, player.level());
                                skeleton.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                skeleton.setHealth(maxHp);
                                skeleton.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                skeleton.getPersistentData().putBoolean("corruptedEntityMid", true);
                                BeyonderUtil.setTargetToHighestHP(skeleton, 50);
                                skeleton.startRiding(skeletonHorse, true);
                                player.level().addFreshEntity(skeletonHorse);
                                player.level().addFreshEntity(skeleton);
                            } else if (sequence == 4 || sequence == 3) {
                                Vex vex = new Vex(EntityType.VEX, player.level());
                                vex.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                vex.setHealth(maxHp);
                                vex.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                vex.getPersistentData().putBoolean("corruptedEntitySaint", true);
                                BeyonderUtil.setTargetToHighestHP(vex, 70);
                                player.level().addFreshEntity(vex);
                            } else if (sequence == 2 || sequence == 1) {
                                Phantom phantom = new Phantom(EntityType.PHANTOM, player.level());
                                phantom.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                phantom.setHealth(maxHp);
                                phantom.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                phantom.getPersistentData().putBoolean("corruptedEntityAngel", true);
                                BeyonderUtil.setTargetToHighestHP(phantom, 100);
                                player.level().addFreshEntity(phantom);

                            } else if (sequence == 0) {
                                WitherBoss wither = new WitherBoss(EntityType.WITHER, player.level());
                                wither.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                wither.setHealth(maxHp);
                                wither.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                wither.getPersistentData().putBoolean("corruptedEntityDeity", true);
                                BeyonderUtil.setTargetToHighestHP(wither, 150);
                                player.level().addFreshEntity(wither);
                            }
                        } else if (livingEntity instanceof PlayerMobEntity player) {
                            int sequence = player.getCurrentSequence();
                            int maxHp = (int) player.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                            if (sequence == 9 || sequence == 8) {
                                WitherSkeleton witherSkeleton = new WitherSkeleton(EntityType.WITHER_SKELETON, player.level());
                                witherSkeleton.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                witherSkeleton.setHealth(maxHp);
                                witherSkeleton.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                witherSkeleton.getPersistentData().putBoolean("corruptedEntityLow", true);
                                BeyonderUtil.setTargetToHighestHP(witherSkeleton, 30);
                                player.level().addFreshEntity(witherSkeleton);

                            } else if (sequence == 7 || sequence == 6 || sequence == 5) {
                                SkeletonHorse skeletonHorse = new SkeletonHorse(EntityType.SKELETON_HORSE, player.level());
                                skeletonHorse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                skeletonHorse.setHealth(maxHp);
                                skeletonHorse.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                skeletonHorse.getPersistentData().putBoolean("corruptedEntityMid", true);
                                BeyonderUtil.setTargetToHighestHP(skeletonHorse, 50);
                                Skeleton skeleton = new Skeleton(EntityType.SKELETON, player.level());
                                skeleton.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                skeleton.setHealth(maxHp);
                                skeleton.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                skeleton.getPersistentData().putBoolean("corruptedEntityMid", true);
                                BeyonderUtil.setTargetToHighestHP(skeleton, 50);
                                skeleton.startRiding(skeletonHorse, true);
                                player.level().addFreshEntity(skeletonHorse);
                                player.level().addFreshEntity(skeleton);
                            } else if (sequence == 4 || sequence == 3) {
                                Vex vex = new Vex(EntityType.VEX, player.level());
                                vex.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                vex.setHealth(maxHp);
                                vex.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                vex.getPersistentData().putBoolean("corruptedEntitySaint", true);
                                BeyonderUtil.setTargetToHighestHP(vex, 70);
                                player.level().addFreshEntity(vex);
                            } else if (sequence == 2 || sequence == 1) {
                                Phantom phantom = new Phantom(EntityType.PHANTOM, player.level());
                                phantom.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                phantom.setHealth(maxHp);
                                phantom.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                phantom.getPersistentData().putBoolean("corruptedEntityAngel", true);
                                BeyonderUtil.setTargetToHighestHP(phantom, 100);
                                player.level().addFreshEntity(phantom);

                            } else if (sequence == 0) {
                                WitherBoss wither = new WitherBoss(EntityType.WITHER, player.level());
                                wither.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                wither.setHealth(maxHp);
                                wither.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                wither.getPersistentData().putBoolean("corruptedEntityDeity", true);
                                BeyonderUtil.setTargetToHighestHP(wither, 150);
                                player.level().addFreshEntity(wither);
                            }
                        } else {
                            int sequence = -1;
                            float maxHp = livingEntity.getMaxHealth();
                            if (maxHp >= 20) {
                                sequence = 9;
                            } else if (maxHp >= 35) {
                                sequence = 8;
                            }
                            else if (maxHp >= 70) {
                                sequence = 7;
                            }
                            else if (maxHp >= 120) {
                                sequence = 6;
                            }
                            else if (maxHp >= 190) {
                                sequence = 5;
                            }
                            else if (maxHp >= 300) {
                                sequence = 4;
                            }
                            else if (maxHp >= 450) {
                                sequence = 3;
                            }
                            else if (maxHp >= 700) {
                                sequence = 2;
                            }
                            else if (maxHp >= 999) {
                                sequence = 1;
                            }
                            else {
                                sequence = 0;
                            }
                            if (sequence == 9 || sequence == 8) {
                                WitherSkeleton witherSkeleton = new WitherSkeleton(EntityType.WITHER_SKELETON, livingEntity.level());
                                witherSkeleton.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                witherSkeleton.setHealth(maxHp);
                                witherSkeleton.teleportTo(livingEntity.getX(), livingEntity.getY() + 1, livingEntity.getZ());
                                witherSkeleton.getPersistentData().putBoolean("corruptedEntityLow", true);
                                BeyonderUtil.setTargetToHighestHP(witherSkeleton, 30);
                                livingEntity.level().addFreshEntity(witherSkeleton);

                            } else if (sequence == 7 || sequence == 6 || sequence == 5) {
                                SkeletonHorse skeletonHorse = new SkeletonHorse(EntityType.SKELETON_HORSE, livingEntity.level());
                                skeletonHorse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                skeletonHorse.setHealth(maxHp);
                                skeletonHorse.teleportTo(livingEntity.getX(), livingEntity.getY() + 1, livingEntity.getZ());
                                skeletonHorse.getPersistentData().putBoolean("corruptedEntityMid", true);
                                BeyonderUtil.setTargetToHighestHP(skeletonHorse, 50);
                                Skeleton skeleton = new Skeleton(EntityType.SKELETON, livingEntity.level());
                                skeleton.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                skeleton.setHealth(maxHp);
                                skeleton.teleportTo(livingEntity.getX(), livingEntity.getY() + 1, livingEntity.getZ());
                                skeleton.getPersistentData().putBoolean("corruptedEntityMid", true);
                                BeyonderUtil.setTargetToHighestHP(skeleton, 50);
                                skeleton.startRiding(skeletonHorse, true);
                                livingEntity.level().addFreshEntity(skeletonHorse);
                                livingEntity.level().addFreshEntity(skeleton);
                            } else if (sequence == 4 || sequence == 3) {
                                Vex vex = new Vex(EntityType.VEX, livingEntity.level());
                                vex.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                vex.setHealth(maxHp);
                                vex.teleportTo(livingEntity.getX(), livingEntity.getY() + 1, livingEntity.getZ());
                                vex.getPersistentData().putBoolean("corruptedEntitySaint", true);
                                BeyonderUtil.setTargetToHighestHP(vex, 70);
                                livingEntity.level().addFreshEntity(vex);
                            } else if (sequence == 2 || sequence == 1) {
                                Phantom phantom = new Phantom(EntityType.PHANTOM, livingEntity.level());
                                phantom.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                phantom.setHealth(maxHp);
                                phantom.teleportTo(livingEntity.getX(), livingEntity.getY() + 1, livingEntity.getZ());
                                phantom.getPersistentData().putBoolean("corruptedEntityAngel", true);
                                BeyonderUtil.setTargetToHighestHP(phantom, 100);
                                livingEntity.level().addFreshEntity(phantom);

                            } else if (sequence == 0) {
                                WitherBoss wither = new WitherBoss(EntityType.WITHER, livingEntity.level());
                                wither.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                                wither.setHealth(maxHp);
                                wither.teleportTo(livingEntity.getX(), livingEntity.getY() + 1, livingEntity.getZ());
                                wither.getPersistentData().putBoolean("corruptedEntityDeity", true);
                                BeyonderUtil.setTargetToHighestHP(wither, 150);
                                livingEntity.level().addFreshEntity(wither);
                            }
                        }
                    }
                }
            }
            if (lotmLuckValue <= 200 && livingEntity.tickCount % 200 == 0) {
                tag.putDouble("misfortune", 0);
            }
            CalamityEnhancementData data = CalamityEnhancementData.getInstance(serverLevel);
            int calamityEnhancement = data.getCalamityEnhancement();
            int misfortuneEnhancement = WorldMisfortuneData.getInstance(serverLevel).getWorldMisfortune();
            int fortuneEnhancement = WorldFortuneValue.getInstance(serverLevel).getWorldFortune();
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
            int cantUseAbility = tag.getInt("cantUseAbility");
            int doubleDamage = tag.getInt("luckDoubleDamage");
            int ignoreDamage = tag.getInt("luckIgnoreDamage");
            Random random = new Random();
            if (lotmMisfortuneValue >= 1) {
                if (livingEntity.tickCount % 397 == 0 && random.nextInt(300) <= (lotmMisfortuneValue * misfortuneEnhancement) && meteor == 0) {
                    tag.putInt("luckMeteor", 40);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 40);
                }
                if (livingEntity.tickCount % 251 == 0 && random.nextInt(100) <= (lotmMisfortuneValue * misfortuneEnhancement) && lotmLightning == 0) {
                    tag.putInt("luckLightningLOTM", 20);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 15);
                }
                if (livingEntity.tickCount % 151 == 0 && random.nextInt(50) <= (lotmMisfortuneValue * misfortuneEnhancement) && paralysis == 0) {
                    tag.putInt("luckParalysis", 15);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 5);
                }
                if (livingEntity.tickCount % 149 == 0 && random.nextInt(75) <= (lotmMisfortuneValue * misfortuneEnhancement) && unequipArmor == 0) {
                    tag.putInt("luckUnequipArmor", 20);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 10);
                }
                if (livingEntity.tickCount % 349 == 0 && random.nextInt(320) <= (lotmMisfortuneValue * misfortuneEnhancement) && wardenSpawn == 0) {
                    tag.putInt("luckWarden", 30);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 30);
                }
                if (livingEntity.tickCount % 41 == 0 && random.nextInt(50) <= (lotmMisfortuneValue * misfortuneEnhancement) && mcLightning <= 3) {
                    tag.putInt("luckLightningMC", mcLightning + calamityEnhancement);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 15);
                }
                if (livingEntity.tickCount % 199 == 0 && random.nextInt(150) <= (lotmMisfortuneValue * misfortuneEnhancement) && !livingEntity.hasEffect(MobEffects.POISON)) {
                    tag.putInt("luckPoison", 15);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 8);
                }
                if (livingEntity.tickCount % 307 == 0 && random.nextInt(300) <= (lotmMisfortuneValue * misfortuneEnhancement) && tornadoInt == 0) {
                    tag.putInt("luckTornado", 35);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 25);
                }
                if (livingEntity.tickCount % 127 == 0 && random.nextInt(100) <= (lotmMisfortuneValue * misfortuneEnhancement) && stone == 0) {
                    tag.putInt("luckStone", 10);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 12);
                }
                if (livingEntity.tickCount % 701 == 0 && random.nextInt(250) <= (lotmMisfortuneValue * misfortuneEnhancement) && cantUseAbility <= 10) {
                    tag.putInt("cantUseAbility", cantUseAbility + calamityEnhancement);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 15);
                }
                if (livingEntity.tickCount % 263 == 0 && random.nextInt(150) <= (lotmMisfortuneValue * misfortuneEnhancement) && doubleDamage <= 15) {
                    tag.putInt("luckDoubleDamage", doubleDamage + calamityEnhancement);
                    tag.putDouble("misfortune", lotmMisfortuneValue - 5);
                }
            }
            if (lotmLuckValue >= 1) {
                if (livingEntity.tickCount % 29 == 0 && random.nextInt(100) <= (lotmLuckValue * fortuneEnhancement) && regeneration == 0) {
                    tag.putInt("luckRegeneration", 1);
                    tag.putDouble("luck", Math.max(0, lotmLuckValue - 5));
                }
                if (livingEntity.tickCount % 907 == 0 && random.nextInt(300) <= (lotmLuckValue * fortuneEnhancement) && livingEntity.onGround() && livingEntity instanceof Player) {
                    tag.putInt("luckDiamonds", diamondsDropped + calamityEnhancement);
                    tag.putDouble("luck", Math.max(0, lotmLuckValue - 5));
                }
                if (livingEntity.tickCount % 11 == 0 && random.nextInt(100) <= (lotmLuckValue * fortuneEnhancement) && windMovingProjectiles <= 25) {
                    tag.putInt("windMovingProjectilesCounter", windMovingProjectiles + calamityEnhancement);
                    tag.putDouble("luck", Math.max(0, lotmLuckValue - 10));
                }
                if (livingEntity.tickCount % 317 == 0 && random.nextInt(150) <= (lotmLuckValue * fortuneEnhancement) && tag.getInt("luckHalveDamage") <= 15) {
                    tag.putInt("luckHalveDamage", tag.getInt("luckHalveDamage") + calamityEnhancement);
                    tag.putDouble("luck", Math.max(0, lotmLuckValue - 12));
                }
                if (livingEntity instanceof Player pPlayer) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && livingEntity.tickCount % 51 == 0 && random.nextInt(70) <= (lotmLuckValue * fortuneEnhancement) && luckIgnoreMobs <= 20) {
                        tag.putInt("luckIgnoreMobs", luckIgnoreMobs + calamityEnhancement);
                        tag.putDouble("luck", Math.max(0, lotmLuckValue - 3));
                    }
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 5 && attackerPoisoned <= 20 && livingEntity.tickCount % 383 == 0 && random.nextInt(200) <= (lotmLuckValue * fortuneEnhancement)) {
                        tag.putInt("luckAttackerPoisoned", attackerPoisoned + calamityEnhancement);
                        tag.putDouble("luck", Math.max(0, lotmLuckValue - 15));
                    }
                    if (livingEntity.tickCount % 503 == 0 && random.nextInt(225) <= (lotmLuckValue * fortuneEnhancement) && ignoreDamage <= 15 && holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 5) {
                        tag.putInt("luckIgnoreDamage", ignoreDamage + calamityEnhancement);
                        tag.putDouble("luck", Math.max(0, lotmLuckValue - 20));
                    }
                } else if (livingEntity instanceof PlayerMobEntity playerMobEntity) {
                    int sequence = playerMobEntity.getCurrentSequence();
                    if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && livingEntity.tickCount % 51 == 0 && random.nextInt(70) <= (lotmLuckValue * fortuneEnhancement) && luckIgnoreMobs <= 20) {
                        tag.putInt("luckIgnoreMobs", luckIgnoreMobs + calamityEnhancement);
                        tag.putDouble("luck", Math.max(0, lotmLuckValue - 3));
                    }
                    if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 5 && attackerPoisoned <= 20 && livingEntity.tickCount % 383 == 0 && random.nextInt(200) <= (lotmLuckValue * fortuneEnhancement)) {
                        tag.putInt("luckAttackerPoisoned", attackerPoisoned + calamityEnhancement);
                        tag.putDouble("luck", Math.max(0, lotmLuckValue - 15));
                    }
                    if (livingEntity.tickCount % 503 == 0 && random.nextInt(225) <= (lotmLuckValue * fortuneEnhancement) && ignoreDamage <= 15 && playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 5) {
                        tag.putInt("luckIgnoreDamage", ignoreDamage + calamityEnhancement);
                        tag.putDouble("luck", Math.max(0, lotmLuckValue - 20));
                    }
                }
            }
            if (livingEntity instanceof Player player) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                int sequence = holder.getCurrentSequence();
                if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
                    if (livingEntity.tickCount % 500 == 0 && livingEntity instanceof Player pPlayer) {
                        SpamClass.sendMonsterMessage(pPlayer);
                    }
                    if (sequence == 7) {
                        if (livingEntity.tickCount % 300 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 6) {
                        if (livingEntity.tickCount % 275 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 5) {
                        if (livingEntity.tickCount % 225 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 4) {
                        if (livingEntity.tickCount % 160 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 3) {
                        if (livingEntity.tickCount % 130 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 2) {
                        if (livingEntity.tickCount % 75 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 1) {
                        if (livingEntity.tickCount % 50 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 0) {
                        if (livingEntity.tickCount % 20 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                }
            } else if (livingEntity instanceof PlayerMobEntity player) {
                int sequence = player.getCurrentSequence();
                if (player.getCurrentPathway() == BeyonderClassInit.MONSTER) {
                    if (sequence == 7) {
                        if (livingEntity.tickCount % 300 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 6) {
                        if (livingEntity.tickCount % 275 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 5) {
                        if (livingEntity.tickCount % 225 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 4) {
                        if (livingEntity.tickCount % 160 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 3) {
                        if (livingEntity.tickCount % 130 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 2) {
                        if (livingEntity.tickCount % 75 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 1) {
                        if (livingEntity.tickCount % 50 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                    if (sequence == 0) {
                        if (livingEntity.tickCount % 20 == 0) {
                            tag.putDouble("luck", Math.max(0, lotmLuckValue + 1));
                        }
                    }
                }
            }
            if (livingEntity instanceof Player pPlayer) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                int sequence = holder.getCurrentSequence();
                if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
                    if (sequence <= 6 && tag.getBoolean("monsterCalamityAttraction") && livingEntity.tickCount % 100 == 0) {
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
                        if (calamityMeteor == 0 && randomInt.nextInt(1000) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityMeteor", (int) Math.max(15, Math.random() * 70));
                        }
                        if (calamityTornado == 0 && randomInt.nextInt(750) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityTornado", (int) Math.max(15, Math.random() * 70));
                        }
                        if (calamityExplosion == 0 && randomInt.nextInt(500) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityExplosion", (int) Math.max(10, Math.random() * 60));
                        }
                        if (calamityWave == 0 && randomInt.nextInt(250) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityWave", (int) Math.max(10, Math.random() * 25));
                        }
                        if (calamityBreeze == 0 && randomInt.nextInt(250) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityBreeze", (int) Math.max(10, Math.random() * 25));
                        }
                        if (calamityWindArmorRemoval == 0 && randomInt.nextInt(400) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityWindArmorRemoval", (int) Math.max(10, Math.random() * 40));
                        }
                        if (calamityBabyZombie == 0 && randomInt.nextInt(200) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityBabyZombie", (int) Math.max(5, Math.random() * 20));
                        }
                        if (calamityUndeadArmy == 0 && randomInt.nextInt(250) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityUndeadArmy", (int) Math.max(5, Math.random() * 20));
                        }
                        if (calamityGaze == 0 && randomInt.nextInt(450) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityGaze", (int) Math.max(10, Math.random() * 50));
                        }
                        if (calamityGroundTremor == 0 && randomInt.nextInt(1000) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityGroundTremor", (int) Math.max(10, Math.random() * 40));
                        }
                        if (calamityLightningBolt == 0 && randomInt.nextInt(150) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityLightningBolt", (int) Math.max(5, Math.random() * 10));
                        }
                        if (calamityLightningStorm == 0 && randomInt.nextInt(600) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityLightningStorm", (int) Math.max(15, Math.random() * 50));
                        }
                    }

                    if (sequence <= 6 && livingEntity.tickCount % 20 == 0 && !livingEntity.level().isClientSide()) {
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
                            tag.putInt("calamityMeteorX", (int) livingEntity.getX());
                            tag.putInt("calamityMeteorY", (int) livingEntity.getY());
                            tag.putInt("calamityMeteorZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("A meteor will start falling to your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityLightningStorm == 16) {
                            tag.putInt("calamityLightningStormX", (int) livingEntity.getX());
                            tag.putInt("calamityLightningStormY", (int) livingEntity.getY());
                            tag.putInt("calamityLightningStormZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("A lightning storm will appear at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityTornado == 16) {
                            tag.putInt("calamityTornadoX", (int) livingEntity.getX());
                            tag.putInt("calamityTornadoY", (int) livingEntity.getY());
                            tag.putInt("calamityTornadoZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("A tornado will appear at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 15 seconds").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityWave == 11) {
                            tag.putInt("calamityWaveX", (int) livingEntity.getX());
                            tag.putInt("calamityWaveY", (int) livingEntity.getY());
                            tag.putInt("calamityWaveZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("A heat wave will pass through at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityBreeze == 11) {
                            tag.putInt("calamityBreezeX", (int) livingEntity.getX());
                            tag.putInt("calamityBreezeY", (int) livingEntity.getY());
                            tag.putInt("calamityBreezeZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("An ice cold breeze will pass through at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityGroundTremor == 11) {
                            tag.putInt("calamityGroundTremorX", (int) livingEntity.getX());
                            tag.putInt("calamityGroundTremorY", (int) livingEntity.getY());
                            tag.putInt("calamityGroundTremorZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("The ground will tremor at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + "), causing a pulse that damages all players and mobs in the ground and sending stone flying, in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityGaze == 11) {
                            tag.putInt("calamityGazeX", (int) livingEntity.getX());
                            tag.putInt("calamityGazeY", (int) livingEntity.getY());
                            tag.putInt("calamityGazeZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("An outer deity will focus it's gaze at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + "), causing corruption, in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityWindArmorRemoval == 11) {
                            tag.putInt("calamityWindArmorRemovalX", (int) livingEntity.getX());
                            tag.putInt("calamityWindArmorRemovalY", (int) livingEntity.getY());
                            tag.putInt("calamityWindArmorRemovalZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("A gust of wind will take armor off all players and mobs at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityExplosion == 11) {
                            tag.putInt("calamityExplosionX", (int) livingEntity.getX());
                            tag.putInt("calamityExplosionY", (int) livingEntity.getY());
                            tag.putInt("calamityExplosionZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("An accumulation of gas will explode at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 10 seconds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityLightningBolt == 6) {
                            tag.putInt("calamityLightningBoltX", (int) livingEntity.getX());
                            tag.putInt("calamityLightningBoltY", (int) livingEntity.getY());
                            tag.putInt("calamityLightningBoltZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("A lightning bolt will strike your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityUndeadArmy == 6) {
                            tag.putInt("calamityUndeadArmyX", (int) livingEntity.getX());
                            tag.putInt("calamityUndeadArmyY", (int) livingEntity.getY());
                            tag.putInt("calamityUndeadArmyZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("An undead army will appear at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                        }
                        if (calamityBabyZombie == 6) {
                            tag.putInt("calamityBabyZombieX", (int) livingEntity.getX());
                            tag.putInt("calamityBabyZombieY", (int) livingEntity.getY());
                            tag.putInt("calamityBabyZombieZ", (int) livingEntity.getZ());
                            livingEntity.sendSystemMessage(Component.literal("A strengthened baby zombie will appear at your current location (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") in 5 seconds").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                        }
                    }
                }
            } else if (livingEntity instanceof PlayerMobEntity playerMobEntity) {
                int sequence = playerMobEntity.getCurrentSequence();
                if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER) {
                    if (sequence <= 6 && tag.getBoolean("monsterCalamityAttraction") && livingEntity.tickCount % 100 == 0) {
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
                        if (calamityMeteor == 0 && randomInt.nextInt(1000) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityMeteor", (int) Math.max(15, Math.random() * 70));
                        }
                        if (calamityTornado == 0 && randomInt.nextInt(750) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityTornado", (int) Math.max(15, Math.random() * 70));
                        }
                        if (calamityExplosion == 0 && randomInt.nextInt(500) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityExplosion", (int) Math.max(10, Math.random() * 60));
                        }
                        if (calamityWave == 0 && randomInt.nextInt(250) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityWave", (int) Math.max(10, Math.random() * 25));
                        }
                        if (calamityBreeze == 0 && randomInt.nextInt(250) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityBreeze", (int) Math.max(10, Math.random() * 25));
                        }
                        if (calamityWindArmorRemoval == 0 && randomInt.nextInt(400) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityWindArmorRemoval", (int) Math.max(10, Math.random() * 40));
                        }
                        if (calamityBabyZombie == 0 && randomInt.nextInt(200) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityBabyZombie", (int) Math.max(5, Math.random() * 20));
                        }
                        if (calamityUndeadArmy == 0 && randomInt.nextInt(250) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityUndeadArmy", (int) Math.max(5, Math.random() * 20));
                        }
                        if (calamityGaze == 0 && randomInt.nextInt(450) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityGaze", (int) Math.max(10, Math.random() * 50));
                        }
                        if (calamityGroundTremor == 0 && randomInt.nextInt(1000) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityGroundTremor", (int) Math.max(10, Math.random() * 40));
                        }
                        if (calamityLightningBolt == 0 && randomInt.nextInt(150) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityLightningBolt", (int) Math.max(5, Math.random() * 10));
                        }
                        if (calamityLightningStorm == 0 && randomInt.nextInt(600) <= (misfortuneEnhancement) - 1) {
                            tag.putInt("calamityLightningStorm", (int) Math.max(15, Math.random() * 50));
                        }
                    }
                    if (sequence <= 6 && livingEntity.tickCount % 20 == 0 && !livingEntity.level().isClientSide()) {
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
                        Random random1 = new Random();
                        double surfaceBigY = livingEntity.getY();
                        double surfaceMediumY = livingEntity.getY();
                        double surfaceSmallY = livingEntity.getY();
                        double bigRandomX = livingEntity.getX();
                        double bigRandomZ = livingEntity.getZ();
                        double mediumRandomX = livingEntity.getX();
                        double mediumRandomZ = livingEntity.getZ();
                        double smallRandomX = livingEntity.getX();
                        double smallRandomZ = livingEntity.getZ();
                        if (random1.nextInt(2) == 1) {
                            bigRandomX = livingEntity.getX() - 80;
                            bigRandomZ = livingEntity.getZ() - 80;
                            mediumRandomX = livingEntity.getX() - 50;
                            mediumRandomZ = livingEntity.getZ() - 50;
                            smallRandomX = livingEntity.getX() - 25;
                            smallRandomZ = livingEntity.getZ() - 25;
                            surfaceBigY = livingEntity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) bigRandomX, 0, (int) bigRandomZ)).getY();
                            surfaceMediumY = livingEntity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) mediumRandomX, 0, (int) mediumRandomZ)).getY();
                            surfaceSmallY = livingEntity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) smallRandomX, 0, (int) smallRandomZ)).getY();


                        } else {
                            bigRandomX = livingEntity.getX() + 80;
                            bigRandomZ = livingEntity.getZ() + 80;
                            mediumRandomX = livingEntity.getX() + 50;
                            mediumRandomZ = livingEntity.getZ() + 50;
                            smallRandomX = livingEntity.getX() + 25;
                            smallRandomZ = livingEntity.getZ() + 25;
                            surfaceBigY = livingEntity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) bigRandomX, 0, (int) bigRandomZ)).getY();
                            surfaceMediumY = livingEntity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) mediumRandomX, 0, (int) mediumRandomZ)).getY();
                            surfaceSmallY = livingEntity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) smallRandomX, 0, (int) smallRandomZ)).getY();
                        }
                        if (calamityMeteor == 16) {
                            tag.putInt("calamityMeteorX", (int) livingEntity.getX());
                            tag.putInt("calamityMeteorY", (int) livingEntity.getY());
                            tag.putInt("calamityMeteorZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(bigRandomX, surfaceBigY, bigRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(75))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A METEOR WILL FALL AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 15 SECONDS").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityLightningStorm == 16) {
                            tag.putInt("calamityLightningStormX", (int) livingEntity.getX());
                            tag.putInt("calamityLightningStormY", (int) livingEntity.getY());
                            tag.putInt("calamityLightningStormZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(bigRandomX, surfaceBigY, bigRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(75))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A LIGHTNING STORM WILL START AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 15 SECONDS").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityTornado == 16) {
                            tag.putInt("calamityTornadoX", (int) livingEntity.getX());
                            tag.putInt("calamityTornadoY", (int) livingEntity.getY());
                            tag.putInt("calamityTornadoZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(bigRandomX, surfaceBigY, bigRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(75))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A TORNADO WILL SPAWN AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 15 SECONDS").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityWave == 11) {
                            tag.putInt("calamityWaveX", (int) livingEntity.getX());
                            tag.putInt("calamityWaveY", (int) livingEntity.getY());
                            tag.putInt("calamityWaveZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(mediumRandomX, surfaceMediumY, mediumRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(50))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A HEAT WAVE WILL PASS BY AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 10 SECONDS").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityBreeze == 11) {
                            tag.putInt("calamityBreezeX", (int) livingEntity.getX());
                            tag.putInt("calamityBreezeY", (int) livingEntity.getY());
                            tag.putInt("calamityBreezeZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(mediumRandomX, surfaceMediumY, mediumRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(50))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A FREEZING BREEZE WILL PASS BY AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 10 SECONDS").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityGroundTremor == 11) {
                            tag.putInt("calamityGroundTremorX", (int) livingEntity.getX());
                            tag.putInt("calamityGroundTremorY", (int) livingEntity.getY());
                            tag.putInt("calamityGroundTremorZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(mediumRandomX, surfaceMediumY, mediumRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(50))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A TREMOR WILL OCCUR AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 10 SECONDS").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityGaze == 11) {
                            tag.putInt("calamityGazeX", (int) livingEntity.getX());
                            tag.putInt("calamityGazeY", (int) livingEntity.getY());
                            tag.putInt("calamityGazeZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(mediumRandomX, surfaceMediumY, mediumRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(50))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "AN EVIL EXISTENCE WILL CORRUPT THE AREA AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 10 SECONDS").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityWindArmorRemoval == 11) {
                            tag.putInt("calamityWindArmorRemovalX", (int) livingEntity.getX());
                            tag.putInt("calamityWindArmorRemovalY", (int) livingEntity.getY());
                            tag.putInt("calamityWindArmorRemovalZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(mediumRandomX, surfaceMediumY, mediumRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(50))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A GUST OF WIND WILL TAKE AWAY ALL ARMOR AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 10 SECONDS").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityExplosion == 11) {
                            tag.putInt("calamityExplosionX", (int) livingEntity.getX());
                            tag.putInt("calamityExplosionY", (int) livingEntity.getY());
                            tag.putInt("calamityExplosionZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(mediumRandomX, surfaceMediumY, mediumRandomZ, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(50))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "AN EXPLOSION WILL OCCUR AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 10 SECONDS").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityLightningBolt == 6) {
                            tag.putInt("calamityLightningBoltX", (int) livingEntity.getX());
                            tag.putInt("calamityLightningBoltY", (int) livingEntity.getY());
                            tag.putInt("calamityLightningBoltZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(smallRandomX, surfaceSmallY, smallRandomX, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(20))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A LIGHTNING BOLT WILL STRIKE AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 5 SECONDS").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityUndeadArmy == 6) {
                            tag.putInt("calamityUndeadArmyX", (int) livingEntity.getX());
                            tag.putInt("calamityUndeadArmyY", (int) livingEntity.getY());
                            tag.putInt("calamityUndeadArmyZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(smallRandomX, surfaceSmallY, smallRandomX, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(20))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "AN UNDEAD ARMY WILL RISE AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 5 SECONDS").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                            }
                        }
                        if (calamityBabyZombie == 6) {
                            tag.putInt("calamityBabyZombieX", (int) livingEntity.getX());
                            tag.putInt("calamityBabyZombieY", (int) livingEntity.getY());
                            tag.putInt("calamityBabyZombieZ", (int) livingEntity.getZ());
                            playerMobEntity.getNavigation().moveTo(smallRandomX, surfaceSmallY, smallRandomX, 2.5);
                            for (Player pPlayer : playerMobEntity.level().getEntitiesOfClass(Player.class, playerMobEntity.getBoundingBox().inflate(20))) {
                                pPlayer.sendSystemMessage(Component.literal("<" + playerMobEntity.getName() + ">:" + "A STRENGTHENED ZOMBIE WILL SPAWN AT (" + (int) livingEntity.getX() + "," + (int) livingEntity.getY() + "," + (int) livingEntity.getZ() + ") IN 5 SECONDS").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                            }
                        }
                    }
                }
            }
            if (!livingEntity.level().isClientSide() && livingEntity.tickCount % 20 == 0) {
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
                    livingEntity.sendSystemMessage(Component.literal("explosion val is " + calamityExplosion));
                    tag.putInt("calamityExplosion", calamityExplosion - 1);
                }
                if (calamityBabyZombie >= 1) {
                    tag.putInt("calamityBabyZombie", calamityBabyZombie - 1);
                }
                if (calamityUndeadArmy >= 1) {
                    livingEntity.sendSystemMessage(Component.literal(calamityUndeadArmy + " value"));
                    tag.putInt("calamityUndeadArmy", calamityUndeadArmy - 1);
                }
                if (calamityLightningBolt >= 1) {
                    tag.putInt("calamityLightningBolt", calamityLightningBolt - 1);
                }


                if (calamityMeteor == 1) {
                    int meteorX = tag.getInt("calamityMeteorX");
                    int meteorY = tag.getInt("calamityMeteorY");
                    int meteorZ = tag.getInt("calamityMeteorZ");
                    int subtractX = meteorX - (int) livingEntity.getX();
                    int subtractY = meteorY - (int) livingEntity.getY();
                    int subtractZ = meteorZ - (int) livingEntity.getZ();
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(40))) {
                        if (entity instanceof Player pPlayer) {
                            BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                            if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                pPlayer.getPersistentData().putInt("calamityMeteorImmunity", 5);
                            }
                        } else if (entity instanceof PlayerMobEntity playerMobEntity) {
                            if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && playerMobEntity.getCurrentSequence() <= 3) {
                                playerMobEntity.getPersistentData().putInt("calamityMeteorImmunity", 5);
                            }
                        }
                    }
                    tag.putInt("luckMeteorDamage", 6);
                    MeteorEntity.summonMeteorAtPositionWithScale(livingEntity, livingEntity.getX(), livingEntity.getY() - 50, livingEntity.getZ(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 6 + (calamityEnhancement));
                }
                if (calamityTornado == 1) {
                    int tornadoX = tag.getInt("calamityTornadoX");
                    int tornadoY = tag.getInt("calamityTornadoY");
                    int tornadoZ = tag.getInt("calamityTornadoZ");
                    int subtractX = tornadoX - (int) livingEntity.getX();
                    int subtractY = tornadoY - (int) livingEntity.getY();
                    int subtractZ = tornadoZ - (int) livingEntity.getZ();
                    TornadoEntity tornadoEntity = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), livingEntity.level());
                    tornadoEntity.setTornadoHeight(60 + (calamityEnhancement * 10));
                    tornadoEntity.setTornadoRadius(30 + (calamityEnhancement * 5));
                    tornadoEntity.setOwner(livingEntity);
                    tornadoEntity.setTornadoLifecount(100 + (calamityEnhancement * 20));
                    tornadoEntity.teleportTo(tornadoX, tornadoY, tornadoZ);
                    tornadoEntity.setTornadoRandom(true);
                    tornadoEntity.setDeltaMovement((Math.random() * 2) - 1, 0, (Math.random() * 2) - 1);
                    tag.putInt("luckTornadoResistance", 5);
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(40))) {
                        if (entity instanceof Player pPlayer) {
                            BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                            if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                pPlayer.getPersistentData().putInt("luckTornadoImmunity", 6);
                            }
                        } else if (entity instanceof PlayerMobEntity playerMobEntity) {
                            if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && playerMobEntity.getCurrentSequence() <= 3) {
                                playerMobEntity.getPersistentData().putInt("luckTornadoImmunity", 6);
                            }
                        }
                    }
                }
                if (calamityLightningStorm == 1) {
                    tag.putInt("luckLightningLOTMDamage", 5 + (calamityEnhancement * 2));
                    tag.putInt("calamityLightningStormSummon", 20 + (calamityEnhancement * 5));
                    if (livingEntity instanceof Player pPlayer) {
                        int sequence = BeyonderHolderAttacher.getHolderUnwrap(pPlayer).getCurrentSequence();
                        if (sequence >= 3) {
                            tag.putInt("calamityLightningStormResistance", 23 + (calamityEnhancement * 5));
                        }
                    } else if (livingEntity instanceof PlayerMobEntity pPlayer) {
                        int sequence = pPlayer.getCurrentSequence();
                        if (sequence >= 3) {
                            tag.putInt("calamityLightningStormResistance", 23 + (calamityEnhancement * 5));
                        }
                    }
                }
                if (calamityWave == 1) {
                    int waveX = tag.getInt("calamityWaveX");
                    int waveY = tag.getInt("calamityWaveY");
                    int waveZ = tag.getInt("calamityWaveZ");
                    int subtractX = waveX - (int) livingEntity.getX();
                    int subtractY = waveY - (int) livingEntity.getY();
                    int subtractZ = waveZ - (int) livingEntity.getZ();
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20 + (calamityEnhancement * 8)))) {
                        if (entity != livingEntity) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                    return;
                                } else {
                                    pPlayer.hurt(pPlayer.damageSources().lava(), 12 + (calamityEnhancement * 3));
                                    pPlayer.setSecondsOnFire(6 + (calamityEnhancement * 2));
                                }
                            } else if (entity instanceof PlayerMobEntity playerMobEntity) {
                                if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && playerMobEntity.getCurrentSequence() <= 3) {
                                    return;
                                } else {
                                    playerMobEntity.hurt(playerMobEntity.damageSources().lava(), 12 + (calamityEnhancement * 3));
                                    playerMobEntity.setSecondsOnFire(6 + (calamityEnhancement * 2));
                                }
                            } else {
                                entity.hurt(entity.damageSources().lava(), 12 + (calamityEnhancement * 3));
                                entity.setSecondsOnFire(6 + (calamityEnhancement * 2));
                            }
                        } else if ((livingEntity instanceof Player player && BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence() <= 3) || livingEntity instanceof PlayerMobEntity playerMobEntity && playerMobEntity.getCurrentSequence() <= 3) {
                            return;
                        } else {
                            livingEntity.hurt(livingEntity.damageSources().lava(), 6 + (calamityEnhancement * 2));
                            livingEntity.setSecondsOnFire(3 + calamityEnhancement * 2);
                        }
                    }
                }
                if (calamityBreeze == 1) {
                    int breezeX = tag.getInt("calamityBreezeX");
                    int breezeY = tag.getInt("calamityBreezeY");
                    int breezeZ = tag.getInt("calamityBreezeZ");
                    int subtractX = breezeX - (int) livingEntity.getX();
                    int subtractY = breezeY - (int) livingEntity.getY();
                    int subtractZ = breezeZ - (int) livingEntity.getZ();
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20 + (calamityEnhancement * 8)))) {
                        if (entity != livingEntity) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                    return;
                                } else {
                                    pPlayer.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60 + (calamityEnhancement * 15), 1, false, false));
                                    pPlayer.setTicksFrozen(60 + (calamityEnhancement * 15));
                                }
                            } else if (entity instanceof PlayerMobEntity playerMobEntity) {
                                if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && playerMobEntity.getCurrentSequence() <= 3) {
                                    return;
                                } else {
                                    playerMobEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60 + (calamityEnhancement * 15), 1, false, false));
                                    playerMobEntity.setTicksFrozen(60 + (calamityEnhancement * 15));
                                }
                            } else {
                                entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60 + (calamityEnhancement * 15), 1, false, false));
                                entity.setTicksFrozen(60 + (calamityEnhancement * 15));
                            }
                        } else if ((livingEntity instanceof Player player && BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence() <= 3) || livingEntity instanceof PlayerMobEntity playerMobEntity && playerMobEntity.getCurrentSequence() <= 3) {
                            return;
                        } else {
                            livingEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60 + (calamityEnhancement * 15), 1, false, false));
                            livingEntity.setTicksFrozen(60 + (calamityEnhancement * 15));
                        }
                    }
                }
                if (calamityGaze == 1) {
                    int gazeX = tag.getInt("calamityGazeX");
                    int gazeY = tag.getInt("calamityGazeY");
                    int gazeZ = tag.getInt("calamityGazeZ");
                    int subtractX = gazeX - (int) livingEntity.getX();
                    int subtractY = gazeY - (int) livingEntity.getY();
                    int subtractZ = gazeZ - (int) livingEntity.getZ();
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                        if (entity != livingEntity) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                CompoundTag pTag = pPlayer.getPersistentData();
                                double pPlayerCorruption = pTag.getDouble("corruption");
                                if (holder1.getCurrentSequence() <= 3) {
                                    return;
                                } else {
                                    pTag.putDouble("corruption", pPlayerCorruption + 45 + (calamityEnhancement * 5));
                                }
                            } else if (entity instanceof PlayerMobEntity pPlayer) {
                                CompoundTag pTag = pPlayer.getPersistentData();
                                double pPlayerCorruption = pTag.getDouble("corruption");
                                if (pPlayer.getCurrentSequence() <= 3) {
                                    return;
                                } else {
                                    pTag.putDouble("corruption", pPlayerCorruption + 45 + (calamityEnhancement * 5));
                                }
                            } else {
                                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 120 + (calamityEnhancement * 15), 3, false, false));
                                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120 + (calamityEnhancement * 15), 3, false, false));
                            }
                        } else if ((livingEntity instanceof Player player && BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence() <= 3) || livingEntity instanceof PlayerMobEntity playerMobEntity && playerMobEntity.getCurrentSequence() <= 3) {
                            return;
                        } else {
                            tag.putDouble("corruption", corruption + 15 + (calamityEnhancement * 5));
                        }
                    }
                }
                if (calamityWindArmorRemoval == 1) {
                    int windArmorX = tag.getInt("calamityWindArmorRemovalX");
                    int windArmorY = tag.getInt("calamityWindArmorRemovalY");
                    int windArmorZ = tag.getInt("calamityWindArmorRemovalZ");
                    int subtractX = windArmorX - (int) livingEntity.getX();
                    int subtractY = windArmorY - (int) livingEntity.getY();
                    int subtractZ = windArmorZ - (int) livingEntity.getZ();
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20 + (calamityEnhancement * 8)))) {
                        if (entity instanceof Player pPlayer) {
                            BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                            List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                            List<EquipmentSlot> equippedArmor = armorSlots.stream()
                                    .filter(slot -> !pPlayer.getItemBySlot(slot).isEmpty())
                                    .toList();
                            if (!equippedArmor.isEmpty()) {
                                EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                                ItemStack armorPiece = pPlayer.getItemBySlot(randomArmorSlot);
                                if (entity != livingEntity) {
                                    if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                        return;
                                    } else if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 6 && holder1.getCurrentSequence() >= 4) {
                                        if (random.nextInt(2) == 1) {
                                            pPlayer.spawnAtLocation(armorPiece);
                                            pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                        }
                                    } else pPlayer.spawnAtLocation(armorPiece);
                                    pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                } else if ((livingEntity instanceof Player player && BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence() <= 3)) {
                                    return;
                                } else if (random.nextInt(2) == 1) {
                                    livingEntity.spawnAtLocation(armorPiece);
                                    livingEntity.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                }
                            }
                        } else if (entity instanceof PlayerMobEntity pPlayer) {
                            int sequence = pPlayer.getCurrentSequence();
                            List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                            List<EquipmentSlot> equippedArmor = armorSlots.stream()
                                    .filter(slot -> !pPlayer.getItemBySlot(slot).isEmpty())
                                    .toList();
                            if (!equippedArmor.isEmpty()) {
                                EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                                ItemStack armorPiece = pPlayer.getItemBySlot(randomArmorSlot);
                                if (entity != livingEntity) {
                                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                                        return;
                                    } else if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                                        if (random.nextInt(2) == 1) {
                                            pPlayer.spawnAtLocation(armorPiece);
                                            pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                        }
                                    } else pPlayer.spawnAtLocation(armorPiece);
                                    pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                } else if ((livingEntity instanceof PlayerMobEntity player && player.getCurrentSequence() <= 3)) {
                                    return;
                                } else if (random.nextInt(2) == 1) {
                                    livingEntity.spawnAtLocation(armorPiece);
                                    livingEntity.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                                }
                            }
                        }
                    }
                }
                if (calamityGroundTremor == 1) {
                    int groundTremorX = tag.getInt("calamityGroundTremorX");
                    int groundTremorY = tag.getInt("calamityGroundTremorY");
                    int groundTremorZ = tag.getInt("calamityGroundTremorZ");
                    int subtractX = groundTremorX - (int) livingEntity.getX();
                    int subtractY = groundTremorY - (int) livingEntity.getY();
                    int subtractZ = groundTremorZ - (int) livingEntity.getZ();
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(50 + (calamityEnhancement * 20)))) {
                        if (entity instanceof Player pPlayer && pPlayer.onGround()) {
                            if (entity != livingEntity) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                                    return;
                                } else {
                                    entity.hurt(entity.damageSources().generic(), 12 + (calamityEnhancement * 4));
                                }
                            } else if ((livingEntity instanceof Player player && BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence() <= 3)) {
                                return;
                            } else entity.hurt(entity.damageSources().generic(), 6 + (calamityEnhancement * 2));
                        } else if (entity instanceof PlayerMobEntity playerMobEntity && playerMobEntity.onGround()) {
                            if (entity != livingEntity) {
                                if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && playerMobEntity.getCurrentSequence() <= 3) {
                                    return;
                                } else {
                                    entity.hurt(entity.damageSources().generic(), 12 + (calamityEnhancement * 4));
                                }
                            } else if (livingEntity instanceof PlayerMobEntity playerMob && playerMob.getCurrentSequence() <= 3 && playerMob.getCurrentPathway() == BeyonderClassInit.MONSTER) {
                                return;
                            } else entity.hurt(entity.damageSources().generic(), 6 + (calamityEnhancement * 2));
                        } else if (entity.onGround()) {
                            entity.hurt(entity.damageSources().generic(), 12 + (calamityEnhancement * 2));
                        }
                    }
                    AABB checkArea = livingEntity.getBoundingBox().inflate(50 + (calamityEnhancement * 20));
                    for (BlockPos blockPos : BlockPos.betweenClosedStream(checkArea).toList()) {
                        if (!livingEntity.level().getBlockState(blockPos).isAir() && Earthquake.isOnSurface(livingEntity.level(), blockPos)) {
                            if (random.nextInt(200) == 1) { // 50% chance to destroy a block
                                livingEntity.level().destroyBlock(blockPos, false);
                            } else if (random.nextInt(200) == 2) { // 10% chance to spawn a stone entity
                                StoneEntity stoneEntity = new StoneEntity(livingEntity.level(), livingEntity);
                                ScaleData scaleData = ScaleTypes.BASE.getScaleData(stoneEntity);
                                stoneEntity.teleportTo(blockPos.getX(), blockPos.getY() + 3, blockPos.getZ());
                                stoneEntity.setDeltaMovement(0, 3 + Math.random() * 2, 0);
                                stoneEntity.setStoneYRot((int) (Math.random() * 18));
                                stoneEntity.setStoneXRot((int) (Math.random() * 18));
                                scaleData.setScale((float) (1 + Math.random() * 2.0f));
                                livingEntity.level().addFreshEntity(stoneEntity);
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
                    int subtractX = babyZombieX - (int) livingEntity.getX();
                    int subtractY = babyZombieY - (int) livingEntity.getY();
                    int subtractZ = babyZombieZ - (int) livingEntity.getZ();
                    Zombie zombie = new Zombie(EntityType.ZOMBIE, livingEntity.level());
                    ItemStack netheriteHelmet = new ItemStack(Items.NETHERITE_HELMET);
                    ItemStack netheriteChestplate = new ItemStack(Items.NETHERITE_BOOTS);
                    ItemStack netheriteLeggings = new ItemStack(Items.NETHERITE_LEGGINGS);
                    ItemStack netheriteBoots = new ItemStack(Items.NETHERITE_BOOTS);
                    ItemStack netheriteSword = new ItemStack(Items.NETHERITE_SWORD);
                    zombie.setDropChance(EquipmentSlot.MAINHAND, 0);
                    zombie.setDropChance(EquipmentSlot.CHEST, 0);
                    zombie.setDropChance(EquipmentSlot.LEGS, 0);
                    zombie.setDropChance(EquipmentSlot.FEET, 0);
                    zombie.setDropChance(EquipmentSlot.HEAD, 0);
                    netheriteHelmet.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
                    netheriteChestplate.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
                    netheriteLeggings.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
                    netheriteBoots.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
                    netheriteSword.enchant(Enchantments.FIRE_ASPECT, 2);
                    netheriteSword.enchant(Enchantments.SHARPNESS, 2);
                    zombie.setItemSlot(EquipmentSlot.HEAD, netheriteHelmet);
                    zombie.setItemSlot(EquipmentSlot.CHEST, netheriteChestplate);
                    zombie.setItemSlot(EquipmentSlot.LEGS, netheriteLeggings);
                    zombie.setItemSlot(EquipmentSlot.FEET, netheriteBoots);
                    zombie.setItemSlot(EquipmentSlot.MAINHAND, netheriteSword);
                    zombie.setBaby(true);
                    zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100000, 3, true, true));
                    zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100000, 3, false, false));
                    zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100000, 2, false, false));
                    zombie.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                    zombie.teleportTo(babyZombieX, babyZombieY, babyZombieZ);
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(10))) {
                        if ((livingEntity instanceof Player player && BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence() <= 3 && BeyonderHolderAttacher.getHolderUnwrap(player).currentClassMatches(BeyonderClassInit.MONSTER)) || livingEntity instanceof PlayerMobEntity playerMobEntity && playerMobEntity.getCurrentSequence() <= 3 && playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER) {
                            if (entity != null) {
                                zombie.setTarget(entity);
                            }
                        }
                        for (int i = 0; i < calamityEnhancement; i++) {
                            livingEntity.level().addFreshEntity(zombie);
                        }
                    }
                }
                if (calamityUndeadArmy == 1) {
                    livingEntity.getPersistentData().putInt("calamityUndeadArmyCounter", 20);
                }
                if (calamityLightningBolt == 1) {
                    int lightningBoltX = tag.getInt("calamityLightningBoltX");
                    int lightningBoltY = tag.getInt("calamityLightningBoltY");
                    int lightningBoltZ = tag.getInt("calamityLightningBoltZ");
                    int subtractX = lightningBoltX - (int) livingEntity.getX();
                    int subtractY = lightningBoltY - (int) livingEntity.getY();
                    int subtractZ = lightningBoltZ - (int) livingEntity.getZ();
                    for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                        if (entity instanceof Player pPlayer) {
                            BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                            int sequence = holder1.getCurrentSequence();
                            if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                                pPlayer.getPersistentData().putInt("calamityLightningBoltImmunity", 10);
                            }
                        }
                        if (entity instanceof PlayerMobEntity pPlayer) {
                            int sequence = pPlayer.getCurrentSequence();
                            if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                                pPlayer.getPersistentData().putInt("calamityLightningBoltImmunity", 10);
                            }
                        }
                    }
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), livingEntity.level());
                    lightningEntity.setSpeed(6);
                    lightningEntity.setNoUp(true);
                    lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                    lightningEntity.teleportTo(lightningBoltX, lightningBoltY + 60, lightningBoltZ);
                    BlockPos pos = new BlockPos(lightningBoltX, lightningBoltY, lightningBoltZ);
                    lightningEntity.setTargetPos(pos.getCenter());
                    lightningEntity.setMaxLength(60);
                    for (int i = 0; i < calamityEnhancement; i++) {
                        livingEntity.level().addFreshEntity(lightningEntity);
                    }
                    tag.putInt("calamityLightningBoltMonsterResistance", 5);
                }
            }
        }


        //LUCK EVENTS
        if (livingEntity.tickCount % 20 == 0) {
            CalamityEnhancementData data = CalamityEnhancementData.getInstance(serverLevel);
            int calamityEnhancement = data.getCalamityEnhancement();
            int misfortuneEnhancement = WorldMisfortuneData.getInstance(serverLevel).getWorldMisfortune();
            int fortuneEnhancement = WorldFortuneValue.getInstance(serverLevel).getWorldFortune();
            CompoundTag tag = livingEntity.getPersistentData();
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
            int cantUseAbility = tag.getInt("cantUseAbility");
            int doubleDamage = tag.getInt("luckDoubleDamage");
            int ignoreDamage = tag.getInt("luckIgnoreDamage");
            Random random = new Random();
            double lotmLuckValue = tag.getDouble("luck");
            double lotmMisfortuneValue = tag.getDouble("misfortune");
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
            if (diamondsDropped >= 2) {
                tag.putInt("luckDiamonds", diamondsDropped - 1);
            }


            if (meteor == 1) {
                MeteorEntity meteorEntity = new MeteorEntity(EntityInit.METEOR_ENTITY.get(), serverLevel);
                meteorEntity.teleportTo(livingEntity.getX(), livingEntity.getY() + 150, livingEntity.getZ());
                ScaleData scaleData = ScaleTypes.BASE.getScaleData(meteorEntity);
                scaleData.setScale(6.0f + (calamityEnhancement));
                double dx = livingEntity.getX() - meteorEntity.getX();
                double dy = livingEntity.getY() - meteorEntity.getY();
                double dz = livingEntity.getZ() - meteorEntity.getZ();
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                dx /= distance;
                dy /= distance;
                dz /= distance;
                double speed = 2.0;
                meteorEntity.setDeltaMovement(dx * speed, dy * speed, dz * speed);
                livingEntity.level().addFreshEntity(meteorEntity);
                tag.putInt("luckMeteor", 0);
                if (livingEntity instanceof Player pPlayer) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6) {
                        tag.putInt("luckMeteorDamage", 6);
                    }
                } else if (livingEntity instanceof PlayerMobEntity pPlayer) {
                    int sequence = pPlayer.getCurrentSequence();
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6) {
                        tag.putInt("luckMeteorDamage", 6);
                    }
                }
                for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(50))) {
                    if (entity instanceof Player pPlayer) {
                        BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                        if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                            tag.putInt("calamityMeteorImmunity", 6);
                        }
                    } else if (livingEntity instanceof PlayerMobEntity pPlayer) {
                        int sequence = pPlayer.getCurrentSequence();
                        if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                            tag.putInt("calamityMeteorImmunity", 6);
                        }
                    }
                }
            }
            if (lotmLightning == 1) {
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), serverLevel);
                lightningEntity.setSpeed(6.0f);
                lightningEntity.setTargetEntity(livingEntity);
                lightningEntity.setMaxLength(60);
                lightningEntity.setDeltaMovement(0, -3, 0);
                lightningEntity.teleportTo(livingEntity.getX(), livingEntity.getY() + 100, livingEntity.getZ());
                for (int i = 0; i < calamityEnhancement; i++) {
                    livingEntity.level().addFreshEntity(lightningEntity);
                }
                if (livingEntity instanceof Player pPlayer) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckLightningLOTMDamage", 5);
                    }
                } else if (livingEntity instanceof PlayerMobEntity pPlayer) {
                    int sequence = pPlayer.getCurrentSequence();
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                        tag.putInt("luckLightningLOTMDamage", 5);
                    }
                }
                for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(20))) {
                    if (entity instanceof Player pPlayer) {
                        BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                        if (holder1.currentClassMatches(BeyonderClassInit.MONSTER) && holder1.getCurrentSequence() <= 3) {
                            tag.putInt("calamityLOTMLightningImmunity", 6);
                        }
                    }
                }
                tag.putInt("luckLightningLOTM", 0);
            }
            if (paralysis == 1) {
                if (livingEntity instanceof Player pPlayer) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6 && sequence >= 4) {
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 5 + (calamityEnhancement), 0, false, false));
                        livingEntity.sendSystemMessage(Component.literal("How unlucky, you tripped!").withStyle(ChatFormatting.BOLD));
                    } else if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                        return;
                    } else {
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 10 + (calamityEnhancement * 3), 0, false, false));
                        livingEntity.sendSystemMessage(Component.literal("How unlucky, you tripped!").withStyle(ChatFormatting.BOLD));
                        tag.putInt("luckParalysis", 0);
                    }
                } else if (livingEntity instanceof PlayerMobEntity pPlayer) {
                    int sequence = pPlayer.getCurrentSequence();
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 5 + (calamityEnhancement), 0, false, false));
                        livingEntity.sendSystemMessage(Component.literal("How unlucky, you tripped!").withStyle(ChatFormatting.BOLD));
                    } else if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                        return;
                    } else {
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 10 + (calamityEnhancement * 3), 0, false, false));
                        livingEntity.sendSystemMessage(Component.literal("How unlucky, you tripped!").withStyle(ChatFormatting.BOLD));
                        tag.putInt("luckParalysis", 0);
                    }
                }
            }
            if (unequipArmor == 1) {
                if (livingEntity instanceof Player pPlayer) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    int sequence = holder.getCurrentSequence();
                    List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                    for (EquipmentSlot slot : armorSlots) {
                        ItemStack armorPiece = livingEntity.getItemBySlot(slot);
                        if (!armorPiece.isEmpty()) {
                            if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6 && sequence >= 4) {
                                if (random.nextInt(2) == 1) {
                                    livingEntity.spawnAtLocation(armorPiece);
                                    livingEntity.setItemSlot(slot, ItemStack.EMPTY);
                                }
                            } else if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                                continue;
                            } else {
                                livingEntity.spawnAtLocation(armorPiece);
                                livingEntity.setItemSlot(slot, ItemStack.EMPTY);
                            }
                        }
                    }
                    tag.putInt("luckUnequipArmor", 0);
                } else if (livingEntity instanceof PlayerMobEntity playerMobEntity) {
                    int sequence = playerMobEntity.getCurrentSequence();
                    List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                    for (EquipmentSlot slot : armorSlots) {
                        ItemStack armorPiece = livingEntity.getItemBySlot(slot);
                        if (!armorPiece.isEmpty()) {
                            if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                                if (random.nextInt(2) == 1) {
                                    livingEntity.spawnAtLocation(armorPiece);
                                    livingEntity.setItemSlot(slot, ItemStack.EMPTY);
                                }
                            } else if (playerMobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                                continue;
                            } else {
                                livingEntity.spawnAtLocation(armorPiece);
                                livingEntity.setItemSlot(slot, ItemStack.EMPTY);
                            }
                        }
                    }
                    tag.putInt("luckUnequipArmor", 0);
                }
            }
            if (wardenSpawn == 1 && livingEntity.onGround()) {
                livingEntity.sendSystemMessage(Component.literal("Warden is 1"));
                Warden warden = new Warden(EntityType.WARDEN, livingEntity.level());
                warden.setLastHurtByMob(livingEntity);
                warden.setTarget(livingEntity);
                warden.setAggressive(true);
                warden.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                warden.setNoAi(false);
                AttributeInstance maxHP = warden.getAttribute(Attributes.MAX_HEALTH);
                maxHP.setBaseValue(60);
                if (calamityEnhancement >= 2) {
                    warden.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100 * calamityEnhancement, 2, false, false));
                }
                if (livingEntity instanceof Player player) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6 && sequence >= 4) {
                        Ravager ravager = new Ravager(EntityType.RAVAGER, livingEntity.level());
                        ravager.setTarget(livingEntity);
                        ravager.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                        ravager.setAggressive(true);
                        livingEntity.level().addFreshEntity(ravager);
                    } else if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                        return;
                    } else livingEntity.level().addFreshEntity(warden);
                    tag.putInt("luckWarden", 0);
                }
                if (livingEntity instanceof PlayerMobEntity player) {
                    int sequence = player.getCurrentSequence();
                    if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        Ravager ravager = new Ravager(EntityType.RAVAGER, livingEntity.level());
                        ravager.setTarget(livingEntity);
                        ravager.setAggressive(true);
                        ravager.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                        livingEntity.level().addFreshEntity(ravager);
                    } else if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                        return;
                    } else livingEntity.level().addFreshEntity(warden);
                    tag.putInt("luckWarden", 0);
                }
            }

            if (mcLightning >= 1 && livingEntity.getHealth() <= 15 + (calamityEnhancement * 3)) {
                tag.putInt("luckLightningMC", mcLightning - 1);
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, livingEntity.level());
                lightningBolt.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                lightningBolt.setDamage(10.0f + (calamityEnhancement * 3));
                livingEntity.level().addFreshEntity(lightningBolt);
                if (livingEntity instanceof Player player) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckLightningMCDamage", 2);
                    } else if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                        tag.putInt("luckMCLightningImmunity", 2);
                    }
                }
                if (livingEntity instanceof PlayerMobEntity player) {
                    int sequence = player.getCurrentSequence();
                    if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckLightningMCDamage", 2);
                    } else if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                        tag.putInt("luckMCLightningImmunity", 2);
                    }
                }
            }
            if (poison == 1 && !livingEntity.hasEffect(MobEffects.POISON)) {
                if (livingEntity instanceof Player player) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6 && sequence >= 4) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2 + calamityEnhancement, false, false));
                    } else if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                        return;
                    } else {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2 + calamityEnhancement, false, false));
                        tag.putInt("luckPoison", 0);
                    }
                } else if (livingEntity instanceof PlayerMobEntity player) {
                    int sequence = player.getCurrentSequence();
                    if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2 + calamityEnhancement, false, false));
                    } else if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                        return;
                    } else {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2 + calamityEnhancement, false, false));
                        tag.putInt("luckPoison", 0);
                    }
                }
            }
            if (tornadoInt == 1) {
                livingEntity.sendSystemMessage(Component.literal("Tornado is CURRENTLYYYYYYYYYYY 1"));
                TornadoEntity tornado = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), livingEntity.level());
                tornado.setTornadoLifecount(120 + (calamityEnhancement * 30));
                tornado.setTornadoPickup(true);
                tornado.setTornadoRandom(true);
                tornado.setTornadoHeight(50 + (calamityEnhancement * 15));
                tornado.setTornadoRadius(25 + (calamityEnhancement * 5));
                tornado.moveTo(livingEntity.getOnPos().getCenter());
                if (livingEntity instanceof Player player) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckTornadoResistance", 6 + (calamityEnhancement * 2));
                    } else if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                        tag.putInt("luckTornadoImmunity", 6 + (calamityEnhancement * 2));
                    }
                    livingEntity.level().addFreshEntity(tornado);
                    tag.putInt("luckTornado", 0);
                } else if (livingEntity instanceof PlayerMobEntity player) {
                    int sequence = player.getCurrentSequence();
                    if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckTornadoResistance", 6 + (calamityEnhancement * 2));
                    } else if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                        tag.putInt("luckTornadoImmunity", 6 + (calamityEnhancement * 2));
                    }
                    livingEntity.level().addFreshEntity(tornado);
                    tag.putInt("luckTornado", 0);
                }
            }
            if (stone == 1) {
                StoneEntity stoneEntity = new StoneEntity(EntityInit.STONE_ENTITY.get(), livingEntity.level());
                stoneEntity.teleportTo(livingEntity.getX(), livingEntity.getY() + 30, livingEntity.getZ());
                stoneEntity.setDeltaMovement(0, -5, 0);
                for (int i = 0; i < calamityEnhancement; i++) {
                    livingEntity.level().addFreshEntity(stoneEntity);
                }
                tag.putInt("luckStone", 0);
                if (livingEntity instanceof Player player) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                    int sequence = holder.getCurrentSequence();
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckStoneDamage", 5 + calamityEnhancement);
                    } else if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 3) {
                        tag.putInt("luckStoneDamageImmunity", 5 + calamityEnhancement);
                    }
                } else if (livingEntity instanceof PlayerMobEntity player) {
                    int sequence = player.getCurrentSequence();
                    if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 6 && sequence >= 4) {
                        tag.putInt("luckStoneDamage", 5 + calamityEnhancement);
                    } else if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && sequence <= 3) {
                        tag.putInt("luckStoneDamageImmunity", 5 + calamityEnhancement);
                    }
                }
            }
            if (regeneration == 1 && livingEntity.getHealth() <= 15) {
                if (livingEntity.hasEffect(MobEffects.REGENERATION)) {
                    if (livingEntity.getEffect(MobEffects.REGENERATION).getAmplifier() <= 4) {
                        tag.putInt("luckRegeneration", 0);
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40 + (calamityEnhancement * 20), 4, false, false));
                    }
                } else {
                    tag.putInt("luckRegeneration", 0);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40 + (calamityEnhancement * 20), 4, false, false));
                }
            }
            if (diamondsDropped >= 1 && livingEntity.onGround() && livingEntity instanceof Player pPlayer) {
                for (int i = 0; i < calamityEnhancement; i++) {
                    pPlayer.addItem(Items.DIAMOND.getDefaultInstance());
                    pPlayer.addItem(Items.DIAMOND.getDefaultInstance());
                    pPlayer.addItem(Items.DIAMOND.getDefaultInstance());
                }
                pPlayer.displayClientMessage(Component.literal("How lucky! You found some diamonds on the ground"), true);
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

    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!player.level().isClientSide()) {
                CompoundTag tag = player.getPersistentData();
                tag.putInt("calamityMeteor", 0);
                tag.putInt("calamityMeteorX", 0);
                tag.putInt("calamityMeteorY", 0);
                tag.putInt("calamityMeteorZ", 0);
                tag.putInt("calamityLightningStorm", 0);
                tag.putInt("calamityLightningStormX", 0);
                tag.putInt("calamityLightningStormY", 0);
                tag.putInt("calamityLightningStormZ", 0);
                tag.putInt("calamityLightningBolt", 0);
                tag.putInt("calamityLightningBoltX", 0);
                tag.putInt("calamityLightningBoltY", 0);
                tag.putInt("calamityLightningBoltZ", 0);
                tag.putInt("calamityGroundTremor", 0);
                tag.putInt("calamityGroundTremorX", 0);
                tag.putInt("calamityGroundTremorY", 0);
                tag.putInt("calamityGroundTremorZ", 0);
                tag.putInt("calamityGaze", 0);
                tag.putInt("calamityGazeX", 0);
                tag.putInt("calamityGazeY", 0);
                tag.putInt("calamityGazeZ", 0);
                tag.putInt("calamityUndeadArmy", 0);
                tag.putInt("calamityUndeadArmyX", 0);
                tag.putInt("calamityUndeadArmyY", 0);
                tag.putInt("calamityUndeadArmyZ", 0);
                tag.putInt("calamityBabyZombie", 0);
                tag.putInt("calamityBabyZombieX", 0);
                tag.putInt("calamityBabyZombieY", 0);
                tag.putInt("calamityBabyZombieZ", 0);
                tag.putInt("calamityWindArmorRemoval", 0);
                tag.putInt("calamityWindArmorRemovalX", 0);
                tag.putInt("calamityWindArmorRemovalY", 0);
                tag.putInt("calamityWindArmorRemovalZ", 0);
                tag.putInt("calamityBreeze", 0);
                tag.putInt("calamityBreezeX", 0);
                tag.putInt("calamityBreezeY", 0);
                tag.putInt("calamityBreezeZ", 0);
                tag.putInt("calamityWave", 0);
                tag.putInt("calamityWaveX", 0);
                tag.putInt("calamityWaveY", 0);
                tag.putInt("calamityWaveZ", 0);
                tag.putInt("calamityExplosion", 0);
                tag.putInt("calamityExplosionX", 0);
                tag.putInt("calamityExplosionY", 0);
                tag.putInt("calamityExplosionZ", 0);
                tag.putInt("calamityTornado", 0);
                tag.putInt("calamityTornadoX", 0);
                tag.putInt("calamityTornadoY", 0);
                tag.putInt("calamityTornadoZ", 0);

                // Set luck-related tags to 0
                tag.putInt("luckMeteor", 0);
                tag.putInt("luckLightningLOTM", 0);
                tag.putInt("luckParalysis", 0);
                tag.putInt("luckUnequipArmor", 0);
                tag.putInt("luckWarden", 0);
                tag.putInt("luckLightningMC", 0);
                tag.putInt("luckPoison", 0);
                tag.putInt("luckAttackerPoisoned", 0);
                tag.putInt("luckTornado", 0);
                tag.putInt("luckStone", 0);
                tag.putInt("luckIgnoreMobs", 0);
                tag.putInt("luckRegeneration", 0);
                tag.putInt("luckDiamonds", 0);
                tag.putInt("windMovingProjectilesCounter", 0);
                tag.putInt("luckLightningLOTMDamage", 0);
                tag.putInt("luckMeteorDamage", 0);
                tag.putInt("luckLightningMCDamage", 0);
                tag.putInt("luckStoneDamage", 0);
                tag.putInt("luckIgnoreAbility", 0);
                tag.putInt("luckDoubleDamage", 0);
                tag.putInt("luckIgnoreDamage", 0);

                // Set additional immunity and resistance tags to 0
                tag.putInt("luckStoneDamageImmunity", 0);
                tag.putInt("luckTornadoResistance", 0);
                tag.putInt("luckTornadoImmunity", 0);
                tag.putInt("luckMCLightningImmunity", 0);
                tag.putInt("calamityLOTMLightningImmunity", 0);
                tag.putInt("calamityMeteorImmunity", 0);
                tag.putInt("calamityLightningBoltMonsterResistance", 0);
                tag.putInt("calamityLightningStormImmunity", 0);
                tag.putInt("calamityLightningStormResistance", 0);
            }
        }
    }
    public void spawnCorruptionParticles(LivingEntity entity, double corruption) {
        Level level = entity.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        RandomSource random = level.getRandom();
        double x = entity.getX();
        double y = entity.getY() + 0.5;
        double z = entity.getZ();
        long gameTime = level.getGameTime();
        if (corruption >= 1 && corruption < 30 && gameTime % 100 == 0) {
            for (int i = 0; i < 10; i++) {
                double offsetX = random.nextDouble() - 0.5;
                double offsetY = random.nextDouble() - 0.5;
                double offsetZ = random.nextDouble() - 0.5;
                serverLevel.sendParticles(ParticleTypes.ASH, x + offsetX, y + offsetY, z + offsetZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        else if (corruption >= 30 && corruption < 60 && gameTime % 100 == 0) {
            DustParticleOptions orangeDust = new DustParticleOptions(new Vector3f(1.0F, 0.5F, 0.0F), 1.0F);
            for (int i = 0; i < 20; i++) {
                double offsetX = random.nextDouble() - 0.5;
                double offsetY = random.nextDouble() - 0.5;
                double offsetZ = random.nextDouble() - 0.5;
                serverLevel.sendParticles(orangeDust, x + offsetX, y + offsetY, z + offsetZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        else if (corruption >= 60 && corruption < 80 && gameTime % 60 == 0) {
            for (int i = 0; i < 30; i++) {
                double offsetX = random.nextDouble() - 0.5;
                double offsetY = random.nextDouble() - 0.5;
                double offsetZ = random.nextDouble() - 0.5;
                serverLevel.sendParticles(ParticleTypes.PORTAL, x + offsetX, y + offsetY, z + offsetZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        else if (corruption >= 80 && gameTime % 20 == 0) {
            for (int i = 0; i < 20; i++) {
                double offsetX = random.nextDouble() - 0.5;
                double offsetY = random.nextDouble() - 0.5;
                double offsetZ = random.nextDouble() - 0.5;
                serverLevel.sendParticles(ParticleTypes.SOUL, x + offsetX, y + offsetY, z + offsetZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}