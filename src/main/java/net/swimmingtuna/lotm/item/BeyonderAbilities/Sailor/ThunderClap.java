package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ThunderClap extends Item {

    public ThunderClap(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 700) {
                pPlayer.displayClientMessage(Component.literal("You need 700 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && spectatorSequence.getCurrentSequence() <= 3 && spectatorSequence.useSpirituality(700)) {
                    applyPotionEffectToEntities(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 400);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }
    private void applyPotionEffectToEntities(Player pPlayer) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        int sequence = holder.getCurrentSequence();
        double radius = 300 - (sequence * 50);
        int duration = 100 - (sequence * 20);
        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
            if (entity != pPlayer) {
                entity.addEffect((new MobEffectInstance(ModEffects.STUN.get(), duration, 1, false, false)));
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, claps together in order to create a thunder clap, stunning all entities freezing them in place and preventing them from using their abilities\n" +
                    "Spirituality Used: 400\n" +
                    "Cooldown: 20 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

}
