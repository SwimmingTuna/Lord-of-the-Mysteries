package net.swimmingtuna.lotm.events;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.custom_events.ModEventFactory;
import net.swimmingtuna.lotm.events.custom_events.ProjectileEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.DreamIntoReality;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionBarrier;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;

import static net.swimmingtuna.lotm.util.BeyonderUtil.getProjectiles;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents implements ReachChangeUUIDs {


    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
    }

    @SubscribeEvent
    public static void attributeHandler(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        CompoundTag tag = pPlayer.getPersistentData();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        Level level = pPlayer.level();
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {


            //NIGHTMARE
            AttributeInstance nightmareAttribute = pPlayer.getAttribute(ModAttributes.NIGHTMARE.get());
            AttributeInstance armorInvisAttribute = pPlayer.getAttribute(ModAttributes.ARMORINVISIBLITY.get());
            int nightmareTimer = tag.getInt("NightmareTimer");
            int matterAccelerationBlockTimer = pPlayer.getPersistentData().getInt("matterAccelerationBlockTimer");
            if (matterAccelerationBlockTimer >= 1) {
                pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", matterAccelerationBlockTimer - 1);
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
            tag.putInt("NightmareTimer", nightmareTimer);
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


                            //SAILOR PASSIVE
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
            if (pPlayer.getPersistentData().getBoolean("manipulateMovementBoolean"))
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

            //
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
}