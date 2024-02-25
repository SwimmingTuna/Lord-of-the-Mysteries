package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.BeyonderClassInit;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BeyonderClassArgument extends RegistryResourceArgument<BeyonderClass> {
    private BeyonderClassArgument() {
    }

    public static BeyonderClassArgument beyonderClass() {
        return new BeyonderClassArgument();
    }

    public static BeyonderClass getBeyonderClass(final CommandContext<?> context, final String name) {
        return context.getArgument(name, BeyonderClass.class);
    }

    @Override
    protected IForgeRegistry<BeyonderClass> getRegistry() {
        return BeyonderClassInit.getRegistry();
    }

    @Override
    protected DynamicCommandExceptionType getUnknownExceptionType() {
        return BeyonderCommand.ERROR_UNKNOWN_BEYONDER_CLASS;
    }
}
