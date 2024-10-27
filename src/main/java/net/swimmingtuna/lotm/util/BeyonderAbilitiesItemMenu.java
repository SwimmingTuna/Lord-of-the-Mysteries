package net.swimmingtuna.lotm.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ClickType;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.AddItemInInventoryC2S;

public class BeyonderAbilitiesItemMenu extends AbstractContainerMenu {
    private final Container container;

    public BeyonderAbilitiesItemMenu(int containerId, Inventory playerInventory, Container abilityContainer) {
        super(MenuType.GENERIC_9x3, containerId);
        this.container = abilityContainer;
        for (int i = 0; i < this.container.getContainerSize(); i++) {
            this.addSlot(new Slot(this.container, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18) {
                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }
            });
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 86 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 144));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < this.container.getContainerSize()) {
            Slot slot = this.slots.get(slotId);
            if (slot != null && slot.hasItem()) {
                ItemStack clickedItem = slot.getItem().copy();
                LOTMNetworkHandler.sendToServer(new AddItemInInventoryC2S(clickedItem));
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack clickedItem = slot.getItem();
            LOTMNetworkHandler.sendToServer(new AddItemInInventoryC2S(clickedItem));
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}