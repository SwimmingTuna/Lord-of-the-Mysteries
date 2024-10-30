package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EnableOrDisableLightning extends SimpleAbilityItem {


    public EnableOrDisableLightning(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 7, 0, 20);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        enableOrDisableLightning(player);
        return InteractionResult.SUCCESS;
    }

    private void enableOrDisableLightning(Player player) {
        CompoundTag tag = player.getPersistentData();
        boolean lightning = tag.getBoolean("SailorLightning");
        tag.putBoolean("SailorLightning", !lightning);
        player.displayClientMessage(Component.literal("Lightning effect turned " + (lightning ? "off" : "on")).withStyle(ChatFormatting.DARK_BLUE).withStyle(ChatFormatting.BOLD), true);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, disables or enables lightning spawning upon users hitting targets\n" +
                "Spirituality Used: 0\n" +
                "Cooldown: 0.5 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}