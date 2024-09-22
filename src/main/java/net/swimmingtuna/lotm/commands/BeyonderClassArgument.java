package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraftforge.registries.IForgeRegistry;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.BeyonderClassInit;

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
