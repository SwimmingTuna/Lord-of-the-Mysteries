package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ExtremeColdness extends SimpleAbilityItem {

    public ExtremeColdness(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 2, 1250,1200);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        extremeColdness(player);
        return InteractionResult.SUCCESS;
    }

    public static void extremeColdness(Player player) {
        player.getPersistentData().putInt("sailorExtremeColdness", 1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, lets out an area of below freezing temperatures that freezes everything in it's range\n" +
                "Spirituality Used: 1250\n" +
                "Cooldown: 1 minute").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }


    public static boolean canFreezeBlock(LivingEntity player, BlockPos targetPos) {
        Block block = player.level().getBlockState(targetPos).getBlock();
        return block != Blocks.BEDROCK && block != Blocks.AIR &&
                block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR &&
                block != Blocks.ICE;
    }
}