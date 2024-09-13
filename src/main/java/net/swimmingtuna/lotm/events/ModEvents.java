package net.swimmingtuna.lotm.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.client.Configs;
import net.swimmingtuna.lotm.entity.*;
import net.swimmingtuna.lotm.events.custom_events.ModEventFactory;
import net.swimmingtuna.lotm.events.custom_events.ProjectileEvent;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.SoundInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.BeyonderAbilityUser;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.DreamIntoReality;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionBarrier;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionLocationBlink;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents {


    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        BeyonderAbilityUser.resetClicks(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        BeyonderUtil.leftClickEmpty(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        BeyonderUtil.leftClickBlock(event.getEntity());
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof BeyonderAbilityUser)) {
            return;
        }
        byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");
        for (int i = 0; i < keysClicked.length; i++) {
            if (keysClicked[i] == 0) {
                keysClicked[i] = 1;
                BeyonderAbilityUser.clicked(player, InteractionHand.MAIN_HAND);
                return;
            }
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Style style = BeyonderUtil.getStyle(player);
        CompoundTag playerPersistentData = player.getPersistentData();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        int sequence = holder.getCurrentSequence();
        if (player.level().isClientSide() || event.phase != TickEvent.Phase.START) {
            return;
        }

        AttributeInstance corruption = player.getAttribute(ModAttributes.CORRUPTION.get());
        AttributeInstance luck = player.getAttribute(ModAttributes.LOTM_LUCK.get());


        Map<String, Long> times = new HashMap<>();

        {
            long startTime = System.nanoTime();
//            corruptionAndLuckManagers(serverLevel, corruption, player, luck, holder, sequence);
            long endTime = System.nanoTime();
            times.put("corruptionAndLuckManagers", endTime - startTime);
        }
        {
            long startTime = System.nanoTime();
            nightmare(player, playerPersistentData);
            long endTime = System.nanoTime();
            times.put("nightmare", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            calamityIncarnationTornado(playerPersistentData, player);
            long endTime = System.nanoTime();
            times.put("calamityIncarnationTornado", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            psychologicalInvisibility(player, playerPersistentData, holder);
            long endTime = System.nanoTime();
            times.put("psychologicalInvisibility", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            windManipulationSense(playerPersistentData, holder, player);
            long endTime = System.nanoTime();
            times.put("windManipulationSense", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            sailorLightningTravel(player);
            long endTime = System.nanoTime();
            times.put("sailorLightningTravel", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            windManipulationCushion(playerPersistentData, player);
            long endTime = System.nanoTime();
            times.put("windManipulationCushion", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            windManipulationGuide(playerPersistentData, holder, player);
            long endTime = System.nanoTime();
            times.put("windManipulationGuide", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            dreamIntoReality(player, holder);
            long endTime = System.nanoTime();
            times.put("dreamIntoReality", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            consciousnessStroll(playerPersistentData, player);
            long endTime = System.nanoTime();
            times.put("consciousnessStroll", endTime - startTime);
        }
        {
            long startTime = System.nanoTime();
            prophesizeTeleportation(playerPersistentData, player);
            long endTime = System.nanoTime();
            times.put("prophesizeTeleportationCounter", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            projectileEvent(player, holder);
            long endTime = System.nanoTime();
            times.put("projectileEvent", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            envisionBarrier(holder, player, style);
            long endTime = System.nanoTime();
            times.put("envisionBarrier", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            envisionLife(player);
            long endTime = System.nanoTime();
            times.put("envisionLife", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            manipulateMovement(player, serverLevel);
            long endTime = System.nanoTime();
            times.put("manipulateMovement", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            envisionKingdom(playerPersistentData, player, holder, serverLevel);
            long endTime = System.nanoTime();
            times.put("envisionKingdom", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            acidicRain(player, sequence);
            long endTime = System.nanoTime();
            times.put("acidicRain", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            calamityIncarnationTsunami(playerPersistentData, player, serverLevel);
            long endTime = System.nanoTime();
            times.put("calamityIncarnationTsunami", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            earthquake(player, sequence);
            long endTime = System.nanoTime();
            times.put("earthquake", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            extremeColdness(playerPersistentData, holder, player);
            long endTime = System.nanoTime();
            times.put("extremeColdness", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            hurricane(playerPersistentData, player);
            long endTime = System.nanoTime();
            times.put("hurricane", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            lightningStorm(player, playerPersistentData, style, holder);
            long endTime = System.nanoTime();
            times.put("lightningStorm", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            matterAccelerationSelf(player, holder, style);
            long endTime = System.nanoTime();
            times.put("matterAccelerationSelf", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            ragingBlows(playerPersistentData, holder, player);
            long endTime = System.nanoTime();
            times.put("ragingBlows", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            rainEyes(player);
            long endTime = System.nanoTime();
            times.put("rainEyes", endTime - startTime);
        }

        {
            long startTime = System.nanoTime();
            sirenSongs(playerPersistentData, holder, player, sequence);
            long endTime = System.nanoTime();
            times.put("sirenSongs", endTime - startTime);
        }
        int ssParticleAttributeHelper = playerPersistentData.getInt("ssParticleAttributeHelper");
        if (ssParticleAttributeHelper >= 1) {
            playerPersistentData.putInt("ssParticleAttributeHelper", ssParticleAttributeHelper - 1);
            player.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).setBaseValue(1);
        }
        if (ssParticleAttributeHelper < 1) {
            player.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).setBaseValue(0);
        }

        AttributeInstance particleAttribute2 = player.getAttribute(ModAttributes.PARTICLE_HELPER2.get());
        int harmCounter = 50 - (sequence * 6);
        if (particleAttribute2.getBaseValue() == 1) {
            SirenSongHarm.spawnParticlesInSphere(player, harmCounter);
        } else {
            particleAttribute2.setBaseValue(0);
        }


        {
            long startTime = System.nanoTime();
            starOfLightning(player, playerPersistentData);
            long endTime = System.nanoTime();
            times.put("starOfLightning", endTime - startTime);
        }
        {
            long startTime = System.nanoTime();
            tsunami(playerPersistentData, player);
            long endTime = System.nanoTime();
            times.put("tsunami", endTime - startTime);
        }
        {
            long startTime = System.nanoTime();
            waterSphereCheck(player, serverLevel);
            long endTime = System.nanoTime();
            times.put("waterSphereCheck", endTime - startTime);
        }
        {
            long startTime = System.nanoTime();
            windManipulationFlight(player, playerPersistentData);
            long endTime = System.nanoTime();
            times.put("windManipulationFlight", endTime - startTime);
        }

//        System.out.println(times.entrySet().stream().max(Map.Entry.comparingByValue()));
    }

    private static void corruptionAndLuckManagers(ServerLevel serverLevel, AttributeInstance corruption, Player player, AttributeInstance luck, BeyonderHolder holder, int sequence) {
        //CORRUPTION AND LUCK MANAGERS
        if (corruption.getValue() >= 1 && player.tickCount % 200 == 0) {
            corruption.setBaseValue(corruption.getValue() - 1);
        }
        Random random = new Random();
        double lotmLuckValue = luck.getValue();
        if (player.tickCount % 400 == 0 && random.nextInt(300) >= lotmLuckValue * -1) {
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
            luck.setBaseValue(Math.min(0, lotmLuckValue + 40));
        }
        if (player.tickCount % 250 == 0 && random.nextInt(100) >= lotmLuckValue * -1) {
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), serverLevel);
            lightningEntity.setSpeed(6.0f);
            lightningEntity.setNoUp(true);
            lightningEntity.setTargetPos(player.getOnPos().getCenter());
            lightningEntity.teleportTo(player.getX() + (Math.random() * 60) - 30, player.getY() + 100, player.getZ() + (Math.random() * 60) - 30);
            player.level().addFreshEntity(lightningEntity);
            luck.setBaseValue(Math.min(0, lotmLuckValue + 15));
        }
        if (player.tickCount % 150 == 0 && random.nextInt(100) >= lotmLuckValue * -2) {
            player.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 10, 0, false, false));
            player.sendSystemMessage(Component.literal("How unlucky, you tripped!").withStyle(ChatFormatting.BOLD));
        }


        if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
            if (sequence == 7) {
                if (player.tickCount % 400 == 0) {
                    luck.setBaseValue(luck.getValue() + 1);
                }
            }
            if (sequence == 6) {
                if (player.tickCount % 360 == 0) {
                    luck.setBaseValue(luck.getValue() + 1);
                }
            }
            if (sequence == 5) {
                if (player.tickCount % 310 == 0) {
                    luck.setBaseValue(luck.getValue() + 1);
                }
            }
            if (sequence == 4) {
                if (player.tickCount % 220 == 0) {
                    luck.setBaseValue(luck.getValue() + 1);
                }
            }
            if (sequence == 3) {
                if (player.tickCount % 140 == 0) {
                    luck.setBaseValue(luck.getValue() + 1);
                }
            }
            if (sequence == 2) {
                if (player.tickCount % 100 == 0) {
                    luck.setBaseValue(luck.getValue() + 1);
                }
            }
            if (sequence == 1) {
                if (player.tickCount % 70 == 0) {
                    luck.setBaseValue(luck.getValue() + 1);
                }
            }
            if (sequence == 0) {
                if (player.tickCount % 40 == 0) {
                    luck.setBaseValue(luck.getValue() + 1);
                }
            }
        }
    }

    private static void nightmare(Player player, CompoundTag playerPersistentData) {
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

    private static void calamityIncarnationTornado(CompoundTag playerPersistentData, Player player) {
        //CALAMITY INCARNATION TORNADO
        if (playerPersistentData.getInt("calamityIncarnationTornado") >= 1) {
            playerPersistentData.putInt("calamityIncarnationTornado", player.getPersistentData().getInt("calamityIncarnationTornado") - 1);
        }
    }

    private static void psychologicalInvisibility(Player player, CompoundTag playerPersistentData, BeyonderHolder holder) {
        //PSYCHOLOGICAL INVISIBILITY

        AttributeInstance armorInvisAttribute = player.getAttribute(ModAttributes.ARMORINVISIBLITY.get());
        if (armorInvisAttribute.getValue() > 0 && !player.hasEffect(MobEffects.INVISIBILITY)) {
            removeArmor(player);
            armorInvisAttribute.setBaseValue(0);

        }
        if (playerPersistentData.getBoolean("armorStored")) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 1, false, false));
            holder.useSpirituality((int) holder.getMaxSpirituality() / 100);
        }
    }

    private static void windManipulationSense(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //WIND MANIPULATION SENSE
        boolean windManipulationSense = playerPersistentData.getBoolean("windManipulationSense");
        if (!windManipulationSense) {
            return;
        }
        if (!holder.useSpirituality(2)) return;
        double radius = 100 - (holder.getCurrentSequence() * 10);
        for (Player otherPlayer : player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(radius))) {
            if (otherPlayer == player) {
                continue;
            }
            Vec3 directionToPlayer = otherPlayer.position().subtract(player.position()).normalize();
            Vec3 lookAngle = player.getLookAngle();
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

            String message = otherPlayer.getName().getString() + " is " + horizontalDirection + " and " + verticalDirection + " you.";
            if (player.tickCount % 200 == 0) {
                player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
            }
        }
    }

    private static void sailorLightningTravel(Player player) {
        //SAILOR LIGHTNING TRAVEL
        if (player.getPersistentData().getInt("sailorLightningTravel") >= 1) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 3, 1, false, false));
            player.getPersistentData().putInt("sailorLightningTravel", player.getPersistentData().getInt("sailorLightningTravel") - 1);
        }
    }

    private static void windManipulationCushion(CompoundTag playerPersistentData, Player player) {
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

    private static void windManipulationGuide(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //WIND MANIPULATION GLIDE
        int regularFlight = playerPersistentData.getInt("sailorFlight");
        boolean enhancedFlight = playerPersistentData.getBoolean("sailorFlight1");
        if (
                holder.currentClassMatches(BeyonderClassInit.SAILOR) &&
                        holder.getCurrentSequence() <= 7 &&
                        player.isShiftKeyDown() &&
                        player.getDeltaMovement().y() < 0 &&
                        !player.getAbilities().instabuild &&
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

    private static void dreamIntoReality(Player player, BeyonderHolder holder) {
        //DREAM INTO REALITY
        boolean canFly = player.getPersistentData().getBoolean("CanFly");
        if (!canFly) {
            return;
        }
        if (holder.getSpirituality() >= 15) {
            holder.useSpirituality(15);
        }
        if (holder.getSpirituality() <= 15) {
            DreamIntoReality.stopFlying(player);
        }
        if (holder.getCurrentSequence() == 2) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4, false, false));
        }
        if (holder.getCurrentSequence() == 1) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4, false, false));
        }
        if (holder.getCurrentSequence() == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 4, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 5, false, false));
        }
    }

    private static void consciousnessStroll(CompoundTag playerPersistentData, Player player) {
        //CONSCIOUSNESS STROLL
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        int strollCounter = playerPersistentData.getInt("consciousnessStrollActivated");
        int consciousnessStrollActivatedX = playerPersistentData.getInt("consciousnessStrollActivatedX");
        int consciousnessStrollActivatedY = playerPersistentData.getInt("consciousnessStrollActivatedY");
        int consciousnessStrollActivatedZ = playerPersistentData.getInt("consciousnessStrollActivatedZ");
        if (strollCounter >= 1) {
            playerPersistentData.putInt("consciousnessStrollActivated", strollCounter - 1);
            serverPlayer.setGameMode(GameType.SPECTATOR);
        }
        if (strollCounter == 1) {
            player.teleportTo(consciousnessStrollActivatedX, consciousnessStrollActivatedY, consciousnessStrollActivatedZ);
            serverPlayer.setGameMode(GameType.SURVIVAL);
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
            livingEntity.teleportTo(x,y,z);
        }
    }

    private static void projectileEvent(Player player, BeyonderHolder holder) {
        //PROJECTILE EVENT
        Projectile projectile = BeyonderUtil.getProjectiles(player);
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
                        if (!holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() == 0) {
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
            if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 7) {
                projectileEvent.addMovement(projectile, (target.getX() - projectile.getX()) * 0.1, (target.getY() - projectile.getY()) * 0.1, (target.getZ() - projectile.getZ()) * 0.1);
                projectile.hurtMarked = true;
            }
        }
    }

    private static void envisionBarrier(BeyonderHolder holder, Player player, Style style) {
        //ENVISION BARRIER
        if (holder.getCurrentSequence() != 0) {
            return;
        }
        int barrierRadius = player.getPersistentData().getInt("BarrierRadius");
        if (player.isShiftKeyDown() && player.getMainHandItem().getItem() instanceof EnvisionBarrier) {
            barrierRadius++;
            player.displayClientMessage(Component.literal("Barrier Radius " + barrierRadius).withStyle(style), true);
        }
        if (barrierRadius > 100) {
            barrierRadius = 0;
        }
        player.getPersistentData().putInt("BarrierRadius", barrierRadius);
    }

    private static void envisionLife(Player player) {
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

    private static void manipulateMovement(Player player, Level level) {
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

    private static void envisionKingdom(CompoundTag playerPersistentData, Player player, BeyonderHolder holder, ServerLevel serverLevel) {
        //ENVISION KINGDOM

        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        int mindScape = playerPersistentData.getInt("inMindscape");
        if (mindScape < 1) return;
        Abilities playerAbilities = player.getAbilities();
        playerPersistentData.putInt("inMindscape", mindScape + 1);
        if (mindScape >= 1200) {
            playerPersistentData.putInt("inMindscape", 0);
        }
        int mindscapeAbilities = playerPersistentData.getInt("mindscapeAbilities");
        if (mindscapeAbilities >= 1) {
            holder.setSpirituality(holder.getMaxSpirituality());
            if (!playerPersistentData.getBoolean("CAN_FLY")) {
                dreamIntoReality.setBaseValue(3);
                playerAbilities.setFlyingSpeed(0.15F);
                playerAbilities.mayfly = true;
                player.onUpdateAbilities();
                playerPersistentData.putInt("mindscapeAbilities", mindscapeAbilities - 1);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                }
            }
        }
        if (mindscapeAbilities == 1 && !playerPersistentData.getBoolean("CAN_FLY")) {
            dreamIntoReality.setBaseValue(1);
            playerAbilities.setFlyingSpeed(0.05F);
            playerAbilities.mayfly = false;
            player.onUpdateAbilities();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
            }
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
        StructureTemplate part = serverLevel.getStructureManager().getOrCreate(new ResourceLocation(LOTM.MOD_ID, "corpse_cathedral" + (partIndex + 1)));
        BlockPos tagPos = new BlockPos(x, y + (partIndex * 2), z);
        StructurePlaceSettings settings = BeyonderUtil.getStructurePlaceSettings(new BlockPos(x, y, z));
        part.placeInWorld(serverLevel, tagPos, tagPos, settings, null, Block.UPDATE_ALL);
        playerPersistentData.putInt("inMindscape", mindScape + 1);
    }

    private static void acidicRain(Player player, int sequence) {
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

    private static void calamityIncarnationTsunami(CompoundTag playerPersistentData, Player player, ServerLevel level) {
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

    private static void earthquake(Player player, int sequence) {
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

    private static void extremeColdness(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
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

    private static void hurricane(CompoundTag playerPersistentData, Player player) {
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
                SailorLightning.shootLineBlockHigh(player, player.level());
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

    private static void lightningStorm(Player player, CompoundTag playerPersistentData, Style style, BeyonderHolder holder) {
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
            if (stormVec > 300) {
                playerPersistentData.putInt("sailorStormVec", 0);
                stormVec = 0;
            }
        }
    }

    private static void matterAccelerationSelf(Player player, BeyonderHolder holder, Style style) {
        //MATTER ACCELERATION SELF
        if (player.isSpectator()) return;
        int matterAccelerationDistance = player.getPersistentData().getInt("tyrantSelfAcceleration");
        int blinkDistance = player.getPersistentData().getInt("BlinkDistance");
        if (player.isShiftKeyDown() && player.getMainHandItem().getItem() instanceof MatterAccelerationSelf && holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
            matterAccelerationDistance += 50;
            player.getPersistentData().putInt("tyrantSelfAcceleration", matterAccelerationDistance);
            player.displayClientMessage(Component.literal("Matter Acceleration Distance is " + matterAccelerationDistance).withStyle(style), true);
        }
        if (player.isShiftKeyDown() && player.getMainHandItem().getItem() instanceof EnvisionLocationBlink && holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
            blinkDistance += 5;
            player.getPersistentData().putInt("BlinkDistance", blinkDistance);
            player.displayClientMessage(Component.literal("Blink Distance is " + blinkDistance).withStyle(style), true);
        }
        if (matterAccelerationDistance >= 1000) {
            player.getPersistentData().putInt("tyrantSelfAcceleration", 0);
        }
        if (blinkDistance > 200) {
            player.getPersistentData().putInt("BlinkDistance", 0);
        }
    }

    private static void ragingBlows(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //RAGING BLOWS
        boolean sailorLightning = playerPersistentData.getBoolean("SailorLightning");
        int ragingBlows = playerPersistentData.getInt("ragingBlows");
        int ragingBlowsRadius = (25 - (holder.getCurrentSequence() * 3));
        int damage = 20 - holder.getCurrentSequence() * 2;
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
                    if (holder.getCurrentSequence() <= 7) {
                        double chanceOfDamage = (100.0 - (holder.getCurrentSequence() * 12.5));
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

    private static void rainEyes(Player player) {
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

    private static void sirenSongs(CompoundTag playerPersistentData, BeyonderHolder holder, Player player, int sequence) {
        //SIREN SONGS
        int sirenSongHarm = playerPersistentData.getInt("sirenSongHarm");
        int sirenSongWeaken = playerPersistentData.getInt("sirenSongWeaken");
        int sirenSongStun = playerPersistentData.getInt("sirenSongStun");
        int sirenSongStrengthen = playerPersistentData.getInt("sirenSongStrengthen");
        if (!holder.currentClassMatches(BeyonderClassInit.SAILOR) || holder.getCurrentSequence() > 5) {
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

    private static void starOfLightning(Player player, CompoundTag playerPersistentData) {
        //STAR OF LIGHTNING
        AttributeInstance attributeInstance4 = player.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
        int sailorLightningStar = playerPersistentData.getInt("sailorLightningStar");
        if (sailorLightningStar >= 2) {
            attributeInstance4.setBaseValue(1.0f);
            playerPersistentData.putInt("sailorLightningStar", sailorLightningStar - 1);
        }
        if (sailorLightningStar == 1) {
            playerPersistentData.putInt("sailorLightningStar", 0);
            attributeInstance4.setBaseValue(0);
            for (int i = 0; i < 500; i++) {
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
                lightningEntity.setSpeed(50);
                double sailorStarX = (Math.random() * 2 - 1);
                double sailorStarY = (Math.random() * 2 - 1); // You might want different random values for y and z
                double sailorStarZ = (Math.random() * 2 - 1);
                lightningEntity.setDeltaMovement(sailorStarX, sailorStarY, sailorStarZ);
                lightningEntity.setMaxLength(10);
                lightningEntity.setOwner(player);
                lightningEntity.teleportTo(player.getX(), player.getY(), player.getZ());
                player.level().addFreshEntity(lightningEntity);
            }
        }
    }

    private static void tsunami(CompoundTag playerPersistentData, Player player) {
        //TSUNAMI
        int tsunami = playerPersistentData.getInt("sailorTsunami");
        if (tsunami >= 1) {
            playerPersistentData.putInt("sailorTsunami", tsunami - 5);
            Tsunami.summonTsunami(player);
        } else {
            playerPersistentData.remove("sailorTsunamiDirection");
            playerPersistentData.remove("sailorTsunamiX");
            playerPersistentData.remove("sailorTsunamiY");
            playerPersistentData.remove("sailorTsunamiZ");
        }

        //TSUNAMI SEAL
        int tsunamiSeal = playerPersistentData.getInt("sailorTsunami");
        if (tsunamiSeal >= 1) {
            playerPersistentData.putInt("sailorTsunami", tsunamiSeal - 5);
            TsunamiSeal.summonTsunami(player);
        } else {
            playerPersistentData.remove("sailorTsunamiDirection");
            playerPersistentData.remove("sailorTsunamiX");
            playerPersistentData.remove("sailorTsunamiY");
            playerPersistentData.remove("sailorTsunamiZ");
        }
    }

    private static void waterSphereCheck(Player player, ServerLevel level) {
        //WATER SPHERE CHECK
        if (player.getPersistentData().getInt("sailorSphere") >= 5) {
            for (Entity entity : player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(4))) {
                if (!(entity instanceof LivingEntity) && !(entity instanceof MeteorEntity) && !(entity instanceof MeteorNoLevelEntity)) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
            BlockPos playerPos = player.blockPosition();
            double radius = 3.0;
            double minRemovalRadius = 4.0;
            double maxRemovalRadius = 7.0;

            // Create a sphere of water around the player
            for (int sphereX = (int) -radius; sphereX <= radius; sphereX++) {
                for (int sphereY = (int) -radius; sphereY <= radius; sphereY++) {
                    for (int sphereZ = (int) -radius; sphereZ <= radius; sphereZ++) {
                        double sphereDistance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                        if (!(sphereDistance <= radius)) {
                            continue;
                        }
                        BlockPos blockPos = playerPos.offset(sphereX, sphereY, sphereZ);
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
                        BlockPos blockPos = playerPos.offset(sphereX, sphereY, sphereZ);
                        if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        if (player.getPersistentData().getInt("sailorSphere") >= 1 && player.getPersistentData().getInt("sailorSphere") <= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 100, false, false));
            for (int sphereX = -6; sphereX <= 6; sphereX++) {
                for (int sphereY = -6; sphereY <= 6; sphereY++) {
                    for (int sphereZ = -6; sphereZ <= 6; sphereZ++) {
                        double sphereDistance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                        if (!(sphereDistance <= 6)) {
                            continue;
                        }
                        BlockPos blockPos = player.getOnPos().offset(sphereX, sphereY, sphereZ);
                        if (player.level().getBlockState(blockPos).getBlock() == Blocks.WATER) {
                            player.level().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        if (player.getPersistentData().getInt("sailorSphere") >= 1) {
            player.getPersistentData().putInt("sailorSphere", player.getPersistentData().getInt("sailorSphere") - 1);
        }
    }

    private static void windManipulationFlight(Player player, CompoundTag playerPersistentData) {
        //WIND MANIPULATION FLIGHT
        Vec3 lookVector = player.getLookAngle();
        if (!playerPersistentData.getBoolean("sailorFlight1")) {
            return;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!holder.useSpirituality(2)) {
            return;
        }
        int flight = playerPersistentData.getInt("sailorFlight");
        int flightCancel = playerPersistentData.getInt("sailorFlightDamageCancel");
        if (flightCancel >= 1) {
            playerPersistentData.putInt("sailorFlightDamageCancel", flightCancel + 1);
        }
        if (flightCancel >= 300) {
            playerPersistentData.putInt("sailorFlightDamageCancel", 0);
        }
        if (flight >= 1) {
            playerPersistentData.putInt("sailorFlight", flight + 1);
        }
        if (flight <= 60 && flight % 20 == 0) {
            player.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
            player.hurtMarked = true;
        }
        if (flight > 60) {
            playerPersistentData.putInt("sailorFlight", 0);
        }
    }

    @SubscribeEvent
    public static void stunEffect(LivingEntityUseItemEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            if (entity.hasEffect(ModEffects.STUN.get())) {
                event.setCanceled(true);
            }
            if (entity instanceof Player player) {
                AttributeInstance luck = player.getAttribute(ModAttributes.LOTM_LUCK.get());
                Random random = new Random();
                if (random.nextInt(100) >= luck.getValue() * -1) {
                    event.setCanceled(true);
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                    luck.setBaseValue(luck.getBaseValue() + holder.getCurrentSequence());
                    player.sendSystemMessage(Component.literal("How unlucky! You made a mistake using " + player.getMainHandItem().getDisplayName() + " and it didn't work").withStyle(BeyonderUtil.getStyle(player)));
                }
            }
        }
    }

    @SubscribeEvent
    public static void handleLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        Level level = entity.level();
        if (!entity.level().isClientSide) {

            dreamWeaving(entity);

            prophesizeTeleportation(tag, entity);

            matterAccelerationEntities(entity);

            mentalPlague(entity);




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
                        if (nonDemise >= 20 && nonDemise <= 180 && nonDemise % 20 == 0) {
                            int standStillSecondsLeft = (200 - nonDemise) / 20;
                            entity.sendSystemMessage(Component.literal("You need to stand still for " + standStillSecondsLeft + " more seconds").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
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
                    entity.setDeltaMovement(entity.getDeltaMovement().x, Math.min(0,entity.getDeltaMovement().y - 0.5), entity.getDeltaMovement().z);
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
                if (entity instanceof Player player) {
                    player.setTicksFrozen(3);
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
                entity.teleportTo(sealX, sealY + 1000, sealZ);
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
                    entity.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 40, 3, false, false));
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
                entity.teleportTo(x, y + 4000, z);
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
                    if (entity instanceof Player player) {
                        int sealSeconds = (int) stormSeal / 20;
                        player.displayClientMessage(Component.literal("You are stuck in the storm seal for " + sealSeconds + " seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                    }
                }
            }
            if (tag.getInt("inStormSeal") == 2 || tag.getInt("inStormSeal") == 1) {
                int x = tag.getInt("stormSealX");
                int y = tag.getInt("stormSealY");
                int z = tag.getInt("stormSealZ");
                tag.putInt("inStormSeal", tag.getInt("inStormSeal") - 1);
                entity.teleportTo(x, y, z);
            }
        }
    }

    private static void dreamWeaving(LivingEntity entity) {
        //DREAM WEAVING
        AttributeInstance maxHp = entity.getAttribute(Attributes.MAX_HEALTH);
        if (entity instanceof Player || maxHp.getBaseValue() != 551) {
            return;
        }
        int deathTimer = entity.getPersistentData().getInt("DeathTimer");
        entity.getPersistentData().putInt("DeathTimer", deathTimer + 1);
        if (deathTimer >= 300) {
            entity.remove(Entity.RemovalReason.KILLED);
        }
    }

    private static void matterAccelerationEntities(LivingEntity entity) {
        //MATTER ACCELERATION: ENTITIES
        int matterAccelerationEntities = entity.getPersistentData().getInt("matterAccelerationEntities");
        if (matterAccelerationEntities < 1) {
            return;
        }
        entity.getPersistentData().putInt("matterAccelerationEntities", matterAccelerationEntities - 1);
        double movementX = Math.abs(entity.getDeltaMovement().x());
        double movementY = Math.abs(entity.getDeltaMovement().y());
        double movementZ = Math.abs(entity.getDeltaMovement().z());
        if (movementX < 6 && movementY < 6 && movementZ < 6) {
            return;
        }
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
            if (entity1 == entity) {
                continue;
            }
            if (entity1 instanceof Player player) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (!holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() == 0) {
                    player.hurt(player.damageSources().lightningBolt(), 10);
                }
            } else {
                entity1.hurt(entity1.damageSources().lightningBolt(), 10);
            }
        }
    }

    private static void mentalPlague(LivingEntity entity) {
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


    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        CompoundTag tag = player.getPersistentData();
        boolean sailorLightning = tag.getBoolean("SailorLightning");
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (player.level().isClientSide()) return;

        if (player.getMainHandItem().getItem() instanceof BeyonderAbilityUser) {
            event.setCanceled(true);
        }

        //SAILOR PASSIVE
        if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 7 && event.getTarget() instanceof LivingEntity livingTarget && sailorLightning && livingTarget != player) {
            double chanceOfDamage = (100.0 - (holder.getCurrentSequence() * 12.5)); // Decrease chance by 12.5% for each level below 9
            if (Math.random() * 100 < chanceOfDamage) {
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, livingTarget.level());
                lightningBolt.moveTo(livingTarget.getX(), livingTarget.getY(), livingTarget.getZ());
                livingTarget.level().addFreshEntity(lightningBolt);
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

    private static void applyEffectsAndDamage(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 2, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 2, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 1, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, 1, false, false));
        entity.hurt(entity.damageSources().magic(), 20);
    }

    private static void removeArmor(Player player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armorStack = player.getItemBySlot(slot);
                if (!armorStack.isEmpty()) {
                    player.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void hurtEvent(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();

        //SAILOR FLIGHT
        if (entity instanceof Player player) {
            int flightCancel = tag.getInt("sailorFlightDamageCancel");
            if (!player.level().isClientSide()) {

                //SAILOR FLIGHT
                if (flightCancel != 0 && event.getSource() == player.damageSources().fall()) {
                    event.setCanceled(true);
                    tag.putInt("sailorFlightDamageCancel", 0);
                }
            }

            //MONSTER LUCK
            AttributeInstance luck = player.getAttribute(ModAttributes.LOTM_LUCK.get());
            double luckValue = luck.getValue();
            float damage = event.getAmount();
            if (luckValue >= 1) {
                if (Math.random() * luckValue > 50) {
                    event.setCanceled(true);
                    luck.setBaseValue((Math.min(0, luckValue - damage)));
                }
            }
            if (luckValue < 0) {
                if (Math.random() * luckValue < 50) {
                    event.setAmount(damage * 2);
                }
            }
        }


        //STORM SEAL
        if (entity.getPersistentData().getInt("inStormSeal") >= 1) {
            event.setCanceled(true);
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

            if (entity instanceof Player player) {


                //REMOVE ALL TAGS
                tag.getAllKeys().removeIf(key -> key.startsWith("lotm:"));


                //RESET PARTICLE ATTRIBUTES
                AttributeInstance particleAttributeInstance = player.getAttribute(ModAttributes.PARTICLE_HELPER.get());
                AttributeInstance particleAttributeInstance1 = player.getAttribute(ModAttributes.PARTICLE_HELPER1.get());
                AttributeInstance particleAttributeInstance2 = player.getAttribute(ModAttributes.PARTICLE_HELPER2.get());
                AttributeInstance particleAttributeInstance3 = player.getAttribute(ModAttributes.PARTICLE_HELPER3.get());
                AttributeInstance particleAttributeInstance4 = player.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
                AttributeInstance particleAttributeInstance5 = player.getAttribute(ModAttributes.PARTICLE_HELPER5.get());
                AttributeInstance particleAttributeInstance6 = player.getAttribute(ModAttributes.PARTICLE_HELPER6.get());
                AttributeInstance particleAttributeInstance7 = player.getAttribute(ModAttributes.PARTICLE_HELPER7.get());
                AttributeInstance particleAttributeInstance8 = player.getAttribute(ModAttributes.PARTICLE_HELPER8.get());
                AttributeInstance particleAttributeInstance9 = player.getAttribute(ModAttributes.PARTICLE_HELPER9.get());
                particleAttributeInstance.setBaseValue(0.0f);
                particleAttributeInstance1.setBaseValue(0.0f);
                particleAttributeInstance2.setBaseValue(0.0f);
                particleAttributeInstance3.setBaseValue(0.0f);
                particleAttributeInstance4.setBaseValue(0.0f);
                particleAttributeInstance5.setBaseValue(0.0f);
                particleAttributeInstance6.setBaseValue(0.0f);
                particleAttributeInstance7.setBaseValue(0.0f);
                particleAttributeInstance8.setBaseValue(0.0f);
                particleAttributeInstance9.setBaseValue(0.0f);

            }
            if (entity instanceof Player && entity.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                DamageSource source = event.getSource();
                Entity trueSource = source.getEntity();
                if (trueSource instanceof Player player) {
                    ItemStack stack = player.getUseItem();
                    int looting = stack.getEnchantmentLevel(Enchantments.MOB_LOOTING);
                    ItemStack drop = getDrop(entity, source, looting);
                    if (!drop.isEmpty()) {
                        player.drop(drop, true);
                    }
                }
            }
        }
    }

    private static ItemStack getDrop(LivingEntity entity, DamageSource source, int looting) {
        if (entity.level().isClientSide() || entity.getHealth() > 0)
            return ItemStack.EMPTY;
        if (entity.isBaby())
            return ItemStack.EMPTY;
        double baseChance = entity instanceof PlayerMobEntity ? Configs.COMMON.mobHeadDropChance.get() : Configs.COMMON.playerHeadDropChance.get();
        if (baseChance <= 0)
            return ItemStack.EMPTY;

        if (poweredCreeper(source) || randomDrop(entity.level().getRandom(), baseChance, looting)) {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            GameProfile profile = entity instanceof PlayerMobEntity ?
                    ((PlayerMobEntity) entity).getProfile() :
                    ((Player) entity).getGameProfile();
            if (entity instanceof PlayerMobEntity playerMob) {
                String skinName = playerMob.getUsername().getSkinName();
                String displayName = playerMob.getUsername().getDisplayName();
                if (playerMob.getCustomName() != null) {
                    displayName = playerMob.getCustomName().getString();
                }

                if (!skinName.equals(displayName)) {
                    stack.setHoverName(Component.translatable("block.minecraft.player_head.named", displayName));
                }
            }
            if (profile != null)
                stack.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), profile));
            return stack;
        }
        return ItemStack.EMPTY;
    }

    private static boolean poweredCreeper(DamageSource source) {
        return source.is(DamageTypeTags.IS_EXPLOSION) && source.getEntity() instanceof Creeper creeper && creeper.isPowered();
    }

    private static boolean randomDrop(RandomSource rand, double baseChance, int looting) {
        return rand.nextDouble() <= Math.max(0, baseChance * Math.max(looting + 1, 1));
    }

    @Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
    public static class SpawnHandler {

        @SubscribeEvent
        public static void onCheckSpawn(MobSpawnEvent.FinalizeSpawn event) {
            if (event.getEntity() instanceof PlayerMobEntity) {
                ResourceKey<Level> worldKey = event.getLevel().getLevel().dimension();
                if (Configs.COMMON.isDimensionBlocked(worldKey)) {
                    event.setSpawnCancelled(true);
                }
            }
        }
    }
}