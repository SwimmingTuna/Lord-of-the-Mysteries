package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
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
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Placate extends Item {
    public Placate(Properties pProperties) {
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
                if (holder != null && holder.isSpectatorClass() && spectatorSequence.getCurrentSequence() <= 7 && spectatorSequence.getCurrentSequence() > 4 && spectatorSequence.useSpirituality(75)) {
                    AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                    halfHarmfulEffects(pPlayer);
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 120 / (int) dreamIntoReality.getValue());
                    }
                }
                if (holder.isSpectatorClass() && spectatorSequence.getCurrentSequence() <= 4 && spectatorSequence.useSpirituality(200)) {
                    AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                    removeHarmfulEffects(pPlayer);
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 240 / (int) dreamIntoReality.getValue());
                    }
                }
            });
        }
        return super.use(level, pPlayer, hand);
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
        System.out.println("removed");
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
        System.out.println("halved");
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
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, reduces or removes the targeted living entity's harmful potion effects\n" +
                    "Spirituality Used: 125\n" +
                    "Cooldown: 15 seconds").withStyle(ChatFormatting.AQUA));
            }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
        }
    }
