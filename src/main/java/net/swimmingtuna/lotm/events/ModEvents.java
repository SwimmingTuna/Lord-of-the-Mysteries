package net.swimmingtuna.lotm.events;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.*;
import net.swimmingtuna.lotm.events.custom_events.ModEventFactory;
import net.swimmingtuna.lotm.events.custom_events.ProjectileEvent;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.SoundInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.DreamIntoReality;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionBarrier;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionLocationBlink;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.SoundManager;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static net.swimmingtuna.lotm.util.BeyonderUtil.getProjectiles;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents implements ReachChangeUUIDs {


    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
    }

    @SubscribeEvent
    public static void attributeHandler(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        Style style = BeyonderUtil.getStyle(pPlayer);
        CompoundTag tag = pPlayer.getPersistentData();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (holder != null) {
            Level level = pPlayer.level();
            int sequence = holder.getCurrentSequence();
            if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {


                //NIGHTMARE
                AttributeInstance nightmareAttribute = pPlayer.getAttribute(ModAttributes.NIGHTMARE.get());
                AttributeInstance armorInvisAttribute = pPlayer.getAttribute(ModAttributes.ARMORINVISIBLITY.get());
                int nightmareTimer = tag.getInt("NightmareTimer");
                int matterAccelerationBlockTimer = pPlayer.getPersistentData().getInt("matterAccelerationBlockTimer");
                if (matterAccelerationBlockTimer >= 1) {
                    pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", matterAccelerationBlockTimer - 1);
                }

                assert nightmareAttribute != null;
                if (nightmareAttribute.getValue() >= 1) {
                    nightmareTimer++;
                    if (nightmareTimer >= 600) {
                        nightmareAttribute.setBaseValue(0);
                        nightmareTimer = 0;
                    }
                } else {
                    nightmareTimer = 0;
                }
                tag.putInt("NightmareTimer", nightmareTimer);


                //CALAMITY INCARNATION TORNADO
                if (tag.getInt("calamityIncarnationTornado") >= 1) {
                    tag.putInt("calamityIncarnationTornado", pPlayer.getPersistentData().getInt("calamityIncarnationTornado") - 1);
                }


                //PSYCHOLOGICAL INVISIBILITY
                if (armorInvisAttribute.getValue() > 0 && !pPlayer.hasEffect(MobEffects.INVISIBILITY)) {
                    removeArmor(pPlayer);
                    armorInvisAttribute.setBaseValue(0);

                }
                if (tag.getBoolean("armorStored")) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 1, false, false));
                    holder.useSpirituality((int) holder.getMaxSpirituality() / 100);
                }


                //WIND MANIPULATION SENSE
                boolean windManipulationSense = tag.getBoolean("windManipulationSense");
                if (windManipulationSense) {
                    double radius = 100 - (holder.getCurrentSequence() * 10);
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                        if (entity != pPlayer && entity instanceof Player player) {
                            Vec3 directionToPlayer = entity.position().subtract(pPlayer.position()).normalize();
                            Vec3 lookAngle = pPlayer.getLookAngle();
                            double horizontalAngle = Math.atan2(directionToPlayer.x, directionToPlayer.z) - Math.atan2(lookAngle.x, lookAngle.z);

                            String horizontalDirection;
                            if (Math.abs(horizontalAngle) < Math.PI / 4) {
                                horizontalDirection = "in front of";
                            } else if (horizontalAngle < -Math.PI * 3 / 4 || horizontalAngle > Math.PI * 3 / 4) {
                                horizontalDirection = "behind";
                            } else if (horizontalAngle < 0) {
                                horizontalDirection = "to the right of";
                            } else {
                                horizontalDirection = "to the left of";
                            }

                            String verticalDirection;
                            if (directionToPlayer.y > 0.2) {
                                verticalDirection = "above";
                            } else if (directionToPlayer.y < -0.2) {
                                verticalDirection = "below";
                            } else {
                                verticalDirection = "at the same level as";
                            }

                            String message = player.getName().getString() + " is " + horizontalDirection + " and " + verticalDirection + " you.";
                            pPlayer.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
                        }
                    }
                }


                //SAILOR LIGHTNING TRAVEL
                if (pPlayer.getPersistentData().getInt("sailorLightningTravel") >= 1) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 3, 1, false, false));
                    pPlayer.getPersistentData().putInt("sailorLightningTravel", pPlayer.getPersistentData().getInt("sailorLightningTravel") - 1);
                }


                //WIND MANIPULATION CUSHION
                int cushion = tag.getInt("windManipulationCushion");
                if (cushion >= 1) {
                    tag.putInt("windManipulationCushion", cushion - 1);
                    pPlayer.resetFallDistance();
                }
                if (cushion >= 80 && pPlayer.getDeltaMovement().y <= 0) {
                    AttributeInstance cushionParticles = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER3.get());
                    cushionParticles.setBaseValue(1.0f);
                    pPlayer.setDeltaMovement(pPlayer.getDeltaMovement().x(), pPlayer.getDeltaMovement().y() * 0.9, pPlayer.getDeltaMovement().z());
                    pPlayer.hurtMarked = true;
                }
                if (cushion == 79) {
                    pPlayer.setDeltaMovement(pPlayer.getLookAngle().scale(2.0f));
                    pPlayer.hurtMarked = true;
                    pPlayer.resetFallDistance();
                    AttributeInstance cushionParticles = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER3.get());
                    cushionParticles.setBaseValue(0.0f);
                }

                //WIND MANIPULATION GLIDE
                int regularFlight = tag.getInt("sailorFlight");
                boolean enhancedFlight = tag.getBoolean("sailorFlight1");
                if (holder.isSailorClass() && holder.getCurrentSequence() <= 7 && pPlayer.isShiftKeyDown() && pPlayer.getDeltaMovement().y() < 0 && !pPlayer.getAbilities().instabuild && !enhancedFlight && regularFlight == 0) {
                    Vec3 movement = pPlayer.getDeltaMovement();
                    double deltaX = Math.cos(Math.toRadians(pPlayer.getYRot() + 90)) * 0.06;
                    double deltaZ = Math.sin(Math.toRadians(pPlayer.getYRot() + 90)) * 0.06;
                    pPlayer.setDeltaMovement(movement.x + deltaX, -0.05, movement.z + deltaZ);
                    pPlayer.resetFallDistance();
                    pPlayer.hurtMarked = true;
                }


                //DREAM INTO REALITY
                boolean canFly = pPlayer.getPersistentData().getBoolean("CanFly");
                BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                    if (canFly) {
                        if (spectatorSequence.getSpirituality() >= 15) {
                            spectatorSequence.useSpirituality(15);
                        }
                        if (spectatorSequence.getSpirituality() <= 15) {
                            DreamIntoReality.stopFlying(pPlayer);
                        }
                        if (spectatorSequence.getCurrentSequence() == 2) {
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2, false, false));
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, false));
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4, false, false));
                        }
                        if (spectatorSequence.getCurrentSequence() == 1) {
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, false, false));
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, false));
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4, false, false));
                        }
                        if (spectatorSequence.getCurrentSequence() == 0) {
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, false, false));
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 4, false, false));
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 5, false, false));
                        }
                    }
                });


                //CONSCIOUSNESS STROLL
                int strollCounter = tag.getInt("consciousnessStrollActivated");
                int consciousnessStrollActivatedX = tag.getInt("consciousnessStrollActivatedX");
                int consciousnessStrollActivatedY = tag.getInt("consciousnessStrollActivatedY");
                int consciousnessStrollActivatedZ = tag.getInt("consciousnessStrollActivatedZ");
                if (strollCounter >= 1) {
                    tag.putInt("consciousnessStrollActivated", strollCounter - 1);
                    ((ServerPlayer) pPlayer).setGameMode(GameType.SPECTATOR);
                }
                if (strollCounter == 1) {
                    pPlayer.teleportTo(consciousnessStrollActivatedX, consciousnessStrollActivatedY, consciousnessStrollActivatedZ);
                    ((ServerPlayer) pPlayer).setGameMode(GameType.SURVIVAL);
                }


                //PROJECTILE EVENT
                Projectile projectile = getProjectiles(pPlayer);
                if (projectile != null) {
                    ProjectileEvent.ProjectileControlEvent projectileEvent = new ProjectileEvent.ProjectileControlEvent(projectile);
                    projectile = projectileEvent.getProjectile();
                    Player player = (Player) projectileEvent.getOwner();
                    if (projectile != null) {
                        if (!player.level().isClientSide()) {
                            ModEventFactory.onSailorShootProjectile(projectile);
                            if (!projectile.level().isClientSide()) {

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
                                                    projectile.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                                                }
                                            }
                                        }
                                        for (LivingEntity entity1 : projectile.level().getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(5))) {
                                            if (entity1 instanceof Player playerEntity) {
                                                if (holder != null) {
                                                    if (!holder.isSailorClass() && holder.getCurrentSequence() == 0) {
                                                        playerEntity.hurt(playerEntity.damageSources().lightningBolt(), 10);
                                                    }
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
                                    if (holder.isSailorClass() && holder.getCurrentSequence() <= 7) {
                                        projectileEvent.addMovement(projectile, (target.getX() - projectile.getX()) * 0.1, (target.getY() - projectile.getY()) * 0.1, (target.getZ() - projectile.getZ()) * 0.1);
                                        projectile.hurtMarked = true;
                                    }
                                }
                            }
                        }
                    }
                }


                //ENVISION BARRIER
                BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                    if (spectatorSequence.getCurrentSequence() == 0) {
                        int barrierRadius = pPlayer.getPersistentData().getInt("BarrierRadius");
                        if (pPlayer.isShiftKeyDown() && pPlayer.getMainHandItem().getItem() instanceof EnvisionBarrier) {
                            barrierRadius++;
                            pPlayer.sendSystemMessage(Component.literal("Barrier Radius " + barrierRadius));
                        }
                        if (barrierRadius > 100) {
                            barrierRadius = 0;
                        }
                        pPlayer.getPersistentData().putInt("BarrierRadius", barrierRadius);
                    }
                });

                //ENVISION LIFE
                int waitMakeLifeCounter = pPlayer.getPersistentData().getInt("waitMakeLifeTimer");
                if (waitMakeLifeCounter >= 1) {
                    waitMakeLifeCounter++;
                }
                if (waitMakeLifeCounter >= 600) {
                    pPlayer.getPersistentData().putInt("waitMakeLifeTimer", 0);
                    waitMakeLifeCounter = 0;
                }


                //MANIPULATE MOVEMENT
                if (pPlayer.getPersistentData().getBoolean("manipulateMovementBoolean")) {
                    for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(250))) {
                        if (entity != pPlayer && entity.hasEffect(ModEffects.MANIPULATION.get())) {
                            int targetX = pPlayer.getPersistentData().getInt("manipulateMovementX");
                            int targetY = pPlayer.getPersistentData().getInt("manipulateMovementY");
                            int targetZ = pPlayer.getPersistentData().getInt("manipulateMovementZ");

                            if (entity.distanceToSqr(targetX, targetY, targetZ) <= 10) {
                                entity.removeEffect(ModEffects.MANIPULATION.get());
                                continue;
                            }

                            if (entity instanceof Player) {
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
                                boolean pathIsClear = pPlayer.level().getBlockState(frontBlockPos).isAir() && pPlayer.level().getBlockState(frontBlockPos1).isAir();

                                if (pathIsClear) {
                                    entity.setDeltaMovement(dx * speed, Math.min(0, dy * speed), dz * speed);
                                } else {
                                    entity.setDeltaMovement(dx * speed, 0.25, dz * speed);
                                }
                            } else if (entity instanceof Mob mob) {
                                mob.getNavigation().moveTo(targetX, targetY, targetZ, 1.7);
                            }
                        }
                    }
                }
                //ENVISION KINGDOM
                int mindscape = tag.getInt("inMindscape");
                if (mindscape >= 1) {
                    tag.putInt("inMindscape", mindscape + 1);
                }
                if (mindscape >= 1200) {
                    tag.putInt("inMindscape", 0);
                }
                AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                double maxSpirituality = holder.getMaxSpirituality();
                Abilities playerAbilities = pPlayer.getAbilities();
                BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                    if (holder.getCurrentSequence() == 0 && holder.isSpectatorClass()) {
                        if (mindscape >= 1) {
                            tag.putInt("mindscapeAbilities", mindscape - 1);
                            holder.setSpirituality((int) maxSpirituality);
                            if (!tag.getBoolean("CAN_FLY")) {
                                dreamIntoReality.setBaseValue(3);
                                playerAbilities.setFlyingSpeed(0.15F);
                                playerAbilities.mayfly  = true;
                                pPlayer.onUpdateAbilities();
                                if (pPlayer instanceof ServerPlayer serverPlayer) {
                                    serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                                }
                            }
                        }
                        if (mindscape == 0) {
                            if (!tag.getBoolean("CAN_FLY")) {
                                dreamIntoReality.setBaseValue(1);
                                playerAbilities.setFlyingSpeed(0.05F);
                                if (!playerAbilities.instabuild) {
                                    playerAbilities.mayfly = false;
                                }
                                pPlayer.onUpdateAbilities();
                                if (pPlayer instanceof ServerPlayer serverPlayer) {
                                    serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                                }
                            }
                        }
                    }
                });


                int mindScape = tag.getInt("inMindscape");
                int x = tag.getInt("mindscapePlayerLocationX");
                int y = tag.getInt("mindscapePlayerLocationY");
                int z = tag.getInt("mindscapePlayerLocationZ");
                StructureTemplate[] parts = new StructureTemplate[48];
                if (mindScape == 6) {
                    pPlayer.teleportTo(pPlayer.getX() + 77, pPlayer.getY() + 8, pPlayer.getZ() + 206);
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(250))) {
                        if (entity != pPlayer) {
                            entity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ() - 10);
                        }
                    }
                }
                for (int i = 0; i < 48; i++) {
                    ServerLevel serverLevel = (ServerLevel) level;
                    parts[i] = serverLevel.getStructureManager().getOrCreate(new ResourceLocation(LOTM.MOD_ID, "corpse_cathedral_" + (i + 1)));
                }
                BlockPos[] tagPos = new BlockPos[48];
                for (int i = 0; i < 48; i++) {
                    tagPos[i] = new BlockPos(x, y + (i * 2), z);
                }
                StructurePlaceSettings settings = BeyonderUtil.getStructurePlaceSettings(new BlockPos(x, y, z));
                for (int i = 0; i < 48; i++) {
                    if (mindScape == (i + 2)) {
                        ServerLevel serverLevel = (ServerLevel) level;
                        parts[i].placeInWorld(serverLevel, tagPos[i], tagPos[i], settings, null, 3);
                    }
                }


                //ACIDIC RAIN
                int acidicRain = pPlayer.getPersistentData().getInt("sailorAcidicRain");
                AttributeInstance particleAttribute = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER.get());
                if (acidicRain > 0 && particleAttribute.getValue() == 1) {
                    pPlayer.getPersistentData().putInt("sailorAcidicRain", acidicRain + 1);
                    double radius1 = 50 - (sequence * 7);
                    double radius2 = 10 - sequence;


                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius1))) {
                        if (entity != pPlayer) {
                            if (entity.hasEffect(MobEffects.POISON)) {
                                int poisonAmp = entity.getEffect(MobEffects.POISON).getAmplifier();
                                if (poisonAmp == 0) {
                                    entity.addEffect((new MobEffectInstance(MobEffects.POISON, 60, 1, false, false)));
                                }
                            } else entity.addEffect((new MobEffectInstance(MobEffects.POISON, 60, 1, false, false)));
                        }
                    }
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius2))) {
                        if (entity != pPlayer) {
                            if (entity.hasEffect(MobEffects.POISON)) {
                                int poisonAmp = entity.getEffect(MobEffects.POISON).getAmplifier();
                                if (poisonAmp <= 2) {
                                    entity.addEffect((new MobEffectInstance(MobEffects.POISON, 60, 2, false, false)));
                                }
                            } else entity.addEffect((new MobEffectInstance(MobEffects.POISON, 60, 2, false, false)));
                        }
                    }


                    if (acidicRain > 300) {
                        pPlayer.getPersistentData().putInt("sailorAcidicRain", 0);
                        particleAttribute.setBaseValue(0);
                    }
                }


                //CALAMITY INCARNATION TSUNAMI
                int calamityIncarnationTsunami = tag.getInt("calamityIncarnationTsunami");
                if (calamityIncarnationTsunami >= 1) {
                    tag.putInt("calamityIncarnationTsunami", calamityIncarnationTsunami - 1);
                    BlockPos playerPos = pPlayer.blockPosition();
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


                //EARTHQUAKE
                int sailorEarthquake = pPlayer.getPersistentData().getInt("sailorEarthquake");
                if (sailorEarthquake == 200 || sailorEarthquake == 180 || sailorEarthquake == 160 || sailorEarthquake == 140 || sailorEarthquake == 120 || sailorEarthquake == 100 || sailorEarthquake == 80 || sailorEarthquake == 60 || sailorEarthquake == 40 || sailorEarthquake == 20 || sailorEarthquake == 1) {
                    int radius = 100 - (sequence * 10);
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate((radius)))) {
                        if (entity != pPlayer) {
                            if (entity.onGround()) {
                                entity.hurt(pPlayer.damageSources().fall(), 35 - (sequence * 5));
                            }
                        }
                    }
                    AABB checkArea = pPlayer.getBoundingBox().inflate(radius);
                    Random random = new Random();
                    for (BlockPos blockPos : BlockPos.betweenClosed(
                            new BlockPos((int) checkArea.minX, (int) checkArea.minY, (int) checkArea.minZ),
                            new BlockPos((int) checkArea.maxX, (int) checkArea.maxY, (int) checkArea.maxZ))) {

                        if (!pPlayer.level().getBlockState(blockPos).isAir() && Earthquake.isOnSurface(pPlayer.level(), blockPos)) {
                            if (random.nextInt(200) == 1) { // 50% chance to destroy a block
                                pPlayer.level().destroyBlock(blockPos, false);
                            } else if (random.nextInt(200) == 2) { // 10% chance to spawn a stone entity
                                StoneEntity stoneEntity = new StoneEntity(pPlayer.level(), pPlayer);
                                ScaleData scaleData = ScaleTypes.BASE.getScaleData(stoneEntity);
                                stoneEntity.teleportTo(blockPos.getX(), blockPos.getY() + 3, blockPos.getZ());
                                stoneEntity.setDeltaMovement(0, (3 + (Math.random() * (6 - 3))), 0);
                                stoneEntity.setStoneYRot((int) (Math.random() * 18));
                                stoneEntity.setStoneXRot((int) (Math.random() * 18));
                                scaleData.setScale((float) (1 + (Math.random()) * 2.0f));
                                pPlayer.level().addFreshEntity(stoneEntity);
                            }
                        }
                    }
                }
                if (sailorEarthquake >= 0) {
                    pPlayer.sendSystemMessage(Component.literal("x is " + sailorEarthquake));
                    pPlayer.getPersistentData().putInt("sailorEarthquake", sailorEarthquake - 1);
                }


                //EXTREME COLDNESS
                int extremeColdness = tag.getInt("sailorExtremeColdness");
                if (extremeColdness >= 150 - (holder.getCurrentSequence()) * 20) {
                    tag.putInt("sailorExtremeColdness", 0);
                    extremeColdness = 0;
                }
                if (extremeColdness >= 1) {
                    pPlayer.sendSystemMessage(Component.literal("x is " + extremeColdness));
                    tag.putInt("sailorExtremeColdness", extremeColdness + 1);

                    AABB areaOfEffect = pPlayer.getBoundingBox().inflate(extremeColdness);
                    List<LivingEntity> entities = pPlayer.level().getEntitiesOfClass(LivingEntity.class, areaOfEffect);
                    for (LivingEntity entity : entities) {
                        if (entity != pPlayer) {
                            int affectedBySailorExtremeColdness = entity.getPersistentData().getInt("affectedBySailorExtremeColdness");
                            entity.getPersistentData().putInt("affectedBySailorExtremeColdness", affectedBySailorExtremeColdness + 1);
                            entity.setTicksFrozen(1);
                        }
                    }
                    List<Entity> entities1 = pPlayer.level().getEntitiesOfClass(Entity.class, areaOfEffect); //test thsi
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
                    BlockPos playerPos = pPlayer.blockPosition();
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
                                surfaceY = pPlayer.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, surfacePos).getY();
                                heightMapCache.put(surfacePos, surfaceY);
                            }

                            for (int dy = 0; dy < 3; dy++) {
                                BlockPos targetPos = new BlockPos(surfacePos.getX(), surfaceY - dy, surfacePos.getZ());
                                if (ExtremeColdness.canFreezeBlock(pPlayer, targetPos)) {
                                    pPlayer.level().setBlockAndUpdate(targetPos, Blocks.ICE.defaultBlockState());
                                    processedBlocks++;
                                }
                            }
                        }
                    }
                }


                //HURRICANE
                boolean sailorHurricaneRain = tag.getBoolean("sailorHurricaneRain");
                BlockPos pos = new BlockPos((int) (pPlayer.getX() + (Math.random() * 100 - 100)), (int) (pPlayer.getY() - 100), (int) (pPlayer.getZ() + (Math.random() * 300 - 300)));
                int hurricane = tag.getInt("sailorHurricane");
                if (hurricane >= 1) {
                    if (sailorHurricaneRain) {
                        tag.putInt("sailorHurricane", hurricane - 1);
                        if (hurricane == 600) {
                            if (pPlayer.level() instanceof ServerLevel) {
                                ServerLevel serverLevel = (ServerLevel) level;
                                serverLevel.setWeatherParameters(0, 700, true, true);
                            }
                        }
                        if (hurricane % 5 == 0) {
                            SailorLightning.shootLineBlockHigh(pPlayer, pPlayer.level());
                        }
                        if (hurricane == 600 || hurricane == 300) {
                            for (int i = 0; i < 5; i++) {
                                TornadoEntity tornado = new TornadoEntity(pPlayer.level(), pPlayer, 0, 0, 0);
                                tornado.teleportTo(pos.getX(), pos.getY() + 100, pos.getZ());
                                tornado.setTornadoRandom(true);
                                tornado.setTornadoHeight(300);
                                tornado.setTornadoRadius(30);
                                tornado.setTornadoPickup(false);
                                pPlayer.level().addFreshEntity(tornado);
                            }
                        }
                    }
                    if (!sailorHurricaneRain) {
                        if (pPlayer.level() instanceof ServerLevel) {
                            ServerLevel serverLevel = (ServerLevel) level;
                            serverLevel.setWeatherParameters(0, 700, true, false);
                        }
                    }
                }


                //LIGHTNING STORM
                double distance = pPlayer.getPersistentData().getDouble("sailorLightningStormDistance");
                if (distance > 300) {
                    tag.putDouble("sailorLightningStormDistance", 0);
                    pPlayer.sendSystemMessage(Component.literal("Storm Radius Is 0").withStyle(style));
                }
                int tyrantVer = tag.getInt("sailorLightningStormTyrant");
                int sailorMentioned = tag.getInt("tyrantMentionedInChat");
                int sailorLightningStorm1 = tag.getInt("sailorLightningStorm1");
                int x1 = tag.getInt("sailorStormVecX1");
                int y1 = tag.getInt("sailorStormVecY1");
                int z1 = tag.getInt("sailorStormVecZ1");
                if (sailorMentioned >= 1) {
                    tag.putInt("tyrantMentionedInChat", sailorMentioned - 1);
                    if (sailorLightningStorm1 >= 1) {
                        LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
                        lightningEntity.setSpeed(10.0f);
                        lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                        lightningEntity.setMaxLength(30);
                        lightningEntity.setOwner(pPlayer);
                        lightningEntity.setOwner(pPlayer);
                        lightningEntity.setNoUp(true);
                        lightningEntity.teleportTo(x1 + ((Math.random() * 300) - (double) 300 / 2), y1 + 80, z1 + ((Math.random() * 300) - (double) 300 / 2));
                        pPlayer.level().addFreshEntity(lightningEntity);
                        pPlayer.level().addFreshEntity(lightningEntity);
                        pPlayer.level().addFreshEntity(lightningEntity);
                        pPlayer.level().addFreshEntity(lightningEntity);
                        if (tyrantVer >= 1) {
                            pPlayer.level().addFreshEntity(lightningEntity);
                            pPlayer.level().addFreshEntity(lightningEntity);
                            pPlayer.level().addFreshEntity(lightningEntity);
                            pPlayer.level().addFreshEntity(lightningEntity);
                            tag.putInt("sailorLightningStormTyrant", tyrantVer - 1);
                        }
                        tag.putInt("sailorLightningStorm1", sailorLightningStorm1 - 1);
                    }
                }
                int sailorLightningStorm = tag.getInt("sailorLightningStorm");
                int stormVec = tag.getInt("sailorStormVec");
                double sailorStormVecX = tag.getInt("sailorStormVecX");
                double sailorStormVecY = tag.getInt("sailorStormVecY");
                double sailorStormVecZ = tag.getInt("sailorStormVecZ");
                if (sailorLightningStorm >= 1) {
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
                    lightningEntity.setSpeed(10.0f);
                    lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                    lightningEntity.setMaxLength(30);
                    lightningEntity.setOwner(pPlayer);
                    lightningEntity.setOwner(pPlayer);
                    lightningEntity.setNoUp(true);
                    lightningEntity.teleportTo(sailorStormVecX + ((Math.random() * distance) - (double) distance / 2), sailorStormVecY + 80, sailorStormVecZ + ((Math.random() * distance) - (double) distance / 2));
                    pPlayer.level().addFreshEntity(lightningEntity);
                    pPlayer.level().addFreshEntity(lightningEntity);
                    pPlayer.level().addFreshEntity(lightningEntity);
                    pPlayer.level().addFreshEntity(lightningEntity);
                    tag.putInt("sailorLightningStorm", sailorLightningStorm - 1);
                }
                if (holder != null) {
                    if (holder.isSailorClass() && holder.getCurrentSequence() <= 3 && pPlayer.getMainHandItem().getItem() instanceof LightningStorm) {
                        if (pPlayer.isShiftKeyDown()) {
                            tag.putInt("sailorStormVec", stormVec + 10);
                            pPlayer.sendSystemMessage(Component.literal("Sailor Storm Spawn Distance is " + stormVec).withStyle(style));
                        }
                        if (stormVec > 300) {
                            tag.putInt("sailorStormVec", 0);
                            stormVec = 0;
                        }
                    }
                }


                //MATTER ACCELERATION SELF
                int matterAccelerationDistance = pPlayer.getPersistentData().getInt("tyrantSelfAcceleration");
                int blinkDistance = pPlayer.getPersistentData().getInt("BlinkDistance");
                if (pPlayer.isShiftKeyDown() && pPlayer.getMainHandItem().getItem() instanceof MatterAccelerationSelf && holder.isSailorClass()) {
                    pPlayer.getPersistentData().putInt("tyrantSelfAcceleration", matterAccelerationDistance + 50);
                    pPlayer.sendSystemMessage(Component.literal("Matter Acceleration Distance is " + matterAccelerationDistance).withStyle(style));
                }
                if (pPlayer.isShiftKeyDown() && pPlayer.getMainHandItem().getItem() instanceof EnvisionLocationBlink && holder.isSpectatorClass()) {
                    pPlayer.getPersistentData().putInt("BlinkDistance", blinkDistance + 5);
                    pPlayer.sendSystemMessage(Component.literal("Blink Distance is " + blinkDistance).withStyle(style));
                }
                if (matterAccelerationDistance >= 1000) {
                    matterAccelerationDistance = 0;
                    pPlayer.getPersistentData().putInt("tyrantSelfAcceleration", 0);
                }
                if (blinkDistance > 200) {
                    blinkDistance = 0;
                    pPlayer.getPersistentData().putInt("BlinkDistance", 0);
                }


                //RAGING BLOWS
                boolean sailorLightning = tag.getBoolean("SailorLightning");
                BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                    int ragingBlows = tag.getInt("ragingBlows");
                    int radius = (25 - (tyrantSequence.getCurrentSequence() * 3));
                    int damage = (int) (20 - tyrantSequence.getCurrentSequence() * 2);
                    if (ragingBlows >= 1) {
                        tag.putInt("ragingBlows", ragingBlows + 1);
                    }
                    if (ragingBlows == 6 || ragingBlows == 12 || ragingBlows == 18 || ragingBlows == 24 || ragingBlows == 30 || ragingBlows == 36 || ragingBlows == 42 ||
                            ragingBlows == 48 || ragingBlows == 54 || ragingBlows == 60 || ragingBlows == 66 || ragingBlows == 72 || ragingBlows == 78 ||
                            ragingBlows == 84 || ragingBlows == 90 || ragingBlows == 96) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                        Vec3 playerLookVector = pPlayer.getViewVector(1.0F);
                        Vec3 playerPos = pPlayer.position();
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, new AABB(playerPos.x - radius, playerPos.y - radius, playerPos.z - radius, playerPos.x + radius, playerPos.y + radius, playerPos.z + radius))) {
                            if (entity != pPlayer && playerLookVector.dot(entity.position().subtract(playerPos)) > 0) {
                                entity.hurt(entity.damageSources().generic(), damage);
                                double ragingBlowsX = pPlayer.getX() - entity.getX();
                                double ragingBlowsZ = pPlayer.getZ() - entity.getZ();
                                entity.knockback(0.25, ragingBlowsX, ragingBlowsZ);
                                if (tyrantSequence.getCurrentSequence() <= 7) {
                                    double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
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
                        tag.putInt("ragingBlows", 0);
                    }
                });
                int rbParticleHelper = tag.getInt("rbParticleHelper");
                AttributeInstance particleHelper = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER1.get());
                if (particleHelper.getBaseValue() == 1) {
                    tag.putInt("rbParticleHelper", rbParticleHelper + 1);
                }
                if (rbParticleHelper >= 100) {
                    tag.putInt("rbParticleHelper", 0);
                    rbParticleHelper = 0;
                    particleHelper.setBaseValue(0);
                }
                if (particleHelper.getBaseValue() == 0) {
                    tag.putInt("rbParticleHelper", 0);
                    rbParticleHelper = 0;
                }


                //RAIN EYES
                if (pPlayer.level().isRaining()) {
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(500))) {
                        if (entity != pPlayer) {
                            if (entity instanceof Player player) {
                                if (player.isInWaterOrRain()) {
                                    pPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "'s location is " + player.getX() + ", " + player.getY() + ", " + player.getZ()));
                                }
                            }
                        }
                    }
                }


                //SIREN SONGS
                int ssHarm = tag.getInt("sirenSongHarm");
                int ssWeaken = tag.getInt("sirenSongWeaken");
                int ssStun = tag.getInt("sirenSongStun");
                int ssStrengthen = tag.getInt("sirenSongStrengthen");
                if (holder.isSailorClass() && holder.getCurrentSequence() <= 5) {
                    if (ssHarm % 20 == 0 && ssHarm != 0) {
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(50 - (sequence * 6)))) {
                            if (entity != pPlayer) {
                                entity.hurt(entity.damageSources().magic(), 10 - sequence);
                            }
                        }
                    }

                    if (ssHarm == 400) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundInit.SIREN_SONG_HARM.get(), SoundSource.NEUTRAL, 1f, 1f);
                    }
                    if (ssHarm >= 1) {
                        tag.putInt("sirenSongHarm", ssHarm - 1);
                    }

                    if (ssWeaken % 20 == 0 && ssWeaken != 0) { //make it for 380,360,430 etc.
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(50 - (sequence * 6)))) {
                            if (entity != pPlayer) {
                                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 19, 2, false, false));
                                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 19, 2, false, false));
                            }
                        }
                    }
                    if (ssWeaken == 400) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundInit.SIREN_SONG_WEAKEN.get(), SoundSource.NEUTRAL, 1f, 1f);
                    }
                    if (ssWeaken >= 1) {
                        tag.putInt("sirenSongWeaken", ssWeaken - 1);
                    }

                    if (ssStun % 20 == 0 && ssStun != 0) {
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(50 - (sequence * 6)))) {
                            if (entity != pPlayer) {
                                entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 19 - (sequence * 2), 2, false, false));
                            }
                        }
                    }
                    if (ssStun == 400) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundInit.SIREN_SONG_STUN.get(), SoundSource.NEUTRAL, 1f, 1f);
                    }
                    if (ssStun >= 1) {
                        tag.putInt("sirenSongStun", ssStun - 1);
                    }
                    if (ssStrengthen % 20 == 0 && ssStrengthen != 0) {
                        if (pPlayer.hasEffect(MobEffects.DAMAGE_BOOST)) {
                            int strengthAmp = pPlayer.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier();
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 19, strengthAmp + 2));
                        } else if (!pPlayer.hasEffect(MobEffects.DAMAGE_BOOST)) {
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 19, 2));
                        }
                        if (pPlayer.hasEffect(MobEffects.REGENERATION)) {
                            int regenAmp = pPlayer.getEffect(MobEffects.REGENERATION).getAmplifier();
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 19, regenAmp + 2));
                        } else if (!pPlayer.hasEffect(MobEffects.REGENERATION)) {
                            pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 19, 2));
                        }
                    }
                    if (ssStrengthen == 400) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundInit.SIREN_SONG_STRENGTHEN.get(), SoundSource.NEUTRAL, 1f, 1f);
                    }
                    if (ssStrengthen >= 1) {
                        tag.putInt("sirenSongStrengthen", ssStrengthen - 1);
                    }
                }
                int ssParticleAttributeHelper = tag.getInt("ssParticleAttributeHelper");
                if (ssParticleAttributeHelper >= 1) {
                    tag.putInt("ssParticleAttributeHelper", ssParticleAttributeHelper - 1);
                    pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).setBaseValue(1);
                }
                if (ssParticleAttributeHelper < 1) {
                    pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).setBaseValue(0);
                }

                AttributeInstance particleAttribute2 = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER2.get());
                int harmCounter = 50 - (sequence * 6);
                if (particleAttribute2.getBaseValue() == 1) {
                    SirenSongHarm.spawnParticlesInSphere(pPlayer, harmCounter);
                } else {
                    particleAttribute2.setBaseValue(0);
                }


                //STAR OF LIGHTNING
                AttributeInstance attributeInstance4 = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
                int sailorLightningStar = tag.getInt("sailorLightningStar");
                if (sailorLightningStar >= 2) {
                    attributeInstance4.setBaseValue(1.0f);
                    tag.putInt("sailorLightningStar", sailorLightningStar - 1);
                }
                if (sailorLightningStar == 1) {
                    tag.putInt("sailorLightningStar", 0);
                    attributeInstance4.setBaseValue(0);
                    for (int i = 0; i < 500; i++) {
                        LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
                        lightningEntity.setSpeed(50);
                        double sailorStarX = (Math.random() * 2 - 1);
                        double sailorStarY = (Math.random() * 2 - 1); // You might want different random values for y and z
                        double sailorStarZ = (Math.random() * 2 - 1);
                        lightningEntity.setDeltaMovement(sailorStarX, sailorStarY, sailorStarZ);
                        lightningEntity.setMaxLength(10);
                        lightningEntity.setOwner(pPlayer);
                        lightningEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
                        pPlayer.level().addFreshEntity(lightningEntity);
                    }
                }


                //TSUNAMI
                int tsunami = tag.getInt("sailorTsunami");
                if (tsunami >= 1) {
                    tag.putInt("sailorTsunami", tsunami - 5);
                    Tsunami.summonTsunami(pPlayer);
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
                    TsunamiSeal.summonTsunami(pPlayer);
                } else {
                    tag.remove("sailorTsunamiDirection");
                    tag.remove("sailorTsunamiX");
                    tag.remove("sailorTsunamiY");
                    tag.remove("sailorTsunamiZ");
                }


                //WATER SPHERE CHECK FROM HERE ONWARDS
                if (pPlayer.getPersistentData().getInt("sailorSphere") >= 5) {
                    for (Entity entity : pPlayer.level().getEntitiesOfClass(Entity.class, pPlayer.getBoundingBox().inflate(4))) {
                        if (!(entity instanceof LivingEntity && !(entity instanceof MeteorEntity) && !(entity instanceof MeteorNoLevelEntity))) {
                            entity.remove(Entity.RemovalReason.DISCARDED);
                        }
                    }
                    BlockPos playerPos = pPlayer.blockPosition();
                    double radius = 3.0;
                    double minRemovalRadius = 4.0;
                    double maxRemovalRadius = 7.0;

                    // Create a sphere of water around the player
                    for (int sphereX = (int) -radius; sphereX <= radius; sphereX++) {
                        for (int sphereY = (int) -radius; sphereY <= radius; sphereY++) {
                            for (int sphereZ = (int) -radius; sphereZ <= radius; sphereZ++) {
                                double sphereDistance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                                if (sphereDistance <= radius) {
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
                                double sphereDistance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                                if (sphereDistance <= maxRemovalRadius && sphereDistance >= minRemovalRadius) {
                                    BlockPos blockPos = playerPos.offset(sphereX, sphereY, sphereZ);
                                    if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                                        level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                    }
                                }
                            }
                        }
                    }
                }
                if (pPlayer.getPersistentData().getInt("sailorSphere") >= 1 && pPlayer.getPersistentData().getInt("sailorSphere") <= 4) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 100, false, false));
                    for (int sphereX = -6; sphereX <= 6; sphereX++) {
                        for (int sphereY = -6; sphereY <= 6; sphereY++) {
                            for (int sphereZ = -6; sphereZ <= 6; sphereZ++) {
                                double sphereDistance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                                if (sphereDistance <= 6) {
                                    BlockPos blockPos = pPlayer.getOnPos().offset(sphereX, sphereY, sphereZ);
                                    if (pPlayer.level().getBlockState(blockPos).getBlock() == Blocks.WATER) {
                                        pPlayer.level().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                    }
                                }
                            }
                        }
                    }
                }
                if (pPlayer.getPersistentData().getInt("sailorSphere") >= 1) {
                    pPlayer.getPersistentData().putInt("sailorSphere", pPlayer.getPersistentData().getInt("sailorSphere") - 1);
                }


                //WIND MANIPULATION FLIGHT
                Vec3 lookVector = pPlayer.getLookAngle();
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
                if (flight == 20) {
                    pPlayer.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
                    pPlayer.hurtMarked = true;
                }
                if (flight == 40) {
                    pPlayer.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
                    pPlayer.hurtMarked = true;
                }
                if (flight == 60) {
                    pPlayer.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
                    pPlayer.hurtMarked = true;
                }
                if (flight > 60) {
                    tag.putInt("sailorFlight", 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void stunEffect(LivingEntityUseItemEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() && entity.hasEffect(ModEffects.STUN.get())) {
            ItemStack itemStack = entity.getMainHandItem();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void handleLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        Level level = entity.level();
        if (!entity.level().isClientSide) {

            //DREAM WEAVING
            AttributeInstance maxHP = entity.getAttribute(Attributes.MAX_HEALTH);
            if (!(entity instanceof Player) && maxHP.getBaseValue() == 551) {
                int deathTimer = entity.getPersistentData().getInt("DeathTimer");
                entity.getPersistentData().putInt("DeathTimer", deathTimer + 1);
                if (deathTimer >= 300) {
                    entity.remove(Entity.RemovalReason.KILLED);
                }
            }

            //MATTER ACCELERATION: ENTITIES
            int matterAceelrationEntities = entity.getPersistentData().getInt("matterAccelerationEntities");
            if (matterAceelrationEntities >= 1) {
                entity.getPersistentData().putInt("matterAccelerationEntities", matterAceelrationEntities - 1);
                double movementX = Math.abs(entity.getDeltaMovement().x());
                double movementY = Math.abs(entity.getDeltaMovement().y());
                double movementZ = Math.abs(entity.getDeltaMovement().z());
                if (movementX >= 6 || movementY >= 6 || movementZ >= 6) {
                    BlockPos entityPos = entity.blockPosition();
                    for (int x = -2; x <= 2; x++) {
                        for (int y = -2; y <= 2; y++) {
                            for (int z = -2; z <= 2; z++) {
                                BlockPos pos = entityPos.offset(x, y, z);

                                // Remove the block (replace with air)
                                entity.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            }
                        }
                    }
                    for (LivingEntity entity1 : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(5))) {
                        if (entity1 != entity) {
                            if (entity1 instanceof Player player) {
                                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(player).orElse(null);
                                if (holder != null) {
                                    if (!holder.isSailorClass() && holder.getCurrentSequence() == 0) {
                                        player.hurt(player.damageSources().lightningBolt(), 10);
                                    }
                                }
                            } else {
                                entity1.hurt(entity1.damageSources().lightningBolt(), 10);
                            }
                        }
                    }
                }
            }


            //MENTAL PLAGUE
            int mentalPlagueTimer = entity.getPersistentData().getInt("MentalPlagueTimer");
            if (entity.hasEffect(ModEffects.MENTALPLAGUE.get())) {
                mentalPlagueTimer++;

                if (mentalPlagueTimer >= 600) {
                    for (LivingEntity entity1 : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(50))) {
                        applyEffectsAndDamage(entity1);

                    }
                    applyEffectsAndDamage(entity);
                    mentalPlagueTimer = 0;
                }
            }
            entity.getPersistentData().putInt("MentalPlagueTimer", mentalPlagueTimer);


            //PROPHESIZE DEMISE
            double prevX = tag.getDouble("prevX");
            double prevY = tag.getDouble("prevY");
            double prevZ = tag.getDouble("prevZ");
            double currentX = tag.getDouble("currentX");
            double currentY = tag.getDouble("currentY");
            double currentZ = tag.getDouble("currentZ");
            int tickCounter = tag.getInt("tickCounter");
            boolean hasSpectatorDemise = entity.hasEffect(ModEffects.SPECTATORDEMISE.get());
            int messageCounter = tag.getInt("MessageCounter");
            if (!hasSpectatorDemise) {
                int demise = tag.getInt("EntityDemise");
                demise = 0;
                tag.putInt("EntityDemise", 0);
                messageCounter = 0;
                tag.putInt("MessageCounter", 0);
                int nonDemise = tag.getInt("NonDemise");
                nonDemise = 0;
                tag.putInt("NonDemise", 0);
            }
            if (hasSpectatorDemise) {
                MobEffectInstance demiseEffect = entity.getEffect(ModEffects.SPECTATORDEMISE.get());
                if (demiseEffect != null) {
                    int effectDuration = demiseEffect.getDuration();
                    int effectDurationSeconds;
                    if (effectDuration < 20) {
                        effectDurationSeconds = 1;
                    } else {
                        effectDurationSeconds = (effectDuration + 19) / 20;
                    }
                    if (hasSpectatorDemise) {

                        int demise = tag.getInt("EntityDemise");
                        int nonDemise = tag.getInt("NonDemise");

                        int nonDemiseSeconds = (nonDemise + 19) / 20;
                        if (tickCounter == 0) {
                            prevX = entity.getX();
                            tag.putDouble("prevX", prevX);

                            prevY = entity.getY();
                            tag.putDouble("prevY", prevY);

                            prevZ = entity.getZ();
                            tag.putDouble("prevZ", prevZ);

                            tag.putInt("tickCounter", 1);
                        } else if (tickCounter == 1) {
                            currentX = entity.getX();
                            tag.putDouble("currentX", currentX);

                            currentY = entity.getY();
                            tag.putDouble("currentY", currentY);

                            currentZ = entity.getZ();
                            tag.putDouble("currentZ", currentZ);

                            tag.putInt("tickCounter", 0);
                        }
                        if (Math.abs(prevX - currentX) > 0.0023 || Math.abs(prevY - currentY) > 0.0023 || Math.abs(prevZ - currentZ) > 0.0023) { //movement check more accurate
                            demise++;
                            tag.putInt("EntityDemise", demise);
                        } else {
                            nonDemise++;
                            tag.putInt("NonDemise", nonDemise);
                        }
                        if (demise == 400) {
                            entity.kill();
                            messageCounter = 0;
                            tag.putInt("MessageCounter", messageCounter);
                            nonDemise = 0;
                            tag.putInt("NonDemise", nonDemise);
                        }
                        if (nonDemise > 200) {
                            demise = 0;
                            tag.putInt("EntityDemise", demise);
                            entity.removeEffect(ModEffects.SPECTATORDEMISE.get());
                            nonDemise = 0;
                            tag.putInt("NonDemise", nonDemise);
                            messageCounter = 0;
                            tag.putInt("MessageCounter", messageCounter);
                        }
                        if (nonDemise == 200) {
                            demise = 0;
                            tag.putInt("EntityDemise", demise);
                            entity.removeEffect(ModEffects.SPECTATORDEMISE.get());
                            entity.sendSystemMessage(Component.literal("You survived your fate").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                            nonDemise = 0;
                            tag.putInt("NonDemise", nonDemise);
                            messageCounter = 0;
                            tag.putInt("MessageCounter", messageCounter);
                        }
                        if (demise == 20 && messageCounter == 0) {
                            messageCounter = 1;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 19 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 40 && messageCounter == 1) {
                            messageCounter = 2;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 18 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 60 && messageCounter == 2) {
                            messageCounter = 3;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 17 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 80 && messageCounter == 3) {
                            messageCounter = 4;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 16 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 100 && messageCounter == 4) {
                            messageCounter = 5;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 15 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 120 && messageCounter == 5) {
                            messageCounter = 6;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 14 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 140 && messageCounter == 6) {
                            messageCounter = 7;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 13 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 160 && messageCounter == 7) {
                            messageCounter = 8;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 12 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 180 && messageCounter == 8) {
                            messageCounter = 9;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 11 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 200 && messageCounter == 9) {
                            messageCounter = 10;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 10 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 220 && messageCounter == 10) {
                            messageCounter = 11;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 9 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 240 && messageCounter == 11) {
                            messageCounter = 12;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 8 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 260 && messageCounter == 12) {
                            messageCounter = 13;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 7 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 280 && messageCounter == 13) {
                            messageCounter = 14;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 6 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 300 && messageCounter == 14) {
                            messageCounter = 15;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 5 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 320 && messageCounter == 15) {
                            messageCounter = 16;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 4 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 340 && messageCounter == 16) {
                            messageCounter = 17;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 3 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 360 && messageCounter == 17) {
                            messageCounter = 18;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 2 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 380 && messageCounter == 18) {
                            messageCounter = 19;
                            tag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 1 second, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 20) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 9 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 40) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 8 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 60) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 7 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 80) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 6 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 100) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 5 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 120) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 4 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 140) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 3 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 160) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 2 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 180) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 1 more second").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                    }
                }
            }

            //AQUEOUS LIGHT DROWN
            BlockPos headPos = BlockPos.containing(entity.getEyePosition());
            int aqueousLight = tag.getInt("lightDrowning");
            if (aqueousLight == 1) {
                entity.setAirSupply(0);
            }
            if (aqueousLight >= 1) {
                if (entity.getDeltaMovement().y <= 0.2) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x, entity.getDeltaMovement().y - 0.01, entity.getDeltaMovement().z);
                }
                tag.putInt("lightDrowning", aqueousLight + 1);
                if (level.getBlockState(headPos).is(Blocks.AIR)) {
                    level.setBlockAndUpdate(headPos, Blocks.WATER.defaultBlockState());
                }
                for (int x = -3; x <= 3; x++) {
                    for (int y = -3; y <= 3; y++) {
                        for (int z = -3; z <= 3; z++) {
                            if (Math.abs(x) > 1 || Math.abs(y) > 1 || Math.abs(z) > 1) {
                                BlockPos blockPos = headPos.offset(x, y, z);
                                if (level.getBlockState(blockPos).is(Blocks.WATER)) {
                                    level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }


            //EXTREME COLDNESS
            int affectedBySailorExtremeColdness = tag.getInt("affectedBySailorExtremeColdness");
            if (!entity.level().isClientSide()) {
                if (entity instanceof Player pPlayer) {
                    pPlayer.setTicksFrozen(3);
                }
                if (affectedBySailorExtremeColdness == 5) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, false));
                }
                if (affectedBySailorExtremeColdness == 10) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false));
                }
                if (affectedBySailorExtremeColdness == 15) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, false));
                }
                if (affectedBySailorExtremeColdness >= 20) {
                    entity.addEffect(new MobEffectInstance(ModEffects.AWE.get(), 100, 1, false, false));
                    tag.putInt("affectedBySailorExtremeColdness", 0);
                    affectedBySailorExtremeColdness = 0;
                    entity.hurt(entity.damageSources().freeze(), 30);
                }
            }


            //TSUNAMI SEAL
            int sealCounter = tag.getInt("sailorSeal");
            if (sealCounter >= 3) {
                int sealX = tag.getInt("sailorSealX");
                int sealY = tag.getInt("sailorSealY");
                int sealZ = tag.getInt("sailorSealZ");
                entity.teleportTo(sealX, sealY, sealZ);
                BlockPos playerPos = entity.blockPosition();
                double radius = 6.0;
                double minRemovalRadius = 6.0;
                double maxRemovalRadius = 11.0;

                // Create a sphere of water around the player
                for (int x = (int) -radius; x <= radius; x++) {
                    for (int y = (int) -radius; y <= radius; y++) {
                        for (int z = (int) -radius; z <= radius; z++) {
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            if (distance <= radius) {
                                BlockPos blockPos = playerPos.offset(x, y, z);
                                if (level.getBlockState(blockPos).isAir() && !level.getBlockState(blockPos).is(Blocks.WATER)) {
                                    level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
                for (int x = (int) -maxRemovalRadius; x <= maxRemovalRadius; x++) {
                    for (int y = (int) -maxRemovalRadius; y <= maxRemovalRadius; y++) {
                        for (int z = (int) -maxRemovalRadius; z <= maxRemovalRadius; z++) {
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            if (distance <= maxRemovalRadius && distance >= minRemovalRadius) {
                                BlockPos blockPos = playerPos.offset(x, y, z);
                                if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
                tag.putInt("sailorSeal", sealCounter - 1);
                if (sealCounter % 20 == 0) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 1, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3, false, false));
                }
            }
            if (sealCounter == 1) {
                double minRemovalRadius = 6.0;
                double maxRemovalRadius = 11.0;
                BlockPos playerPos = entity.blockPosition();
                for (int x = (int) -maxRemovalRadius; x <= maxRemovalRadius; x++) {
                    for (int y = (int) -maxRemovalRadius; y <= maxRemovalRadius; y++) {
                        for (int z = (int) -maxRemovalRadius; z <= maxRemovalRadius; z++) {
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            if (distance <= maxRemovalRadius && distance >= minRemovalRadius) {
                                BlockPos blockPos = playerPos.offset(x, y, z);
                                if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }


            //STORM SEAL
            if (tag.getInt("inStormSeal") >= 3) {

                int stormSeal = tag.getInt("inStormSeal");
                int x = tag.getInt("stormSealX");
                int y = tag.getInt("stormSealY");
                int z = tag.getInt("stormSealZ");
                entity.teleportTo(x, y + 10, z);
                BlockPos lightningSpawnPos = new BlockPos((int) (entity.getX() + (Math.random() * 20) - 10), (int) (entity.getY() + (Math.random() * 20) - 10), (int) (entity.getZ() + (Math.random() * 20) - 10));
                MCLightningBoltEntity lightningBolt = new MCLightningBoltEntity(EntityInit.MC_LIGHTNING_BOLT.get(), entity.level());
                lightningBolt.teleportTo(lightningSpawnPos.getX(), lightningSpawnPos.getY(), lightningSpawnPos.getZ());
                if (entity.tickCount % 3 == 0) {
                    if (!entity.level().isClientSide()) {
                        level.addFreshEntity(lightningBolt);
                    }
                }
                tag.putInt("inStormSeal", stormSeal - 1);
                if (entity.tickCount % 10 == 0) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20, 0, false, false));
                    entity.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 20, 0, false, false));
                }
                if (stormSeal % 20 == 0) {
                    if (entity instanceof Player pPlayer) {
                        int sealSeconds = (int) stormSeal / 20;
                        pPlayer.displayClientMessage(Component.literal("You are stuck in the storm seal for " + sealSeconds + " seconds").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE),true);
                    }
                }
            }
            if (tag.getInt("inStormSeal") == 2 || tag.getInt("inStormSeal") == 1) {
                int x = tag.getInt("stormSealX");
                int y = tag.getInt("stormSealY");
                int z = tag.getInt("stormSealZ");
                tag.putInt("inStormSeal", tag.getInt("inStormSeal") - 1);
                entity.teleportTo(x,y,z);
            }
        }
    }


    @SubscribeEvent
    public static void sailorLightningEvent(AttackEntityEvent event) {
        Player pPlayer = event.getEntity();
        CompoundTag tag = pPlayer.getPersistentData();
        boolean sailorLightning = tag.getBoolean("SailorLightning");
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (!pPlayer.level().isClientSide()) {


            //SAILOR PASSIVE
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 7) {
                    LivingEntity target = (LivingEntity) event.getTarget();
                    if (sailorLightning) {
                        if (target != pPlayer) {
                            double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5)); // Decrease chance by 12.5% for each level below 9
                            if (Math.random() * 100 < chanceOfDamage) {
                                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, target.level());
                                lightningBolt.moveTo(target.getX(), target.getY(), target.getZ());
                                target.level().addFreshEntity(lightningBolt);
                            }
                        }
                    }
                }
            });


        }
    }

    @SubscribeEvent
    public static void projectileImpactEvent(ProjectileImpactEvent event) {
        Entity projectile = event.getProjectile();
        if (!projectile.level().isClientSide()) {
            CompoundTag tag = projectile.getPersistentData();
            int x = tag.getInt("sailorLightningProjectileCounter");
            if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY && x >= 1) {
                EntityHitResult entityHit = (EntityHitResult) event.getRayTraceResult();
                Entity entity = entityHit.getEntity();
                if (!entity.level().isClientSide()) {
                    if (entity instanceof LivingEntity) {
                        entity.hurt(projectile.damageSources().lightningBolt(), (x * 5));
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                        lightningBolt.moveTo(entity.getX(), entity.getY(), entity.getZ());
                        entity.level().addFreshEntity(lightningBolt);
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
            if (event.getRayTraceResult().getType() == HitResult.Type.BLOCK && x >= 1) {
                Vec3 blockPos = event.getRayTraceResult().getLocation();
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, projectile.level());
                lightningBolt.moveTo(blockPos);
                projectile.level().addFreshEntity(lightningBolt);
                projectile.level().explode(null, blockPos.x(), blockPos.y(), blockPos.z(), 4, Level.ExplosionInteraction.BLOCK);
            }
        }
    }

    private static void applyEffectsAndDamage(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 2, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 2, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 1, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, 1, false, false));
        entity.hurt(entity.damageSources().magic(), 20);
    }

    private static void removeArmor(Player pPlayer) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armorStack = pPlayer.getItemBySlot(slot);
                if (!armorStack.isEmpty()) {
                    pPlayer.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
        }
    }
    @SubscribeEvent
    public static void hurtEvent(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();

        //SAILOR FLIGHT
        if (entity instanceof Player pPlayer) {
            int flightCancel = tag.getInt("sailorFlightDamageCancel");
            if (!pPlayer.level().isClientSide()) {
                if (flightCancel != 0 && event.getSource() == pPlayer.damageSources().fall()) {
                    event.setCanceled(true);
                    tag.putInt("sailorFlightDamageCancel", 0);
                }
            }
        }


        //STORM SEAL
        if (entity.getPersistentData().getInt("inStormSeal") >= 1) {
            event.setCanceled(true);
            System.out.println("hurt canceled");
        }
    }

    @SubscribeEvent
    public static void deathEvent(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        if (!entity.level().isClientSide()) {


            //STORM SEAL
            if (tag.getInt("inStormSeal") >= 1) {
                event.setCanceled(true);
                System.out.println("death canceled");
                entity.setHealth(5.0f);
            }
        }
    }
}