package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

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

public class EnableDisableRipple extends SimpleAbilityItem {
    public EnableDisableRipple(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 3, 0, 20);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        enableOrDisableDangerSense(player);
        return InteractionResult.SUCCESS;
    }

    public static void enableOrDisableDangerSense(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            boolean ripple = tag.getBoolean("monsterRipple");
            tag.putBoolean("monsterRipple", !ripple);
            player.displayClientMessage(Component.literal("Ripple of Chaos " + (ripple ? "Off" : "On")).withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE), true);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, enables or disables your Ripple of Misfortune. If enabled, when you get hit, all entities around you will face a calamity or misfortunate effect."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}