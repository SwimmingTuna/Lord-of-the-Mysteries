package net.swimmingtuna.lotm.init;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.commands.AbilitiesCommand;
import net.swimmingtuna.lotm.REQUEST_FILES.AbilityRegisterCommand;
import net.swimmingtuna.lotm.commands.BeyonderClassArgument;
import net.swimmingtuna.lotm.commands.BeyonderCommand;


public class CommandInit {
    public static final DeferredRegister<ArgumentTypeInfo<?,?>> ARGUMENT_TYPES = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.key(), LOTM.MOD_ID);
    public static final RegistryObject<SingletonArgumentInfo<BeyonderClassArgument>> BEYONDER_CLASS = ARGUMENT_TYPES.register("beyonder_class", () -> ArgumentTypeInfos.registerByClass(BeyonderClassArgument.class, SingletonArgumentInfo.contextFree(BeyonderClassArgument::beyonderClass)));



    public static void onCommandRegistration(RegisterCommandsEvent event) {
        BeyonderCommand.register(event.getBuildContext(),event.getDispatcher());
        AbilitiesCommand.register(event.getDispatcher());
        AbilityRegisterCommand.register(event.getDispatcher());
    }
}
