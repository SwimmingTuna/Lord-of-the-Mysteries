package net.swimmingtuna.lotm.init;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.commands.*;

public class CommandInit {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, LOTM.MOD_ID);
    public static final RegistryObject<SingletonArgumentInfo<BeyonderClassArgument>> BEYONDER_CLASS = ARGUMENT_TYPES.register("beyonder_class",
            () -> ArgumentTypeInfos.registerByClass(BeyonderClassArgument.class, SingletonArgumentInfo.contextFree(BeyonderClassArgument::beyonderClass)));



    public static void onCommandRegistration(RegisterCommandsEvent event) {
        BeyonderCommand.register(event.getBuildContext(), event.getDispatcher());
        AbilitiesCommand.register(event.getDispatcher());
        AbilityRegisterCommand.register(event.getDispatcher(), event.getBuildContext());
        PlayerMobsCommand.register(event.getDispatcher());
        FatedConnectionCommand.register(event.getDispatcher(), event.getBuildContext());
        BeyonderRecipeCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}
