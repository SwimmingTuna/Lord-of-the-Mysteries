package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Ability;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityRegisterCommand {

    private static final String REGISTERED_ABILITIES_KEY = "RegisteredAbilities";
    private static final Map<String, Integer> COMBINATION_MAP = new HashMap<>();

    private static final DynamicCommandExceptionType NOT_ABILITY = new DynamicCommandExceptionType(o -> Component.literal("Not an ability: " + o));

    static {
        initializeCombinationMap();
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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("abilityput")
                .then(Commands.argument("combination", StringArgumentType.word())
                        .then(Commands.argument("item", ResourceArgument.resource(buildContext, Registries.ITEM))
                                .executes(context -> registerAbility(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "combination"),
                                        ResourceArgument.getResource(context, "item", Registries.ITEM)
                                )))));
    }

    private static int registerAbility(CommandSourceStack source, String combination, Holder.Reference<Item> itemReference) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        // Validate combination
        if (!COMBINATION_MAP.containsKey(combination)) {
            source.sendFailure(Component.literal("Invalid combination. Please use a valid 5-character combination of L and R."));
            return 0;
        }

        int combinationNumber = COMBINATION_MAP.get(combination);
        List<Item> availableAbilities = BeyonderUtil.getAbilities(player);

        Item item = itemReference.get();
        ResourceLocation resourceLocation = itemReference.key().location();
        if (!(item instanceof Ability)) {
            throw NOT_ABILITY.create(resourceLocation);
        }
        if (!availableAbilities.contains(item)) {
            source.sendFailure(Component.literal("Ability not available: " + itemReference));
            return 0;
        }

        CompoundTag tag = player.getPersistentData();
        CompoundTag registeredAbilities;
        if (tag.contains(REGISTERED_ABILITIES_KEY, Tag.TAG_COMPOUND)) {
            registeredAbilities = tag.getCompound(REGISTERED_ABILITIES_KEY);
        } else {
            registeredAbilities = new CompoundTag();
            tag.put(REGISTERED_ABILITIES_KEY, registeredAbilities);
        }

        registeredAbilities.putString(String.valueOf(combinationNumber), resourceLocation.toString());
        tag.put(REGISTERED_ABILITIES_KEY, registeredAbilities);
        source.sendSuccess(() -> Component.literal("Added ability: ").append(Component.translatable(item.getDescriptionId())).append(Component.literal(" for combination " + combination)), true);

        return 1;
    }
}