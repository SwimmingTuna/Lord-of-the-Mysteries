package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import org.jetbrains.annotations.Nullable;

public class AbilitiesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("abilities")
                        .executes(context -> {
                                    BeyonderHolderAttacher.getHolder(context.getSource().getPlayerOrException()).ifPresent(holder -> {
                                        context.getSource().getPlayer().openMenu(new MenuProvider(){
                                            @Override
                                            public Component getDisplayName() {
                                                return Component.translatable("container.lotm.abilities");
                                            }

                                            @Nullable
                                            @Override
                                            public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                                                return new ChestMenu(
                                                        MenuType.GENERIC_9x3,
                                                        pContainerId, context.getSource().getPlayer().getInventory(),
                                                        holder.getCurrentClass().getAbilityItemsContainer(holder.getCurrentSequence()), 3

                                                );
                                            }});
                                    });
                                    return 1;
                                }
                        )
        );
    }
}

