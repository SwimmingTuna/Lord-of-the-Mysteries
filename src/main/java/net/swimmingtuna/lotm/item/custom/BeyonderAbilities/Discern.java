package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Discern extends Item {

    public Discern(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 7 && spectatorSequence.useSpirituality(75)) {
                applyPotionEffectToEntities(pPlayer, spectatorSequence.getCurrentSequence(), hand);
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 240);
            }
        });
        return super.use(level, pPlayer, hand);
    }

    private void applyPotionEffectToEntities(Player pPlayer, int sequence, InteractionHand hand) {
       ItemCooldowns cooldowns = pPlayer.getCooldowns();
       ItemStack itemStack = pPlayer.getItemInHand(hand);
       pPlayer.getCooldowns().removeCooldown(itemStack.getItem());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, makes all living entities around the user freeze in place\n" +
                    "Spirituality Used: 75\n" +
                    "Cooldown: 12 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

}
