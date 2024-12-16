package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Placate extends SimpleAbilityItem {

    public Placate(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 7, 125, 15);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!checkAll(player, BeyonderClassInit.SPECTATOR.get(), 7, 125)) {
            return InteractionResult.FAIL;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder.getCurrentSequence() >= 4) {
            removeHarmfulEffects(interactionTarget);
            addCooldown(player);
            useSpirituality(player);
            return InteractionResult.SUCCESS;
        }
        else {
            halfHarmfulEffects(interactionTarget);
            addCooldown(player);
            useSpirituality(player);
            return InteractionResult.SUCCESS;
        }
    }
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player, BeyonderClassInit.SPECTATOR.get(), 7, 125)) {
            return InteractionResult.FAIL;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder.getCurrentSequence() >= 4) {
            removeHarmfulEffects(player);
            addCooldown(player);
            useSpirituality(player);
            return InteractionResult.SUCCESS;
        }
        else {
            halfHarmfulEffects(player);
            addCooldown(player);
            useSpirituality(player);
            return InteractionResult.SUCCESS;
        }
    }

    public static void removeHarmfulEffects(LivingEntity entity) {
        if (!entity.level().isClientSide()) {
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
    }

    public static void halfHarmfulEffects(LivingEntity entity) {
        // Collect harmful effects to modify later
        if (!entity.level().isClientSide()) {
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
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on an entity, placate them, removing or halving their harmful potion effects, depending on your sequence. "));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("250").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("10 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

}
