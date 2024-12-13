package net.swimmingtuna.lotm.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.client.Configs;
import net.swimmingtuna.lotm.commands.AbilityRegisterCommand;
import net.swimmingtuna.lotm.entity.*;
import net.swimmingtuna.lotm.events.custom_events.ModEventFactory;
import net.swimmingtuna.lotm.events.custom_events.ProjectileEvent;
import net.swimmingtuna.lotm.init.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.BeyonderAbilityUser;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.BattleHypnotism;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.DreamIntoReality;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionBarrier;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionLocationBlink;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.SendParticleS2C;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ClientSequenceData;
import net.swimmingtuna.lotm.util.CorruptionAndLuckHandler;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.util.effect.NoRegenerationEffect;
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import net.swimmingtuna.lotm.world.worldgen.MirrorWorldChunkGenerator;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.lang.reflect.Method;
import java.util.*;

import static net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.ProbabilityManipulationWorldFortune.probabilityManipulationWorld;
import static net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.CalamityIncarnationTsunami.calamityIncarnationTsunamiTick;
import static net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Earthquake.isOnSurface;
import static net.swimmingtuna.lotm.world.worldgen.dimension.DimensionInit.SPIRIT_WORLD_LEVEL_KEY;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onUseItemEvent(LivingEntityUseItemEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level().isClientSide()) {
            MisfortuneManipulation.livingUseAbilityMisfortuneManipulation(event);
            CompoundTag tag = livingEntity.getPersistentData();
            if (tag.getInt("cantUseAbility") >= 1 && livingEntity.getMainHandItem().getItem() instanceof SimpleAbilityItem) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel().dimensionType().equals(SPIRIT_WORLD_LEVEL_KEY)) {
            if (event.getLevel() instanceof ServerLevel spiritWorld) {
                ServerLevel overworld = spiritWorld.getServer().getLevel(Level.OVERWORLD);
                if (overworld != null && spiritWorld.getChunkSource().getGenerator() instanceof MirrorWorldChunkGenerator) {
                    ChunkGenerator newGenerator = new MirrorWorldChunkGenerator(
                            spiritWorld.getChunkSource().getGenerator().getBiomeSource(),
                            overworld.dimension()
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        BeyonderAbilityUser.resetClicks(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        BeyonderUtil.leftClickEmpty(event.getEntity());
    }

    public static void mobEffectEvent(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel serverLevel) {
            CalamityEnhancementData data = CalamityEnhancementData.getInstance(serverLevel);
            int chaosLevel = data.getCalamityEnhancement();
            if (chaosLevel != 1) {
                MobEffectInstance mobEffectInstance = event.getEffectInstance();
                entity.addEffect(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier() * chaosLevel, mobEffectInstance.isAmbient(), mobEffectInstance.isVisible()));
                event.setCanceled(true);
            }
            if (event.getEffectInstance().getEffect() == ModEffects.NOREGENERATION.get()) {
                entity.getPersistentData().putInt("noRegenerationEffectHealth", (int) entity.getHealth());
            }
        }

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
    public static void onPlayerTickClient(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Style style = BeyonderUtil.getStyle(player);
        CompoundTag playerPersistentData = player.getPersistentData();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int sequence = holder.getCurrentSequence();

    }

    @SubscribeEvent
    public static void onPlayerTickServer(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Style style = BeyonderUtil.getStyle(player);
        CompoundTag playerPersistentData = player.getPersistentData();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        int sequence = holder.getCurrentSequence();
        if (player.level().isClientSide() || event.phase != TickEvent.Phase.START) {
            return;
        }
        if (!player.level().isClientSide() && holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 8 && player.tickCount % 5 == 0) {
            checkForProjectiles(player);
        }


        if (player.tickCount % 20 == 0 && player instanceof ServerPlayer serverPlayer) {
            AbilityRegisterCommand.tickEvent(serverPlayer);
            if (holder.getCurrentSequence() != 0 && ClientSequenceData.getCurrentSequence() == 0) {
                ClientSequenceData.setCurrentSequence(-1);
            }
        }


        Map<String, Long> times = new HashMap<>();
        {
            FateReincarnation.monsterReincarnationChecker(player);
        }
        {
            decrementMonsterAttackEvent(player);
        }
        {
            onChaosWalkerCombat(player);
        }
        {
            monsterLuckIgnoreMobs(player);
        }
        {
            monsterLuckPoisonAttacker(player);
        }
        {
            calamityExplosion(player);
        }
        {
            long startTime = System.nanoTime();
            calamityLightningStorm(player);
            long endTime = System.nanoTime();
            times.put("calamityLightningStorm", endTime - startTime);
        }
        {
            long startTime = System.nanoTime();
            calamityUndeadArmy(player);
            long endTime = System.nanoTime();
            times.put("calamityUndeadArmy", endTime - startTime);
        }
        {
            long startTime = System.nanoTime();
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
        monsterDomainIntHandler(player);
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
            calamityIncarnationTsunamiTick(playerPersistentData, player, serverLevel);
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
        {
            sirenSongs(player);
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
            if (player.tickCount % 10 == 0) {
                holder.useSpirituality((int) holder.getMaxSpirituality() / 100);
            }
        }
    }

    private static void monsterDangerSense(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //WIND MANIPULATION SENSE
        boolean monsterDangerSense = playerPersistentData.getBoolean("monsterDangerSense");
        if (!monsterDangerSense) {
            return;
        }
        if (!holder.useSpirituality(2)) return;
        double radius = 150 - (holder.getCurrentSequence() * 15);
        for (Player otherPlayer : player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(radius))) {
            if (otherPlayer == player) {
                continue;
            }
            if (otherPlayer.getMainHandItem().getItem() instanceof SimpleAbilityItem || otherPlayer.getMainHandItem().getItem() instanceof ProjectileWeaponItem || otherPlayer.getMainHandItem().getItem() instanceof SwordItem || otherPlayer.getMainHandItem().getItem() instanceof AxeItem) { //also add for sealed artifacts
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
                    player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE));
                }
            }
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
            WindManipulationCushion.summonWindCushionParticles(player);
            playerPersistentData.putInt("windManipulationCushion", cushion - 1);
            player.resetFallDistance();
        }
        if (cushion >= 20 && player.getDeltaMovement().y <= 0) {
            player.setDeltaMovement(player.getDeltaMovement().x(), player.getDeltaMovement().y() * 0.7, player.getDeltaMovement().z());
            player.hurtMarked = true;
        }
        if (cushion == 1) {
            player.setDeltaMovement(player.getLookAngle().scale(2.0f));
            player.hurtMarked = true;
            player.resetFallDistance();
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
            if (player.tickCount % 2 == 0) {
                holder.useSpirituality(20);
            }
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
            livingEntity.teleportTo(x, y, z);
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
                            playerEntity.hurt(playerEntity.damageSources().lightningBolt(), 40);
                        }
                    } else {
                        entity1.hurt(entity1.damageSources().lightningBolt(), 40);
                    }
                }
            }
        }


        //SAILOR PASSIVE CHECK FROM HERE
        LivingEntity target = projectileEvent.getTarget(75, 0);
        if (target != null) {
            if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 7 && player.getPersistentData().getBoolean("sailorProjectileMovement")) {
                projectileEvent.addMovement(projectile, (target.getX() - projectile.getX()) * 0.1, (target.getY() - projectile.getY()) * 0.1, (target.getZ() - projectile.getZ()) * 0.1);
                projectile.hurtMarked = true;
            }
        }

        //MONSTER CALCULATION PASSIVE
        if (target != null) {
            if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 8 && player.getPersistentData().getBoolean("monsterProjectileControl")) {
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
            player.displayClientMessage(Component.literal("Barrier Radius: " + barrierRadius).withStyle(style), true);
        }
        if (barrierRadius > 101) {
            barrierRadius = 0;
            player.displayClientMessage(Component.literal("Barrier Radius: 0").withStyle(style), true);
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
                playerAbilities.setFlyingSpeed(0.1F);
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
        if (mindScape1 == 11) {
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(250))) {
                if (entity != player) {
                    if (entity instanceof Player) {
                        entity.teleportTo(player.getX(), player.getY() + 1, player.getZ() - 10);
                    } else if (entity.getMaxHealth() >= 50) {
                        entity.teleportTo(player.getX(), player.getY() + 1, player.getZ() - 10);
                    }
                }
            }
        }
        if (mindScape == 2 || mindScape == 4 || mindScape == 6 || mindScape == 8 || mindScape == 10) {
            player.teleportTo(player.getX(), player.getY() + 4.5, player.getZ());
        }
        StructureTemplate part = serverLevel.getStructureManager().getOrCreate(new ResourceLocation(LOTM.MOD_ID, "corpse_cathedral_" + (partIndex + 1)));
        BlockPos tagPos = new BlockPos(x, y + (partIndex * 2), z);
        StructurePlaceSettings settings = BeyonderUtil.getStructurePlaceSettings(new BlockPos(x, y, z));
        part.placeInWorld(serverLevel, tagPos, tagPos, settings, null, Block.UPDATE_ALL);
        playerPersistentData.putInt("inMindscape", mindScape + 1);
    }

    private static void acidicRain(Player player, int sequence) {
        //ACIDIC RAIN
        int acidicRain = player.getPersistentData().getInt("sailorAcidicRain");
        if (acidicRain <= 0) {
            return;
        }
        player.getPersistentData().putInt("sailorAcidicRain", acidicRain + 1);
        AcidicRain.spawnAcidicRainParticles(player);
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
        }
    }


    private static void earthquake(Player player, int sequence) {
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

                    if (!player.level().getBlockState(blockPos).isAir() && isOnSurface(player.level(), blockPos)) {
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
            if (sailorEarthquake >= 0) {
                player.getPersistentData().putInt("sailorEarthquake", sailorEarthquake - 1);
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
            if (stormVec > 301) {
                player.displayClientMessage(Component.literal("Sailor Storm Spawn Distance is 0").withStyle(style), true);
                playerPersistentData.putInt("sailorStormVec", 0);
                stormVec = 0;
            }
        }
    }

    private static void luckDenial(LivingEntity livingEntity) {
        CompoundTag tag = livingEntity.getPersistentData();
        double luck = tag.getDouble("luck");
        double misfortune = tag.getDouble("misfortune");
        double luckDenialTimer = tag.getDouble("luckDenialTimer");
        double luckDenialLuck = tag.getDouble("luckDenialLuck");
        double luckDenialMisfortune = tag.getDouble("luckDenialMisfortune");
        if (luckDenialTimer >= 1) {
            tag.putDouble("luckDenialTimer", luckDenialTimer - 1);
            if (luck >= luckDenialLuck) {
                tag.putDouble("luck", luckDenialTimer);
            }
            if (misfortune <= luckDenialMisfortune) {
                tag.putDouble("misfortune", luckDenialMisfortune);
            }
        }
    }

    private static void domainDrops(LivingDropsEvent event) {
        if (event.getEntity().getPersistentData().getInt("inMonsterProvidenceDomain") >= 1) {
            Random random = new Random();
            if (random.nextInt(3) == 1) {
                event.getDrops().add((ItemEntity) event.getEntity().captureDrops());
            }
        }
        if (event.getEntity().getPersistentData().getInt("inMonsterDecayDomain") >= 1) {
            Random random = new Random();
            if (random.nextInt(4) == 1) {
                event.getDrops().remove((ItemEntity) event.getEntity().captureDrops());
            }
        }
    }

    private static void matterAccelerationSelf(Player player, BeyonderHolder holder, Style style) {
        //MATTER ACCELERATION SELF
        if (player.isSpectator()) return;
        int matterAccelerationDistance = player.getPersistentData().getInt("tyrantSelfAcceleration");
        int blinkDistance = player.getPersistentData().getInt("BlinkDistance");
        int luckGiftingAmount = player.getPersistentData().getInt("monsterLuckGifting");
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
        if (matterAccelerationDistance >= 1001) {
            player.displayClientMessage(Component.literal("Matter Acceleration Distance is 0").withStyle(style), true);
            player.getPersistentData().putInt("tyrantSelfAcceleration", 0);
        }
        if (blinkDistance > 201) {
            player.displayClientMessage(Component.literal("Blink Distance is 0").withStyle(style), true);
            player.getPersistentData().putInt("BlinkDistance", 0);
        }
        //LUCK GIFTING
        if (player.isShiftKeyDown() && player.getMainHandItem().getItem() instanceof LuckGifting && holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
            player.getPersistentData().putInt("monsterLuckGifting", luckGiftingAmount + 1);
            player.displayClientMessage(Component.literal("Luck Gifting Amount is " + luckGiftingAmount).withStyle(style), true);
        }
        if (luckGiftingAmount > 101) {
            player.displayClientMessage(Component.literal("Luck Gifting Amount is 0").withStyle(style), true);
            player.getPersistentData().putInt("monsterLuckGifting", 0);
        }
    }

    private static void ragingBlows(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //RAGING BLOWS
        boolean sailorLightning = playerPersistentData.getBoolean("SailorLightning");
        int ragingBlows = playerPersistentData.getInt("ragingBlows");
        int ragingBlowsRadius = (27 - (holder.getCurrentSequence() * 3));
        int damage = 20 - holder.getCurrentSequence() * 2;
        if (ragingBlows >= 1) {
            RagingBlows.spawnRagingBlowsParticles(player);
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

    private static void monsterDomainIntHandler(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            int maxRadius = 250 - (holder.getCurrentSequence() * 45);
            int radius = tag.getInt("monsterDomainRadius");
            if (player.tickCount % 500 == 0) {
                tag.putInt("monsterDomainMaxRadius", maxRadius);
            }
            if (player.isShiftKeyDown() && (player.getMainHandItem().getItem() instanceof DomainOfDecay || player.getMainHandItem().getItem() instanceof DomainOfProvidence)) {
                tag.putInt("monsterDomainRadius", radius + 5);
                player.displayClientMessage(Component.literal("Current Domain Radius is " + radius).withStyle(BeyonderUtil.getStyle(player)), true);
                if (radius > maxRadius + 1) {
                    player.displayClientMessage(Component.literal("Current Domain Radius is 0").withStyle(BeyonderUtil.getStyle(player)), true);
                    tag.putInt("monsterDomainRadius", 0);
                }
            }
        }
    }

    private static void starOfLightning(Player player, CompoundTag playerPersistentData) {
        //STAR OF LIGHTNING
        int sailorLightningStar = playerPersistentData.getInt("sailorLightningStar");
        if (sailorLightningStar >= 2) {
            StarOfLightning.summonLightningParticles(player);
            player.level().playSound(player, player.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 10, 1);
            playerPersistentData.putInt("sailorLightningStar", sailorLightningStar - 1);
        }
        if (sailorLightningStar == 1) {
            playerPersistentData.putInt("sailorLightningStar", 0);
            for (int i = 0; i < 500; i++) {
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
                lightningEntity.setSpeed(50);
                double sailorStarX = (Math.random() * 2 - 1);
                double sailorStarY = (Math.random() * 2 - 1);
                double sailorStarZ = (Math.random() * 2 - 1);
                lightningEntity.setDeltaMovement(sailorStarX, sailorStarY, sailorStarZ);
                lightningEntity.setMaxLength(10);
                lightningEntity.setOwner(player);
                lightningEntity.teleportTo(player.getX(), player.getY(), player.getZ());
                player.level().addFreshEntity(lightningEntity);
            }
        }
    }

    private static void domainDropsExperience(LivingExperienceDropEvent event) {
        //MONSTER PROVIDENCE DOMAIN
        if (!event.getEntity().level().isClientSide()) {
            if (event.getEntity().getPersistentData().getInt("inMonsterProvidenceDomain") >= 1) {
                int droppedExperience = event.getDroppedExperience();
                event.setDroppedExperience((int) (droppedExperience * 1.5));
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
        int tsunamiSeal = playerPersistentData.getInt("sailorTsunamiSeal");
        if (tsunamiSeal >= 1) {
            playerPersistentData.putInt("sailorTsunamiSeal", tsunamiSeal - 5);
            TsunamiSeal.summonTsunami(player);
        } else {
            playerPersistentData.remove("sailorTsunamiSealDirection");
            playerPersistentData.remove("sailorTsunamiSealX");
            playerPersistentData.remove("sailorTsunamiSealY");
            playerPersistentData.remove("sailorTsunamiSealZ");
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

    private static void windManipulationFlight1(Player player, CompoundTag playerPersistentData) {
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

    private static void windManipulationFlight(Player player, CompoundTag tag) {
        Vec3 lookVector = player.getLookAngle();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (tag.getBoolean("sailorFlight1")) {
            if (holder.getSpirituality() >= 3) {
                holder.useSpirituality(3);
            } else {
                WindManipulationFlight.stopFlying(player);
            }
        }
        int flightCancel = tag.getInt("sailorFlightDamageCancel");
        if (flightCancel >= 1) {
            player.fallDistance = 0;
            tag.putInt("sailorFlightDamageCancel", flightCancel + 1);
            if (flightCancel >= 300) {
                tag.putInt("sailorFlightDamageCancel", 0);
            }
        }
        int flight = tag.getInt("sailorFlight");
        if (flight >= 1) {
            tag.putInt("sailorFlight", flight + 1);
            if (flight <= 45 && flight % 15 == 0) {
                player.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
                player.hurtMarked = true;
            }
            if (flight > 45) {
                tag.putInt("sailorFlight", 0);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());

        // Check if the player is holding your SimpleAbilityItem
        if (itemStack.getItem() instanceof SimpleAbilityItem) {
            LivingEntity targetEntity = (LivingEntity) event.getTarget();

            // Execute custom interaction logic
            InteractionResult result = ((SimpleAbilityItem) itemStack.getItem())
                    .useAbilityOnEntity(itemStack, player, targetEntity, event.getHand());

            // Cancel the default interaction if your item interaction is successful
            if (result == InteractionResult.SUCCESS) {
                event.setCanceled(true);  // Cancels the event, preventing default interaction
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }

    }


    private static void livingLightningStorm(LivingEntity livingEntity) {
        //MISFORTUNE MANIPULATION
        if (livingEntity.tickCount % 5 == 0) {
            CompoundTag tag = livingEntity.getPersistentData();
            int sailorLightningStorm1 = tag.getInt("sailorLightningStorm1");
            int x1 = livingEntity.getPersistentData().getInt("sailorStormVecX1");
            int y1 = livingEntity.getPersistentData().getInt("sailorStormVecY1");
            int z1 = livingEntity.getPersistentData().getInt("sailorStormVecZ1");
            if (sailorLightningStorm1 >= 1) {
                Random random = new Random();
                tag.putInt("sailorLightningStorm1", sailorLightningStorm1 - 1);
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), livingEntity.level());
                lightningEntity.setSpeed(7.0f);
                lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -3, (Math.random() * 0.4) - 0.2);
                lightningEntity.setMaxLength(30);
                lightningEntity.setNoUp(true);
                if (random.nextInt(30) == 1) {
                    lightningEntity.teleportTo(livingEntity.getX(), lightningEntity.getY() + 50, lightningEntity.getZ());
                    lightningEntity.setTargetPos(livingEntity.getOnPos().getCenter());
                } else {
                    lightningEntity.teleportTo(x1 + ((Math.random() * 150) - (double) 150 / 2), y1 + 80, z1 + ((Math.random() * 150) - (double) 150 / 2));
                }
                lightningEntity.level().addFreshEntity(lightningEntity);
            }
        }
    }


    @SubscribeEvent
    public static void handleLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        Level level = entity.level();
        if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel serverLevel) {
                CorruptionAndLuckHandler.corruptionAndLuckManagers(serverLevel, entity);
            }
            BattleHypnotism.untargetMobs(event);
            ProbabilityManipulationInfiniteMisfortune.infiniteFortuneMisfortuneTick(event);
            probabilityManipulationWorld(entity);
            CycleOfFate.tickEvent(event);
            dodgeProjectiles(entity);
            MisfortuneManipulation.livingTickMisfortuneManipulation(event);
            FalseProphecy.falseProphecyTick(entity);
            AuraOfChaos.auraOfChaos(event);
            NoRegenerationEffect.preventRegeneration(entity);
            WhisperOfCorruptionEntity.decrementWhisper(tag);
            MisfortuneRedirection.misfortuneLivingTickEvent(event);
            MonsterCalamityIncarnation.calamityTickEvent(event);
            dreamWeaving(entity);

            prophesizeTeleportation(tag, entity);

            matterAccelerationEntities(entity);

            mentalPlague(entity);

            AqueousLightDrown.lightTickEvent(entity);


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
                    entity.setDeltaMovement(entity.getDeltaMovement().x, Math.min(0, entity.getDeltaMovement().y - 0.5), entity.getDeltaMovement().z);
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
                entity.fallDistance = 0;
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
            if (!(entity instanceof Player)) {
                livingLightningStorm(entity);
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
                    player.hurt(player.damageSources().lightningBolt(), 40);
                }
            } else {
                entity1.hurt(entity1.damageSources().lightningBolt(), 40);
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
            event.setCanceled(true); // Cancel default attack interaction

            // Add byte 'L' to keysClicked array
            byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");
            for (int i = 0; i < keysClicked.length; i++) {
                if (keysClicked[i] == 0) {
                    keysClicked[i] = 1;
                    player.getPersistentData().putByteArray("keysClicked", keysClicked);
                    BeyonderAbilityUser.clicked(player, InteractionHand.MAIN_HAND);
                    break;
                }
            }
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

    private static void monsterLuckPoisonAttacker(Player pPlayer) {
        if (pPlayer.tickCount % 100 == 0) {
            if (pPlayer.getPersistentData().getInt("luckAttackerPoisoned") >= 1) {
                for (LivingEntity livingEntity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(50))) {
                    if (livingEntity.getPersistentData().getInt("attackedMonster") >= 1) {
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60, 1, false, false));
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 2, true, true));
                        livingEntity.getPersistentData().putInt("attackedMonster", 0);
                        pPlayer.getPersistentData().putInt("luckAttackerPoisoned", pPlayer.getPersistentData().getInt("luckAttackerPoisoned") - 1);
                    }
                }
            }
        }
    }

    private static void monsterLuckIgnoreMobs(Player pPlayer) {
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

    private static void decrementMonsterAttackEvent(Player pPlayer) {
        if (pPlayer.getPersistentData().getInt("attackedMonster") >= 1) {
            pPlayer.getPersistentData().putInt("attackedMonster", pPlayer.getPersistentData().getInt("attackedMonster") - 1);
        }
    }

    private static void rippleOfMisfortune(Player player) { //ADD CHECKS FOR NEARBY MONSTERS AT SEQ 6 AND 3
        if (!player.level().isClientSide() && player.getPersistentData().getBoolean("monsterRipple")) {
            Level level = player.level();
            int enhancement = CalamityEnhancementData.getInstance((ServerLevel) player.level()).getCalamityEnhancement();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            holder.useSpirituality(200);
            for (LivingEntity livingEntity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(150 - (holder.getCurrentSequence()) * 20))) {
                Random random = new Random();
                if (livingEntity != player) {
                    int randomInt = random.nextInt(14);
                    if (randomInt == 0) {
                        livingEntity.hurt(livingEntity.damageSources().generic(), livingEntity.getMaxHealth() / (10 - enhancement));
                    }
                    if (randomInt == 1) {
                        BlockPos hitPos = livingEntity.blockPosition();
                        double radius = 10 - (holder.getCurrentSequence() * 2);
                        for (BlockPos pos : BlockPos.betweenClosed(
                                hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                                hitPos.offset((int) radius, (int) radius, (int) radius))) {
                            if (pos.distSqr(hitPos) <= radius * radius) {
                                if (livingEntity.level().getBlockState(pos).getDestroySpeed(livingEntity.level(), pos) >= 0) {
                                    livingEntity.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                        List<Entity> entities = livingEntity.level().getEntities(livingEntity,
                                new AABB(hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                                        hitPos.offset((int) radius, (int) radius, (int) radius)));
                        for (Entity entity : entities) {
                            if (entity instanceof LivingEntity explosionHitEntity) {
                                if (explosionHitEntity instanceof Player player1 && BeyonderHolderAttacher.getHolderUnwrap(player1).currentClassMatches(BeyonderClassInit.MONSTER)) {
                                    BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(player1);
                                    int sequence = holder1.getCurrentSequence();
                                    if (sequence <= 5 && sequence > 3) {
                                        player1.hurt(BeyonderUtil.genericSource(player), 10 + (enhancement * 3));
                                    } else if (sequence <= 3) {
                                        return;
                                    }
                                } else {
                                    explosionHitEntity.hurt(BeyonderUtil.genericSource(player), 10 + (enhancement * 3));
                                }
                            }
                        }
                    }
                    if (randomInt == 2) {
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                        lightningBolt.setDamage(30 - (holder.getCurrentSequence() * 5));
                        lightningBolt.setPos(livingEntity.getOnPos().getCenter());
                        if (player instanceof ServerPlayer serverPlayer) {
                            lightningBolt.setCause(serverPlayer);
                        }
                        for (int i = 0; i < enhancement; i++) {
                            player.level().addFreshEntity(lightningBolt);
                        }
                    }
                    if (randomInt == 3) {
                        TornadoEntity tornadoEntity = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), player.level());
                        tornadoEntity.setTornadoLifecount(100);
                        tornadoEntity.setOwner(player);
                        tornadoEntity.setTornadoPickup(true);
                        tornadoEntity.setTornadoRadius(30 - (holder.getCurrentSequence() * 6) + (enhancement * 5));
                        tornadoEntity.setTornadoHeight(50 - (holder.getCurrentSequence() * 8) + (enhancement * 8));
                        tornadoEntity.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                        player.level().addFreshEntity(tornadoEntity);
                        for (LivingEntity otherEntities : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(60))) {
                            if (otherEntities instanceof Player player1 && BeyonderHolderAttacher.getHolderUnwrap(player1).currentClassMatches(BeyonderClassInit.MONSTER)) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(player1);
                                int sequence = holder1.getCurrentSequence();
                                if (sequence <= 5 && sequence > 3) {
                                    player1.getPersistentData().putInt("luckTornadoResistance", 6);
                                } else if (sequence <= 3) {
                                    player1.getPersistentData().putInt("luckTornadoImmunity", 6);
                                }
                            }
                        }
                    }
                    if (randomInt == 4) {
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(40 - (holder.getCurrentSequence() * 10) + (enhancement * 10)))) {
                            if (entity != player) {
                                if (entity instanceof Player player1 && BeyonderHolderAttacher.getHolderUnwrap(player1).currentClassMatches(BeyonderClassInit.MONSTER)) {
                                    BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(player1);
                                    int sequence = holder1.getCurrentSequence();
                                    if (sequence <= 5 && sequence > 3) {
                                        entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 30 - (holder.getCurrentSequence() * 6), 1, false, false));
                                        entity.setTicksFrozen(60 - (holder.getCurrentSequence() * 12));
                                    } else if (sequence <= 3) {
                                        return;
                                    }
                                }
                                entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 60 - (holder.getCurrentSequence() * 12), 1, false, false));
                                entity.setTicksFrozen(60 - (holder.getCurrentSequence() * 12));

                            }
                        }
                    }
                    if (randomInt == 5) {
                        StoneEntity stoneEntity = new StoneEntity(EntityInit.STONE_ENTITY.get(), level);
                        stoneEntity.teleportTo(livingEntity.getX() + (Math.random() * 10) - 5, livingEntity.getY() + (Math.random() * 10) - 5, livingEntity.getZ() + (Math.random() * 10) - 5);
                        stoneEntity.setStoneXRot((int) (Math.random() * 10) - 5);
                        stoneEntity.setStoneYRot((int) (Math.random() * 10) - 5);
                        stoneEntity.setDeltaMovement(0, -2, 0);
                        for (int i = 0; i < enhancement; i++) {
                            if (holder.getCurrentSequence() >= 2) {
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                            } else {
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                                player.level().addFreshEntity(stoneEntity);
                            }
                        }
                    }
                    if (randomInt == 6) {
                        if (livingEntity instanceof ServerPlayer serverPlayer && serverPlayer.getAbilities().mayfly) {
                            serverPlayer.setDeltaMovement(livingEntity.getDeltaMovement().x, -6 - enhancement, livingEntity.getDeltaMovement().z);
                        } else {
                            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 6 + enhancement, livingEntity.getDeltaMovement().z);
                        }
                    }
                    if (randomInt == 7) {
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(25 - (holder.getCurrentSequence() * 5) + (enhancement * 5)))) {
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                if (holder1.currentClassMatches(BeyonderClassInit.MONSTER)) {
                                    if (holder1.getCurrentSequence() <= 3) {
                                        return;
                                    } else if (holder1.getCurrentSequence() <= 6) {
                                        pPlayer.hurt(pPlayer.damageSources().lava(), 9);
                                        pPlayer.setSecondsOnFire(4 + (enhancement * 2));
                                    }
                                }
                            } else entity.hurt(entity.damageSources().lava(), 12);
                            entity.setSecondsOnFire(6 + (enhancement * 3));
                        }
                    }
                    if (randomInt == 8) {
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(25 - (holder.getCurrentSequence() * 5) + (enhancement * 5)))) {
                            CompoundTag tag = entity.getPersistentData();
                            if (entity instanceof Player pPlayer) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                                double corruptionAmount = pPlayer.getPersistentData().getDouble("misfortune");
                                if (holder1.getCurrentSequence() == 3) {
                                    tag.putDouble("corruption", corruptionAmount + 10 + (enhancement * 3));
                                } else if (holder1.getCurrentSequence() <= 2) {
                                    return;
                                } else {
                                    tag.putDouble("corruption", corruptionAmount + 30 + (enhancement * 5));
                                }
                            } else if (entity instanceof PlayerMobEntity pPlayer) {
                                double corruptionAmount = pPlayer.getPersistentData().getDouble("misfortune");
                                if (pPlayer.getCurrentSequence() == 3) {
                                    tag.putDouble("corruption", corruptionAmount + 10 + (enhancement * 3));
                                } else if (pPlayer.getCurrentSequence() <= 2) {
                                    return;
                                } else {
                                    tag.putDouble("corruption", corruptionAmount + 30 + (enhancement * 5));
                                }
                            } else {
                                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 3 + enhancement, false, false));
                                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3 + enhancement, false, false));
                            }
                        }
                    }
                    if (randomInt == 9) {
                        LightningEntity lightning = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), livingEntity.level());
                        lightning.setSpeed(5.0f);
                        lightning.setTargetEntity(livingEntity);
                        lightning.setMaxLength(120);
                        lightning.setNewStartPos(new Vec3(livingEntity.getX(), livingEntity.getY() + 80, livingEntity.getZ()));
                        lightning.setDeltaMovement(0, -3, 0);
                        lightning.setNoUp(true);
                        if (holder.getCurrentSequence() == 3) {
                            player.level().addFreshEntity(lightning);
                            if (enhancement >= 2) {
                                player.level().addFreshEntity(lightning);
                            }
                        }
                        if (holder.getCurrentSequence() <= 2 && holder.getCurrentSequence() >= 1) {
                            player.level().addFreshEntity(lightning);
                            player.level().addFreshEntity(lightning);
                            player.level().addFreshEntity(lightning);
                            if (enhancement >= 2) {
                                player.level().addFreshEntity(lightning);
                                player.level().addFreshEntity(lightning);
                            }
                        }
                        if (holder.getCurrentSequence() == 0) {
                            player.level().addFreshEntity(lightning);
                            player.level().addFreshEntity(lightning);
                            player.level().addFreshEntity(lightning);
                            player.level().addFreshEntity(lightning);
                            player.level().addFreshEntity(lightning);
                            if (enhancement >= 2) {
                                player.level().addFreshEntity(lightning);
                                player.level().addFreshEntity(lightning);
                                player.level().addFreshEntity(lightning);
                            }
                        }
                    }
                    if (randomInt == 10) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200 - (holder.getCurrentSequence() * 30) + (enhancement * 30), 1, false, false));
                    }
                    if (randomInt == 11) {
                        List<SimpleAbilityItem> validAbilities = new ArrayList<>();

                        // First collect all valid abilities
                        for (Item item : BeyonderUtil.getAbilities(player)) {
                            if (item instanceof SimpleAbilityItem simpleAbilityItem) {  // Changed to SimpleAbilityItem instead of Ability
                                boolean hasEntityInteraction = false;
                                try {
                                    Method entityMethod = item.getClass().getDeclaredMethod("useAbilityOnEntity", ItemStack.class, Player.class, LivingEntity.class, InteractionHand.class);
                                    hasEntityInteraction = !entityMethod.equals(SimpleAbilityItem.class.getDeclaredMethod("useAbilityOnEntity", ItemStack.class, Player.class, LivingEntity.class, InteractionHand.class));

                                    if (hasEntityInteraction) {
                                        validAbilities.add(simpleAbilityItem);
                                    }
                                } catch (NoSuchMethodException ignored) {
                                }
                            }
                        }

                        // Then use one random ability outside the loop
                        if (!validAbilities.isEmpty()) {
                            int randomIndex = player.getRandom().nextInt(validAbilities.size());
                            SimpleAbilityItem selectedAbility = validAbilities.get(randomIndex);
                            ItemStack stack = selectedAbility.getDefaultInstance();
                            selectedAbility.useAbilityOnEntity(stack, player, player, InteractionHand.MAIN_HAND);
                        }
                    }
                    if (randomInt == 12) {

                        Vex vex = new Vex(EntityType.VEX, level);
                        vex.setTarget(livingEntity);
                        vex.setPos(player.getX(), player.getY(), player.getZ());
                        vex.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 2, false, false));
                        vex.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 4 - holder.getCurrentSequence(), false, false));
                        vex.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false));
                        for (int i = 0; i < enhancement; i++) {
                            if (holder.getCurrentSequence() == 3) {
                                player.level().addFreshEntity(vex);
                            }
                            if (holder.getCurrentSequence() <= 2 && holder.getCurrentSequence() >= 1) {
                                player.level().addFreshEntity(vex);
                                player.level().addFreshEntity(vex);
                                player.level().addFreshEntity(vex);
                            }
                            if (holder.getCurrentSequence() == 0) {
                                player.level().addFreshEntity(vex);
                                player.level().addFreshEntity(vex);
                                player.level().addFreshEntity(vex);
                                player.level().addFreshEntity(vex);
                                player.level().addFreshEntity(vex);
                            }
                        }
                    }
                    if (randomInt == 13) {
                        if (livingEntity instanceof Player itemPlayer) {
                            for (Item item : BeyonderUtil.getAbilities(itemPlayer)) {
                                if (item instanceof SimpleAbilityItem simpleAbilityItem) {
                                    int currentCooldown = (int) player.getCooldowns().getCooldownPercent(item, 0);
                                    int cooldownToSet = simpleAbilityItem.getCooldown() * (100 - currentCooldown) + (enhancement * 10);
                                    if (currentCooldown < cooldownToSet) {
                                        player.getCooldowns().addCooldown(item, cooldownToSet);
                                    }
                                }
                            }
                        }
                    }
                    if (randomInt == 0) {
                        boolean healthCheck = player.getHealth() >= livingEntity.getHealth();
                        if (healthCheck) {
                            double x = player.getX() - livingEntity.getX();
                            double y = Math.min(5, player.getY() - livingEntity.getY());
                            double z = player.getZ() - livingEntity.getZ();
                            livingEntity.setDeltaMovement(x * 0.3, y * 0.3, z * 0.3);
                        } else {
                            double x = livingEntity.getX() - player.getX();
                            double y = livingEntity.getY() - player.getY();
                            double z = livingEntity.getZ() - player.getZ();
                            double magnitude = Math.sqrt(x * x + y * y + z * z);
                            livingEntity.setDeltaMovement(x / magnitude * 8, y / magnitude * 8, z / magnitude * 8);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void attackEvent(LivingAttackEvent event) {
        LivingEntity attacked = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker != null) {
            if (!attacker.level().isClientSide()) {
                if (attacked instanceof Player pPlayer) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 5) {
                        if (attacker instanceof LivingEntity) {
                            attacker.getPersistentData().putInt("attackedMonster", 100);
                        }
                    }
                }
                if (attacker.getPersistentData().getInt("beneficialFalseProphecyAttack") >= 1) {
                    attacker.getPersistentData().putInt("beneficialDamageDoubled", 5);
                    attacker.getPersistentData().putInt("beneficialFalseProphecyAttack", 0);
                }
                if (attacker.getPersistentData().getInt("beneficialDamageDoubled") >= 1) {
                    attacker.getPersistentData().putInt("beneficialDamageDoubled", attacker.getPersistentData().getInt("beneficialDamageDoubled") - 1);
                    event.setCanceled(true);
                    attacked.hurt(BeyonderUtil.magicSource(attacker), event.getAmount() * 2);
                }
                if (attacker.getPersistentData().getInt("harmfulFalseProphecyAttack") >= 1) {
                    attacker.getPersistentData().putInt("luckDoubleDamage", attacker.getPersistentData().getInt("luckDoubleDamage") + 5);
                    attacker.getPersistentData().putInt("harmfulFalseProphecyAttack", 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingJumpEvent(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            CompoundTag tag = entity.getPersistentData();
            int falseProphecyBeneficial = tag.getInt("beneficialFalseProphecyJump");
            int falseProphecyHarmful = tag.getInt("harmfulFalseProphecyJump");
            if (falseProphecyBeneficial >= 1) {
                tag.putInt("falseProphecyJumpBeneficial", tag.getInt("falseProphecyJumpBeneficial") + 1);
            }
            if (falseProphecyHarmful >= 1) {
                tag.putInt("falseProphecyJumpHarmful", tag.getInt("falseProphecyJumpHarmful") + 1);
            }
        }

    }

    public static void monsterDodgeAttack(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (!entity.level().isClientSide() && BeyonderUtil.isBeyonderCapable(entity)) {
            if (entity instanceof Player pPlayer && !source.is(DamageTypes.FALL) && !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.ON_FIRE)) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
                    int randomChance = (int) ((Math.random() * 20) - holder.getCurrentSequence());
                    if (randomChance >= 13) {
                        double amount = event.getAmount();
                        double x = 0;
                        double z = 0;
                        Random random = new Random();
                        if (random.nextInt(2) == 0) {
                            x = amount * -0.15;
                            z = amount * -0.15;
                        } else {
                            x = amount * 0.15;
                            z = amount * 0.15;
                        }
                        entity.setDeltaMovement(x,1,z);
                        entity.hurtMarked = true;
                        event.setAmount(0);
                        entity.sendSystemMessage(Component.literal("A breeze of wind moved you out of the way of damage").withStyle(ChatFormatting.GREEN));
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void hurtEvent(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        DamageSource source = event.getSource();
        Entity entitySource = source.getEntity();
        if (!event.getEntity().level().isClientSide()) {
            if (entity instanceof LivingEntity) {
                monsterDodgeAttack(event);
                int stoneImmunity = tag.getInt("luckStoneDamageImmunity");
                int stoneDamage = tag.getInt("luckStoneDamage");
                int meteorDamage = tag.getInt("luckMeteorDamage");
                int meteorImmunity = tag.getInt("calamityMeteorImmunity");
                int MCLightingDamage = tag.getInt("luckLightningMCDamage");
                int mcLightningImmunity = tag.getInt("luckMCLightningImmunity");
                int calamityExplosionOccurrenceDamage = tag.getInt("calamityExplosionOccurrence");
                int lotmLightningDamage = tag.getInt("luckLightningLOTMDamage");
                int lightningBoltResistance = tag.getInt("calamityLightningBoltMonsterResistance");
                int lotmLightningDamageCalamity = tag.getInt("calamityLightningStormResistance");
                int tornadoResistance = tag.getInt("luckTornadoResistance");
                int tornadoImmunity = tag.getInt("luckTornadoImmunity");
                int lotmLightningImmunity = tag.getInt("calamityLOTMLightningImmunity");
                int lightningStormImmunity = tag.getInt("calamityLightningStormImmunity");
                int enhancement = CalamityEnhancementData.getInstance((ServerLevel) entity.level()).getCalamityEnhancement();
                if (enhancement >= 2) {
                    event.setAmount((float) (event.getAmount() + (enhancement * 0.25)));
                }
                if (entitySource instanceof StoneEntity) {
                    if (stoneImmunity >= 1) {
                        event.setCanceled(true);
                    } else if (stoneDamage >= 1) {
                        event.setAmount(event.getAmount() / 2);
                    }
                }
                if (entitySource instanceof MeteorEntity || entitySource instanceof MeteorNoLevelEntity) {
                    if (meteorImmunity >= 1) {
                        event.setCanceled(true);
                    } else if (meteorDamage >= 1) {
                        event.setAmount(event.getAmount() / 2);
                    }
                }
                if (source.is(DamageTypes.LIGHTNING_BOLT)) {
                    if (mcLightningImmunity >= 1) {
                        event.setCanceled(true);
                    } else if (MCLightingDamage >= 1) {
                        event.setAmount(event.getAmount() / 2);
                    }
                }
                if (source.is(DamageTypes.EXPLOSION)) {
                    if (calamityExplosionOccurrenceDamage >= 1) {
                        event.setAmount(event.getAmount() / 2);
                    }
                }
                if (entitySource instanceof LightningEntity) {
                    if (lotmLightningImmunity >= 1 || lightningStormImmunity >= 1) {
                        event.setCanceled(true);
                    } else if (lotmLightningDamage >= 1 || lightningBoltResistance >= 1 || lotmLightningDamageCalamity >= 1) {
                        event.setAmount(event.getAmount() / 2);
                    }
                }
            }
            //SAILOR FLIGHT
            if (entity instanceof Player player) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                int flightCancel = tag.getInt("sailorFlightDamageCancel");
                if (!player.level().isClientSide()) {

                    //SAILOR FLIGHT
                    if (flightCancel != 0 && event.getSource() == player.damageSources().fall()) {
                        event.setCanceled(true);
                        tag.putInt("sailorFlightDamageCancel", 0);
                    }
                }
                rippleOfMisfortune(player);

                //MONSTER LUCK
                int doubleDamage = tag.getInt("luckDoubleDamage");
                int ignoreDamage = tag.getInt("luckIgnoreDamage");
                int halveDamage = tag.getInt("luckHalveDamage");
                if (halveDamage >= 1) {
                    event.setAmount(event.getAmount() / 2);
                }
                if (ignoreDamage >= 1) {
                    event.setCanceled(true);
                } else if (doubleDamage >= 1) {
                    event.setAmount(event.getAmount() * 2);
                }
            }


            //STORM SEAL
            if (entity.getPersistentData().getInt("inStormSeal") >= 1) {
                event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public static void deathEvent(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        if (!entity.level().isClientSide()) {
            if (entity instanceof Player pPlayer) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);

            }
            CycleOfFate.cycleOfFateDeath(event);


            //AQUEOUS LIGHT DROWN
            if (entity.getPersistentData().getInt("lightDrowning") >= 1) {
                Level level = entity.level();
                BlockPos headPos = BlockPos.containing(entity.getEyePosition());
                for (int x = -3; x <= 3; x++) {
                    for (int y = -3; y <= 3; y++) {
                        for (int z = -3; z <= 3; z++) {
                            BlockPos blockPos = headPos.offset(x, y, z);
                            if (level.getBlockState(blockPos).is(Blocks.WATER)) {
                                level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
            }
            CorruptionAndLuckHandler.onPlayerDeath(event);

            //STORM SEAL
            if (tag.getInt("inStormSeal") >= 1) {
                event.setCanceled(true);
                System.out.println("death canceled");
                entity.setHealth(5.0f);
            }

            if (entity instanceof Player player) {

                byte[] keysClicked = new byte[5]; // Example size; match this to the intended array size
                player.getPersistentData().putByteArray("keysClicked", keysClicked);

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

    private static void dodgeProjectiles(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            if (livingEntity.getPersistentData().getInt("windMovingProjectilesCounter") >= 1) {
                for (Projectile projectile : livingEntity.level().getEntitiesOfClass(Projectile.class, livingEntity.getBoundingBox().inflate(200))) {
                    if (projectile.getPersistentData().getInt("windDodgeProjectilesCounter") == 0) {
                        if (projectile instanceof Arrow arrow && arrow.tickCount >= 100) {
                            return;
                        }
                        float scale = ScaleTypes.BASE.getScaleData(projectile).getScale();
                        double maxDistance = 6 * scale;
                        double deltaX = Math.abs(projectile.getX() - livingEntity.getX());
                        double deltaY = Math.abs(projectile.getY() - livingEntity.getY());
                        double deltaZ = Math.abs(projectile.getZ() - livingEntity.getZ());
                        if (deltaX <= maxDistance || deltaY <= maxDistance || deltaZ <= maxDistance && projectile.getOwner() != livingEntity) {
                            double mathRandom = (Math.random() + .4) - 0.2;
                            double x = projectile.getDeltaMovement().x() + (mathRandom * scale);
                            double y = projectile.getDeltaMovement().y() + (mathRandom * scale);
                            double z = projectile.getDeltaMovement().z() + (mathRandom * scale);
                            projectile.setDeltaMovement(x, y, z);
                            projectile.hurtMarked = true;
                            projectile.getPersistentData().putInt("windDodgeProjectilesCounter", 40);
                            livingEntity.getPersistentData().putInt("windMovingProjectilesCounter", livingEntity.getPersistentData().getInt("windMovingProjectilesCounter") - 1);
                            if (livingEntity instanceof Player player) {
                                player.displayClientMessage(Component.literal("A gust of wind moved a projectile headed towards you").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE), true);
                            }
                        }
                    } else {
                        projectile.getPersistentData().putInt("windDodgeProjectilesCounter", projectile.getPersistentData().getInt("windDodgeProjectilesCounter") - 1);
                    }
                }
            } else {
                if (BeyonderUtil.isBeyonderCapable(livingEntity)) {
                    if (livingEntity instanceof Player pPlayer) {
                        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                        int sequence = holder.getCurrentSequence();
                        if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 7) {
                            int reverseChance = (int) (Math.random() * 20 - sequence);
                            for (Projectile projectile : livingEntity.level().getEntitiesOfClass(Projectile.class, livingEntity.getBoundingBox().inflate(200))) {
                                if (projectile.getPersistentData().getInt("monsterReverseProjectiles") == 0) {
                                    if (projectile instanceof Arrow arrow && arrow.tickCount >= 100) {
                                        return;
                                    }
                                    if (reverseChance >= 10) {
                                        float scale = ScaleTypes.BASE.getScaleData(projectile).getScale();
                                        double maxDistance = 6 * scale;
                                        double deltaX = Math.abs(projectile.getX() - livingEntity.getX());
                                        double deltaY = Math.abs(projectile.getY() - livingEntity.getY());
                                        double deltaZ = Math.abs(projectile.getZ() - livingEntity.getZ());
                                        if (deltaX <= maxDistance || deltaY <= maxDistance || deltaZ <= maxDistance && projectile.getOwner() != livingEntity) {
                                            double x = projectile.getDeltaMovement().x() * -1;
                                            double y = projectile.getDeltaMovement().y() * -1;
                                            double z = projectile.getDeltaMovement().z() * -1;
                                            projectile.setDeltaMovement(x, y, z);
                                            projectile.hurtMarked = true;
                                            projectile.getPersistentData().putInt("monsterReverseProjectiles", 40);
                                            if (livingEntity instanceof Player player) {
                                                player.displayClientMessage(Component.literal("A strong breeze luckily reversed a projectile headed towards you").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE), true);
                                            }
                                        }
                                    }
                                } else {
                                    projectile.getPersistentData().putInt("monsterReverseProjectiles", projectile.getPersistentData().getInt("windDodgeProjectilesCounter") - 1);

                                }
                            }
                        }
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

    private static void sirenSongs(Player player) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int sequence = holder.getCurrentSequence();
        CompoundTag playerPersistentData = player.getPersistentData();
        int harmCounter = 50 - (sequence * 6);
        int sirenSongWeaken = playerPersistentData.getInt("sirenSongWeaken");
        int sirenSongStrengthen = playerPersistentData.getInt("sirenSongStrengthen");
        int sirenSongHarm = playerPersistentData.getInt("sirenSongHarm");
        int sirenSongStun = playerPersistentData.getInt("sirenSongStun");
        if (sirenSongStrengthen >= 1 || sirenSongWeaken >= 1 || sirenSongStun >= 1 || sirenSongHarm >= 1) {
            SirenSongStrengthen.spawnParticlesInSphere(player, harmCounter);
        }
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

    public static void showMonsterParticles(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide() && livingEntity.tickCount % 100 == 0) {
            for (LivingEntity entities : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(50))) {
                if ((entities instanceof Player pPlayer && entities != livingEntity)) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.getCurrentSequence() <= 2 && holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
                        CompoundTag tag = livingEntity.getPersistentData();
                        int cantUseAbility = tag.getInt("cantUseAbility");
                        int meteor = tag.getInt("luckMeteor");
                        int lotmLightning = tag.getInt("luckLightningLOTM");
                        int paralysis = tag.getInt("luckParalysis");
                        int unequipArmor = tag.getInt("luckUnequipArmor");
                        int wardenSpawn = tag.getInt("luckWarden");
                        int mcLightning = tag.getInt("luckLightningMC");
                        int poison = tag.getInt("luckPoison");
                        int tornadoInt = tag.getInt("luckTornado");
                        int stone = tag.getInt("luckStone");
                        int doubleDamage = tag.getInt("luckDoubleDamage");
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
                        int ignoreDamage = tag.getInt("luckIgnoreDamage");
                        int diamonds = tag.getInt("luckDiamonds");
                        int regeneration = tag.getInt("luckRegeneration");
                        int moveProjectiles = tag.getInt("windMovingProjectilesCounter");
                        int halveDamage = tag.getInt("luckHalveDamage");
                        int ignoreMobs = tag.getInt("luckIgnoreMobs");
                        int luckAttackerPoisoned = tag.getInt("luckAttackerPoisoned");
                        if (cantUseAbility >= 1) {
                            for (int i = 0; i < cantUseAbility; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.CANT_USE_ABILITY_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (meteor >= 1) {
                            int particleCount = Math.max(1, (int) 20 - (meteor / 2));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.METEOR_CALAMITY_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (lotmLightning >= 1) {
                            int particleCount = Math.max(1, (int) 15 - (lotmLightning));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.LOTM_LIGHTNING_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (paralysis >= 1) {
                            int particleCount = Math.max(1, (int) 15 - (paralysis));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.TRIP_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (unequipArmor >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (unequipArmor));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.WIND_UNEQUIP_ARMOR_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (wardenSpawn >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (wardenSpawn / 1.5));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.WARDEN_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (mcLightning >= 1) {
                            for (int i = 0; i < mcLightning; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.MC_LIGHTNING_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (poison >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (poison / 0.75));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.POISON_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (tornadoInt >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (tornadoInt * 0.75));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.TORNADO_CALAMITY_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (stone >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (tornadoInt / 0.5));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.FALLING_STONE_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (doubleDamage >= 1) {
                            for (int i = 0; i < doubleDamage; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.DOUBLE_DAMAGE_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityMeteor >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (calamityMeteor / 3.5));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.METEOR_CALAMITY_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityLightningStorm >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (calamityLightningStorm / 2.5));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.LIGHTNING_STORM_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityLightningBolt >= 1) {
                            int particleCount = Math.max(1, (int) 20 - (calamityLightningBolt * 2));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.LOTM_LIGHTNING_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityGroundTremor >= 1) {
                            int particleCount = Math.max(1, (int) 20 - (calamityGroundTremor / 2));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.GROUND_TREMOR_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityGaze >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (calamityGaze / 2.5));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.GOO_GAZE_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityUndeadArmy >= 1) {
                            int particleCount = Math.max(1, (int) 20 - (calamityUndeadArmy));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.UNDEAD_ARMY_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityBabyZombie >= 1) {
                            int particleCount = Math.max(1, (int) 20 - (calamityBabyZombie));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.BABY_ZOMBIE_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityWindArmorRemoval >= 1) {
                            int particleCount = Math.max(1, (int) 20 - (calamityWindArmorRemoval / 2));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.WIND_UNEQUIP_ARMOR_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityBreeze >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (calamityBreeze / 1.25));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.BREEZE_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityWave >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (calamityWave / 1.25));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.HEAT_WAVE_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityExplosion >= 1) {
                            int particleCount = Math.max(1, (int) 20 - (calamityExplosion / 3));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.EXPLOSION_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (calamityTornado >= 1) {
                            int particleCount = (int) Math.max(1, (int) 20 - (calamityTornado / 3.5));
                            for (int i = 0; i < particleCount; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.TORNADO_CALAMITY_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (ignoreDamage >= 1) {
                            for (int i = 0; i < ignoreDamage; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.IGNORE_DAMAGE_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (diamonds >= 1) {
                            for (int i = 0; i < diamonds; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.DIAMOND_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (regeneration >= 1) {
                            for (int i = 0; i < regeneration; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.REGENERATION_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (moveProjectiles >= 1) {
                            for (int i = 0; i < moveProjectiles; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.WIND_MOVE_PROJECTILES_PARTICLES.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (halveDamage >= 1) {
                            for (int i = 0; i < halveDamage; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.HALF_DAMAGE_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (ignoreMobs >= 1) {
                            for (int i = 0; i < ignoreMobs; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.IGNORE_MOBS_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                        if (luckAttackerPoisoned >= 1) {
                            for (int i = 0; i < luckAttackerPoisoned; i++) {
                                double offsetX = (Math.random() - 0.5) * 2;
                                double offsetY = Math.random();
                                double offsetZ = (Math.random() - 0.5) * 2;
                                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleInit.ATTACKER_POISONED_PARTICLE.get(), offsetX, offsetY, offsetZ, 0, 0, 0), (ServerPlayer) livingEntity);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void checkForProjectiles(Player player) {
        Level level = player.level();
        for (Projectile projectile : level.getEntitiesOfClass(Projectile.class, player.getBoundingBox().inflate(100))) {
            List<Vec3> trajectory = predictProjectileTrajectory(projectile, player);
            float scale = ScaleTypes.BASE.getScaleData(projectile).getScale();
            double maxDistance = 20 * scale;
            double deltaX = Math.abs(projectile.getX() - player.getX());
            double deltaY = Math.abs(projectile.getY() - player.getY());
            double deltaZ = Math.abs(projectile.getZ() - player.getZ());
            if (deltaX <= maxDistance || deltaY <= maxDistance || deltaZ <= maxDistance) {
                if (player.level() instanceof ServerLevel serverLevel) {
                    drawParticleLine(serverLevel, (ServerPlayer) player, trajectory);
                }
            }
        }
    }

    public static void drawParticleLine(ServerLevel level, ServerPlayer player, List<Vec3> points) {
        int particleInterval = 2; // Only spawn a particle every 5 points
        for (int i = 0; i < points.size() - 1; i += particleInterval) {
            Vec3 start = points.get(i);
            Vec3 end = i + particleInterval < points.size() ? points.get(i + particleInterval) : points.get(points.size() - 1);
            Vec3 direction = end.subtract(start).normalize();
            double distance = start.distanceTo(end);
            Vec3 particlePosition = start.add(direction.scale(distance / 2));
            LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(DustParticleOptions.REDSTONE, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0), player);
        }
    }

    public static List<Vec3> predictProjectileTrajectory(Projectile projectile, LivingEntity player) {
        List<Vec3> trajectory = new ArrayList<>();
        Vec3 projectilePos = projectile.position();
        Vec3 projectileDelta = projectile.getDeltaMovement();
        Level level = projectile.level();

        boolean isArrow = projectile instanceof AbstractArrow;

        trajectory.add(projectilePos);

        int maxIterations = 1000; // Increased for a longer trajectory
        double maxDistance = 100.0; // Maximum distance to calculate the trajectory

        for (int i = 0; i < maxIterations; i++) {
            projectilePos = projectilePos.add(projectileDelta);
            trajectory.add(projectilePos);

            // Check if the block at the projectile's position is not air
            if (projectile instanceof Arrow arrow) {
                if (arrow.tickCount >= 100) {
                    break;
                }
            } else if (projectilePos.distanceTo(projectile.position()) > maxDistance) {
                break;
            }

            if (isArrow) {
                projectileDelta = projectileDelta.scale(0.99F);
                projectileDelta = projectileDelta.add(0, -0.05, 0);
            } else {
                projectileDelta = projectileDelta.scale(0.99F); // Air resistance
                projectileDelta = projectileDelta.add(0, -0.03, 0); // Gravity effect
            }
        }

        return trajectory;
    }

    private static void calamityLightningStorm(Player pPlayer) {
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

    private static void calamityUndeadArmy(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        if (pPlayer.level() instanceof ServerLevel serverLevel) {
            int enhancement = CalamityEnhancementData.getInstance(serverLevel).getCalamityEnhancement();

            int x = tag.getInt("calamityUndeadArmyX");
            int y = tag.getInt("calamityUndeadArmyY");
            int z = tag.getInt("calamityUndeadArmyZ");
            int subtractX = (int) (x - pPlayer.getX());
            int subtractY = (int) (y - pPlayer.getY());
            int subtractZ = (int) (z - pPlayer.getZ());
            int surfaceY = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;
            int undeadArmyCounter = tag.getInt("calamityUndeadArmyCounter");
            if (undeadArmyCounter >= 1) {
                for (int i = 0; i < enhancement; i++) {
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
    }

    private static void calamityExplosion(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        if (pPlayer.level() instanceof ServerLevel serverLevel) {
            int x = tag.getInt("calamityExplosionOccurrence");
            if (x >= 1 && pPlayer.tickCount % 20 == 0 && !pPlayer.level().isClientSide()) {
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
                int data = CalamityEnhancementData.getInstance(serverLevel).getCalamityEnhancement();
                pPlayer.level().playSound(null, explosionX, explosionY, explosionZ, SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 5.0F, 5.0F);
                Explosion explosion = new Explosion(pPlayer.level(), null, explosionX, explosionY, explosionZ, 10.0F + (data * 3), true, Explosion.BlockInteraction.DESTROY);
                explosion.explode();
                explosion.finalizeExplosion(true);
                tag.putInt("calamityExplosionOccurrence", 0);
            }
        }
    }


    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            domainDrops(event);
        }
    }

    @SubscribeEvent
    public static void onLivingDropExperience(LivingExperienceDropEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            domainDropsExperience(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        CompoundTag persistentData = player.getPersistentData();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int sequence = holder.getCurrentSequence();
        if (holder.getCurrentClass() != null) {
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(holder.getCurrentClass().maxHealth().get(sequence));
            player.setHealth(player.getMaxHealth());

        }
        if (!persistentData.contains("keysClicked")) {
            byte[] keysClicked = new byte[5]; // Use appropriate size
            persistentData.putByteArray("keysClicked", keysClicked);
        }

    }

    @SubscribeEvent
    public static void onLivingJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (!livingEntity.level().isClientSide()) {
                if (livingEntity instanceof PlayerMobEntity playerMobEntity) {
                    if (!playerMobEntity.level().getLevelData().getGameRules().getBoolean(GameRuleInit.NPC_SHOULD_SPAWN)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void addAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityInit.PLAYER_MOB_ENTITY.get(), AttributeSupplier.builder().add(ModAttributes.DIR.get()).build());
        event.put(EntityInit.PLAYER_MOB_ENTITY.get(), AttributeSupplier.builder().add(ModAttributes.SANITY.get()).build());
        event.put(EntityInit.PLAYER_MOB_ENTITY.get(), AttributeSupplier.builder().add(ModAttributes.NIGHTMARE.get()).build());
    }

    public static void onChaosWalkerCombat(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            if (livingEntity instanceof Player pPlayer) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                int sequence = holder.getCurrentSequence();
                CompoundTag tag = pPlayer.getPersistentData();
                int occursion = tag.getInt("chaosWalkerCalamityOccursion");
                if (pPlayer.getHealth() <= pPlayer.getMaxHealth() * 0.75 && pPlayer.tickCount % 500 == 0 && tag.getInt("chaosWalkerCombat") == 0 && tag.getBoolean("monsterChaosWalkerCombat")) {
                    tag.putInt("chaosWalkerCombat", 300);
                    Random random = new Random();
                    int radius = Math.max(50, 200 - (sequence * 35));
                    tag.putInt("chaosWalkerSafeX", (int) (pPlayer.getX() + (random.nextInt(radius) - (radius * 0.5))));
                    tag.putInt("chaosWalkerSafeZ", (int) (pPlayer.getZ() + (random.nextInt(radius) - (radius * 0.5))));
                    tag.putInt("chaosWalkerRadius", radius);
                }
                int chaosWalkerCounter = tag.getInt("chaosWalkerCombat");
                int chaosWalkerSafeX = tag.getInt("chaosWalkerSafeX");
                int chaosWalkerSafeZ = tag.getInt("chaosWalkerSafeZ");
                int surfaceY1 = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, chaosWalkerSafeX, chaosWalkerSafeZ) + 1;
                int radius = tag.getInt("chaosWalkerRadius");
                if (chaosWalkerCounter >= 1) {
                    tag.putInt("chaosWalkerCombat", chaosWalkerCounter - 1);
                    if (chaosWalkerCounter % 100 == 0) {
                        pPlayer.sendSystemMessage(Component.literal("A calamity will fall everywhere around you in " + chaosWalkerCounter / 20 + " seconds, move to X: " + chaosWalkerSafeX + " Z: " + chaosWalkerSafeZ).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (chaosWalkerCounter == 80 || chaosWalkerCounter == 60 || chaosWalkerCounter == 40 || chaosWalkerCounter == 20) {
                        pPlayer.sendSystemMessage(Component.literal("A calamity will fall everywhere around you in " + chaosWalkerCounter / 20 + " seconds, move to X: " + chaosWalkerSafeX + " Z: " + chaosWalkerSafeZ).withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (pPlayer instanceof ServerPlayer serverPlayer) {
                        if (chaosWalkerCounter % 20 == 0 || (occursion >= 1 && occursion % 20 == 0)) {
                            spawnParticleCylinder(serverPlayer, chaosWalkerSafeX, surfaceY1, chaosWalkerSafeZ, 150, 10);
                        }
                    }
                }
                if (chaosWalkerCounter == 1) {
                    tag.putInt("chaosWalkerCalamityOccursion", 120);
                }
                if (occursion >= 1) {
                    tag.putInt("chaosWalkerCalamityOccursion", occursion - 1);
                    if (occursion % 3 == 0) {
                        pPlayer.sendSystemMessage(Component.literal("Occursion value is " + occursion));
                        Random random = new Random();
                        int randomInt = random.nextInt(100);
                        if (randomInt >= 85) {
                            int farAwayX = getCoordinateAtLeastAway(chaosWalkerSafeX, 60, radius);
                            int farAwayZ = getCoordinateAtLeastAway(chaosWalkerSafeZ, 60, radius);
                            int randomInt2 = (int) ((Math.random() * radius) - radius);
                            int surfaceY = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, farAwayX, farAwayZ) + 1;
                            MeteorEntity.summonMeteorAtPositionWithScale(pPlayer, pPlayer.getX() + randomInt2, pPlayer.getY(), pPlayer.getZ() + randomInt2, farAwayX, surfaceY, farAwayZ, 4);
                        }
                        if (randomInt >= 70 && randomInt <= 84) {
                            int farAwayX = getCoordinateAtLeastAway(chaosWalkerSafeX, 30, radius);
                            int farAwayZ = getCoordinateAtLeastAway(chaosWalkerSafeZ, 30, radius);
                            int surfaceY = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, farAwayX, farAwayZ) + 1;
                            TornadoEntity tornado = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), pPlayer.level());
                            tornado.setTornadoRadius(50 - (sequence * 7));
                            tornado.setTornadoHeight(80 - (sequence * 10));
                            tornado.setOwner(pPlayer);
                            tornado.setTornadoPickup(false);
                            tornado.setTornadoLifecount(100);
                            tornado.teleportTo(farAwayX, surfaceY, pPlayer.getZ());
                            pPlayer.level().addFreshEntity(tornado);
                        }
                        if (randomInt >= 50 && randomInt <= 69) {
                            int farAwayX = getCoordinateAtLeastAway(chaosWalkerSafeX, 20, radius);
                            int farAwayZ = getCoordinateAtLeastAway(chaosWalkerSafeZ, 20, radius);
                            int surfaceY = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, farAwayX, farAwayZ) + 1;
                            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
                            lightningEntity.setSpeed(5);
                            Vec3 targetPos = new Vec3(farAwayX, surfaceY, farAwayZ);
                            lightningEntity.setTargetPos(targetPos);
                            lightningEntity.setBranchOut(true);
                            lightningEntity.setDeltaMovement(0, -3, 0);
                            lightningEntity.setNewStartPos(new Vec3(farAwayX, surfaceY + 100, farAwayZ));
                            lightningEntity.setOwner(pPlayer);
                            lightningEntity.setMaxLength(80);
                            lightningEntity.setNoUp(true);
                            pPlayer.level().addFreshEntity(lightningEntity);
                        }
                        if (randomInt >= 30 && randomInt <= 49) {
                            int farAwayX = getCoordinateAtLeastAway(chaosWalkerSafeX, 40 - (sequence * 5), radius);
                            int farAwayZ = getCoordinateAtLeastAway(chaosWalkerSafeZ, 40 - (sequence * 5), radius);
                            int surfaceY = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, farAwayX, farAwayZ) + 1;
                            int columnHeight = 100 - (sequence * 10);
                            AABB effectBox = new AABB(farAwayX - (20 - sequence * 2), surfaceY, farAwayZ - (20 - sequence * 2), farAwayX + (20 - sequence * 2), surfaceY + columnHeight, farAwayZ + (20 - sequence * 2));
                            List<LivingEntity> affectedEntities = pPlayer.level().getEntitiesOfClass(LivingEntity.class, effectBox);
                            for (LivingEntity entity : affectedEntities) {
                                if (entity == pPlayer) continue;
                                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 2, false, true));
                                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2, false, true));
                                spawnParticleCylinderServerSide((ServerPlayer) pPlayer, farAwayX - 10, farAwayZ - 10, 100 - (sequence * 10), 10);
                            }
                        }
                        if (randomInt >= 15 && randomInt <= 29) {
                            for (int i = 0; i < 20; i++) {
                                int farAwayX = getCoordinateAtLeastAway(chaosWalkerSafeX, 20, radius);
                                int farAwayZ = getCoordinateAtLeastAway(chaosWalkerSafeZ, 20, radius);
                                int surfaceY = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, farAwayX, farAwayZ) + 1;
                                StoneEntity stoneEntity = new StoneEntity(EntityInit.STONE_ENTITY.get(), pPlayer.level());
                                stoneEntity.teleportTo(farAwayX, surfaceY + 150 + 30, farAwayZ);
                                stoneEntity.setDeltaMovement(0, -4, 0);
                                pPlayer.level().addFreshEntity(stoneEntity);
                            }
                        }
                        if (randomInt <= 14) {
                            int farAwayX = getCoordinateAtLeastAway(chaosWalkerSafeX, 25, radius);
                            int farAwayZ = getCoordinateAtLeastAway(chaosWalkerSafeZ, 25, radius);
                            int surfaceY = pPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, farAwayX, farAwayZ) + 1;
                            Level level = pPlayer.level();
                            int spawnCount = 120 - (sequence * 10);
                            Random random1 = new Random();
                            BlockPos playerPos = pPlayer.blockPosition();
                            for (int i = 0; i < spawnCount; i++) {
                                int offsetX = random1.nextInt(21) - 10;
                                int offsetZ = random1.nextInt(21) - 10;
                                BlockPos spawnPos = new BlockPos(farAwayX, surfaceY, farAwayZ);
                                if (!level.isEmptyBlock(spawnPos) && isOnSurface(level, spawnPos)) {
                                    LavaEntity lavaEntity = new LavaEntity(EntityInit.LAVA_ENTITY.get(), level);
                                    lavaEntity.teleportTo(spawnPos.getX(), spawnPos.getY() + 3, spawnPos.getZ());
                                    lavaEntity.setDeltaMovement(0, 3 + (Math.random() * 3), 0);
                                    lavaEntity.setLavaXRot(random1.nextInt(18));
                                    lavaEntity.setLavaYRot(random1.nextInt(18));
                                    ScaleData scaleData = ScaleTypes.BASE.getScaleData(lavaEntity);
                                    scaleData.setScale(1.0f + random1.nextFloat() * 2.0f);
                                    level.addFreshEntity(lavaEntity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void spawnParticleCylinder(ServerPlayer player, int centerX, int centerY, int centerZ,
                                              int height, int radius) {
        for (int y = centerY; y <= height; y++) {
            for (double angle = 0; angle < 360; angle += 10) { // Adjust the step size for density
                double radians = Math.toRadians(angle);
                double x = centerX + radius * Math.cos(radians);
                double z = centerZ + radius * Math.sin(radians);
                LOTMNetworkHandler.sendToPlayer(new SendParticleS2C(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0, 0), player);
            }
        }
    }

    private static void spawnParticleCylinderServerSide(ServerPlayer player, int centerX, int centerZ, int height,
                                                        int radius) {
        for (int y = 0; y <= height; y++) {
            for (double angle = 0; angle < 360; angle += 10) { // Adjust the step size for density
                double radians = Math.toRadians(angle);
                double x = centerX + radius * Math.cos(radians);
                double z = centerZ + radius * Math.sin(radians);
                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ASH, x, y, z, 0, 0, 0, 0, 0);
                }
            }
        }
    }

    private static int getCoordinateAtLeastAway(int centerCoord, int minDistance, int maxDistance) {
        Random random = new Random();
        int offset = random.nextInt(maxDistance - minDistance + 1) + minDistance;
        return random.nextBoolean() ? centerCoord + offset : centerCoord - offset;
    }
}