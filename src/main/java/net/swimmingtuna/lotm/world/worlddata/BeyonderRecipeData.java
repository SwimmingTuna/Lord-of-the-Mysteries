package net.swimmingtuna.lotm.world.worlddata;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeyonderRecipeData extends SavedData {
    private static final String RECIPES_KEY = "BeyonderRecipes";
    private final Map<ItemStack, List<ItemStack>> beyonderRecipes = new HashMap<>();

    private BeyonderRecipeData() {}

    public static BeyonderRecipeData getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                BeyonderRecipeData::load,
                BeyonderRecipeData::create,
                RECIPES_KEY
        );
    }

    public boolean setRecipe(ItemStack potion, List<ItemStack> ingredients) {
        boolean recipeExists = beyonderRecipes.keySet().stream().anyMatch(existingPotion -> ItemStack.isSameItemSameTags(existingPotion, potion));
        if (recipeExists) {
            return false;
        }
        beyonderRecipes.put(potion, new ArrayList<>(ingredients));
        setDirty();
        return true;
    }
    public boolean removeRecipe(ItemStack potion) {
        List<ItemStack> removedRecipe = null;
        for (ItemStack existingPotion : beyonderRecipes.keySet()) {
            if (ItemStack.isSameItemSameTags(existingPotion, potion)) {
                removedRecipe = beyonderRecipes.remove(existingPotion);
                break;
            }
        }
        if (removedRecipe != null) {
            setDirty();
            return true;
        }
        return false;
    }

    public void clearRecipes() {
        beyonderRecipes.clear();
        setDirty();
    }

    public Map<ItemStack, List<ItemStack>> getBeyonderRecipes() {
        return new HashMap<>(beyonderRecipes);
    }

    public void sendPlayerRecipeValues(Player player) {
        if (beyonderRecipes.isEmpty()) {
            player.sendSystemMessage(Component.literal("No Beyonder recipes found.").withStyle(ChatFormatting.RED));
            return;
        }
        for (Map.Entry<ItemStack, List<ItemStack>> entry : beyonderRecipes.entrySet()) {
            StringBuilder recipeMessage = new StringBuilder("Potion: ").append(entry.getKey().getHoverName().getString()).append(" - Ingredients: ");
            for (ItemStack ingredient : entry.getValue()) {
                recipeMessage.append(ingredient.getHoverName().getString()).append(", ");
            }
            player.sendSystemMessage(Component.literal(recipeMessage.toString())
                    .withStyle(ChatFormatting.WHITE)
                    .withStyle(ChatFormatting.BOLD));
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag recipeList = new ListTag();
        for (Map.Entry<ItemStack, List<ItemStack>> entry : beyonderRecipes.entrySet()) {
            CompoundTag recipeTag = new CompoundTag();
            CompoundTag potionTag = new CompoundTag();
            entry.getKey().save(potionTag);
            recipeTag.put("beyonderPotion", potionTag);
            ListTag ingredientsTag = new ListTag();
            for (ItemStack ingredient : entry.getValue()) {
                CompoundTag ingredientTag = new CompoundTag();
                ingredient.save(ingredientTag);
                ingredientsTag.add(ingredientTag);
            }
            recipeTag.put("beyonderRecipe", ingredientsTag);
            recipeList.add(recipeTag);
        }

        compoundTag.put(RECIPES_KEY, recipeList);
        return compoundTag;
    }

    public static BeyonderRecipeData load(CompoundTag compoundTag) {
        BeyonderRecipeData data = new BeyonderRecipeData();
        if (compoundTag.contains(RECIPES_KEY)) {
            ListTag recipeList = compoundTag.getList(RECIPES_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < recipeList.size(); i++) {
                CompoundTag recipeTag = recipeList.getCompound(i);
                ItemStack potion = ItemStack.of(recipeTag.getCompound("beyonderPotion"));
                List<ItemStack> ingredients = new ArrayList<>();
                ListTag ingredientsTag = recipeTag.getList("beyonderRecipe", Tag.TAG_COMPOUND);
                for (int j = 0; j < ingredientsTag.size(); j++) {
                    ingredients.add(ItemStack.of(ingredientsTag.getCompound(j)));
                }
                data.beyonderRecipes.put(potion, ingredients);
            }
        }
        return data;
    }

    public static BeyonderRecipeData create() {
        return new BeyonderRecipeData();
    }
}