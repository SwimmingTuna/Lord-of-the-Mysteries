package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SpiritVision extends SimpleAbilityItem {

    public SpiritVision(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 0, 3500, 2400);
    }
                                                                    //required class            required sequence   how much "mana"     how long the cooldown is in ticks (20 ticks = 1 second)
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) return InteractionResult.FAIL;
        useSpirituality(player);
        addCooldown(player);
        applyPotionEffectToEntities(player);
        return InteractionResult.SUCCESS;
    }

    private void applyPotionEffectToEntities(Player player) {
        //logic for ability
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(
                "Upon use, exude an aura of tyranny, not giving any entity permission to move, implanting fear strong enough to not allow them to use their abilities"
        ).withStyle(/*ChatFormatting.BOLD, ChatFormatting.BLUE*/));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
        //the description of the item, don't worry about this yet though
}
