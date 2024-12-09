package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

public class ProbabilityManipulationWorldFortune extends SimpleAbilityItem {

    public ProbabilityManipulationWorldFortune(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 0, 1500, 600);
    }
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        worldFortuneManipulation(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof ProbabilityManipulationWorldFortune) {
                    player.displayClientMessage(Component.literal("Probability of fortunate events to happen will be amplified by: " + player.getPersistentData().getInt("probabilityManipulationWorldFortuneValue")), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    private void worldFortuneManipulation(Player player) {
        int value = player.getPersistentData().getInt("probabilityManipulationWorldFortuneValue");
        if (!player.level().isClientSide() && player.level() instanceof ServerLevel serverLevel) {
            WorldFortuneValue data = WorldFortuneValue.getInstance(serverLevel);
            data.setWorldFortune(value);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(
                "Upon use, exude an aura of tyranny, not giving any entity permission to move, implanting fear strong enough to not allow them to use their abilities"
        ).withStyle(/*ChatFormatting.BOLD, ChatFormatting.BLUE*/));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public static void probabilityManipulationWorld(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide() && livingEntity.tickCount % 20 == 0) {
            CompoundTag tag = livingEntity.getPersistentData();
            int fortune = tag.getInt("probabilityManipulationWorldFortuneValue");
            int misfortune = tag.getInt("probabilityManipulationWorldMisfortuneValue");
            if (livingEntity.getMainHandItem().getItem() instanceof ProbabilityManipulationWorldFortune) {
                if (fortune <= 4) {
                    tag.putInt("probabilityManipulationWorldFortuneValue", fortune + 1);
                } else {
                    tag.putInt("probabilityManipulationWorldFortuneValue", 0);
                }
            }
            if (livingEntity.getMainHandItem().getItem() instanceof ProbabilityManipulationWorldMisfortune) {
                if (misfortune <= 4) {
                    tag.putInt("probabilityManipulationWorldFortuneValue", misfortune + 1);
                } else {
                    tag.putInt("probabilityManipulationWorldFortuneValue", 0);
                }
            }
        }
    }
}
