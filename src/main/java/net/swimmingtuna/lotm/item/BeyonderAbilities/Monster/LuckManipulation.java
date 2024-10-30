package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class LuckManipulation extends SimpleAbilityItem {
    public LuckManipulation(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 5, 250, 60);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        changeBoolean(player);
        return InteractionResult.SUCCESS;
    }

    public static void changeBoolean(Player player) {

    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 5 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof LuckManipulation) {
                    player.displayClientMessage(Component.literal("Current Luck Manipulation is: " + luckManipulationString(player)), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }
    public static String luckManipulationString(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int luckManipulation = tag.getInt("luckManipulationItem");
        if (luckManipulation == 1) {
            return "Regeneration";
        }
        if (luckManipulation == 2) {
            return "Diamonds";
        }
        if (luckManipulation == 3) {
            return "Wind Moving Projectiles";
        }
        if (luckManipulation == 4) {
            return "Halve Next Damage";
        }
        if (luckManipulation == 5) {
            return "Mobs will get Distracted from you";
        }
        if (luckManipulation == 6) {
            return "Players that hurt you recently will get poisoned and stunned";
        }
        if (luckManipulation == 7) {
            return "Ignore the next damage";
        }

        return null;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Upon use, activates your danger sense, alerting you of players around you and where they are"));
        tooltipComponents.add(Component.literal("Activation Cost: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("40 per second").withStyle(ChatFormatting.YELLOW)));
        Component.literal("Cooldown: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}