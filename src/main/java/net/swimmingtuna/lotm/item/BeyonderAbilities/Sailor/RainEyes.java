package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class RainEyes extends SimpleAbilityItem {

    public RainEyes(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 2, 0, 20);
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
        tooltipComponents.add(Component.literal("Upon use, share your senses with the rain, letting you know the location of all players in a huge radius around you every 1 0seconds"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
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
