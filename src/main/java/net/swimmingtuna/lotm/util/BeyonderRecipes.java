package net.swimmingtuna.lotm.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import net.swimmingtuna.lotm.blocks.PotionCauldronBlockEntity;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.*;

public class BeyonderRecipes {
    // Recipe map where the key is a list of required items, and the value is the result item
    private static final Map<List<Item>, Item> recipes = new HashMap<>();
    static {
        recipes.put(Arrays.asList(Items.NETHER_STAR, Items.NETHERITE_BLOCK), ItemInit.SPECTATOR_5_POTION.get());
    }

    public static void checkForRecipes(ItemStackHandler inventory, PotionCauldronBlockEntity potionCauldronBlock) {
        List<Item> inventoryItems = new ArrayList<>();

        // Collect all unique items in the inventory
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                inventoryItems.add(stack.getItem());
            }
        }

        // Check if the current inventory items match any recipe

    }
}
