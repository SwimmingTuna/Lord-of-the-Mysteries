package net.swimmingtuna.lotm.events.ability_events;

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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.swimmingtuna.lotm.entity.*;
import net.swimmingtuna.lotm.events.custom_events.ModEventFactory;
import net.swimmingtuna.lotm.events.custom_events.ProjectileEvent;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.GameRuleInit;
import net.swimmingtuna.lotm.init.SoundInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.BeyonderAbilityUser;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.DomainOfDecay;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.DomainOfProvidence;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.LuckGifting;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.DreamIntoReality;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionBarrier;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionLocationBlink;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ClientSequenceData;
import net.swimmingtuna.lotm.util.CorruptionAndLuckHandler;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.worldgen.MirrorWorldChunkGenerator;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.*;

import static net.swimmingtuna.lotm.events.ability_events.Calamity.*;
import static net.swimmingtuna.lotm.events.ability_events.Envision.*;
import static net.swimmingtuna.lotm.events.ability_events.Envoirement.*;
import static net.swimmingtuna.lotm.events.ability_events.Monster.*;
import static net.swimmingtuna.lotm.events.ability_events.WindManipulation.*;
import static net.swimmingtuna.lotm.worldgen.dimension.DimensionInit.SPIRIT_WORLD_LEVEL_KEY;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents {

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
                    // Use reflection or other means to set the generator if needed
                    // This part might need additional work depending on your setup
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
        if (player.level().isClientSide() && holder.currentClassMatches(BeyonderClassInit.MONSTER) && sequence <= 8 && player.tickCount % 15 == 0) {
            checkForProjectiles(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickServer(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        if (player.level().isClientSide() || event.phase != TickEvent.Phase.START) return;

        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        Style style = BeyonderUtil.getStyle(player);
        CompoundTag playerPersistentData = player.getPersistentData();
        int sequence = holder.getCurrentSequence();

        handleClientSequenceDataSync(player, holder);
        handleAttributes(player);

        Map<String, Long> executionTimes = new HashMap<>();
        executeTimedTasks(executionTimes, serverLevel, player, holder, playerPersistentData, style, sequence);
        // System.out.println(executionTimes.entrySet().stream().max(Map.Entry.comparingByValue()));
    }

    private static void handleClientSequenceDataSync(Player player, BeyonderHolder holder) {
        if (player.tickCount % 20 == 0) {
            if (holder.getCurrentSequence() != 0 && ClientSequenceData.getCurrentSequence() == 0) {
                ClientSequenceData.setCurrentSequence(-1);
            }
        }
    }

    private static void handleAttributes(Player player) {
        AttributeInstance corruption = player.getAttribute(ModAttributes.CORRUPTION.get());
        AttributeInstance luck = player.getAttribute(ModAttributes.LOTM_LUCK.get());
        AttributeInstance misfortune = player.getAttribute(ModAttributes.MISFORTUNE.get());
        if (corruption == null || luck == null || misfortune == null) {
            throw new IllegalStateException("Required attributes are not initialized.");
        }
    }

    private static void executeTimedTasks(
            Map<String, Long> times,
            ServerLevel serverLevel,
            Player player,
            BeyonderHolder holder,
            CompoundTag playerPersistentData,
            Style style,
            int sequence
    ) {
        runTimedTask(times, "decrementMonsterAttackEvent", () -> decrementMonsterAttackEvent(player));
        runTimedTask(times, "monsterLuckIgnoreMobs", () -> monsterLuckIgnoreMobs(player));
        runTimedTask(times, "monsterLuckPoisonAttacker", () -> monsterLuckPoisonAttacker(player));
        runTimedTask(times, "calamityExplosion", () -> calamityExplosion(player));
        runTimedTask(times, "calamityLightningStorm", () -> calamityLightningStorm(player));
        runTimedTask(times, "calamityUndeadArmy", () -> calamityUndeadArmy(player));
        runTimedTask(times, "corruptionAndLuckManagers", () -> CorruptionAndLuckHandler.corruptionAndLuckManagers(serverLevel,
                player.getAttribute(ModAttributes.MISFORTUNE.get()),
                player.getAttribute(ModAttributes.CORRUPTION.get()),
                player,
                player.getAttribute(ModAttributes.LOTM_LUCK.get()),
                holder,
                sequence));
        runTimedTask(times, "nightmare", () -> nightmare(player, playerPersistentData));
        runTimedTask(times, "calamityIncarnationTornado", () -> calamityIncarnationTornado(playerPersistentData, player));
        runTimedTask(times, "psychologicalInvisibility", () -> psychologicalInvisibility(player, playerPersistentData, holder));
        runTimedTask(times, "monsterDomainIntHandler", () -> monsterDomainIntHandler(player));
        runTimedTask(times, "windManipulationSense", () -> windManipulationSense(playerPersistentData, holder, player));
        runTimedTask(times, "sailorLightningTravel", () -> sailorLightningTravel(player));
        runTimedTask(times, "windManipulationCushion", () -> windManipulationCushion(playerPersistentData, player));
        runTimedTask(times, "windManipulationGuide", () -> windManipulationGuide(playerPersistentData, holder, player));
        runTimedTask(times, "dreamIntoReality", () -> dreamIntoReality(player, holder));
        runTimedTask(times, "consciousnessStroll", () -> consciousnessStroll(playerPersistentData, player));
        runTimedTask(times, "prophesizeTeleportation", () -> prophesizeTeleportation(playerPersistentData, player));
        runTimedTask(times, "projectileEvent", () -> projectileEvent(player, holder));
        runTimedTask(times, "envisionBarrier", () -> envisionBarrier(holder, player, style));
        runTimedTask(times, "envisionLife", () -> envisionLife(player));
        runTimedTask(times, "manipulateMovement", () -> manipulateMovement(player, serverLevel));
        runTimedTask(times, "envisionKingdom", () -> envisionKingdom(playerPersistentData, player, holder, serverLevel));
        runTimedTask(times, "acidicRain", () -> acidicRain(player, sequence));
        runTimedTask(times, "calamityIncarnationTsunami", () -> calamityIncarnationTsunami(playerPersistentData, player, serverLevel));
        runTimedTask(times, "earthquake", () -> earthquake(player, sequence));
        runTimedTask(times, "extremeColdness", () -> extremeColdness(playerPersistentData, holder, player));
        runTimedTask(times, "hurricane", () -> hurricane(playerPersistentData, player));
        runTimedTask(times, "lightningStorm", () -> lightningStorm(player, playerPersistentData, style, holder));
        runTimedTask(times, "matterAccelerationSelf", () -> matterAccelerationSelf(player, holder, style));
        runTimedTask(times, "ragingBlows", () -> ragingBlows(playerPersistentData, holder, player));
        runTimedTask(times, "rainEyes", () -> rainEyes(player));
        runTimedTask(times, "sirenSongs", () -> sirenSongs(playerPersistentData, holder, player, sequence));
        runTimedTask(times, "starOfLightning", () -> starOfLightning(player, playerPersistentData));
        runTimedTask(times, "tsunami", () -> tsunami(playerPersistentData, player));
        runTimedTask(times, "waterSphereCheck", () -> waterSphereCheck(player, serverLevel));
        runTimedTask(times, "windManipulationFlight", () -> windManipulationFlight(player, playerPersistentData));
    }

    private static void runTimedTask(Map<String, Long> times, String taskName, Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        times.put(taskName, endTime - startTime);
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

    private static void sailorLightningTravel(Player player) {
        //SAILOR LIGHTNING TRAVEL
        if (player.getPersistentData().getInt("sailorLightningTravel") >= 1) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 3, 1, false, false));
            player.getPersistentData().putInt("sailorLightningTravel", player.getPersistentData().getInt("sailorLightningTravel") - 1);
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

    private static void luckDenial(LivingEntity livingEntity) {
        CompoundTag tag = livingEntity.getPersistentData();
        AttributeInstance luck = livingEntity.getAttribute(ModAttributes.LOTM_LUCK.get());
        AttributeInstance misfortune = livingEntity.getAttribute(ModAttributes.MISFORTUNE.get());
        double luckDenialTimer = tag.getDouble("luckDenialTimer");
        double luckDenialLuck = tag.getDouble("luckDenialLuck");
        double luckDenialMisfortune = tag.getDouble("luckDenialMisfortune");
        double misfortuneAmount = misfortune.getBaseValue();
        double luckAmount = luck.getBaseValue();
        if (luckDenialTimer >= 1) {
            tag.putDouble("luckDenialTimer", luckDenialTimer - 1);
            if (luckAmount >= luckDenialLuck) {
                luck.setBaseValue(luckAmount);
            }
            if (misfortuneAmount <= luckDenialMisfortune) {
                misfortune.setBaseValue(luckDenialMisfortune);
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
        if (!event.getEntity().level().isClientSide() && event.getEntity().getPersistentData().getInt("inMonsterProvidenceDomain") >= 1) {
                int droppedExperience = event.getDroppedExperience();
                event.setDroppedExperience((int) (droppedExperience * 1.5));
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
                        if (!(radius >= sphereDistance)) {
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
                        if ( (sphereDistance > maxRemovalRadius) || (sphereDistance < minRemovalRadius)) {
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
                        if (sphereDistance > 6) {
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


    @SubscribeEvent
    public static void stunEffect(LivingEntityUseItemEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            if (entity.hasEffect(ModEffects.STUN.get())) {
                event.setCanceled(true);
            }
            if (entity instanceof Player player) {
                CompoundTag tag = player.getPersistentData();
                if (tag.getInt("luckIgnoreAbility") >= 1 && player.getMainHandItem().getItem() instanceof SimpleAbilityItem) {
                    event.setCanceled(true);
                    tag.putInt("luckIgnoreAbility", tag.getInt("luckIgnoreAbility") - 1);
                    player.sendSystemMessage(Component.literal("How unlucky! You made a mistake using " + player.getMainHandItem().getDisplayName().getString() + " and it didn't work").withStyle(BeyonderUtil.getStyle(player)));
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
                tag.putInt("EntityDemise", 0);
                messageCounter = 0;
                tag.putInt("MessageCounter", 0);
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
                    int demise = tag.getInt("EntityDemise");
                    int nonDemise = tag.getInt("NonDemise");

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
                    if (demise % 20 == 0) {
                        messageCounter++;
                        tag.putInt("MessageCounter", messageCounter);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in" + (20 - messageCounter) + " seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));

                    }
                    if (nonDemise >= 20 && nonDemise <= 180 && nonDemise % 20 == 0) {
                        int standStillSecondsLeft = (200 - nonDemise) / 20;
                        entity.sendSystemMessage(Component.literal("You need to stand still for " + standStillSecondsLeft + " more seconds").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
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
                if (stormSeal % 20 == 0 && entity instanceof Player player) {
                        int sealSeconds = stormSeal / 20;
                        player.displayClientMessage(Component.literal("You are stuck in the storm seal for " + sealSeconds + " seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
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

    private static void decrementMonsterAttackEvent(Player pPlayer) {
        if (pPlayer.getPersistentData().getInt("attackedMonster") >= 1) {
            pPlayer.getPersistentData().putInt("attackedMonster", pPlayer.getPersistentData().getInt("attackedMonster") - 1);
        }
    }

    @SubscribeEvent
    public static void attackEvent(LivingAttackEvent event) {
        LivingEntity attacked = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker != null && !attacker.level().isClientSide() && attacked instanceof Player pPlayer) {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
                    if (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 5 && attacker instanceof LivingEntity) {
                            attacker.getPersistentData().putInt("attackedMonster", 100);
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
                    entity.sendSystemMessage(Component.literal("explosion"));
                    if (calamityExplosionOccurrenceDamage >= 1) {
                        event.setAmount(event.getAmount() / 2);
                    }
                }
                if (entitySource instanceof LightningEntity) {
                    entity.sendSystemMessage(Component.literal("lightningentity"));
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
                if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
                    Random random = new Random();
                    if (random.nextInt(10) == 0) {
                        event.setCanceled(true);
                    }
                }

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

            //STORM SEAL
            if (tag.getInt("inStormSeal") >= 1) {
                event.setCanceled(true);
                System.out.println("death canceled");
                entity.setHealth(5.0f);
            }

            if (entity instanceof Player player) {

                byte[] keysClicked = new byte[5]; // Example size; match this to the intended array size
                player.getPersistentData().putByteArray("keysClicked", keysClicked);

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

    private static void dodgeProjectiles(Player pPlayer) {
        if (pPlayer.getPersistentData().getInt("windMovingProjectilesCounter") >= 1) {
            if (!pPlayer.level().isClientSide()) {
                for (Projectile projectile : pPlayer.level().getEntitiesOfClass(Projectile.class, pPlayer.getBoundingBox().inflate(200))) {
                    if (projectile.getPersistentData().getInt("windDodgeProjectilesCounter") == 0) {
                        if (projectile instanceof Arrow arrow && arrow.inGround) {
                            continue;
                        }
                        float scale = ScaleTypes.BASE.getScaleData(projectile).getScale();
                        double maxDistance = 10 * scale;
                        double deltaX = Math.abs(projectile.getX() - pPlayer.getX());
                        double deltaY = Math.abs(projectile.getY() - pPlayer.getY());
                        double deltaZ = Math.abs(projectile.getZ() - pPlayer.getZ());
                        if (deltaX <= maxDistance || deltaY <= maxDistance || deltaZ <= maxDistance && projectile.getOwner() != pPlayer) {
                            double mathRandom = (Math.random() + .4) - 0.2;
                            double x = projectile.getDeltaMovement().x() + mathRandom;
                            double y = projectile.getDeltaMovement().y() + mathRandom;
                            double z = projectile.getDeltaMovement().z() + mathRandom;
                            projectile.setDeltaMovement(x, y, z);
                            projectile.hurtMarked = true;
                            projectile.getPersistentData().putInt("windDodgeProjectilesCounter", 40);
                            pPlayer.getPersistentData().putInt("windMovingProjectilesCounter", pPlayer.getPersistentData().getInt("windMovingProjectilesCounter") - 1);
                            pPlayer.displayClientMessage(Component.literal("A gust of wind moved a projectile headed towards you").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE), true);
                        }
                    } else
                        projectile.getPersistentData().putInt("windDodgeProjectilesCounter", projectile.getPersistentData().getInt("windDodgeProjectilesCounter") - 1);
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
        int particleInterval = 5; // Only spawn a particle every 5 points
        for (int i = 0; i < points.size() - 1; i += particleInterval) {
            Vec3 start = points.get(i);
            Vec3 end = i + particleInterval < points.size() ? points.get(i + particleInterval) : points.get(points.size() - 1);
            Vec3 direction = end.subtract(start).normalize();
            double distance = start.distanceTo(end);
            Vec3 particlePosition = start.add(direction.scale(distance / 2));
            level.sendParticles(player, DustParticleOptions.REDSTONE, true, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0, 0, 0);
        }
    }

    public static List<Vec3> predictProjectileTrajectory(Projectile projectile, LivingEntity player) {
        List<Vec3> trajectory = new ArrayList<>();
        Vec3 projectilePos = projectile.position();
        Vec3 projectileDelta = projectile.getDeltaMovement();

        boolean isArrow = projectile instanceof AbstractArrow;

        trajectory.add(projectilePos);

        int maxIterations = 1000; // Increased for a longer trajectory
        double maxDistance = 100.0; // Maximum distance to calculate the trajectory

        for (int i = 0; i < maxIterations; i++) {
            projectilePos = projectilePos.add(projectileDelta);
            trajectory.add(projectilePos);

            // Check if the block at the projectile's position is not air
            if (projectile instanceof Arrow arrow) {
                if (arrow.inGround) {
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
        double beyonderHealth = holder.getCurrentClass().maxHealth().get(sequence);
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(beyonderHealth);
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
                AttributeInstance luck = livingEntity.getAttribute(ModAttributes.LOTM_LUCK.get());
                AttributeInstance misfortune = livingEntity.getAttribute(ModAttributes.LOTM_LUCK.get());
                AttributeInstance particle = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER.get());
                AttributeInstance particle1 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER1.get());
                AttributeInstance particle2 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER2.get());
                AttributeInstance particle3 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER3.get());
                AttributeInstance particle4 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
                AttributeInstance particle5 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER5.get());
                AttributeInstance particle6 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER6.get());
                AttributeInstance particle7 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER7.get());
                AttributeInstance particle8 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER8.get());
                AttributeInstance particle9 = livingEntity.getAttribute(ModAttributes.PARTICLE_HELPER9.get());
                AttributeInstance nightmare = livingEntity.getAttribute(ModAttributes.NIGHTMARE.get());
                AttributeInstance armorinvis = livingEntity.getAttribute(ModAttributes.ARMORINVISIBLITY.get());
                AttributeInstance mobSpirituality = livingEntity.getAttribute(ModAttributes.MOB_SPIRITUALITY.get());
                AttributeInstance dir = livingEntity.getAttribute(ModAttributes.DIR.get());
                if (!(livingEntity instanceof Player)) {
                    if (mobSpirituality == null) {
                        return;
                    }
                    mobSpirituality.setBaseValue(0);
                }
                if (nightmare == null) {
                    return;
                }
                nightmare.setBaseValue(0);
                if (dir == null) {
                    return;
                }
                dir.setBaseValue(1);
                if (armorinvis == null) {
                    return;
                }
                armorinvis.setBaseValue(0);
                if (luck == null) {
                    return;
                }
                luck.setBaseValue(0);
                if (misfortune == null) {
                    return;
                }
                misfortune.setBaseValue(0);

                if (particle == null) {
                    return;
                }
                particle.setBaseValue(0);
                if (particle1 == null) {
                    return;
                }
                particle1.setBaseValue(0);
                if (particle2 == null) {
                    return;
                }
                particle2.setBaseValue(0);
                if (particle3 == null) {
                    return;
                }
                particle3.setBaseValue(0);
                if (particle4 == null) {
                    return;
                }
                particle4.setBaseValue(0);
                if (particle5 == null) {
                    return;
                }
                particle5.setBaseValue(0);
                if (particle6 == null) {
                    return;
                }
                particle6.setBaseValue(0);
                if (particle7 == null) {
                    return;
                }
                particle7.setBaseValue(0);
                if (particle8 == null) {
                    return;
                }
                particle8.setBaseValue(0);
                if (particle9 == null) {
                    return;
                }
                particle9.setBaseValue(0);
                if (livingEntity instanceof PlayerMobEntity playerMobEntity && !playerMobEntity.level().getLevelData().getGameRules().getBoolean(GameRuleInit.NPC_SHOULD_SPAWN)) {
                        event.setCanceled(true);
                    }

            }
        }
    }

}