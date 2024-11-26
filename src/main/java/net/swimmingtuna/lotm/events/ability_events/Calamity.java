package net.swimmingtuna.lotm.events.ability_events;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;

import java.util.Random;

public class Calamity {
    private Calamity(){}

    protected static void calamityIncarnationTornado(CompoundTag playerPersistentData, Player player) {
        //CALAMITY INCARNATION TORNADO
        if (playerPersistentData.getInt("calamityIncarnationTornado") >= 1) {
            playerPersistentData.putInt("calamityIncarnationTornado", player.getPersistentData().getInt("calamityIncarnationTornado") - 1);
        }
    }

    protected static void calamityIncarnationTsunami(CompoundTag playerPersistentData, Player player, ServerLevel level) {
        //CALAMITY INCARNATION TSUNAMI
        int calamityIncarnationTsunami = playerPersistentData.getInt("calamityIncarnationTsunami");
        if (calamityIncarnationTsunami < 1) {
            return;
        }
        playerPersistentData.putInt("calamityIncarnationTsunami", calamityIncarnationTsunami - 1);
        BlockPos playerPos = player.blockPosition();
        double radius = 23.0;
        double minRemovalRadius = 25.0;
        double maxRemovalRadius = 30.0;

        // Create a sphere of water around the player
        for (int sphereX = (int) -radius; sphereX <= radius; sphereX++) {
            for (int sphereY = (int) -radius; sphereY <= radius; sphereY++) {
                for (int sphereZ = (int) -radius; sphereZ <= radius; sphereZ++) {
                    double distance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                    if (distance <= radius) {
                        BlockPos blockPos = playerPos.offset(sphereX, sphereY, sphereZ);
                        if (level.getBlockState(blockPos).isAir() && !level.getBlockState(blockPos).is(Blocks.WATER)) {
                            level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        for (int sphereX = (int) -maxRemovalRadius; sphereX <= maxRemovalRadius; sphereX++) {
            for (int sphereY = (int) -maxRemovalRadius; sphereY <= maxRemovalRadius; sphereY++) {
                for (int sphereZ = (int) -maxRemovalRadius; sphereZ <= maxRemovalRadius; sphereZ++) {
                    double distance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                    if (distance <= maxRemovalRadius && distance >= minRemovalRadius) {
                        BlockPos blockPos = playerPos.offset(sphereX, sphereY, sphereZ);
                        if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    protected static void calamityExplosion(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int x = tag.getInt("calamityExplosionOccurrence");
        if (x >= 1 && pPlayer.tickCount % 20 == 0 && !pPlayer.level().isClientSide()) {
            pPlayer.sendSystemMessage(Component.literal("working"));
            int explosionX = tag.getInt("calamityExplosionX");
            int explosionY = tag.getInt("calamityExplosionY");
            int explosionZ = tag.getInt("calamityExplosionZ");
            int subtractX = explosionX - (int) pPlayer.getX();
            int subtractY = explosionY - (int) pPlayer.getY();
            int subtractZ = explosionZ - (int) pPlayer.getZ();
            tag.putInt("calamityExplosionOccurrence", x - 1);
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(15))) {
                if (entity instanceof Player player) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 3) {
                        player.getPersistentData().putInt("calamityExplosionImmunity", 2);
                    }
                }
            }
        }
        if (x == 1) {
            int explosionX = tag.getInt("calamityExplosionX");
            int explosionY = tag.getInt("calamityExplosionY");
            int explosionZ = tag.getInt("calamityExplosionZ");
            pPlayer.level().playSound(null, explosionX, explosionY, explosionZ, SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 5.0F, 5.0F);
            Explosion explosion = new Explosion(pPlayer.level(), null, explosionX, explosionY, explosionZ, 10.0F, true, Explosion.BlockInteraction.DESTROY);
            explosion.explode();
            explosion.finalizeExplosion(true);
            tag.putInt("calamityExplosionOccurrence", 0);
        }
    }

    protected static void calamityLightningStorm(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int stormCounter = tag.getInt("calamityLightningStormSummon");
        if (stormCounter >= 1) {
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
            tag.putInt("calamityLightningStormSummon", stormCounter - 1);
            lightningEntity.setSpeed(6);
            lightningEntity.setNoUp(true);
            lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
            int stormX = tag.getInt("calamityLightningStormX");
            int stormY = tag.getInt("calamityLightningStormY");
            int stormZ = tag.getInt("calamityLightningStormZ");
            int subtractX = (int) (stormX - pPlayer.getX());
            int subtractY = (int) (stormY - pPlayer.getY());
            int subtractZ = (int) (stormZ - pPlayer.getZ());
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(40))) {
                if (entity instanceof Player player) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 3) {
                        player.getPersistentData().putInt("calamityLightningStormImmunity", 20);
                    }
                }
            }
            double random = (Math.random() * 60) - 30;
            lightningEntity.teleportTo(stormX + random, stormY + 60, stormZ + random);
            lightningEntity.setMaxLength(60);
            pPlayer.level().addFreshEntity(lightningEntity);
        }
    }

    protected static void calamityUndeadArmy(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int x = tag.getInt("calamityUndeadArmyX");
        int y = tag.getInt("calamityUndeadArmyY");
        int z = tag.getInt("calamityUndeadArmyZ");
        int subtractX = (int) (x - pPlayer.getX());
        int subtractY = (int) (y - pPlayer.getY());
        int subtractZ = (int) (z - pPlayer.getZ());
        int surfaceY = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;
        int undeadArmyCounter = tag.getInt("calamityUndeadArmyCounter");
        if (undeadArmyCounter >= 1) {
            Random random = new Random();
            ItemStack leatherHelmet = new ItemStack(Items.LEATHER_HELMET);
            ItemStack leatherChestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
            ItemStack leatherLeggings = new ItemStack(Items.LEATHER_LEGGINGS);
            ItemStack leatherBoots = new ItemStack(Items.LEATHER_BOOTS);
            ItemStack ironHelmet = new ItemStack(Items.IRON_HELMET);
            ItemStack ironChestplate = new ItemStack(Items.IRON_CHESTPLATE);
            ItemStack ironLeggings = new ItemStack(Items.IRON_LEGGINGS);
            ItemStack ironBoots = new ItemStack(Items.IRON_BOOTS);
            ItemStack diamondHelmet = new ItemStack(Items.DIAMOND_HELMET);
            ItemStack diamondChestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
            ItemStack diamondLeggings = new ItemStack(Items.DIAMOND_LEGGINGS);
            ItemStack diamondBoots = new ItemStack(Items.DIAMOND_BOOTS);
            ItemStack netheriteHelmet = new ItemStack(Items.NETHERITE_HELMET);
            ItemStack netheriteChestplate = new ItemStack(Items.NETHERITE_CHESTPLATE);
            ItemStack netheriteLeggings = new ItemStack(Items.NETHERITE_LEGGINGS);
            ItemStack netheriteBoots = new ItemStack(Items.NETHERITE_BOOTS);
            ItemStack enchantedBow = new ItemStack(Items.BOW);
            ItemStack woodSword = new ItemStack(Items.WOODEN_SWORD);
            ItemStack ironSword = new ItemStack(Items.IRON_SWORD);
            ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
            ItemStack netheriteSword = new ItemStack(Items.NETHERITE_SWORD);
            Zombie zombie = new Zombie(EntityType.ZOMBIE, pPlayer.level());
            Skeleton skeleton = new Skeleton(EntityType.SKELETON, pPlayer.level());
            int randomPos = (int) ((Math.random() * 24) - 12);
            if (random.nextInt(11) == 10) {
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(zombie);
            }
            if (random.nextInt(10) == 9) {
                zombie.setPos(x + randomPos, surfaceY, z + randomPos);
                zombie.setItemSlot(EquipmentSlot.HEAD, leatherHelmet);
                zombie.setItemSlot(EquipmentSlot.CHEST, leatherChestplate);
                zombie.setItemSlot(EquipmentSlot.LEGS, leatherLeggings);
                zombie.setItemSlot(EquipmentSlot.FEET, leatherBoots);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, woodSword);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(zombie);
            }
            if (random.nextInt(10) == 8) {
                zombie.setPos(x + randomPos, surfaceY, z + randomPos);
                zombie.setItemSlot(EquipmentSlot.HEAD, ironHelmet);
                zombie.setItemSlot(EquipmentSlot.CHEST, ironChestplate);
                zombie.setItemSlot(EquipmentSlot.LEGS, ironLeggings);
                zombie.setItemSlot(EquipmentSlot.FEET, ironBoots);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, ironSword);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(zombie);
            }
            if (random.nextInt(10) == 7) {
                zombie.setPos(x + randomPos, surfaceY, z + randomPos);
                zombie.setItemSlot(EquipmentSlot.HEAD, diamondHelmet);
                zombie.setItemSlot(EquipmentSlot.CHEST, diamondChestplate);
                zombie.setItemSlot(EquipmentSlot.LEGS, diamondLeggings);
                zombie.setItemSlot(EquipmentSlot.FEET, diamondBoots);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, diamondSword);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(zombie);
            }
            if (random.nextInt(10) == 6) {
                zombie.setPos(x + randomPos, surfaceY, z + randomPos);
                zombie.setItemSlot(EquipmentSlot.HEAD, netheriteHelmet);
                zombie.setItemSlot(EquipmentSlot.CHEST, netheriteChestplate);
                zombie.setItemSlot(EquipmentSlot.LEGS, netheriteLeggings);
                zombie.setItemSlot(EquipmentSlot.FEET, netheriteBoots);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, netheriteSword);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(zombie);
            }
            if (random.nextInt(20) == 5) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(skeleton);
            }
            if (random.nextInt(20) == 4) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.HEAD, leatherHelmet);
                skeleton.setItemSlot(EquipmentSlot.CHEST, leatherChestplate);
                skeleton.setItemSlot(EquipmentSlot.LEGS, leatherLeggings);
                skeleton.setItemSlot(EquipmentSlot.FEET, leatherBoots);
                enchantedBow.enchant(Enchantments.POWER_ARROWS, 1);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(skeleton);
            }
            if (random.nextInt(20) == 3) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.HEAD, ironHelmet);
                skeleton.setItemSlot(EquipmentSlot.CHEST, ironChestplate);
                skeleton.setItemSlot(EquipmentSlot.LEGS, ironLeggings);
                skeleton.setItemSlot(EquipmentSlot.FEET, ironBoots);
                enchantedBow.enchant(Enchantments.POWER_ARROWS, 2);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(skeleton);
            }
            if (random.nextInt(20) == 2) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.HEAD, diamondHelmet);
                skeleton.setItemSlot(EquipmentSlot.CHEST, diamondChestplate);
                skeleton.setItemSlot(EquipmentSlot.LEGS, diamondLeggings);
                skeleton.setItemSlot(EquipmentSlot.FEET, diamondBoots);
                enchantedBow.enchant(Enchantments.POWER_ARROWS, 3);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(skeleton);
            }
            if (random.nextInt(20) == 1) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.HEAD, netheriteHelmet);
                skeleton.setItemSlot(EquipmentSlot.CHEST, netheriteChestplate);
                skeleton.setItemSlot(EquipmentSlot.LEGS, netheriteLeggings);
                skeleton.setItemSlot(EquipmentSlot.FEET, netheriteBoots);
                enchantedBow.enchant(Enchantments.POWER_ARROWS, 4);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 6) {
                        if (entity != null) {
                            zombie.setTarget(entity);
                        }
                    }
                }
                pPlayer.level().addFreshEntity(skeleton);
            }
            zombie.setDropChance(EquipmentSlot.HEAD, 0.0F);
            zombie.setDropChance(EquipmentSlot.CHEST, 0.0F);
            zombie.setDropChance(EquipmentSlot.LEGS, 0.0F);
            zombie.setDropChance(EquipmentSlot.FEET, 0.0F);
            skeleton.setDropChance(EquipmentSlot.HEAD, 0.0F);
            skeleton.setDropChance(EquipmentSlot.CHEST, 0.0F);
            skeleton.setDropChance(EquipmentSlot.LEGS, 0.0F);
            skeleton.setDropChance(EquipmentSlot.FEET, 0.0F);
            tag.putInt("calamityUndeadArmyCounter", tag.getInt("calamityUndeadArmyCounter") - 1);
        }
    }
}
