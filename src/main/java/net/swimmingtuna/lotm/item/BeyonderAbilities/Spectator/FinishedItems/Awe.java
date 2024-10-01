package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
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
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Awe extends Item {

    public Awe(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 75) {
                player.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 7 && holder.useSpirituality(75)) {
                AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
                applyPotionEffectToEntities(player);
                if (!player.getAbilities().instabuild)
                    player.getCooldowns().addCooldown(this, 240);
            }
        }
        return super.use(level, player, hand);
    }

    public static void applyPotionEffectToEntities(Player player) {
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int sequence = holder.getCurrentSequence();
        int dir = (int) dreamIntoReality.getValue();
        double radius = (15.0 - sequence) * dir;
        float damage = (float) (12.0 - (sequence/2));
        int duration = 250 - (sequence * 15);
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius))) {
            if (entity != player) {
                entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false)));
                entity.hurt(entity.damageSources().magic(), damage);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, makes all living entities around the user freeze in place and take damage\n" +
                "Spirituality Used: 75\n" +
                "Cooldown: 12 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

}
