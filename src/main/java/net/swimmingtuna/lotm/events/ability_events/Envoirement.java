package net.swimmingtuna.lotm.events.ability_events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Earthquake;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.ExtremeColdness;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.LightningStorm;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.SailorLightning;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Envoirement {
    
    protected static void earthquake(Player player, int sequence) {
        int sailorEarthquake = player.getPersistentData().getInt("sailorEarthquake");
        if (sailorEarthquake >= 1) {
            int radius = 75 - (sequence * 6);
            if (sailorEarthquake % 20 == 0) {
                for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate((radius)))) {
                    if (entity != player) {
                        if (entity.onGround()) {
                            entity.hurt(player.damageSources().fall(), 35 - (sequence * 5));
                        }
                    }
                }
            }
            if (sailorEarthquake % 2 == 0) {
                AABB checkArea = player.getBoundingBox().inflate(radius);
                Random random = new Random();
                for (BlockPos blockPos : BlockPos.betweenClosed(
                        new BlockPos((int) checkArea.minX, (int) checkArea.minY, (int) checkArea.minZ),
                        new BlockPos((int) checkArea.maxX, (int) checkArea.maxY, (int) checkArea.maxZ))) {

                    if (!player.level().getBlockState(blockPos).isAir() && Earthquake.isOnSurface(player.level(), blockPos)) {
                        if (random.nextInt(20) == 1) {
                            BlockState blockState = player.level().getBlockState(blockPos); // Use the desired block type here
                            if (player.level() instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                                        blockPos.getX(),
                                        blockPos.getY() + 1,
                                        blockPos.getZ(),
                                        0, 0.0, 0.0, 0, 0);
                            }
                        }
                        if (random.nextInt(4000) == 1) { // 50% chance to destroy a block
                            player.level().destroyBlock(blockPos, false);
                        } else if (random.nextInt(10000) == 2) { // 10% chance to spawn a stone entity
                            StoneEntity stoneEntity = new StoneEntity(player.level(), player);
                            ScaleData scaleData = ScaleTypes.BASE.getScaleData(stoneEntity);
                            stoneEntity.teleportTo(blockPos.getX(), blockPos.getY() + 3, blockPos.getZ());
                            stoneEntity.setDeltaMovement(0, (3 + (Math.random() * (6 - 3))), 0);
                            stoneEntity.setStoneYRot((int) (Math.random() * 18));
                            stoneEntity.setStoneXRot((int) (Math.random() * 18));
                            scaleData.setScale((float) (1 + (Math.random()) * 2.0f));
                            player.level().addFreshEntity(stoneEntity);
                        }
                    }
                }
            }
            player.getPersistentData().putInt("sailorEarthquake", sailorEarthquake - 1);
        }
    }

    protected static void extremeColdness(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //EXTREME COLDNESS
        int extremeColdness = playerPersistentData.getInt("sailorExtremeColdness");
        if (extremeColdness >= 150 - (holder.getCurrentSequence()) * 20) {
            playerPersistentData.putInt("sailorExtremeColdness", 0);
            extremeColdness = 0;
        }
        if (extremeColdness < 1) {
            return;
        }
        playerPersistentData.putInt("sailorExtremeColdness", extremeColdness + 1);

        AABB areaOfEffect = player.getBoundingBox().inflate(extremeColdness);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, areaOfEffect);
        for (LivingEntity entity : entities) {
            if (entity != player) {
                int affectedBySailorExtremeColdness = entity.getPersistentData().getInt("affectedBySailorExtremeColdness");
                entity.getPersistentData().putInt("affectedBySailorExtremeColdness", affectedBySailorExtremeColdness + 1);
                entity.setTicksFrozen(1);
            }
        }
        List<Entity> entities1 = player.level().getEntitiesOfClass(Entity.class, areaOfEffect); //test thsi
        for (Entity entity : entities1) {
            if (!(entity instanceof LivingEntity)) {
                int affectedBySailorColdness = entity.getPersistentData().getInt("affectedBySailorColdness");
                entity.getPersistentData().putInt("affectedBySailorColdness", affectedBySailorColdness + 1);
                if (affectedBySailorColdness == 10) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x() / 5, entity.getDeltaMovement().y() / 5, entity.getDeltaMovement().z() / 5);
                    entity.hurtMarked = true;
                    entity.getPersistentData().putInt("affectedBySailorColdness", 0);
                }
            }
        }

        // Additional part: Turn the top 3 surface blocks within radius into ice
        BlockPos playerPos = player.blockPosition();
        int radius = extremeColdness; // Adjust the division factor as needed
        int blocksToProcessPerTick = 2000;  // Adjust as needed
        int processedBlocks = 0;

        // Cache for heightmap lookups
        Map<BlockPos, Integer> heightMapCache = new HashMap<>();

        for (int dx = -radius; dx <= radius && processedBlocks < blocksToProcessPerTick; dx++) {
            for (int dz = -radius; dz <= radius && processedBlocks < blocksToProcessPerTick; dz++) {
                BlockPos surfacePos = playerPos.offset(dx, 0, dz);

                // Check cache first
                Integer surfaceY = heightMapCache.get(surfacePos);
                if (surfaceY == null) {
                    // If not cached, calculate and store in cache
                    surfaceY = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, surfacePos).getY();
                    heightMapCache.put(surfacePos, surfaceY);
                }

                for (int dy = 0; dy < 3; dy++) {
                    BlockPos targetPos = new BlockPos(surfacePos.getX(), surfaceY - dy, surfacePos.getZ());
                    if (ExtremeColdness.canFreezeBlock(player, targetPos)) {
                        player.level().setBlockAndUpdate(targetPos, Blocks.ICE.defaultBlockState());
                        processedBlocks++;
                    }
                }
            }
        }
    }

    protected static void hurricane(CompoundTag playerPersistentData, Player player) {
        //HURRICANE
        boolean sailorHurricaneRain = playerPersistentData.getBoolean("sailorHurricaneRain");
        BlockPos pos = new BlockPos((int) (player.getX() + (Math.random() * 100 - 100)), (int) (player.getY() - 100), (int) (player.getZ() + (Math.random() * 300 - 300)));
        int hurricane = playerPersistentData.getInt("sailorHurricane");
        if (hurricane < 1) {
            return;
        }
        if (sailorHurricaneRain) {
            playerPersistentData.putInt("sailorHurricane", hurricane - 1);
            if (hurricane == 600 && player.level() instanceof ServerLevel serverLevel) {
                serverLevel.setWeatherParameters(0, 700, true, true);
            }
            if (hurricane % 5 == 0) {
                SailorLightning.lightningHigh(player, player.level());
            }
            if (hurricane == 600 || hurricane == 300) {
                for (int i = 0; i < 5; i++) {
                    TornadoEntity tornado = new TornadoEntity(player.level(), player, 0, 0, 0);
                    tornado.teleportTo(pos.getX(), pos.getY() + 100, pos.getZ());
                    tornado.setTornadoRandom(true);
                    tornado.setTornadoHeight(300);
                    tornado.setTornadoRadius(30);
                    tornado.setTornadoPickup(false);
                    player.level().addFreshEntity(tornado);
                }
            }
        }
        if (!sailorHurricaneRain && player.level() instanceof ServerLevel serverLevel && hurricane == 600) {
            playerPersistentData.putInt("sailorHurricane", hurricane - 1);
            serverLevel.setWeatherParameters(0, 700, true, false);
        }
    }

    protected static void lightningStorm(Player player, CompoundTag playerPersistentData, Style style, BeyonderHolder holder) {
        //LIGHTNING STORM
        double distance = player.getPersistentData().getDouble("sailorLightningStormDistance");
        if (distance > 300) {
            playerPersistentData.putDouble("sailorLightningStormDistance", 0);
            player.displayClientMessage(Component.literal("Storm Radius Is 0").withStyle(style), true);
        }
        int tyrantVer = playerPersistentData.getInt("sailorLightningStormTyrant");
        int sailorMentioned = playerPersistentData.getInt("tyrantMentionedInChat");
        int sailorLightningStorm1 = playerPersistentData.getInt("sailorLightningStorm1");
        int x1 = playerPersistentData.getInt("sailorStormVecX1");
        int y1 = playerPersistentData.getInt("sailorStormVecY1");
        int z1 = playerPersistentData.getInt("sailorStormVecZ1");
        if (sailorMentioned >= 1) {
            playerPersistentData.putInt("tyrantMentionedInChat", sailorMentioned - 1);
            if (sailorLightningStorm1 >= 1) {
                for (int i = 0; i < (tyrantVer >= 1 ? 8 : 4); i++) {
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
                    lightningEntity.setSpeed(10.0f);
                    lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                    lightningEntity.setMaxLength(30);
                    lightningEntity.setOwner(player);
                    lightningEntity.setNoUp(true);
                    lightningEntity.teleportTo(x1 + ((Math.random() * 300) - (double) 300 / 2), y1 + 80, z1 + ((Math.random() * 300) - (double) 300 / 2));
                    player.level().addFreshEntity(lightningEntity);
                }
                if (tyrantVer >= 1) {
                    playerPersistentData.putInt("sailorLightningStormTyrant", tyrantVer - 1);
                }
                playerPersistentData.putInt("sailorLightningStorm1", sailorLightningStorm1 - 1);
            }
        }
        int sailorLightningStorm = playerPersistentData.getInt("sailorLightningStorm");
        int stormVec = playerPersistentData.getInt("sailorStormVec");
        double sailorStormVecX = playerPersistentData.getInt("sailorStormVecX");
        double sailorStormVecY = playerPersistentData.getInt("sailorStormVecY");
        double sailorStormVecZ = playerPersistentData.getInt("sailorStormVecZ");
        if (sailorLightningStorm >= 1) {
            for (int i = 0; i < 4; i++) {
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
                lightningEntity.setSpeed(10.0f);
                lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                lightningEntity.setMaxLength(30);
                lightningEntity.setOwner(player);
                lightningEntity.setNoUp(true);
                lightningEntity.teleportTo(sailorStormVecX + ((Math.random() * distance) - distance / 2), sailorStormVecY + 80, sailorStormVecZ + ((Math.random() * distance) - distance / 2));
                player.level().addFreshEntity(lightningEntity);
            }
            playerPersistentData.putInt("sailorLightningStorm", sailorLightningStorm - 1);
        }
        if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 3 && player.getMainHandItem().getItem() instanceof LightningStorm) {
            if (player.isShiftKeyDown()) {
                playerPersistentData.putInt("sailorStormVec", stormVec + 10);
                player.displayClientMessage(Component.literal("Sailor Storm Spawn Distance is " + stormVec).withStyle(style), true);
            }
            if (stormVec > 301) {
                player.displayClientMessage(Component.literal("Sailor Storm Spawn Distance is 0").withStyle(style), true);
                playerPersistentData.putInt("sailorStormVec", 0);
                stormVec = 0;
            }
        }
    }
}
