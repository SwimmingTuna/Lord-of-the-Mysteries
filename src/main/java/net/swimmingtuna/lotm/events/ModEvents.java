package net.swimmingtuna.lotm.events;

import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.custom_events.ModEventFactory;
import net.swimmingtuna.lotm.events.custom_events.ProjectileEvent;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;

import java.util.List;

import static net.minecraft.commands.arguments.EntityArgument.getEntity;
import static net.swimmingtuna.lotm.util.BeyonderUtil.getProjectiles;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents implements ReachChangeUUIDs {


    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
    }

    @SubscribeEvent
    public static void attributeHandler(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {
            AttributeInstance nightmareAttribute = pPlayer.getAttribute(ModAttributes.NIGHTMARE.get());
            AttributeInstance armorInvisAttribute = pPlayer.getAttribute(ModAttributes.ARMORINVISIBLITY.get());
            if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.START) {
                int nightmareTimer = pPlayer.getPersistentData().getInt("NightmareTimer");

                if (nightmareAttribute.getValue() >= 1) {
                    nightmareTimer++;
                    if (nightmareTimer >= 600) {
                        nightmareAttribute.setBaseValue(0);
                        nightmareTimer = 0;
                    }
                } else {
                    nightmareTimer = 0;
                }
                if (armorInvisAttribute.getValue() > 0 && !pPlayer.hasEffect(MobEffects.INVISIBILITY)) {
                    removeArmor(pPlayer);
                    armorInvisAttribute.setBaseValue(0);
                }

                // Save the updated nightmareTimer in player persistent data
                pPlayer.getPersistentData().putInt("NightmareTimer", nightmareTimer);
            }
        }
    }

    @SubscribeEvent
    public static void handleLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.level().isClientSide) {
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
    public static void sailorLightningEvent(AttackEntityEvent event) {
        Player pPlayer = event.getEntity();
        CompoundTag tag = pPlayer.getPersistentData();
        boolean x = tag.getBoolean("SailorLightning");
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 7) {
                    LivingEntity target = (LivingEntity) event.getTarget();
                    if (x) {
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
    public static void playerWindCushionTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        CompoundTag tag = pPlayer.getPersistentData();
        Vec3 lookVector = pPlayer.getLookAngle();
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {
            int windCushion = tag.getInt("windCushion");
            if (windCushion == 1) {
                tag.putDouble("lookVectorXCushion", lookVector.x());
                tag.putDouble("lookVectorYCushion", lookVector.y());
                tag.putDouble("lookVectorZCushion", lookVector.z());
            }
            if (windCushion >= 1 && windCushion < 16) {
                tag.putInt("windCushion", windCushion + 1);
                pPlayer.setDeltaMovement(pPlayer.getDeltaMovement().x(), pPlayer.getDeltaMovement().y() * 0.2, pPlayer.getDeltaMovement().z());
                pPlayer.hurtMarked = true;
            }
            if (windCushion >= 16) {
                tag.putInt("windCushion", windCushion + 1);
            }
            if (windCushion == 16) {
                pPlayer.setDeltaMovement(tag.getDouble("lookVectorXCushion") * 2, tag.getDouble("lookVectorYCushion") * 2, tag.getDouble("lookVectorZCushion") * 2);
                pPlayer.resetFallDistance();
                pPlayer.hurtMarked = true;
            }
            if (windCushion >= 20) {
                tag.putInt("windCushion", 0);
                windCushion = 0;
            }
        }
    }

    @SubscribeEvent
    public static void windManipulationGlide(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (event.phase == TickEvent.Phase.END && !pPlayer.level().isClientSide()) {
            CompoundTag tag = pPlayer.getPersistentData();
            int regularFlight = tag.getInt("sailorFlight");
            boolean enhancedFlight = tag.getBoolean("sailorFlight1");
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder.isSailorClass() && holder.getCurrentSequence() <= 7 && pPlayer.isShiftKeyDown() && pPlayer.getDeltaMovement().y() < 0 && !pPlayer.getAbilities().instabuild && !enhancedFlight && regularFlight == 0) {
                Vec3 movement = pPlayer.getDeltaMovement();
                double deltaX = Math.cos(Math.toRadians(pPlayer.getYRot() + 90)) * 0.06;
                double deltaZ = Math.sin(Math.toRadians(pPlayer.getYRot() + 90)) * 0.06;
                pPlayer.setDeltaMovement(movement.x + deltaX, -0.05, movement.z + deltaZ);


                pPlayer.resetFallDistance();
                pPlayer.hurtMarked = true;
                pPlayer.sendSystemMessage(Component.literal("y delta movement is" + pPlayer.getDeltaMovement().y()));
            }
        }
    }

    @SubscribeEvent
    public static void windSailorSense(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (event.phase == TickEvent.Phase.END) {
            if (!pPlayer.level().isClientSide()) {

                if (pPlayer.getPersistentData().getBoolean("sailorFlight1") == false) {
                    pPlayer.getPersistentData().putBoolean("sailorFlight1", false);
                }

                CompoundTag tag = pPlayer.getPersistentData();
                int windGlowing = tag.getInt("windGlowing");
                if (windGlowing > 200) {
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(20))) {
                        if (entity != pPlayer) {
                            entity.removeEffect(ModEffects.LOTMGLOWING.get());
                        }
                    }
                } else { //LOTMNetworkHandler.sendToPlayer(new GlowingPacketC2S(), (ServerPlayer) pPlayer);
                }
            }

        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        int glowing = tag.getInt("LOTMisGlowing");
        if (entity.isCurrentlyGlowing() && glowing == 0) {
            tag.putInt("LOTMisGlowing", 1);
            ;
        }
        if (glowing >= 1) {
            tag.putInt("LOTMisGlowing", glowing + 1);
        }
        if (glowing > 100) {
            if (entity.hasEffect(MobEffects.GLOWING) || entity.hasEffect(ModEffects.LOTMGLOWING.get())) {
                tag.putInt("LOTMisGlowing", 1);
                entity.setGlowingTag(false);
            } else {
                tag.putInt("LOTMisGlowing", 0);
            }
        }
    }

    @SubscribeEvent
    public static void sailorProjectileMovement(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        Projectile projectile = getProjectiles(pPlayer);
        if (projectile != null) {
            ProjectileEvent.ProjectileControlEvent projectileEvent = new ProjectileEvent.ProjectileControlEvent(projectile);
            projectile = projectileEvent.getProjectile();

            Player player = (Player) projectileEvent.getOwner();
            if (projectile != null) {
                if (!player.level().isClientSide()) {
                    ModEventFactory.onSailorShootProjectile(projectile);
                    if (!projectile.level().isClientSide()) {
                        LivingEntity target = projectileEvent.getTarget(75, 0);
                        if (target != null) {
                            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(player).orElse(null);
                            if (holder.isSailorClass() && holder.getCurrentSequence() <= 7) {
                                projectileEvent.addMovement(projectile, (target.getX() - projectile.getX()) * 0.1, (target.getY() - projectile.getY()) * 0.1, (target.getZ() - projectile.getZ()) * 0.1);
                                projectile.hurtMarked = true;
                            }
                        }
                    }
                }
            }
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
}