package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Discern extends Item {

    public Discern(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < (int) (1000 / dreamIntoReality.getValue())) {
                player.displayClientMessage(Component.literal("You need " + ((int) (1000 / dreamIntoReality.getValue()) + " spirituality in order to use this")).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD), true);
            }

            if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 2 && holder.useSpirituality((int) (1000 / dreamIntoReality.getValue()))) {
                removeCooldown(player);
                if (!player.getAbilities().instabuild)
                    player.getCooldowns().addCooldown(this, 900);
            }
        }
        return super.use(level, player, hand);
    }

    private void removeCooldown(Player player) {
        if (!player.level().isClientSide()) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);
                if (!itemStack.isEmpty()) {
                    player.getCooldowns().removeCooldown(itemStack.getItem());
                }
            }
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.literal("Upon use, resets the cooldown of all your abilities\n" +
                    "Spirituality Used: 1000\n" +
                    "Cooldown: 45 seconds").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

}
