package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class RainEyes extends SimpleAbilityItem {

    public RainEyes(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 2, 0, 10);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        rainEyes(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, if it's ever raining, tells you the location of all players around you every 10 seconds\n" +
                "Spirituality Used: 0\n" +
                "Cooldown: 0.5 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    public static void rainEyes(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            boolean torrentialDownpour = tag.getBoolean("torrentialDownpour");
            if (torrentialDownpour) {
                tag.putBoolean("torrentialDownpour", false);
                player.displayClientMessage(Component.literal("Rain eyes disabled").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            } else {
                tag.putBoolean("torrentialDownpour", true);
                player.displayClientMessage(Component.literal("Rain eyes enabled").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);

            }
        }
    }
}
