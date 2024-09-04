package net.swimmingtuna.lotm.REQUEST_FILES;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.ArrayList;
import java.util.List;

public class BeyonderUtil {

    public static Projectile getProjectiles(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            for (Projectile projectile : pPlayer.level().getEntitiesOfClass(Projectile.class, pPlayer.getBoundingBox().inflate(30))) {
                if (projectile.getOwner() == pPlayer) {
                    if (projectile.tickCount > 8 && projectile.tickCount < 50) {
                        return projectile;
                    }
                }
            }
        }
        return null;
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

    public static List<String> getAbilities(Player pPlayer) {
        List<String> abilityNames = new ArrayList<>();
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            int sequence = holder.getCurrentSequence();
            if (holder.isSpectatorClass()) {
                if (sequence <= 8) {
                    abilityNames.add(getItemName(ItemInit.MindReading.get()));
                }
                if (sequence <= 7) {
                    abilityNames.add(getItemName(ItemInit.Awe.get()));
                    abilityNames.add(getItemName(ItemInit.Frenzy.get()));
                    abilityNames.add(getItemName(ItemInit.Placate.get()));
                }
                if (sequence <= 6) {
                    abilityNames.add(getItemName(ItemInit.PsychologicalInvisibility.get()));
                    abilityNames.add(getItemName(ItemInit.BattleHypnotism.get()));
                }
                if (sequence <= 5) {
                    abilityNames.add(getItemName(ItemInit.Guidance.get()));
                    abilityNames.add(getItemName(ItemInit.Alteration.get()));
                    abilityNames.add(getItemName(ItemInit.DreamWalking.get()));
                    abilityNames.add(getItemName(ItemInit.Nightmare.get()));
                }
                if (sequence <= 4) {
                    abilityNames.add(getItemName(ItemInit.ApplyManipulation.get()));
                    abilityNames.add(getItemName(ItemInit.ManipulateMovement.get()));
                    abilityNames.add(getItemName(ItemInit.ManipulateFondness.get()));
                    abilityNames.add(getItemName(ItemInit.ManipulateEmotion.get()));
                    abilityNames.add(getItemName(ItemInit.MentalPlague.get()));
                    abilityNames.add(getItemName(ItemInit.MindStorm.get()));
                    abilityNames.add(getItemName(ItemInit.DragonBreath.get()));
                }
                if (sequence <= 3) {
                    abilityNames.add(getItemName(ItemInit.ConsciousnessStroll.get()));
                    abilityNames.add(getItemName(ItemInit.PlagueStorm.get()));
                    abilityNames.add(getItemName(ItemInit.DreamWeaving.get()));
                }
                if (sequence <= 2) {
                    abilityNames.add(getItemName(ItemInit.Discern.get()));
                    abilityNames.add(getItemName(ItemInit.DreamIntoReality.get()));
                }
                if (sequence <= 1) {
                    abilityNames.add(getItemName(ItemInit.ProphesizeDemise.get()));
                    abilityNames.add(getItemName(ItemInit.ProphesizeTeleportPlayer.get()));
                    abilityNames.add(getItemName(ItemInit.MeteorShower.get()));
                    abilityNames.add(getItemName(ItemInit.MeteorNoLevelShower.get()));
                }
                if (sequence <= 0) {
                    abilityNames.add(getItemName(ItemInit.EnvisionBarrier.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionDeath.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionHealth.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionKingdom.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionLife.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionLocation.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionLocationBlink.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionWeather.get()));
                }
            }

            if (holder.isSailorClass()) {
                if (sequence <= 8) {
                    abilityNames.add(getItemName(ItemInit.RagingBlows.get()));
                }
                if (sequence <= 7) {
                    abilityNames.add(getItemName(ItemInit.EnableOrDisableLightning.get()));
                    abilityNames.add(getItemName(ItemInit.AqueousLightPush.get()));
                    abilityNames.add(getItemName(ItemInit.AqueousLightPull.get()));
                    abilityNames.add(getItemName(ItemInit.AqueousLightDrown.get()));
                }
                if (sequence <= 6) {
                    abilityNames.add(getItemName(ItemInit.WindManipulationBlade.get()));
                    abilityNames.add(getItemName(ItemInit.WindManipulationCushion.get()));
                    abilityNames.add(getItemName(ItemInit.WindManipulationFlight.get()));
                    abilityNames.add(getItemName(ItemInit.WindManipulationSense.get()));
                }
                if (sequence <= 5) {
                    abilityNames.add(getItemName(ItemInit.SailorLightning.get()));
                    abilityNames.add(getItemName(ItemInit.SirenSongHarm.get()));
                    abilityNames.add(getItemName(ItemInit.SirenSongStrengthen.get()));
                    abilityNames.add(getItemName(ItemInit.SirenSongWeaken.get()));
                    abilityNames.add(getItemName(ItemInit.SirenSongStun.get()));
                    abilityNames.add(getItemName(ItemInit.AcidicRain.get()));
                    abilityNames.add(getItemName(ItemInit.WaterSphere.get()));
                }
                if (sequence <= 4) {
                    abilityNames.add(getItemName(ItemInit.Tsunami.get()));
                    abilityNames.add(getItemName(ItemInit.TsunamiSeal.get()));
                    abilityNames.add(getItemName(ItemInit.Hurricane.get()));
                    abilityNames.add(getItemName(ItemInit.Tornado.get()));
                    abilityNames.add(getItemName(ItemInit.Earthquake.get()));
                    abilityNames.add(getItemName(ItemInit.Roar.get()));
                }
                if (sequence <= 3) {
                    abilityNames.add(getItemName(ItemInit.AquaticLifeManipulation.get()));
                    abilityNames.add(getItemName(ItemInit.LightningStorm.get()));
                    abilityNames.add(getItemName(ItemInit.LightningBranch.get()));
                    abilityNames.add(getItemName(ItemInit.SonicBoom.get()));
                    abilityNames.add(getItemName(ItemInit.ThunderClap.get()));
                }
                if (sequence <= 2) {
                    abilityNames.add(getItemName(ItemInit.RainEyes.get()));
                    abilityNames.add(getItemName(ItemInit.VolcanicEruption.get()));
                    abilityNames.add(getItemName(ItemInit.ExtremeColdness.get()));
                    abilityNames.add(getItemName(ItemInit.LightningBall.get()));
                }
                if (sequence <= 1) {
                    abilityNames.add(getItemName(ItemInit.LightningBallAbsorb.get()));
                    abilityNames.add(getItemName(ItemInit.SailorLightningTravel.get()));
                    abilityNames.add(getItemName(ItemInit.StarOfLightning.get()));
                    abilityNames.add(getItemName(ItemInit.LightningRedirection.get()));
                }
                if (sequence <= 0) {
                    abilityNames.add(getItemName(ItemInit.StormSeal.get()));
                    abilityNames.add(getItemName(ItemInit.WaterColumn.get()));
                    abilityNames.add(getItemName(ItemInit.MatterAccelerationSelf.get()));
                    abilityNames.add(getItemName(ItemInit.MatterAccelerationBlocks.get()));
                    abilityNames.add(getItemName(ItemInit.MatterAccelerationEntities.get()));
                    abilityNames.add(getItemName(ItemInit.Tyranny.get()));
                }
            }
        }
        return abilityNames;
    }

    private static String getItemName(Item item) {
        return I18n.get(item.getDescriptionId()).toLowerCase();
    }
    private static final String REGISTERED_ABILITIES_KEY = "RegisteredAbilities";

    public static void useAbility1(Player player) { useAbilityByNumber(player, 1); }
    public static void useAbility2(Player player) { useAbilityByNumber(player, 2); }
    public static void useAbility3(Player player) { useAbilityByNumber(player, 3); }
    public static void useAbility4(Player player) { useAbilityByNumber(player, 4); }
    public static void useAbility5(Player player) { useAbilityByNumber(player, 5); }
    public static void useAbility6(Player player) { useAbilityByNumber(player, 6); }
    public static void useAbility7(Player player) { useAbilityByNumber(player, 7); }
    public static void useAbility8(Player player) { useAbilityByNumber(player, 8); }
    public static void useAbility9(Player player) { useAbilityByNumber(player, 9); }
    public static void useAbility10(Player player) { useAbilityByNumber(player, 10); }
    public static void useAbility11(Player player) { useAbilityByNumber(player, 11); }
    public static void useAbility12(Player player) { useAbilityByNumber(player, 12); }
    public static void useAbility13(Player player) { useAbilityByNumber(player, 13); }
    public static void useAbility14(Player player) { useAbilityByNumber(player, 14); }
    public static void useAbility15(Player player) { useAbilityByNumber(player, 15); }
    public static void useAbility16(Player player) { useAbilityByNumber(player, 16); }
    public static void useAbility17(Player player) { useAbilityByNumber(player, 17); }
    public static void useAbility18(Player player) { useAbilityByNumber(player, 18); }
    public static void useAbility19(Player player) { useAbilityByNumber(player, 19); }
    public static void useAbility20(Player player) { useAbilityByNumber(player, 20); }
    public static void useAbility21(Player player) { useAbilityByNumber(player, 21); }
    public static void useAbility22(Player player) { useAbilityByNumber(player, 22); }
    public static void useAbility23(Player player) { useAbilityByNumber(player, 23); }
    public static void useAbility24(Player player) { useAbilityByNumber(player, 24); }
    public static void useAbility25(Player player) { useAbilityByNumber(player, 25); }
    public static void useAbility26(Player player) { useAbilityByNumber(player, 26); }
    public static void useAbility27(Player player) { useAbilityByNumber(player, 27); }
    public static void useAbility28(Player player) { useAbilityByNumber(player, 28); }
    public static void useAbility29(Player player) { useAbilityByNumber(player, 29); }
    public static void useAbility30(Player player) { useAbilityByNumber(player, 30); }
    public static void useAbility31(Player player) { useAbilityByNumber(player, 31); }
    public static void useAbility32(Player player) { useAbilityByNumber(player, 32); }

    public static void useAbilityByNumber(Player player, int abilityNumber) {
        CompoundTag persistentData = player.getPersistentData();
        if (!player.level().isClientSide()) {
            if (persistentData.contains(REGISTERED_ABILITIES_KEY, 9)) {
                ListTag registeredAbilities = persistentData.getList(REGISTERED_ABILITIES_KEY, 8);
                for (int i = 0; i < registeredAbilities.size(); i++) {
                    String entry = registeredAbilities.getString(i);
                    String[] parts = entry.split(":", 2);
                    if (parts.length == 2) {
                        if (Integer.parseInt(parts[0]) == abilityNumber) {
                            String registryName = parts[1];
                            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
                            if (item != null) {
                                ItemStack itemStack = new ItemStack(item);
                                itemStack.getItem().use(player.level(), player, InteractionHand.MAIN_HAND);
                                return;
                            } else {
                                player.sendSystemMessage(Component.literal("Item not found in registry for ability " + abilityNumber + " with registry name: " + registryName).withStyle(getStyle(player)));
                            }
                        }
                    } else {
                        player.sendSystemMessage(Component.literal("Entry did not split into two parts: " + entry).withStyle(getStyle(player)));
                    }
                }
            } else {
                player.sendSystemMessage(Component.literal("No registered abilities found.").withStyle(getStyle(player)));
            }
            player.sendSystemMessage(Component.literal("Ability " + abilityNumber + " not found or not registered.").withStyle(getStyle(player)));
        }
    }
    public static Style getStyle(Player pPlayer) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);

        // Check if holder is not null and the player is of the Spectator class
        if (holder != null) {
            if (holder.isSpectatorClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.AQUA);
            }
            if (holder.isSailorClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.BLUE);
            }
            if (holder.isApothecaryClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_GREEN);
            }
            if (holder.isApprenticeClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.BLUE);
            }
            if (holder.isArbiterClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.GOLD);
            }
            if (holder.isAssassinClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.RED);
            }
            if (holder.isBardClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.YELLOW);
            }
            if (holder.isCorpseCollectorClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_BLUE);
            }
            if (holder.isCriminalClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.GRAY);
            }
            if (holder.isHunterClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_RED);
            }
            if (holder.isLawyerClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_BLUE);
            }
            if (holder.isMarauderClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_PURPLE);
            }
            if (holder.isMonsterClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.WHITE);
            }
            if (holder.isMysteryPryerClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_PURPLE);
            }
            if (holder.isPlanterClass()) {
                // Create a Style object with multiple formatting options
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN);
            }
            if (holder.isPrisonerClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_GRAY);
            }
            if (holder.isReaderClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.WHITE);
            }
            if (holder.isSavantClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.GOLD);
            }
            if (holder.isSeerClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_PURPLE);
            }
            if (holder.isSleeplessClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_BLUE);
            }
            if (holder.isSecretsSupplicantClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.LIGHT_PURPLE);
            }
            if (holder.isWarriorClass()) {
                return Style.EMPTY.withBold(true).withColor(ChatFormatting.DARK_RED);
            }
        }
        return Style.EMPTY;
    }
    public static void mentalDamage(Player source, Player hurtEntity, int damage) { //can make it so that with useOn, sets shiftKeyDown to true for pPlayer
        BeyonderHolder sourceHolder = BeyonderHolderAttacher.getHolder(source).orElse(null);
        BeyonderHolder hurtHolder = BeyonderHolderAttacher.getHolder(hurtEntity).orElse(null);
        float x = Math.min(damage, damage * (hurtHolder.getMentalStrength() / sourceHolder.getMentalStrength()));
        hurtEntity.hurt(hurtEntity.damageSources().magic(), x);
    }
    public static float mentalInt(Player source, Player hurtEntity, int mentalInt) {
        BeyonderHolder sourceHolder = BeyonderHolderAttacher.getHolder(source).orElse(null);
        BeyonderHolder hurtHolder = BeyonderHolderAttacher.getHolder(hurtEntity).orElse(null);
        float x = Math.min(mentalInt, mentalInt * (hurtHolder.getMentalStrength() / sourceHolder.getMentalStrength()));
        return x;
    }
}
