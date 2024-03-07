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
            if (spectatorSequence.getCurrentSequence() <= 2 && spectatorSequence.useSpirituality(1000)) {
                removeCooldown(pPlayer);
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 900);
            }
        });
        return super.use(level, pPlayer, hand);
    }

    private void removeCooldown(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            for (int i = 0; i < pPlayer.getInventory().getContainerSize(); i++) {
                ItemStack itemStack = pPlayer.getInventory().getItem(i);
                if (!itemStack.isEmpty()) {
                    pPlayer.sendSystemMessage(Component.literal("in inventory:" + itemStack));
                    pPlayer.getCooldowns().removeCooldown(itemStack.getItem());
                }
            }
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, resets the cooldown of all your abilities\n" +
                    "Spirituality Used: 1000\n" +
                    "Cooldown: 45 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

}
