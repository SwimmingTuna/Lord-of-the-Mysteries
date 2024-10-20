package net.swimmingtuna.lotm.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.*;
import net.swimmingtuna.lotm.events.ModEvents;
import net.swimmingtuna.lotm.events.custom_events.ModEventFactory;
import net.swimmingtuna.lotm.events.custom_events.ProjectileEvent;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.SoundInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Earthquake;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.ExtremeColdness;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.SailorLightning;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;
import java.util.*;

import static net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.SirenSongHarm.isInsideSphere;

public class TickEventUtil {

    public static void tickEventUtil(PlayerMobEntity playerMobEntity) {
        Map<String, Long> times = new HashMap<>();
        CompoundTag livingEntityPersistentData = playerMobEntity.getPersistentData();
        ServerLevel serverLevel = (ServerLevel) playerMobEntity.level();
        AttributeInstance corruption = playerMobEntity.getAttribute(ModAttributes.CORRUPTION.get());
        AttributeInstance luck = playerMobEntity.getAttribute(ModAttributes.LOTM_LUCK.get());
        AttributeInstance misfortune = playerMobEntity.getAttribute(ModAttributes.MISFORTUNE.get());
        int sequence = playerMobEntity.getCurrentSequence();
        decrementMonsterAttackEvent(playerMobEntity);
        monsterLuckIgnoreMobs(playerMobEntity);
        monsterLuckPoisonAttacker(playerMobEntity);
        calamityExplosion(playerMobEntity);
        calamityLightningStorm(playerMobEntity);
        calamityUndeadArmy(playerMobEntity);
        PlayerMobCorruptionAndLuckHandler.corruptionAndLuckManagers(serverLevel, misfortune, corruption, playerMobEntity, luck, sequence);
        nightmare(playerMobEntity, livingEntityPersistentData);
        envisionKingdom(livingEntityPersistentData, playerMobEntity, serverLevel);
        calamityIncarnationTornado(livingEntityPersistentData, playerMobEntity);
        psychologicalInvisibility(playerMobEntity, livingEntityPersistentData);
        windManipulationSense(livingEntityPersistentData, playerMobEntity);
        windManipulationCushion(livingEntityPersistentData, playerMobEntity);
        windManipulationGuide(livingEntityPersistentData, playerMobEntity);
        sailorLightningTravel(playerMobEntity);
        dreamIntoReality(playerMobEntity);
        //consciousnessStroll is meant to be here but no real use for entities
        prophesizeTeleportation(livingEntityPersistentData, playerMobEntity);
        projectileEvent(playerMobEntity);
        envisionBarrier(playerMobEntity);
        envisionLife(playerMobEntity);
        manipulateMovement(playerMobEntity, serverLevel);
        acidicRain(playerMobEntity, sequence);
        calamityIncarnationTsunami(livingEntityPersistentData, playerMobEntity, serverLevel);
        earthquake(playerMobEntity, sequence);
        extremeColdness(livingEntityPersistentData, playerMobEntity);
        hurricane(livingEntityPersistentData, playerMobEntity);
        lightningStorm(playerMobEntity, livingEntityPersistentData);
        matterAccelerationSelf(playerMobEntity);
        ragingBlows(livingEntityPersistentData, playerMobEntity);
        rainEyes(playerMobEntity);
        sirenSongs(livingEntityPersistentData, playerMobEntity, sequence);
        starOfLightning(playerMobEntity, livingEntityPersistentData);
        tsunami(livingEntityPersistentData, playerMobEntity);
        waterSphereCheck(playerMobEntity, serverLevel);
        windManipulationFlight(playerMobEntity, livingEntityPersistentData);
        sirenSongParticles(playerMobEntity, sequence);
    }

