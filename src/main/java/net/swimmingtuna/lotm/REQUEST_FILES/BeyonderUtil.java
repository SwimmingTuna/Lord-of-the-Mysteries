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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
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
        if (pPlayer.level().isClientSide()) {
            return abilityNames;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (holder == null) {
            return abilityNames;
        }
        int sequence = holder.getCurrentSequence();
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
            if (sequence <= 8) {
                abilityNames.add(getItemName(ItemInit.MIND_READING.get()));
            }
            if (sequence <= 7) {
                abilityNames.add(getItemName(ItemInit.AWE.get()));
                abilityNames.add(getItemName(ItemInit.FRENZY.get()));
                abilityNames.add(getItemName(ItemInit.PLACATE.get()));
            }
            if (sequence <= 6) {
                abilityNames.add(getItemName(ItemInit.PSYCHOLOGICAL_INVISIBILITY.get()));
                abilityNames.add(getItemName(ItemInit.BATTLE_HYPNOTISM.get()));
            }
            if (sequence <= 5) {
                abilityNames.add(getItemName(ItemInit.GUIDANCE.get()));
                abilityNames.add(getItemName(ItemInit.ALTERATION.get()));
                abilityNames.add(getItemName(ItemInit.DREAM_WALKING.get()));
                abilityNames.add(getItemName(ItemInit.NIGHTMARE.get()));
            }
            if (sequence <= 4) {
                abilityNames.add(getItemName(ItemInit.APPLY_MANIPULATION.get()));
                abilityNames.add(getItemName(ItemInit.MANIPULATE_MOVEMENT.get()));
                abilityNames.add(getItemName(ItemInit.MANIPULATE_FONDNESS.get()));
                abilityNames.add(getItemName(ItemInit.MANIPULATE_EMOTION.get()));
                abilityNames.add(getItemName(ItemInit.MENTAL_PLAGUE.get()));
                abilityNames.add(getItemName(ItemInit.MIND_STORM.get()));
                abilityNames.add(getItemName(ItemInit.DRAGON_BREATH.get()));
            }
            if (sequence <= 3) {
                abilityNames.add(getItemName(ItemInit.CONSCIOUSNESS_STROLL.get()));
                abilityNames.add(getItemName(ItemInit.PLAGUE_STORM.get()));
                abilityNames.add(getItemName(ItemInit.DREAM_WEAVING.get()));
            }
            if (sequence <= 2) {
                abilityNames.add(getItemName(ItemInit.DISCERN.get()));
                abilityNames.add(getItemName(ItemInit.DREAM_INTO_REALITY.get()));
            }
            if (sequence <= 1) {
                abilityNames.add(getItemName(ItemInit.PROPHESIZE_DEMISE.get()));
                abilityNames.add(getItemName(ItemInit.PROPHESIZE_TELEPORT_PLAYER.get()));
                abilityNames.add(getItemName(ItemInit.METEOR_SHOWER.get()));
                abilityNames.add(getItemName(ItemInit.METEOR_NO_LEVEL_SHOWER.get()));
            }
            if (sequence <= 0) {
                abilityNames.add(getItemName(ItemInit.ENVISION_BARRIER.get()));
                abilityNames.add(getItemName(ItemInit.ENVISION_DEATH.get()));
                abilityNames.add(getItemName(ItemInit.ENVISIONHEALTH.get()));
                abilityNames.add(getItemName(ItemInit.ENVISION_KINGDOM.get()));
                abilityNames.add(getItemName(ItemInit.ENVISION_LIFE.get()));
                abilityNames.add(getItemName(ItemInit.ENVISION_LOCATION.get()));
                abilityNames.add(getItemName(ItemInit.ENVISION_LOCATION_BLINK.get()));
                abilityNames.add(getItemName(ItemInit.ENVISION_WEATHER.get()));
            }
        }

        if (holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
            if (sequence <= 8) {
                abilityNames.add(getItemName(ItemInit.RAGING_BLOWS.get()));
            }
            if (sequence <= 7) {
                abilityNames.add(getItemName(ItemInit.ENABLE_OR_DISABLE_LIGHTNING.get()));
                abilityNames.add(getItemName(ItemInit.AQUEOUS_LIGHT_PUSH.get()));
                abilityNames.add(getItemName(ItemInit.AQUEOUS_LIGHT_PULL.get()));
                abilityNames.add(getItemName(ItemInit.AQUEOUS_LIGHT_DROWN.get()));
            }
            if (sequence <= 6) {
                abilityNames.add(getItemName(ItemInit.WIND_MANIPULATION_BLADE.get()));
                abilityNames.add(getItemName(ItemInit.WIND_MANIPULATION_CUSHION.get()));
                abilityNames.add(getItemName(ItemInit.WIND_MANIPULATION_FLIGHT.get()));
                abilityNames.add(getItemName(ItemInit.WIND_MANIPULATION_SENSE.get()));
            }
            if (sequence <= 5) {
                abilityNames.add(getItemName(ItemInit.SAILOR_LIGHTNING.get()));
                abilityNames.add(getItemName(ItemInit.SIREN_SONG_HARM.get()));
                abilityNames.add(getItemName(ItemInit.SIREN_SONG_STRENGTHEN.get()));
                abilityNames.add(getItemName(ItemInit.SIREN_SONG_WEAKEN.get()));
                abilityNames.add(getItemName(ItemInit.SIREN_SONG_STUN.get()));
                abilityNames.add(getItemName(ItemInit.ACIDIC_RAIN.get()));
                abilityNames.add(getItemName(ItemInit.WATER_SPHERE.get()));
            }
            if (sequence <= 4) {
                abilityNames.add(getItemName(ItemInit.TSUNAMI.get()));
                abilityNames.add(getItemName(ItemInit.TSUNAMI_SEAL.get()));
                abilityNames.add(getItemName(ItemInit.HURRICANE.get()));
                abilityNames.add(getItemName(ItemInit.TORNADO.get()));
                abilityNames.add(getItemName(ItemInit.EARTHQUAKE.get()));
                abilityNames.add(getItemName(ItemInit.ROAR.get()));
            }
            if (sequence <= 3) {
                abilityNames.add(getItemName(ItemInit.AQUATIC_LIFE_MANIPULATION.get()));
                abilityNames.add(getItemName(ItemInit.LIGHTNING_STORM.get()));
                abilityNames.add(getItemName(ItemInit.LIGHTNING_BRANCH.get()));
                abilityNames.add(getItemName(ItemInit.SONIC_BOOM.get()));
                abilityNames.add(getItemName(ItemInit.THUNDER_CLAP.get()));
            }
            if (sequence <= 2) {
                abilityNames.add(getItemName(ItemInit.RAIN_EYES.get()));
                abilityNames.add(getItemName(ItemInit.VOLCANIC_ERUPTION.get()));
                abilityNames.add(getItemName(ItemInit.EXTREME_COLDNESS.get()));
                abilityNames.add(getItemName(ItemInit.LIGHTNING_BALL.get()));
            }
            if (sequence <= 1) {
                abilityNames.add(getItemName(ItemInit.LIGHTNING_BALL_ABSORB.get()));
                abilityNames.add(getItemName(ItemInit.SAILOR_LIGHTNING_TRAVEL.get()));
                abilityNames.add(getItemName(ItemInit.STAR_OF_LIGHTNING.get()));
                abilityNames.add(getItemName(ItemInit.LIGHTNING_REDIRECTION.get()));
            }
            if (sequence <= 0) {
                abilityNames.add(getItemName(ItemInit.STORM_SEAL.get()));
                abilityNames.add(getItemName(ItemInit.WATER_COLUMN.get()));
                abilityNames.add(getItemName(ItemInit.MATTER_ACCELERATION_SELF.get()));
                abilityNames.add(getItemName(ItemInit.MATTER_ACCELERATION_BLOCKS.get()));
                abilityNames.add(getItemName(ItemInit.MATTER_ACCELERATION_ENTITIES.get()));
                abilityNames.add(getItemName(ItemInit.TYRANNY.get()));
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
        BeyonderHolder holderLazyOptional = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (holderLazyOptional != null && holderLazyOptional.getCurrentClass() != null) {
            return Style.EMPTY.withBold(true).withColor(holderLazyOptional.getCurrentClass().getColorFormatting());
        }
        return Style.EMPTY;
    }

}
