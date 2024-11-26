package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EnvisionHealth extends SimpleAbilityItem {

    public EnvisionHealth(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 0, 0, 2400);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        int dreamIntoReality = (int) player.getAttribute(ModAttributes.DIR.get()).getValue();
        if (!checkAll(player, BeyonderClassInit.SPECTATOR.get(), 0, 3500 / dreamIntoReality)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player, 3500 / dreamIntoReality);
        envisionHealth(player);
        return InteractionResult.SUCCESS;
    }

    private void envisionHealth(Player player) {
        if (!player.level().isClientSide()) {
            AttributeInstance maxHP = player.getAttribute(Attributes.MAX_HEALTH);
            double maxHealth = maxHP.getValue();
            double health = player.getHealth();
            double x = (health + ((maxHealth - health) * 0.66));
            player.setHealth((float) x);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, heals to full health\n" +
                "Left Click for Envision Health\n" +
                "Spirituality Used: 4000\n" +
                "Cooldown: 2 minutes seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
