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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        // Validate combination
        if (!COMBINATION_MAP.containsKey(combination)) {
            source.sendFailure(Component.literal("Invalid combination. Please use a valid 5-character combination of L and R."));
            return 0;
        }

        int combinationNumber = COMBINATION_MAP.get(combination);

        // Get the player's available abilities
        List<String> availableAbilities = BeyonderUtil.getAbilities(player);

        // Find the matching ability
        String matchingAbility = availableAbilities.stream()
                .filter(ability -> ability.equalsIgnoreCase(itemName.trim()))
                .findFirst()
                .orElse(null);

        if (matchingAbility == null) {
            source.sendFailure(Component.literal("Ability not available or not found: " + itemName));
            return 0;
        }

        // Find the corresponding item
        Item foundItem = null;
        String registryName = null;
        for (Item item : ForgeRegistries.ITEMS) {
            String localizedName = Component.translatable(item.getDescriptionId()).getString().toLowerCase(Locale.ROOT);
            if (localizedName.equals(matchingAbility)) {
                foundItem = item;
                registryName = ForgeRegistries.ITEMS.getKey(item).toString();
                break;
            }
        }

        if (foundItem == null) {
            source.sendFailure(Component.literal("Item not found for ability: " + matchingAbility));
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

        // Create the new ability entry with combination number
        String newAbilityEntry = combinationNumber + ":" + registryName;

        // Check if the ability is already registered
        boolean alreadyRegistered = false;
        for (int i = 0; i < registeredAbilities.size(); i++) {
            if (registeredAbilities.getString(i).split(":")[1].equals(registryName)) {
                alreadyRegistered = true;
                break;
            }
        }

        if (!alreadyRegistered) {
            // Add the new ability to the list
            registeredAbilities.add(StringTag.valueOf(newAbilityEntry));
            String finalRegistryName1 = registryName;
            source.sendSuccess(() -> Component.literal("Registered new ability: " + matchingAbility + " (" + finalRegistryName1 + ") with combination " + combination + " (number: " + combinationNumber + ")"), true);
        } else {
            String finalRegistryName = registryName;
            source.sendSuccess(() -> Component.literal("Ability already registered: " + matchingAbility + " (" + finalRegistryName + ")"), true);
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