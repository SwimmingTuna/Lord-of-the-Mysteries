package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
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
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Placate extends Item {
    public Placate(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
            player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        if (holder.getSpirituality() < 75) {
            player.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 7 && holder.getCurrentSequence() > 4 && holder.useSpirituality(75)) {
            AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
            halfHarmfulEffects(player);
            if (!player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(this, 120 / (int) dreamIntoReality.getValue());
            }
        }
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 4 && holder.useSpirituality(200)) {
            AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
            removeHarmfulEffects(player);
            if (!player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(this, 240 / (int) dreamIntoReality.getValue());
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResult.FAIL;
            }

            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!SimpleAbilityItem.checkAll(player, BeyonderClassInit.SPECTATOR.get(), 7, 120)) {
                return InteractionResult.FAIL;
            }
            if (holder.getCurrentSequence() <= 4 && holder.useSpirituality(250)) {
                Placate.removeHarmfulEffects(interactionTarget);
                if (!player.isCreative()) {
                    player.getCooldowns().addCooldown(stack.getItem(), 120);
                }
                return InteractionResult.SUCCESS;
            }
            holder.useSpirituality(120);
            AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());

            Placate.halfHarmfulEffects(interactionTarget);
            player.getCooldowns().addCooldown(stack.getItem(), 120 / (int) dreamIntoReality.getValue());
            return InteractionResult.SUCCESS;

        }
        return super.interactLivingEntity(stack, player, interactionTarget, hand);
    }

    public static void removeHarmfulEffects(LivingEntity entity) {
        List<MobEffect> effectsToRemove = new ArrayList<>();
        for (MobEffectInstance effect : entity.getActiveEffects()) {
            MobEffect type = effect.getEffect();
            if (!type.isBeneficial()) {
                effectsToRemove.add(type);
            }
        }
        for (MobEffect effect : effectsToRemove) {
            entity.removeEffect(effect);
        }
    }

    public static void halfHarmfulEffects(LivingEntity entity) {
        // Collect harmful effects to modify later
        List<MobEffectInstance> effectsToModify = new ArrayList<>();
        for (MobEffectInstance effect : entity.getActiveEffects()) {
            MobEffect type = effect.getEffect();
            if (!type.isBeneficial()) {
                effectsToModify.add(effect);
            }
        }
        // Modify duration of each collected effect
        for (MobEffectInstance effect : effectsToModify) {
            MobEffect type = effect.getEffect();
            int newDuration = (effect.getDuration() + 1) / 2;

            // Remove the current effect and re-add with new duration
            entity.removeEffect(type);
            entity.addEffect(new MobEffectInstance(type, newDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(
                "Upon use, reduces or removes the targeted living entity's harmful potion effects\n" +
                        "Spirituality Used: 125\n" +
                        "Cooldown: 15 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
