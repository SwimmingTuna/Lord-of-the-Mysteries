package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ExtremeColdness extends Item {
    public ExtremeColdness(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 1250) {
                pPlayer.displayClientMessage(Component.literal("You need 1250 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }

            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && tyrantSequence.getCurrentSequence() <= 2 && tyrantSequence.useSpirituality(1250)) {
                    useItem(pPlayer);
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 1200);

            });
        }
        return super.use(level, pPlayer, hand);
    }

    public static void useItem(Player pPlayer) {
        pPlayer.getPersistentData().putInt("sailorExtremeColdness", 1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, lets out an area of below freezing temperatures that freezes everything in it's range\n" +
                    "Spirituality Used: 1250\n" +
                    "Cooldown: 1 minute").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }


    public static boolean canFreezeBlock(Player player, BlockPos targetPos) {
        Block block = player.level().getBlockState(targetPos).getBlock();
        return block != Blocks.BEDROCK && block != Blocks.AIR &&
                block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR &&
                block != Blocks.ICE;
    }
}