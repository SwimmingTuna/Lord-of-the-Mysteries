package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Awe extends Item {

    public Awe(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (holder.isSpectatorClass() && spectatorSequence.getCurrentSequence() <= 7 && spectatorSequence.useSpirituality(75)) {
                    AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                    applyPotionEffectToEntities(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    public static void applyPotionEffectToEntities(Player pPlayer) {
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        int sequence = holder.getCurrentSequence();
        pPlayer.sendSystemMessage(Component.literal("WORKING WORKING"));
        pPlayer.sendSystemMessage(Component.literal("WORKING WORKING"));
        pPlayer.sendSystemMessage(Component.literal("WORKING WORKING"));
        pPlayer.sendSystemMessage(Component.literal("WORKING WORKING"));
        int dir = (int) dreamIntoReality.getValue();
        double radius = (15.0 - sequence) * dir;
        float damage = (float) (12.0 - (sequence/2));
        int duration = 250 - (sequence * 15);
        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
            if (entity != pPlayer) {
                entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false)));
                entity.hurt(entity.damageSources().magic(), damage);
            }
        }
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
