package net.swimmingtuna.lotm.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.EndStoneEntity;
import net.swimmingtuna.lotm.entity.NetherrackEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Ability;
import net.swimmingtuna.lotm.item.BeyonderAbilities.BeyonderAbilityUser;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.*;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.*;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.LeftClickC2S;
import net.swimmingtuna.lotm.networking.packet.MatterAccelerationBlockC2S;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BeyonderUtil {

    public static Projectile getProjectiles(Player player) {
        if (player.level().isClientSide()) {
            return null;
        }
        List<Projectile> projectiles = player.level().getEntitiesOfClass(Projectile.class, player.getBoundingBox().inflate(30));
        for (Projectile projectile : projectiles) {
            if (projectile.getOwner() == player && projectile.tickCount > 8 && projectile.tickCount < 50) {
                return projectile;
            }
        }
        return null;
    }
    public static Projectile getLivingEntitiesProjectile(LivingEntity player) {
        if (player.level().isClientSide()) {
            return null;
        }
        List<Projectile> projectiles = player.level().getEntitiesOfClass(Projectile.class, player.getBoundingBox().inflate(30));
        for (Projectile projectile : projectiles) {
            if (projectile.getOwner() == player && projectile.tickCount > 8 && projectile.tickCount < 50) {
                return projectile;
            }
        }
        return null;
    }

    public static DamageSource genericSource(Entity entity) {
        Level level = entity.level();
        Holder<DamageType> damageTypeHolder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC);
        return new DamageSource(damageTypeHolder, entity, entity, entity.getOnPos().getCenter());
    }

    public static DamageSource explosionSource(Entity entity) {
        Level level = entity.level();
        Holder<DamageType> damageTypeHolder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.EXPLOSION);
        return new DamageSource(damageTypeHolder, entity, entity, entity.getOnPos().getCenter());
    }

    public static DamageSource fallSource(Entity entity) {
        Level level = entity.level();
        Holder<DamageType> damageTypeHolder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FALL);
        return new DamageSource(damageTypeHolder, entity, entity, entity.getOnPos().getCenter());
    }

    public static DamageSource lightningSource(Entity entity) {
        Level level = entity.level();
        Holder<DamageType> damageTypeHolder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT);
        return new DamageSource(damageTypeHolder, entity, entity, entity.getOnPos().getCenter());
    }

    public static StructurePlaceSettings getStructurePlaceSettings(BlockPos pos) {
        BoundingBox boundingBox = new BoundingBox(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX() + 160,
                pos.getY() + 97,
                pos.getZ() + 265
        );
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setRotation(Rotation.NONE);
        settings.setMirror(Mirror.NONE);
        settings.setRotationPivot(pos);
        settings.setBoundingBox(boundingBox);
        return settings;
    }

    public static List<Item> getAbilities(Player player) {
        List<Item> abilityNames = new ArrayList<>();
        if (player.level().isClientSide()) {
            return abilityNames;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int sequence = holder.getCurrentSequence();
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
            if (sequence <= 8) {
                abilityNames.add(ItemInit.MIND_READING.get());
            }
            if (sequence <= 7) {
                abilityNames.add(ItemInit.AWE.get());
                abilityNames.add(ItemInit.FRENZY.get());
                abilityNames.add(ItemInit.PLACATE.get());
            }
            if (sequence <= 6) {
                abilityNames.add(ItemInit.PSYCHOLOGICAL_INVISIBILITY.get());
                abilityNames.add(ItemInit.BATTLE_HYPNOTISM.get());
            }
            if (sequence <= 5) {
                abilityNames.add(ItemInit.GUIDANCE.get());
                abilityNames.add(ItemInit.ALTERATION.get());
                abilityNames.add(ItemInit.DREAM_WALKING.get());
                abilityNames.add(ItemInit.NIGHTMARE.get());
            }
            if (sequence <= 4) {
                abilityNames.add(ItemInit.APPLY_MANIPULATION.get());
                abilityNames.add(ItemInit.MANIPULATE_MOVEMENT.get());
                abilityNames.add(ItemInit.MANIPULATE_FONDNESS.get());
                abilityNames.add(ItemInit.MANIPULATE_EMOTION.get());
                abilityNames.add(ItemInit.MENTAL_PLAGUE.get());
                abilityNames.add(ItemInit.MIND_STORM.get());
                abilityNames.add(ItemInit.DRAGON_BREATH.get());
            }
            if (sequence <= 3) {
                abilityNames.add(ItemInit.CONSCIOUSNESS_STROLL.get());
                abilityNames.add(ItemInit.PLAGUE_STORM.get());
                abilityNames.add(ItemInit.DREAM_WEAVING.get());
            }
            if (sequence <= 2) {
                abilityNames.add(ItemInit.DISCERN.get());
                abilityNames.add(ItemInit.DREAM_INTO_REALITY.get());
            }
            if (sequence <= 1) {
                abilityNames.add(ItemInit.PROPHESIZE_DEMISE.get());
                abilityNames.add(ItemInit.PROPHESIZE_TELEPORT_PLAYER.get());
                abilityNames.add(ItemInit.METEOR_SHOWER.get());
                abilityNames.add(ItemInit.METEOR_NO_LEVEL_SHOWER.get());
            }
            if (sequence <= 0) {
                abilityNames.add(ItemInit.ENVISION_BARRIER.get());
                abilityNames.add(ItemInit.ENVISION_DEATH.get());
                abilityNames.add(ItemInit.ENVISIONHEALTH.get());
                abilityNames.add(ItemInit.ENVISION_KINGDOM.get());
                abilityNames.add(ItemInit.ENVISION_LIFE.get());
                abilityNames.add(ItemInit.ENVISION_LOCATION.get());
                abilityNames.add(ItemInit.ENVISION_LOCATION_BLINK.get());
                abilityNames.add(ItemInit.ENVISION_WEATHER.get());
            }
        }

        if (holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
            if (sequence <= 8) {
                abilityNames.add(ItemInit.RAGING_BLOWS.get());
            }
            if (sequence <= 7) {
                abilityNames.add(ItemInit.ENABLE_OR_DISABLE_LIGHTNING.get());
                abilityNames.add(ItemInit.AQUEOUS_LIGHT_PUSH.get());
                abilityNames.add(ItemInit.AQUEOUS_LIGHT_PULL.get());
                abilityNames.add(ItemInit.AQUEOUS_LIGHT_DROWN.get());
                abilityNames.add(ItemInit.SAILORPROJECTILECTONROL.get());
            }
            if (sequence <= 6) {
                abilityNames.add(ItemInit.WIND_MANIPULATION_BLADE.get());
                abilityNames.add(ItemInit.WIND_MANIPULATION_CUSHION.get());
                abilityNames.add(ItemInit.WIND_MANIPULATION_FLIGHT.get());
                abilityNames.add(ItemInit.WIND_MANIPULATION_SENSE.get());
            }
            if (sequence <= 5) {
                abilityNames.add(ItemInit.SAILOR_LIGHTNING.get());
                abilityNames.add(ItemInit.SIREN_SONG_HARM.get());
                abilityNames.add(ItemInit.SIREN_SONG_STRENGTHEN.get());
                abilityNames.add(ItemInit.SIREN_SONG_WEAKEN.get());
                abilityNames.add(ItemInit.SIREN_SONG_STUN.get());
                abilityNames.add(ItemInit.ACIDIC_RAIN.get());
                abilityNames.add(ItemInit.WATER_SPHERE.get());
            }
            if (sequence <= 4) {
                abilityNames.add(ItemInit.TSUNAMI.get());
                abilityNames.add(ItemInit.TSUNAMI_SEAL.get());
                abilityNames.add(ItemInit.HURRICANE.get());
                abilityNames.add(ItemInit.TORNADO.get());
                abilityNames.add(ItemInit.EARTHQUAKE.get());
                abilityNames.add(ItemInit.ROAR.get());
            }
            if (sequence <= 3) {
                abilityNames.add(ItemInit.AQUATIC_LIFE_MANIPULATION.get());
                abilityNames.add(ItemInit.LIGHTNING_STORM.get());
                abilityNames.add(ItemInit.LIGHTNING_BRANCH.get());
                abilityNames.add(ItemInit.SONIC_BOOM.get());
                abilityNames.add(ItemInit.THUNDER_CLAP.get());
            }
            if (sequence <= 2) {
                abilityNames.add(ItemInit.RAIN_EYES.get());
                abilityNames.add(ItemInit.VOLCANIC_ERUPTION.get());
                abilityNames.add(ItemInit.EXTREME_COLDNESS.get());
                abilityNames.add(ItemInit.LIGHTNING_BALL.get());
            }
            if (sequence <= 1) {
                abilityNames.add(ItemInit.LIGHTNING_BALL_ABSORB.get());
                abilityNames.add(ItemInit.SAILOR_LIGHTNING_TRAVEL.get());
                abilityNames.add(ItemInit.STAR_OF_LIGHTNING.get());
                abilityNames.add(ItemInit.LIGHTNING_REDIRECTION.get());
            }
            if (sequence <= 0) {
                abilityNames.add(ItemInit.STORM_SEAL.get());
                abilityNames.add(ItemInit.WATER_COLUMN.get());
                abilityNames.add(ItemInit.MATTER_ACCELERATION_SELF.get());
                abilityNames.add(ItemInit.MATTER_ACCELERATION_BLOCKS.get());
                abilityNames.add(ItemInit.MATTER_ACCELERATION_ENTITIES.get());
                abilityNames.add(ItemInit.TYRANNY.get());
            }
        }
        return abilityNames;
    }

    private static String getItemName(Item item) {
        return I18n.get(item.getDescriptionId()).toLowerCase();
    }

    private static final String REGISTERED_ABILITIES_KEY = "RegisteredAbilities";

    public static void useAbilityByNumber(Player player, int abilityNumber, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return;
        }
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(REGISTERED_ABILITIES_KEY, Tag.TAG_COMPOUND)) {
            player.sendSystemMessage(Component.literal("No registered abilities found."));
            return;
        }
        CompoundTag registeredAbilities = persistentData.getCompound(REGISTERED_ABILITIES_KEY);
        if (!registeredAbilities.contains(String.valueOf(abilityNumber), Tag.TAG_STRING)) {
            player.sendSystemMessage(Component.literal("Ability " + abilityNumber + " not found."));
            return;
        }
        ResourceLocation resourceLocation = new ResourceLocation(registeredAbilities.getString(String.valueOf(abilityNumber)));
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item == null) {
            player.sendSystemMessage(Component.literal("Item not found in registry for ability " + abilityNumber + " with resource location: " + resourceLocation));
            return;
        }
        if (!(item instanceof Ability ability)) {
            player.sendSystemMessage(Component.literal("Registered ability ").append(item.getDescription()).append(" for ability number " + abilityNumber + " is not an ability."));
            return;
        }
        if (player.getCooldowns().isOnCooldown(item)) {
            player.sendSystemMessage(Component.literal("Ability ").append(item.getDescription()).append(" is on cooldown!"));
            return;
        }
        ability.useAbility(player.level(), player, hand);
    }

    public static Style getStyle(Player player) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder.getCurrentClass() != null) {
            return Style.EMPTY.withBold(true).withColor(holder.getCurrentClass().getColorFormatting());
        }
        return Style.EMPTY;
    }

    public static void mentalDamage(Player source, Player hurtEntity, int damage) { //can make it so that with useOn, sets shiftKeyDown to true for player
        BeyonderHolder sourceHolder = BeyonderHolderAttacher.getHolderUnwrap(source);
        BeyonderHolder hurtHolder = BeyonderHolderAttacher.getHolderUnwrap(hurtEntity);
        float x = Math.min(damage, damage * (hurtHolder.getMentalStrength() / sourceHolder.getMentalStrength()));
        hurtEntity.hurt(hurtEntity.damageSources().magic(), x);
    }

    public static float mentalInt(Player source, Player hurtEntity, int mentalInt) {
        BeyonderHolder sourceHolder = BeyonderHolderAttacher.getHolderUnwrap(source);
        BeyonderHolder hurtHolder = BeyonderHolderAttacher.getHolderUnwrap(hurtEntity);
        float x = Math.min(mentalInt, mentalInt * (hurtHolder.getMentalStrength() / sourceHolder.getMentalStrength()));
        return x;
    }

    public static void leftClickEmpty(Player pPlayer) {
        Style style = BeyonderUtil.getStyle(pPlayer);
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty()) {
            if (heldItem.getItem() instanceof BeyonderAbilityUser) {
                LOTMNetworkHandler.sendToServer(new LeftClickC2S()); //DIFFERENT FOR LEFT CLICK BLOCK

            } else if (heldItem.getItem() instanceof AqueousLightPush) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.AQUEOUS_LIGHT_PULL.get())));

            } else if (heldItem.getItem() instanceof AqueousLightPull) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.AQUEOUS_LIGHT_DROWN.get())));

            } else if (heldItem.getItem() instanceof AqueousLightDrown) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.AQUEOUS_LIGHT_PUSH.get())));

            } else if (heldItem.getItem() instanceof Hurricane) {
                LOTMNetworkHandler.sendToServer(new LeftClickC2S());

            } else if (heldItem.getItem() instanceof LightningStorm) {
                LOTMNetworkHandler.sendToServer(new LeftClickC2S());

            } else if (heldItem.getItem() instanceof MatterAccelerationBlocks) {
                LOTMNetworkHandler.sendToServer(new MatterAccelerationBlockC2S());

            } else if (heldItem.getItem() instanceof MatterAccelerationEntities) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.MATTER_ACCELERATION_SELF.get())));

            } else if (heldItem.getItem() instanceof MatterAccelerationSelf) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.MATTER_ACCELERATION_BLOCKS.get())));

            } else if (heldItem.getItem() instanceof WindManipulationBlade) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.WIND_MANIPULATION_CUSHION.get())));

            } else if (heldItem.getItem() instanceof WindManipulationCushion) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.WIND_MANIPULATION_FLIGHT.get())));

            } else if (heldItem.getItem() instanceof WindManipulationFlight) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.WIND_MANIPULATION_SENSE.get())));

            } else if (heldItem.getItem() instanceof WindManipulationSense) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.WIND_MANIPULATION_BLADE.get())));

            } else if (heldItem.getItem() instanceof ApplyManipulation) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.MANIPULATE_EMOTION.get())));

            } else if (heldItem.getItem() instanceof ManipulateEmotion) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.MANIPULATE_MOVEMENT.get())));

            } else if (heldItem.getItem() instanceof ManipulateMovement) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.MANIPULATE_FONDNESS.get())));

            } else if (heldItem.getItem() instanceof ManipulateFondness) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.APPLY_MANIPULATION.get())));

            } else if (heldItem.getItem() instanceof EnvisionBarrier) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.ENVISION_DEATH.get())));

            } else if (heldItem.getItem() instanceof EnvisionDeath) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.ENVISIONHEALTH.get())));

            } else if (heldItem.getItem() instanceof EnvisionHealth) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.ENVISION_LIFE.get())));

            } else if (heldItem.getItem() instanceof EnvisionLife) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.ENVISION_WEATHER.get())));

            } else if (heldItem.getItem() instanceof EnvisionWeather) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.ENVISION_LOCATION.get())));

            } else if (heldItem.getItem() instanceof EnvisionLocation) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.ENVISION_LOCATION_BLINK.get())));

            } else if (heldItem.getItem() instanceof EnvisionLocationBlink) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.ENVISION_KINGDOM.get())));

            } else if (heldItem.getItem() instanceof EnvisionKingdom) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.ENVISION_BARRIER.get())));

            } else if (heldItem.getItem() instanceof MeteorShower) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.METEOR_NO_LEVEL_SHOWER.get())));

            } else if (heldItem.getItem() instanceof MeteorNoLevelShower) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.METEOR_SHOWER.get())));

            } else if (heldItem.getItem() instanceof ProphesizeDemise) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.PROPHESIZE_TELEPORT_BLOCK.get())));

            } else if (heldItem.getItem() instanceof ProphesizeTeleportBlock) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.PROPHESIZE_TELEPORT_PLAYER.get())));

            } else if (heldItem.getItem() instanceof ProphesizeTeleportPlayer) {
                LOTMNetworkHandler.sendToServer(new UpdateItemInHandC2S(activeSlot, new ItemStack(ItemInit.PROPHESIZE_DEMISE.get())));

            }
        }
    }

    public static void leftClickBlock(Player pPlayer) {
        Style style = BeyonderUtil.getStyle(pPlayer);
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty()) {
            if (heldItem.getItem() instanceof AqueousLightPush) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.AQUEOUS_LIGHT_PULL.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof AqueousLightPull) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.AQUEOUS_LIGHT_DROWN.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof AqueousLightDrown) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.AQUEOUS_LIGHT_PUSH.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof Hurricane) {
                CompoundTag tag = pPlayer.getPersistentData();
                boolean sailorHurricaneRain = tag.getBoolean("sailorHurricaneRain");
                if (sailorHurricaneRain) {
                    pPlayer.displayClientMessage(Component.literal("Hurricane will only cause rain").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                    tag.putBoolean("sailorHurricaneRain", false);
                } else {
                    pPlayer.displayClientMessage(Component.literal("Hurricane cause lightning, tornadoes, and rain").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                    tag.putBoolean("sailorHurricaneRain", true);
                }
            } else if (heldItem.getItem() instanceof LightningStorm) {
                CompoundTag tag = pPlayer.getPersistentData();
                double distance = tag.getDouble("sailorLightningStormDistance");
                tag.putDouble("sailorLightningStormDistance", (int) (distance + 30));
                pPlayer.sendSystemMessage(Component.literal("Storm Radius Is" + distance).withStyle(style));
                if (distance > 300) {
                    tag.putDouble("sailorLightningStormDistance", 0);
                }


            } else if (heldItem.getItem() instanceof MatterAccelerationBlocks) {
                MatterAccelerationBlocks.leftClick(pPlayer);
            } else if (heldItem.getItem() instanceof MatterAccelerationEntities) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.MATTER_ACCELERATION_SELF.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof MatterAccelerationSelf) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.MATTER_ACCELERATION_BLOCKS.get())));
                heldItem.shrink(1);
            }  else if (heldItem.getItem() instanceof WindManipulationBlade) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.WIND_MANIPULATION_CUSHION.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof WindManipulationCushion) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.WIND_MANIPULATION_FLIGHT.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof WindManipulationFlight) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.WIND_MANIPULATION_SENSE.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof WindManipulationSense) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.WIND_MANIPULATION_BLADE.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof ApplyManipulation) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.MANIPULATE_EMOTION.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof ManipulateEmotion) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.MANIPULATE_MOVEMENT.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof ManipulateMovement) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.MANIPULATE_FONDNESS.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof ManipulateFondness) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.APPLY_MANIPULATION.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof EnvisionBarrier) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.ENVISION_DEATH.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof EnvisionDeath) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.ENVISIONHEALTH.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof EnvisionHealth) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.ENVISION_LIFE.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof EnvisionLife) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.ENVISION_WEATHER.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof EnvisionWeather) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.ENVISION_LOCATION.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof EnvisionLocation) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.ENVISION_LOCATION_BLINK.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof EnvisionLocationBlink) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.ENVISION_KINGDOM.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof EnvisionKingdom) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.ENVISION_BARRIER.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof MeteorShower) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.METEOR_NO_LEVEL_SHOWER.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof MeteorNoLevelShower) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.METEOR_SHOWER.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof ProphesizeDemise) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.PROPHESIZE_TELEPORT_BLOCK.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof ProphesizeTeleportBlock) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.PROPHESIZE_TELEPORT_PLAYER.get())));
                heldItem.shrink(1);
            } else if (heldItem.getItem() instanceof ProphesizeTeleportPlayer) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack((ItemInit.PROPHESIZE_DEMISE.get())));
                heldItem.shrink(1);
            }
        }
    }
}
