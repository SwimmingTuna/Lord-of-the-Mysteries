package net.swimmingtuna.lotm.init;

import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.commands.AbilitiesCommand;
import net.swimmingtuna.lotm.commands.BeyonderClassArgument;
import net.swimmingtuna.lotm.commands.BeyonderCommand;
import vazkii.patchouli.common.item.PatchouliItems;


public class CommandInit {
    public static final DeferredRegister<ArgumentTypeInfo<?,?>> ARGUMENT_TYPES = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.key(), LOTM.MOD_ID);
    public static final RegistryObject<SingletonArgumentInfo<BeyonderClassArgument>> BEYONDER_CLASS = ARGUMENT_TYPES.register("beyonder_class", () -> ArgumentTypeInfos.registerByClass(BeyonderClassArgument.class, SingletonArgumentInfo.contextFree(BeyonderClassArgument::beyonderClass)));



    public static void onCommandRegistration(RegisterCommandsEvent event) {
        BeyonderCommand.register(event.getBuildContext(),event.getDispatcher());
        AbilitiesCommand.register(event.getDispatcher());

        event.getDispatcher().register(
                Commands.literal("lotmbook").executes(
                        (context) -> {
                            ItemStack book = new ItemStack(PatchouliItems.BOOK);
                            CompoundTag tag = new CompoundTag();
                            tag.putString("patchouli:book", "lotm:lotmbook");
                            book.setTag(tag);
                            context.getSource().getPlayerOrException().getInventory().add(book);
                            return 1;
                        }
                )
        );
    }
}
