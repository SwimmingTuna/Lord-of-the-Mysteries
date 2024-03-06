package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EnvisionLife extends Item {

    public EnvisionLife(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() == 0 && spectatorSequence.useSpirituality(4000)) {
                setHealth(pPlayer);
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 2400);
            }
        });
        return super.use(level, pPlayer, hand);
    }

    private void setHealth(Player pPlayer) {
        AttributeInstance maxHP = pPlayer.getAttribute(Attributes.MAX_HEALTH);
        double maxHealth = maxHP.getValue();
        pPlayer.setHealth((float) maxHealth);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, makes all living entities around the user freeze in place\n" +
                    "Spirituality Used: 4000\n" +
                    "Cooldown: 2 minutes seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

}
