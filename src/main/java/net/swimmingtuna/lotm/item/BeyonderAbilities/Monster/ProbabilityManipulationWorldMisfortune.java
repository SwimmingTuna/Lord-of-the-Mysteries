package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.world.worlddata.WorldFortuneValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ProbabilityManipulationWorldMisfortune extends SimpleAbilityItem {

    public ProbabilityManipulationWorldMisfortune(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 0, 1500, 600);
    }
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        worldMisfortuneManipulation(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof ProbabilityManipulationWorldMisfortune) {
                    player.displayClientMessage(Component.literal("Probability of misfortunate events to happen will be amplified by: " + player.getPersistentData().getInt("probabilityManipulationWorldMisfortuneValue")), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    private void worldMisfortuneManipulation(Player player) {
        int value = player.getPersistentData().getInt("probabilityManipulationWorldMisfortuneValue");
        if (!player.level().isClientSide() && player.level() instanceof ServerLevel serverLevel) {
            WorldFortuneValue data = WorldFortuneValue.getInstance(serverLevel);
            data.setWorldFortune(value);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, increases the chances of misfortunate events occuring to everything by the chosen amount"));
        tooltipComponents.add(Component.literal("Left click for Probability Manipulation: Wipe"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("1500").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("30 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
