package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EnvisionLocation extends SimpleAbilityItem {

    public EnvisionLocation(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 0, 0, 0);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("While holding this item, type in three coordinates, e.g. (100, 100, 100) or a player's name, and you'll teleport to that location\n" +
                "Spirituality Used: 500\n" +
                "Left Click for Envision Location (Blink)\n" +
                "Cooldown: 0 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public static boolean isThreeIntegers(String message) {
        message = message.replace(",", " ").trim();
        message = message.replaceAll("\\s+", " ");
        return message.matches("\\d+ \\d+ \\d+");
    }


}
