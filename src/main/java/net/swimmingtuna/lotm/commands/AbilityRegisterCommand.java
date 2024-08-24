package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.LOTM;

import java.util.Locale;

public class AbilityRegisterCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("abilityput")
                .then(Commands.argument("item", StringArgumentType.greedyString())
                        .executes(context -> registerAbility(context.getSource(), StringArgumentType.getString(context, "item")))));
    }

    private static int registerAbility(CommandSourceStack source, String itemName) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        // Find the item by its localized name
        Item foundItem = null;
        String registryName = null;
        String trimmedItemName = itemName.trim().toLowerCase(Locale.ROOT);

        for (ResourceLocation itemRL : ForgeRegistries.ITEMS.getKeys()) {
            Item item = ForgeRegistries.ITEMS.getValue(itemRL);
            if (item != null) {
                String localizedName = Component.translatable(item.getDescriptionId()).getString().trim().toLowerCase(Locale.ROOT);
                if (localizedName.equals(trimmedItemName)) {
                    foundItem = item;
                    registryName = itemRL.toString();
                    break;
                }
            }
        }

        if (foundItem == null) {
            source.sendFailure(Component.literal("Item not found: " + itemName));
            return 0;
        }

        // Get the player's persistent data
        CompoundTag persistentData = player.getPersistentData();

        // Store the item's registry name in the persistent data
        persistentData.putString("RegisteredAbility", registryName);

        String finalRegistryName = registryName;
        source.sendSuccess(() -> Component.literal("Registered ability: " + itemName + " (" + finalRegistryName + ")"), true);
        return 1;
    }
}