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

public class WindManipulationSense extends SimpleAbilityItem {


    public WindManipulationSense(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 6, 0, 20);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        windManipulationSense(player);
        return InteractionResult.SUCCESS;
    }

    public static void windManipulationSense(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            boolean windManipulationSense = tag.getBoolean("windManipulationSense");
            tag.putBoolean("windManipulationSense", !windManipulationSense);
            player.displayClientMessage(Component.literal("Wind Sense Turned " + (windManipulationSense ? "Off" : "On")).withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summon constant winds around you. While active, you'll get the location of nearby entities"));
        tooltipComponents.add(Component.literal("Left Click for Wind Manipulation (Blade)"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("20 per second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}