    private static void lightningStorm(PlayerMobEntity player, CompoundTag playerPersistentData) {
        //LIGHTNING STORM
        double distance = player.getPersistentData().getDouble("sailorLightningStormDistance");
        if (distance > 300) {
            playerPersistentData.putDouble("sailorLightningStormDistance", 0);
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
        if (player.getCurrentPathway() == BeyonderClassInit.SAILOR && player.getCurrentSequence() <= 3) {
            playerPersistentData.putInt("sailorStormVec", 50);
            if (stormVec > 300) {
                playerPersistentData.putInt("sailorStormVec", 0);
                stormVec = 0;
            }
        }
    }

    private static void matterAccelerationSelf(PlayerMobEntity player) {
        //MATTER ACCELERATION SELF
        if (player.isSpectator()) return;
        int matterAccelerationDistance = player.getPersistentData().getInt("tyrantSelfAcceleration");
        int blinkDistance = player.getPersistentData().getInt("BlinkDistance");
        if (player.getTarget() != null) {
            int distance = (int) player.getTarget().distanceTo(player);
            player.getPersistentData().putInt("tyrantSelfAcceleration", distance + (distance / 10));
            player.getPersistentData().putInt("BlinkDistance", distance);
        }
        if (player.tickCount % 500 == 0) {
            if (player.getTarget() == null) {
                player.getPersistentData().putInt("BlinkDistance", 200);
            }
        }
        if (matterAccelerationDistance >= 1000) {
            player.getPersistentData().putInt("tyrantSelfAcceleration", 0);
        }
        if (blinkDistance > 200) {
            player.getPersistentData().putInt("BlinkDistance", 0);
        }
    }

    private static void earthquake(PlayerMobEntity player, int sequence) {
        //EARTHQUAKE
        int sailorEarthquake = player.getPersistentData().getInt("sailorEarthquake");
        if (sailorEarthquake >= 0) {
            player.getPersistentData().putInt("sailorEarthquake", sailorEarthquake - 1);
        }
        if (!(sailorEarthquake % 20 == 0 && sailorEarthquake != 0 || sailorEarthquake == 1)) {
            return;
        }
        int radius = 100 - (sequence * 10);
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate((radius)))) {
            if (entity != player) {
                if (entity.onGround()) {
                    entity.hurt(player.damageSources().fall(), 35 - (sequence * 5));
                }
            }
        }
        AABB checkArea = player.getBoundingBox().inflate(radius);
        Random random = new Random();
        for (BlockPos blockPos : BlockPos.betweenClosedStream(checkArea).toList()) {

            if (!player.level().getBlockState(blockPos).isAir() && Earthquake.isOnSurface(player.level(), blockPos)) {
                if (random.nextInt(200) == 1) { // 50% chance to destroy a block
                    player.level().destroyBlock(blockPos, false);
                } else if (random.nextInt(200) == 2) { // 10% chance to spawn a stone entity
                    StoneEntity stoneEntity = new StoneEntity(player.level(), player);
                    ScaleData scaleData = ScaleTypes.BASE.getScaleData(stoneEntity);
                    stoneEntity.teleportTo(blockPos.getX(), blockPos.getY() + 3, blockPos.getZ());
                    stoneEntity.setDeltaMovement(0, 3 + Math.random() * 3, 0);
                    stoneEntity.setStoneYRot((int) (Math.random() * 18));
                    stoneEntity.setStoneXRot((int) (Math.random() * 18));
                    scaleData.setScale((float) (1 + Math.random() * 2.0f));
                    player.level().addFreshEntity(stoneEntity);
                }
            }
        }
    }

    private static void extremeColdness(CompoundTag playerPersistentData, PlayerMobEntity player) {
        //EXTREME COLDNESS
        int extremeColdness = playerPersistentData.getInt("sailorExtremeColdness");
        if (extremeColdness >= 150 - (player.getCurrentSequence()) * 20) {
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

    private static void hurricane(CompoundTag playerPersistentData, PlayerMobEntity player) {
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
                SailorLightning.shootLineBlockHighPM(player, player.level());
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

    private static void acidicRain(PlayerMobEntity player, int sequence) {
        //ACIDIC RAIN
        int acidicRain = player.getPersistentData().getInt("sailorAcidicRain");
        AttributeInstance particleAttribute = player.getAttribute(ModAttributes.PARTICLE_HELPER.get());
        if (acidicRain <= 0 || particleAttribute.getValue() != 1) {
            return;
        }
        player.getPersistentData().putInt("sailorAcidicRain", acidicRain + 1);
        double radius1 = 50 - (sequence * 7);
        double radius2 = 10 - sequence;


        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius1))) {
            if (entity == player) {
                continue;
            }
            if (entity.hasEffect(MobEffects.POISON)) {
                int poisonAmp = entity.getEffect(MobEffects.POISON).getAmplifier();
                if (poisonAmp == 0) {
                    entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1, false, false));
                }
            } else {
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1, false, false));
            }
        }

        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius2))) {
            if (entity == player) {
                continue;
            }
            if (entity.hasEffect(MobEffects.POISON)) {
                int poisonAmp = entity.getEffect(MobEffects.POISON).getAmplifier();
                if (poisonAmp <= 2) {
                    entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2, false, false));
                }
            } else {
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2, false, false));
            }
        }


        if (acidicRain > 300) {
            player.getPersistentData().putInt("sailorAcidicRain", 0);
            particleAttribute.setBaseValue(0);
        }
    }

    private static void calamityIncarnationTsunami(CompoundTag playerPersistentData, PlayerMobEntity player, ServerLevel level) {
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

    private static void envisionBarrier(PlayerMobEntity player) {
        //ENVISION BARRIER
        if (player.getCurrentSequence() != 0) {
            return;
        }
        int barrierRadius = player.getPersistentData().getInt("BarrierRadius");
        for (Projectile projectile : player.level().getEntitiesOfClass(Projectile.class, player.getBoundingBox().inflate(100))) {
            if (projectile.distanceTo(player) <= 50) {
                List<Vec3> trajectory = ModEvents.predictProjectileTrajectory(projectile, player);
                BlockPos playerPos = player.blockPosition();
                int radius = 20;
                List<BlockPos> blocksInRadius = new ArrayList<>();

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos newPos = playerPos.offset(x, y, z);
                            if (playerPos.distSqr(newPos) <= radius * radius) {
                                blocksInRadius.add(newPos);
                            }
                        }
                    }
                }
                if (trajectory.contains(blocksInRadius)) {
                    player.getPersistentData().putInt("BarrierRadius", (int) (projectile.distanceTo(player) - 5));
                }
            }
        }
        if (barrierRadius > 100) {
            barrierRadius = 0;
        }
        player.getPersistentData().putInt("BarrierRadius", barrierRadius);
    }

    private static void envisionLife(LivingEntity player) {
        //ENVISION LIFE
        int waitMakeLifeCounter = player.getPersistentData().getInt("waitMakeLifeTimer");
        if (waitMakeLifeCounter >= 1) {
            waitMakeLifeCounter++;
        }
        if (waitMakeLifeCounter >= 600) {
            waitMakeLifeCounter = 0;
        }
        player.getPersistentData().putInt("waitMakeLifeTimer", waitMakeLifeCounter);
    }

    private static void manipulateMovement(LivingEntity player, Level level) {
        //MANIPULATE MOVEMENT
        if (!player.getPersistentData().getBoolean("manipulateMovementBoolean")) {
            return;
        }
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(250))) {
            if (entity == player || !entity.hasEffect(ModEffects.MANIPULATION.get())) {
                continue;
            }
            int targetX = player.getPersistentData().getInt("manipulateMovementX");
            int targetY = player.getPersistentData().getInt("manipulateMovementY");
            int targetZ = player.getPersistentData().getInt("manipulateMovementZ");

            if (entity.distanceToSqr(targetX, targetY, targetZ) <= 8) {
                entity.removeEffect(ModEffects.MANIPULATION.get());
                continue;
            }

            if (!(entity instanceof Player)) {
                if (entity instanceof Mob mob) {
                    mob.getNavigation().moveTo(targetX, targetY, targetZ, 1.7);
                }
                continue;
            }
            // Existing logic for players
            double entityX = entity.getX();
            double entityY = entity.getY();
            double entityZ = entity.getZ();

            double dx = targetX - entityX;
            double dy = targetY - entityY;
            double dz = targetZ - entityZ;

            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance > 0) {
                dx /= distance;
                dy /= distance;
                dz /= distance;
            }

            double speed = 3.0 / 20;

            BlockPos frontBlockPos = new BlockPos((int) (entityX + dx), (int) (entityY + dy), (int) (entityZ + dz));
            BlockPos frontBlockPos1 = new BlockPos((int) (entityX + dx * 2), (int) (entityY + dy * 2), (int) (entityZ + dz * 2));
            boolean pathIsClear = level.getBlockState(frontBlockPos).isAir() && level.getBlockState(frontBlockPos1).isAir();

            if (pathIsClear) {
                entity.setDeltaMovement(dx * speed, Math.min(0, dy * speed), dz * speed);
            } else {
                entity.setDeltaMovement(dx * speed, 0.25, dz * speed);
            }
        }
    }

    private static void projectileEvent(PlayerMobEntity player) {
        //PROJECTILE EVENT
        Projectile projectile = BeyonderUtil.getLivingEntitiesProjectile(player);
        if (projectile == null) return;
        ProjectileEvent.ProjectileControlEvent projectileEvent = new ProjectileEvent.ProjectileControlEvent(projectile);
        ModEventFactory.onSailorShootProjectile(projectile);

        //MATTER ACCELERATION ENTITIES
        if (projectile.getPersistentData().getInt("matterAccelerationEntities") >= 10) {
            double movementX = Math.abs(projectile.getDeltaMovement().x());
            double movementY = Math.abs(projectile.getDeltaMovement().y());
            double movementZ = Math.abs(projectile.getDeltaMovement().z());
            if (movementX >= 6 || movementY >= 6 || movementZ >= 6) {
                BlockPos entityPos = projectile.blockPosition();
                for (int x = -2; x <= 2; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -2; z <= 2; z++) {
                            BlockPos pos = entityPos.offset(x, y, z);

                            // Remove the block (replace with air)
                            projectile.level().setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
                }
                for (LivingEntity entity1 : projectile.level().getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(5))) {
                    if (entity1 instanceof Player playerEntity) {
                        if (player.getCurrentPathway() != BeyonderClassInit.SAILOR && player.getCurrentSequence() == 0) {
                            playerEntity.hurt(playerEntity.damageSources().lightningBolt(), 10);
                        }
                    } else {
                        entity1.hurt(entity1.damageSources().lightningBolt(), 10);
                    }
                }
            }
        }


        //SAILOR PASSIVE CHECK FROM HERE
        LivingEntity target = projectileEvent.getTarget(75, 0);
        if (target != null) {
            if (player.getCurrentPathway() == BeyonderClassInit.SAILOR && player.getCurrentSequence() <= 7 && player.getPersistentData().getBoolean("sailorProjectileMovement")) {
                projectileEvent.addMovement(projectile, (target.getX() - projectile.getX()) * 0.1, (target.getY() - projectile.getY()) * 0.1, (target.getZ() - projectile.getZ()) * 0.1);
                projectile.hurtMarked = true;
            }
        }

        //MONSTER CALCULATION PASSIVE
        if (target != null) {
            if (player.getCurrentPathway() == BeyonderClassInit.MONSTER && player.getCurrentSequence() <= 8 && player.getPersistentData().getBoolean("monsterProjectileControl")) {
                projectileEvent.addMovement(projectile, (target.getX() - projectile.getX()) * 0.1, (target.getY() - projectile.getY()) * 0.1, (target.getZ() - projectile.getZ()) * 0.1);
                projectile.hurtMarked = true;
            }
        }

    }

    private static void prophesizeTeleportation(CompoundTag playerPersistentData, LivingEntity livingEntity) {
        //PROPHESIZE TELEPORT BLOCK/PLAYER
        if (playerPersistentData.getInt("prophesizeTeleportationCounter") >= 1) {
            playerPersistentData.putInt("prophesizeTeleportationCounter", playerPersistentData.getInt("prophesizeTeleportationCounter") - 1);
        }
        if (playerPersistentData.getInt("prophesizeTeleportationCounter") == 1) {
            playerPersistentData.putInt("prophesizeTeleportationCounter", playerPersistentData.getInt("prophesizeTeleportationCounter") - 1);
            int x = playerPersistentData.getInt("prophesizeTeleportX");
            int y = playerPersistentData.getInt("prophesizeTeleportY");
            int z = playerPersistentData.getInt("prophesizeTeleportZ");
            livingEntity.teleportTo(x, y, z);
        }
    }

    private static void stopFlying(LivingEntity player) {
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        player.getPersistentData().putBoolean("CanFly", false);
        CompoundTag compoundTag = player.getPersistentData();
        int mindscape = compoundTag.getInt("inMindscape");
        if (mindscape >= 1) {
            //flying is false
        }
        dreamIntoReality.setBaseValue(1);
        //set flyingspeed 0.05
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
        scaleData.setTargetScale(1);
        scaleData.markForSync(true);
    }

    private static void dreamIntoReality(PlayerMobEntity player) {
        //DREAM INTO REALITY
        boolean canFly = player.getPersistentData().getBoolean("CanFly");
        if (!canFly) {
            return;
        }
        if (player.getSpirituality() >= 15) {
            player.useSpirituality(15);
        }
        if (player.getSpirituality() <= 15) {
            stopFlying(player);
        }
        if (player.getCurrentSequence() == 2) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4, false, false));
        }
        if (player.getCurrentSequence() == 1) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4, false, false));
        }
        if (player.getCurrentSequence() == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 4, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 5, false, false));
        }
    }

    private static void sailorLightningTravel(LivingEntity player) {
        //SAILOR LIGHTNING TRAVEL
        if (player.getPersistentData().getInt("sailorLightningTravel") >= 1) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 3, 1, false, false));
            player.getPersistentData().putInt("sailorLightningTravel", player.getPersistentData().getInt("sailorLightningTravel") - 1);
        }
    }

    private static void windManipulationGuide(CompoundTag playerPersistentData, PlayerMobEntity player) {
        //WIND MANIPULATION GLIDE
        int regularFlight = playerPersistentData.getInt("sailorFlight");
        boolean enhancedFlight = playerPersistentData.getBoolean("sailorFlight1");
        if (
                player.getCurrentPathway() == BeyonderClassInit.SAILOR &&
                        player.getCurrentSequence() <= 7 &&
                        player.fallDistance >= 10 &&
                        player.getDeltaMovement().y() < 0 &&
                        !enhancedFlight &&
                        regularFlight == 0
        ) {
            Vec3 movement = player.getDeltaMovement();
            double deltaX = Math.cos(Math.toRadians(player.getYRot() + 90)) * 0.06;
            double deltaZ = Math.sin(Math.toRadians(player.getYRot() + 90)) * 0.06;
            player.setDeltaMovement(movement.x + deltaX, -0.05, movement.z + deltaZ);
            player.resetFallDistance();
            player.hurtMarked = true;
        }
    }

    private static void windManipulationCushion(CompoundTag playerPersistentData, LivingEntity player) {
        //WIND MANIPULATION CUSHION
        int cushion = playerPersistentData.getInt("windManipulationCushion");
        if (cushion >= 1) {
            playerPersistentData.putInt("windManipulationCushion", cushion - 1);
            player.resetFallDistance();
        }
        if (cushion >= 80 && player.getDeltaMovement().y <= 0) {
            AttributeInstance cushionParticles = player.getAttribute(ModAttributes.PARTICLE_HELPER3.get());
            cushionParticles.setBaseValue(1.0f);
            player.setDeltaMovement(player.getDeltaMovement().x(), player.getDeltaMovement().y() * 0.9, player.getDeltaMovement().z());
            player.hurtMarked = true;
        }
        if (cushion == 79) {
            player.setDeltaMovement(player.getLookAngle().scale(2.0f));
            player.hurtMarked = true;
            player.resetFallDistance();
            AttributeInstance cushionParticles = player.getAttribute(ModAttributes.PARTICLE_HELPER3.get());
            cushionParticles.setBaseValue(0.0f);
        }
    }

    private static void windManipulationSense(CompoundTag playerPersistentData, PlayerMobEntity player) {
        //WIND MANIPULATION SENSE
        boolean windManipulationSense = playerPersistentData.getBoolean("windManipulationSense");
        if (!windManipulationSense) {
            return;
        }
        if (player.useSpirituality(2)) return;
        double radius = 100 - (player.getCurrentSequence() * 10);
        for (LivingEntity otherEntity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius))) {
            if (otherEntity == player) {
                continue;
            }
            if (otherEntity.getMaxHealth() >= player.getMaxHealth()) {
                player.setTarget(otherEntity);
            }
        }
    }

    private static void envisionKingdom(CompoundTag playerPersistentData, PlayerMobEntity player, ServerLevel serverLevel) {
        //ENVISION KINGDOM

        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        int mindScape = playerPersistentData.getInt("inMindscape");
        if (mindScape < 1) return;
        playerPersistentData.putInt("inMindscape", mindScape + 1);
        if (mindScape >= 1200) {
            playerPersistentData.putInt("inMindscape", 0);
        }
        int mindscapeAbilities = playerPersistentData.getInt("mindscapeAbilities");
        if (mindscapeAbilities >= 1) { //
            player.setSpirituality(player.getMaxSpirituality());
            if (!playerPersistentData.getBoolean("CAN_FLY")) {
                dreamIntoReality.setBaseValue(3);
                //allow it to fly with a fly speed of 0.15
                playerPersistentData.putInt("mindscapeAbilities", mindscapeAbilities - 1);
            }
        }
        if (mindscapeAbilities == 1 && !playerPersistentData.getBoolean("CAN_FLY")) {
            dreamIntoReality.setBaseValue(1);
            //disable flight, return flight speed to 0.05

        }

        int partIndex = mindScape - 2;
        if (partIndex < 0) return;

        int mindScape1 = playerPersistentData.getInt("inMindscape");
        int x = playerPersistentData.getInt("mindscapePlayerLocationX");
        int y = playerPersistentData.getInt("mindscapePlayerLocationY");
        int z = playerPersistentData.getInt("mindscapePlayerLocationZ");
        if (mindScape1 < 1) return;
        if (mindScape1 == 6) {
            player.teleportTo(player.getX() + 77, player.getY() + 8, player.getZ() + 206);
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(250))) {
                if (entity != player) {
                    entity.teleportTo(player.getX(), player.getY(), player.getZ() - 10);
                }
            }
        }
        StructureTemplate part = serverLevel.getStructureManager().getOrCreate(new ResourceLocation(LOTM.MOD_ID, "corpse_cathedral_" + (partIndex + 1)));
        BlockPos tagPos = new BlockPos(x, y + (partIndex * 2), z);
        StructurePlaceSettings settings = BeyonderUtil.getStructurePlaceSettings(new BlockPos(x, y, z));
        part.placeInWorld(serverLevel, tagPos, tagPos, settings, null, Block.UPDATE_ALL);
        playerPersistentData.putInt("inMindscape", mindScape + 1);
    }

    private static void ragingBlows(CompoundTag playerPersistentData, PlayerMobEntity player) {
        //RAGING BLOWS
        boolean sailorLightning = playerPersistentData.getBoolean("SailorLightning");
        int ragingBlows = playerPersistentData.getInt("ragingBlows");
        int sequence = player.getCurrentSequence();
        int ragingBlowsRadius = (25 - (sequence * 3));
        int damage = 20 - sequence * 2;
        if (ragingBlows >= 1) {
            playerPersistentData.putInt("ragingBlows", ragingBlows + 1);
        }
        if (ragingBlows >= 6 && ragingBlows <= 96 && ragingBlows % 6 == 0) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
            Vec3 playerLookVector = player.getViewVector(1.0F);
            Vec3 playerPos = player.position();
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, new AABB(playerPos.x - ragingBlowsRadius, playerPos.y - ragingBlowsRadius, playerPos.z - ragingBlowsRadius, playerPos.x + ragingBlowsRadius, playerPos.y + ragingBlowsRadius, playerPos.z + ragingBlowsRadius))) {
                if (entity != player && playerLookVector.dot(entity.position().subtract(playerPos)) > 0) {
                    entity.hurt(entity.damageSources().generic(), damage);
                    double ragingBlowsX = player.getX() - entity.getX();
                    double ragingBlowsZ = player.getZ() - entity.getZ();
                    entity.knockback(0.25, ragingBlowsX, ragingBlowsZ);
                    if (sequence <= 7) {
                        double chanceOfDamage = (100.0 - (sequence * 12.5));
                        if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                            lightningBolt.moveTo(entity.getX(), entity.getY(), entity.getZ());
                            entity.level().addFreshEntity(lightningBolt);
                        }
                    }
                }
            }
        }
        if (ragingBlows >= 100) {
            ragingBlows = 0;
            playerPersistentData.putInt("ragingBlows", 0);
        }
        int rbParticleHelper = playerPersistentData.getInt("rbParticleHelper");
        AttributeInstance particleHelper = player.getAttribute(ModAttributes.PARTICLE_HELPER1.get());
        if (particleHelper.getBaseValue() == 1) {
            playerPersistentData.putInt("rbParticleHelper", rbParticleHelper + 1);
        }
        if (rbParticleHelper >= 100) {
            playerPersistentData.putInt("rbParticleHelper", 0);
            rbParticleHelper = 0;
            particleHelper.setBaseValue(0);
        }
        if (particleHelper.getBaseValue() == 0) {
            playerPersistentData.putInt("rbParticleHelper", 0);
            rbParticleHelper = 0;
        }
    }

    private static void psychologicalInvisibility(PlayerMobEntity player, CompoundTag playerPersistentData) {
        //PSYCHOLOGICAL INVISIBILITY

        AttributeInstance armorInvisAttribute = player.getAttribute(ModAttributes.ARMORINVISIBLITY.get());
        if (armorInvisAttribute.getValue() > 0 && !player.hasEffect(MobEffects.INVISIBILITY)) {
            removeArmor(player);
            armorInvisAttribute.setBaseValue(0);

        }
        if (playerPersistentData.getBoolean("armorStored")) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 1, false, false));
            player.useSpirituality((int) player.getMaxSpirituality() / 100);
        }
    }

    private static void removeArmor(LivingEntity player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armorStack = player.getItemBySlot(slot);
                if (!armorStack.isEmpty()) {
                    player.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
        }
    }

    private static void nightmare(LivingEntity player, CompoundTag playerPersistentData) {
        //NIGHTMARE
        AttributeInstance nightmareAttribute = player.getAttribute(ModAttributes.NIGHTMARE.get());
        int nightmareTimer = playerPersistentData.getInt("NightmareTimer");
        int matterAccelerationBlockTimer = player.getPersistentData().getInt("matterAccelerationBlockTimer");
        if (matterAccelerationBlockTimer >= 1) {
            player.getPersistentData().putInt("matterAccelerationBlockTimer", matterAccelerationBlockTimer - 1);
        }

        if (nightmareAttribute.getValue() >= 1) {
            nightmareTimer++;
            if (nightmareTimer >= 600) {
                nightmareAttribute.setBaseValue(0);
                nightmareTimer = 0;
            }
        } else {
            nightmareTimer = 0;
        }
        playerPersistentData.putInt("NightmareTimer", nightmareTimer);
    }

    private static void calamityIncarnationTornado(CompoundTag playerPersistentData, LivingEntity player) {
        //CALAMITY INCARNATION TORNADO
        if (playerPersistentData.getInt("calamityIncarnationTornado") >= 1) {
            playerPersistentData.putInt("calamityIncarnationTornado", player.getPersistentData().getInt("calamityIncarnationTornado") - 1);
        }
    }

    private static void calamityUndeadArmy(PlayerMobEntity pPlayer) {
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
            if (random.nextInt(10) == 10) {
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().move(subtractX, subtractY, subtractZ).inflate(20))) {
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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
                    if (pPlayer.getCurrentPathway() == BeyonderClassInit.MONSTER && pPlayer.getCurrentSequence() <= 6) {
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

    private static void calamityLightningStorm(LivingEntity pPlayer) {
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
                if (entity instanceof PlayerMobEntity mobEntity) {
                    if (mobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && mobEntity.getCurrentSequence() <= 3) {
                        mobEntity.getPersistentData().putInt("calamityLightningStormImmunity", 20);
                    }
                }
            }
            double random = (Math.random() * 60) - 30;
            lightningEntity.teleportTo(stormX + random, stormY + 60, stormZ + random);
            lightningEntity.setMaxLength(60);
            pPlayer.level().addFreshEntity(lightningEntity);
        }
    }

    private static void calamityExplosion(LivingEntity pPlayer) {
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
                if (entity instanceof PlayerMobEntity mobEntity) {
                    if (mobEntity.getCurrentPathway() == BeyonderClassInit.MONSTER && mobEntity.getCurrentSequence() <= 3) {
                        mobEntity.getPersistentData().putInt("calamityExplosionImmunity", 2);
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

    private static void monsterLuckPoisonAttacker(LivingEntity pPlayer) {
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

    private static void decrementMonsterAttackEvent(LivingEntity pPlayer) {
        if (pPlayer.getPersistentData().getInt("attackedMonster") >= 1) {
            pPlayer.getPersistentData().putInt("attackedMonster", pPlayer.getPersistentData().getInt("attackedMonster") - 1);
        }
    }

    private static void monsterLuckIgnoreMobs(LivingEntity pPlayer) {
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

    private static void rainEyes(LivingEntity player) {
        //RAIN EYES
        if (!player.level().isRaining()) {
            return;
        }
        if (player.getPersistentData().getBoolean("torrentialDownpour") && player.tickCount % 200 == 0) {
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(500))) {
                if (entity != player && entity instanceof Player otherPlayer && otherPlayer.isInWaterOrRain()) {
                    player.sendSystemMessage(Component.literal(otherPlayer.getName().getString() + "'s location is " + otherPlayer.getX() + ", " + otherPlayer.getY() + ", " + otherPlayer.getZ()).withStyle(ChatFormatting.BOLD));
                }
            }
        }
    }

    private static void sirenSongs(CompoundTag playerPersistentData, PlayerMobEntity player, int sequence) {
        //SIREN SONGS
        int sirenSongHarm = playerPersistentData.getInt("sirenSongHarm");
        int sirenSongWeaken = playerPersistentData.getInt("sirenSongWeaken");
        int sirenSongStun = playerPersistentData.getInt("sirenSongStun");
        int sirenSongStrengthen = playerPersistentData.getInt("sirenSongStrengthen");
        if (player.getCurrentPathway() != BeyonderClassInit.SAILOR || player.getCurrentSequence() > 5) {
            return;
        }
        if (sirenSongHarm % 20 == 0 && sirenSongHarm != 0) {
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50 - (sequence * 6)))) {
                if (entity != player) {
                    entity.hurt(entity.damageSources().magic(), 10 - sequence);
                }
            }
        }
        SoundEvent harmSoundEvent = switch (sirenSongHarm) {
            case 400 -> SoundInit.SIREN_SONG_HARM_1.get();
            case 380 -> SoundInit.SIREN_SONG_HARM_2.get();
            case 360 -> SoundInit.SIREN_SONG_HARM_3.get();
            case 340 -> SoundInit.SIREN_SONG_HARM_4.get();
            case 320 -> SoundInit.SIREN_SONG_HARM_5.get();
            case 300 -> SoundInit.SIREN_SONG_HARM_6.get();
            case 280 -> SoundInit.SIREN_SONG_HARM_7.get();
            case 260 -> SoundInit.SIREN_SONG_HARM_8.get();
            case 240 -> SoundInit.SIREN_SONG_HARM_9.get();
            case 220 -> SoundInit.SIREN_SONG_HARM_10.get();
            case 200 -> SoundInit.SIREN_SONG_HARM_11.get();
            case 180 -> SoundInit.SIREN_SONG_HARM_12.get();
            case 160 -> SoundInit.SIREN_SONG_HARM_13.get();
            case 140 -> SoundInit.SIREN_SONG_HARM_14.get();
            case 120 -> SoundInit.SIREN_SONG_HARM_15.get();
            case 100 -> SoundInit.SIREN_SONG_HARM_16.get();
            case 80 -> SoundInit.SIREN_SONG_HARM_17.get();
            case 60 -> SoundInit.SIREN_SONG_HARM_18.get();
            case 40 -> SoundInit.SIREN_SONG_HARM_19.get();
            case 20 -> SoundInit.SIREN_SONG_HARM_20.get();
            default -> null;
        };
        if (harmSoundEvent != null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), harmSoundEvent, SoundSource.NEUTRAL, 6f, 1f);
        }

        if (sirenSongHarm >= 1) {
            playerPersistentData.putInt("sirenSongHarm", sirenSongHarm - 1);
        }

        if (sirenSongWeaken % 20 == 0 && sirenSongWeaken != 0) { //make it for 380,360,430 etc.
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50 - (sequence * 6)))) {
                if (entity != player) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 19, 2, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 19, 2, false, false));
                }
            }
        }

        SoundEvent weakenSoundEvent = switch (sirenSongWeaken) {
            case 400 -> SoundInit.SIREN_SONG_WEAKEN_1.get();
            case 380 -> SoundInit.SIREN_SONG_WEAKEN_2.get();
            case 360 -> SoundInit.SIREN_SONG_WEAKEN_3.get();
            case 340 -> SoundInit.SIREN_SONG_WEAKEN_4.get();
            case 320 -> SoundInit.SIREN_SONG_WEAKEN_5.get();
            case 300 -> SoundInit.SIREN_SONG_WEAKEN_6.get();
            case 280 -> SoundInit.SIREN_SONG_WEAKEN_7.get();
            case 260 -> SoundInit.SIREN_SONG_WEAKEN_8.get();
            case 240 -> SoundInit.SIREN_SONG_WEAKEN_9.get();
            case 220 -> SoundInit.SIREN_SONG_WEAKEN_10.get();
            case 200 -> SoundInit.SIREN_SONG_WEAKEN_11.get();
            case 180 -> SoundInit.SIREN_SONG_WEAKEN_12.get();
            case 160 -> SoundInit.SIREN_SONG_WEAKEN_13.get();
            case 140 -> SoundInit.SIREN_SONG_WEAKEN_14.get();
            case 120 -> SoundInit.SIREN_SONG_WEAKEN_15.get();
            case 100 -> SoundInit.SIREN_SONG_WEAKEN_16.get();
            case 80 -> SoundInit.SIREN_SONG_WEAKEN_17.get();
            case 60 -> SoundInit.SIREN_SONG_WEAKEN_18.get();
            case 40 -> SoundInit.SIREN_SONG_WEAKEN_19.get();
            case 20 -> SoundInit.SIREN_SONG_WEAKEN_20.get();
            default -> null;
        };
        if (weakenSoundEvent != null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), weakenSoundEvent, SoundSource.NEUTRAL, 6f, 1f);
        }

        if (sirenSongWeaken >= 1) {
            playerPersistentData.putInt("sirenSongWeaken", sirenSongWeaken - 1);
        }

        if (sirenSongStun % 20 == 0 && sirenSongStun != 0) {
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50 - (sequence * 6)))) {
                if (entity != player) {
                    entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 19 - (sequence * 2), 2, false, false));
                }
            }
        }
        SoundEvent stunSoundEvent = switch (sirenSongStun) {
            case 400 -> SoundInit.SIREN_SONG_STUN_1.get();
            case 380 -> SoundInit.SIREN_SONG_STUN_2.get();
            case 360 -> SoundInit.SIREN_SONG_STUN_3.get();
            case 340 -> SoundInit.SIREN_SONG_STUN_4.get();
            case 320 -> SoundInit.SIREN_SONG_STUN_5.get();
            case 300 -> SoundInit.SIREN_SONG_STUN_6.get();
            case 280 -> SoundInit.SIREN_SONG_STUN_7.get();
            case 260 -> SoundInit.SIREN_SONG_STUN_8.get();
            case 240 -> SoundInit.SIREN_SONG_STUN_9.get();
            case 220 -> SoundInit.SIREN_SONG_STUN_10.get();
            case 200 -> SoundInit.SIREN_SONG_STUN_11.get();
            case 180 -> SoundInit.SIREN_SONG_STUN_12.get();
            case 160 -> SoundInit.SIREN_SONG_STUN_13.get();
            case 140 -> SoundInit.SIREN_SONG_STUN_14.get();
            case 120 -> SoundInit.SIREN_SONG_STUN_15.get();
            case 100 -> SoundInit.SIREN_SONG_STUN_16.get();
            case 80 -> SoundInit.SIREN_SONG_STUN_17.get();
            case 60 -> SoundInit.SIREN_SONG_STUN_18.get();
            case 40 -> SoundInit.SIREN_SONG_STUN_19.get();
            case 20 -> SoundInit.SIREN_SONG_STUN_20.get();
            default -> null;
        };

        if (stunSoundEvent != null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), stunSoundEvent, SoundSource.NEUTRAL, 6f, 1f);
        }

        if (sirenSongStun >= 1) {
            playerPersistentData.putInt("sirenSongStun", sirenSongStun - 1);
        }
        if (sirenSongStrengthen % 20 == 0 && sirenSongStrengthen != 0) {
            if (player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                int strengthAmp = player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier();
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 19, strengthAmp + 2));
            } else if (!player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 19, 2));
            }
            if (player.hasEffect(MobEffects.REGENERATION)) {
                int regenAmp = player.getEffect(MobEffects.REGENERATION).getAmplifier();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 19, regenAmp + 2));
            } else if (!player.hasEffect(MobEffects.REGENERATION)) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 19, 2));
            }
        }
        SoundEvent strengthenSoundEvent = switch (sirenSongStrengthen) {
            case 400 -> SoundInit.SIREN_SONG_STRENGTHEN_1.get();
            case 380 -> SoundInit.SIREN_SONG_STRENGTHEN_2.get();
            case 360 -> SoundInit.SIREN_SONG_STRENGTHEN_3.get();
            case 340 -> SoundInit.SIREN_SONG_STRENGTHEN_4.get();
            case 320 -> SoundInit.SIREN_SONG_STRENGTHEN_5.get();
            case 300 -> SoundInit.SIREN_SONG_STRENGTHEN_6.get();
            case 280 -> SoundInit.SIREN_SONG_STRENGTHEN_7.get();
            case 260 -> SoundInit.SIREN_SONG_STRENGTHEN_8.get();
            case 240 -> SoundInit.SIREN_SONG_STRENGTHEN_9.get();
            case 220 -> SoundInit.SIREN_SONG_STRENGTHEN_10.get();
            case 200 -> SoundInit.SIREN_SONG_STRENGTHEN_11.get();
            case 180 -> SoundInit.SIREN_SONG_STRENGTHEN_12.get();
            case 160 -> SoundInit.SIREN_SONG_STRENGTHEN_13.get();
            case 140 -> SoundInit.SIREN_SONG_STRENGTHEN_14.get();
            case 120 -> SoundInit.SIREN_SONG_STRENGTHEN_15.get();
            case 100 -> SoundInit.SIREN_SONG_STRENGTHEN_16.get();
            case 80 -> SoundInit.SIREN_SONG_STRENGTHEN_17.get();
            case 60 -> SoundInit.SIREN_SONG_STRENGTHEN_18.get();
            case 40 -> SoundInit.SIREN_SONG_STRENGTHEN_19.get();
            case 20 -> SoundInit.SIREN_SONG_STRENGTHEN_20.get();
            default -> null;
        };

        if (strengthenSoundEvent != null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), strengthenSoundEvent, SoundSource.NEUTRAL, 6f, 1f);
        }

        if (sirenSongStrengthen >= 1) {
            playerPersistentData.putInt("sirenSongStrengthen", sirenSongStrengthen - 1);
        }
    }

    private static void starOfLightning(LivingEntity livingEntity, CompoundTag tag) {
        //STAR OF LIGHTNING
        AttributeInstance attributeInstance4 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
        int sailorLightningStar = tag.getInt("sailorLightningStar");
        if (sailorLightningStar >= 2) {
            attributeInstance4.setBaseValue(1.0f);
            tag.putInt("sailorLightningStar", sailorLightningStar - 1);
        }
        if (sailorLightningStar == 1) {
            tag.putInt("sailorLightningStar", 0);
            attributeInstance4.setBaseValue(0);
            for (int i = 0; i < 500; i++) {
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), livingEntity.level());
                lightningEntity.setSpeed(50);
                double sailorStarX = (Math.random() * 2 - 1);
                double sailorStarY = (Math.random() * 2 - 1); // You might want different random values for y and z
                double sailorStarZ = (Math.random() * 2 - 1);
                lightningEntity.setDeltaMovement(sailorStarX, sailorStarY, sailorStarZ);
                lightningEntity.setMaxLength(10);
                lightningEntity.setOwner(livingEntity);
                lightningEntity.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                livingEntity.level().addFreshEntity(lightningEntity);
            }
        }
    }

    private static void sirenSongParticles(LivingEntity livingEntity, int sequence) {
        CompoundTag livingEntityPersistentData = livingEntity.getPersistentData();
        int ssParticleAttributeHelper = livingEntityPersistentData.getInt("ssParticleAttributeHelper");
        if (ssParticleAttributeHelper >= 1) {
            livingEntityPersistentData.putInt("ssParticleAttributeHelper", ssParticleAttributeHelper - 1);
            livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).setBaseValue(1);
        }
        if (ssParticleAttributeHelper < 1) {
            livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).setBaseValue(0);
        }

        AttributeInstance particleAttribute2 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER2.get());
        int harmCounter = 50 - (sequence * 6);
        if (particleAttribute2.getBaseValue() == 1) {
            spawnParticlesInSphere(livingEntity, harmCounter);
        } else {
            particleAttribute2.setBaseValue(0);
        }

    }

    public static void summonTsunami(LivingEntity livingEntity) {
        CompoundTag tag = livingEntity.getPersistentData();
        int livingEntityX = tag.getInt("sailorTsunamiX");
        int livingEntityY = tag.getInt("sailorTsunamiY");
        int livingEntityZ = tag.getInt("sailorTsunamiZ");
        int tsunami = tag.getInt("sailorTsunami");
        String direction = tag.getString("sailorTsunamiDirection");

        int offsetX = 0;
        int offsetZ = 0;

        switch (direction) {
            case "N":
                offsetZ = 1;
                break;
            case "E":
                offsetX = -1;
                break;
            case "S":
                offsetZ = -1;
                break;
            case "W":
                offsetX = 1;
                break;
        }

        int waveWidth = 80;
        int waveHeight = 10;
        int startDistance = 85;

        for (int w = -waveWidth / 2; w < waveWidth / 2; w++) {
            for (int h = 0; h < waveHeight; h++) {
                int x = livingEntityX + (offsetX * startDistance) + (offsetX * (200 - tsunami) / 5);
                int y = livingEntityY + h;
                int z = livingEntityZ + (offsetZ * startDistance) + (offsetZ * (200 - tsunami) / 5);

                if (offsetX == 0) {
                    x += w;
                } else {
                    z += w;
                }

                BlockPos blockPos = new BlockPos(x, y, z);
                if (livingEntity.level().getBlockState(blockPos).isAir()) {
                    livingEntity.level().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void tsunami(CompoundTag tag, LivingEntity livingEntity) {
        //TSUNAMI
        int tsunami = tag.getInt("sailorTsunami");
        if (tsunami >= 1) {
            tag.putInt("sailorTsunami", tsunami - 5);
            summonTsunami(livingEntity);
        } else {
            tag.remove("sailorTsunamiDirection");
            tag.remove("sailorTsunamiX");
            tag.remove("sailorTsunamiY");
            tag.remove("sailorTsunamiZ");
        }

        //TSUNAMI SEAL
        int tsunamiSeal = tag.getInt("sailorTsunami");
        if (tsunamiSeal >= 1) {
            tag.putInt("sailorTsunami", tsunamiSeal - 5);
            summonTsunami(livingEntity);
        } else {
            tag.remove("sailorTsunamiDirection");
            tag.remove("sailorTsunamiX");
            tag.remove("sailorTsunamiY");
            tag.remove("sailorTsunamiZ");
        }
    }

    public static void spawnParticlesInSphere(LivingEntity livingEntity, int radius) {
        Level level = livingEntity.level();
        Random random = new Random();

        for (int i = 0; i < 20; i++) { // Adjust the number of particles as needed
            double x = livingEntity.getX() + (random.nextDouble() * 2 - 1) * radius;
            double y = livingEntity.getY() + (random.nextDouble() * 2 - 1) * radius;
            double z = livingEntity.getZ() + (random.nextDouble() * 2 - 1) * radius;

            // Check if the point is within the sphere
            if (isInsideSphere(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), x, y, z, radius)) {
                double noteValue = random.nextInt(25) / 24.0;
                level.addParticle(ParticleTypes.NOTE, x, y, z, noteValue, 0, 0);
            }
        }
    }

    private static void windManipulationFlight(LivingEntity livingEntity, CompoundTag tag) {
        //WIND MANIPULATION FLIGHT
        Vec3 lookVector = livingEntity.getLookAngle();
        if (!tag.getBoolean("sailorFlight1")) {
            return;
        }
        int flight = tag.getInt("sailorFlight");
        int flightCancel = tag.getInt("sailorFlightDamageCancel");
        if (flightCancel >= 1) {
            tag.putInt("sailorFlightDamageCancel", flightCancel + 1);
        }
        if (flightCancel >= 300) {
            tag.putInt("sailorFlightDamageCancel", 0);
        }
        if (flight >= 1) {
            tag.putInt("sailorFlight", flight + 1);
        }
        if (flight <= 60 && flight % 20 == 0) {
            livingEntity.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
            livingEntity.hurtMarked = true;
        }
        if (flight > 60) {
            tag.putInt("sailorFlight", 0);
        }
    }

    private static void waterSphereCheck(LivingEntity livingEntity, ServerLevel level) {
        //WATER SPHERE CHECK
        if (livingEntity.getPersistentData().getInt("sailorSphere") >= 5) {
            for (Entity entity : livingEntity.level().getEntitiesOfClass(Entity.class, livingEntity.getBoundingBox().inflate(4))) {
                if (!(entity instanceof LivingEntity) && !(entity instanceof MeteorEntity) && !(entity instanceof MeteorNoLevelEntity)) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
            BlockPos livingEntityPos = livingEntity.blockPosition();
            double radius = 3.0;
            double minRemovalRadius = 4.0;
            double maxRemovalRadius = 7.0;

            // Create a sphere of water around the livingEntity
            for (int sphereX = (int) -radius; sphereX <= radius; sphereX++) {
                for (int sphereY = (int) -radius; sphereY <= radius; sphereY++) {
                    for (int sphereZ = (int) -radius; sphereZ <= radius; sphereZ++) {
                        double sphereDistance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                        if (!(sphereDistance <= radius)) {
                            continue;
                        }
                        BlockPos blockPos = livingEntityPos.offset(sphereX, sphereY, sphereZ);
                        if (level.getBlockState(blockPos).isAir() && !level.getBlockState(blockPos).is(Blocks.WATER)) {
                            level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                        }
                    }
                }
            }
            for (int sphereX = (int) -maxRemovalRadius; sphereX <= maxRemovalRadius; sphereX++) {
                for (int sphereY = (int) -maxRemovalRadius; sphereY <= maxRemovalRadius; sphereY++) {
                    for (int sphereZ = (int) -maxRemovalRadius; sphereZ <= maxRemovalRadius; sphereZ++) {
                        double sphereDistance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                        if (!(sphereDistance <= maxRemovalRadius) || !(sphereDistance >= minRemovalRadius)) {
                            continue;
                        }
                        BlockPos blockPos = livingEntityPos.offset(sphereX, sphereY, sphereZ);
                        if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        if (livingEntity.getPersistentData().getInt("sailorSphere") >= 1 && livingEntity.getPersistentData().getInt("sailorSphere") <= 4) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 100, false, false));
            for (int sphereX = -6; sphereX <= 6; sphereX++) {
                for (int sphereY = -6; sphereY <= 6; sphereY++) {
                    for (int sphereZ = -6; sphereZ <= 6; sphereZ++) {
                        double sphereDistance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                        if (!(sphereDistance <= 6)) {
                            continue;
                        }
                        BlockPos blockPos = livingEntity.getOnPos().offset(sphereX, sphereY, sphereZ);
                        if (livingEntity.level().getBlockState(blockPos).getBlock() == Blocks.WATER) {
                            livingEntity.level().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        if (livingEntity.getPersistentData().getInt("sailorSphere") >= 1) {
            livingEntity.getPersistentData().putInt("sailorSphere", livingEntity.getPersistentData().getInt("sailorSphere") - 1);
        }
    }
}
