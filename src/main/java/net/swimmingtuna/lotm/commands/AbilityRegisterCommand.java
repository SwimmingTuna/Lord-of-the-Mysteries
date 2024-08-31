package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityRegisterCommand {

    private static final String REGISTERED_ABILITIES_KEY = "RegisteredAbilities";
    private static final Map<String, Integer> COMBINATION_MAP = new HashMap<>();

    static {
        initializeCombinationMap();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("abilityput")
                .then(Commands.argument("combination", StringArgumentType.word())
                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                .executes(context -> registerAbility(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "combination"),
                                        StringArgumentType.getString(context, "item")
                                )))));
    }

    private static int registerAbility(CommandSourceStack source, String combination, String itemName) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Style style = BeyonderUtil.getStyle(player);

        // Validate combination
        if (!COMBINATION_MAP.containsKey(combination)) {
            source.sendFailure(Component.literal("Invalid combination. Please use a valid 5-character combination of L and R.").withStyle(style));
            return 0;
        }

        int combinationNumber = COMBINATION_MAP.get(combination);

        // Get the player's available abilities
        List<String> availableAbilities = BeyonderUtil.getAbilities(player);

        // Find the matching ability by comparing localized names
        String matchingAbility = availableAbilities.stream()
                .filter(ability -> ability.equalsIgnoreCase(itemName.trim()))
                .findFirst()
                .orElse(null);

        if (matchingAbility == null) {
            source.sendFailure(Component.literal("Ability not available or not found: " + itemName).withStyle(style));
            return 0;
        }

        // Find the corresponding item by comparing localization keys
        Item foundItem = null;
        String registryName = null;
        String localizedAbilityName = null;
        for (Item item : ForgeRegistries.ITEMS) {
            String descriptionId = item.getDescriptionId();
            String localizedName = Component.translatable(descriptionId).getString();

            if (localizedName.equalsIgnoreCase(matchingAbility)) {
                foundItem = item;
                registryName = ForgeRegistries.ITEMS.getKey(item).toString();
                localizedAbilityName = localizedName;
                break;
            }
        }

        if (foundItem == null) {
            source.sendFailure(Component.literal("Item not found for ability: " + matchingAbility).withStyle(style));
            return 0;
        }

        // Get the player's persistent data
        CompoundTag persistentData = player.getPersistentData();

        // Get or create the list of registered abilities
        ListTag registeredAbilities;
        if (persistentData.contains(REGISTERED_ABILITIES_KEY, 9)) { // 9 is the ID for ListTag
            registeredAbilities = persistentData.getList(REGISTERED_ABILITIES_KEY, 8); // 8 is the ID for StringTag
        } else {
            registeredAbilities = new ListTag();
            persistentData.put(REGISTERED_ABILITIES_KEY, registeredAbilities);
        }

        // Create the new ability entry with combination number and full registry name
        String newAbilityEntry = combinationNumber + ":" + registryName;

        // Check if there's already an ability registered for this combination
        String removedAbility = null;
        for (int i = 0; i < registeredAbilities.size(); i++) {
            String entry = registeredAbilities.getString(i);
            String[] parts = entry.split(":");
            if (parts[0].equals(String.valueOf(combinationNumber))) {
                removedAbility = parts.length > 1 ? parts[1] + ":" + parts[2] : null;
                registeredAbilities.remove(i);
                break;
            }
        }

        // Add the new ability to the list
        registeredAbilities.add(StringTag.valueOf(newAbilityEntry));

        // Save the persistent data
        persistentData.put(REGISTERED_ABILITIES_KEY, registeredAbilities);

        // Notify the player
        if (removedAbility != null) {
            // Debugging output to verify removedAbility
            String finalRemovedAbility2 = removedAbility;
            source.sendSuccess(() -> Component.literal("Debug: removedAbility = " + finalRemovedAbility2), true);

            ResourceLocation removedItemRL = new ResourceLocation(removedAbility);
            Item removedItem = ForgeRegistries.ITEMS.getValue(removedItemRL);

            if (removedItem != null) {
                String removedItemName = Component.translatable(removedItem.getDescriptionId()).getString();

                // Debugging output
                String finalRemovedAbility1 = removedAbility;
                source.sendSuccess(() -> Component.literal("Debug: Removed item registry name = " + finalRemovedAbility1), true);
                source.sendSuccess(() -> Component.literal("Debug: Removed item localized name = " + removedItemName), true);

                String finalLocalizedAbilityName2 = localizedAbilityName;
                source.sendSuccess(() -> Component.literal("Replaced ability: " + removedItemName + " with " + finalLocalizedAbilityName2 + " for combination " + combination).withStyle(style), true);
            } else {
                String finalRemovedAbility = removedAbility;
                source.sendSuccess(() -> Component.literal("Debug: Could not find item for registry name: " + finalRemovedAbility), true);
                String finalLocalizedAbilityName1 = localizedAbilityName;
                source.sendSuccess(() -> Component.literal("Added ability: " + finalLocalizedAbilityName1 + " for combination " + combination + " (previous ability not found)").withStyle(style), true);
            }
        } else {
            String finalLocalizedAbilityName = localizedAbilityName;
            source.sendSuccess(() -> Component.literal("Added ability: " + finalLocalizedAbilityName + " for combination " + combination).withStyle(style), true);
        }

        return 1;
    }
    private static void initializeCombinationMap() {
        String[] combinations = {
                "LLLLL", "LLLLR", "LLLRL", "LLLRR", "LLRLL", "LLRLR", "LLRRL", "LLRRR",
                "LRLLL", "LRLLR", "LRLRL", "LRLRR", "LRRLL", "LRRLR", "LRRRL", "LRRRR",
                "RLLLL", "RLLLR", "RLLRL", "RLLRR", "RLRLL", "RLRLR", "RLRRL", "RLRRR",
                "RRLLL", "RRLLR", "RRLRL", "RRLRR", "RRRLL", "RRRLR", "RRRRL", "RRRRR"
        };
        for (int i = 0; i < combinations.length; i++) {
            COMBINATION_MAP.put(combinations[i], i + 1);
        }
    }
}