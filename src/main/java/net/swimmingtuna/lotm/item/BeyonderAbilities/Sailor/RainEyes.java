package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RainEyes extends Item implements ReachChangeUUIDs {

    public RainEyes(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 0) {
                pPlayer.displayClientMessage(Component.literal("You need 0 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && sailorSequence.getCurrentSequence() <= 2 && sailorSequence.useSpirituality(0)) {
                    useItem(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 10);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, if it's ever raining, tells you the location of all players around you every 10 seconds\n" +
                    "Spirituality Used: 0\n" +
                    "Cooldown: 0.5 seconds").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    public static void useItem(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            CompoundTag tag = pPlayer.getPersistentData();
            boolean torrentialDownpour = tag.getBoolean("torrentialDownpour");
            if (torrentialDownpour) {
                tag.putBoolean("torrentialDownpour", false);
                pPlayer.displayClientMessage(Component.literal("Rain eyes disabled").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            } else {
                tag.putBoolean("torrentialDownpour", true);
                pPlayer.displayClientMessage(Component.literal("Rain eyes enabled").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);

            }
        }
    }
}
