package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.swimmingtuna.lotm.world.worlddata.BeyonderRecipeData;

import java.util.ArrayList;
import java.util.List;

public class BeyonderRecipeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("beyonderrecipe")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("craftedItem", ItemArgument.item(buildContext))
                                .then(Commands.argument("ingredient1", ItemArgument.item(buildContext))
                                        .executes(BeyonderRecipeCommand::addRecipe)
                                        .then(Commands.argument("ingredient2", ItemArgument.item(buildContext))
                                                .executes(BeyonderRecipeCommand::addRecipe)
                                                .then(Commands.argument("ingredient3", ItemArgument.item(buildContext))
                                                        .executes(BeyonderRecipeCommand::addRecipe)
                                                        .then(Commands.argument("ingredient4", ItemArgument.item(buildContext))
                                                                .executes(BeyonderRecipeCommand::addRecipe)
                                                                .then(Commands.argument("ingredient5", ItemArgument.item(buildContext))
                                                                        .executes(BeyonderRecipeCommand::addRecipe)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("craftedItem", ItemArgument.item(buildContext))
                                .executes(BeyonderRecipeCommand::removeRecipe)
                        )
                )
        );
    }
    private static int removeRecipe(CommandContext<CommandSourceStack> context) {
        try {
            ItemStack craftedItem = ItemArgument.getItem(context, "craftedItem").createItemStack(1, false);
            ServerLevel level = context.getSource().getLevel();
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(level);

            boolean recipeRemoved = recipeData.removeRecipe(craftedItem);
            if (recipeRemoved) {
                context.getSource().sendSuccess(() -> Component.literal("Successfully removed recipe for " +
                        craftedItem.getHoverName().getString()).withStyle(ChatFormatting.GREEN), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.literal("No recipe found for this item!").withStyle(ChatFormatting.YELLOW));
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error removing recipe: " + e.getMessage()).withStyle(ChatFormatting.DARK_RED));
            return 0;
        }
    }

    private static int addRecipe(CommandContext<CommandSourceStack> context) {
        try {
            ItemStack craftedItem = ItemArgument.getItem(context, "craftedItem").createItemStack(1, false);
            List<ItemStack> ingredients = new ArrayList<>();
            try {
                ingredients.add(ItemArgument.getItem(context, "ingredient1").createItemStack(1, false));
                try {
                    ingredients.add(ItemArgument.getItem(context, "ingredient2").createItemStack(1, false));
                    try {
                        ingredients.add(ItemArgument.getItem(context, "ingredient3").createItemStack(1, false));
                        try {
                            ingredients.add(ItemArgument.getItem(context, "ingredient4").createItemStack(1, false));
                            try {
                                ingredients.add(ItemArgument.getItem(context, "ingredient5").createItemStack(1, false));
                            } catch (IllegalArgumentException e) {

                            }
                        } catch (IllegalArgumentException e) {

                        }
                    } catch (IllegalArgumentException e) {

                    }
                } catch (IllegalArgumentException e) {

                }
            } catch (IllegalArgumentException e) {
                context.getSource().sendFailure(Component.literal("At least one ingredient is required!").withStyle(ChatFormatting.RED));
                return 0;
            }
            ServerLevel level = context.getSource().getLevel();
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(level);
            boolean recipeAdded = recipeData.setRecipe(craftedItem, ingredients);
            if (recipeAdded) {
                context.getSource().sendSuccess(() -> Component.literal("Successfully added recipe for " +
                        craftedItem.getHoverName().getString()).withStyle(ChatFormatting.GREEN), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.literal("A recipe for this item already exists!").withStyle(ChatFormatting.YELLOW));
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error adding recipe: " + e.getMessage()).withStyle(ChatFormatting.DARK_RED));
            return 0;
        }
    }
}

