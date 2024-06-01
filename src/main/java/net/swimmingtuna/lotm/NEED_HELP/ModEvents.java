package net.swimmingtuna.lotm.NEED_HELP;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.particle.AcidRainParticle;
import net.swimmingtuna.lotm.particle.NullParticle;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents {

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
                pPlayer.setDeltaMovement(tag.getDouble("lookVectorXCushion") * 2,  tag.getDouble("lookVectorYCushion") * 2, tag.getDouble("lookVectorZCushion") * 2);
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
    public static void windSailorSense(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (event.phase == TickEvent.Phase.END) {
            if (!pPlayer.level().isClientSide()) {
                CompoundTag tag = pPlayer.getPersistentData();
                int windGlowing = tag.getInt("windGlowing");
                if (windGlowing >= 1 && windGlowing <= 200) {
                    pPlayer.sendSystemMessage(Component.literal("windglowing is" + windGlowing));
                }
                if (windGlowing > 200) {
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(20))) {
                        if (entity != pPlayer) {
                            entity.removeEffect(ModEffects.LOTMGLOWING.get());
                        }}
                }
            } else {
                LOTMNetworkHandler.sendToServer(new GlowingPacketC2S());
            }

        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        int glowing = tag.getInt("LOTMisGlowing");
        if (entity.isCurrentlyGlowing() && glowing == 0) {
            tag.putInt("LOTMisGlowing", 1);;
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
}