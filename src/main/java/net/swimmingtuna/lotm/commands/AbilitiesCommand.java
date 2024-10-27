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
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.util.BeyonderAbilitiesItemMenu;
import org.jetbrains.annotations.Nullable;

public class AbilitiesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("abilities")
                .executes(context -> {
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(context.getSource().getPlayerOrException());
                    if (holder.getCurrentClass() == null) {
                        context.getSource().sendFailure(Component.literal("You are not in any class!"));
                        return 0;
                    }
                    context.getSource().getPlayer().openMenu(new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.translatable("container.lotm.abilities");
                        }

                        @Nullable
                        @Override
                        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                            return new BeyonderAbilitiesItemMenu(containerId, playerInventory, holder.getCurrentClass().getAbilityItemsContainer(holder.getCurrentSequence()));
                        }
                    });
                    return 1;
                })
        );
    }
}

