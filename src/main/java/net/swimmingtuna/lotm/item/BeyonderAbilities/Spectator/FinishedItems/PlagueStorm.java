package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class PlagueStorm extends SimpleAbilityItem {

    public PlagueStorm(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 8, 400, 160);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        plagueStorm(player, interactionTarget);
        return InteractionResult.SUCCESS;
    }

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);


    @SuppressWarnings("deprecation")
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 80, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 80, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on a living entity, all living entities around it gain Darkness, Wither, Blindness, and Confusion, as well as take damage\n" +
                "Spirituality Used: 400\n" +
                "Cooldown: 8 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public void plagueStorm(Player player, LivingEntity interactionTarget) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        interactionTarget.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 1, false, false));
        for (LivingEntity entityInRange : interactionTarget.level().getEntitiesOfClass(LivingEntity.class, interactionTarget.getBoundingBox().inflate(30 * dreamIntoReality.getValue()))) {
            if (entityInRange == player) {
                continue;
            }
            if (entityInRange != interactionTarget) {
                entityInRange.hurt(entityInRange.damageSources().magic(), (float) ((20 - (holder.getCurrentSequence() * 3)) * dreamIntoReality.getValue()));
            } else {
                entityInRange.hurt(entityInRange.damageSources().magic(), (float) ((40 - (holder.getCurrentSequence() * 6)) * dreamIntoReality.getValue()));
            }
            entityInRange.addEffect(new MobEffectInstance(MobEffects.DARKNESS,80, 1, false, false));
            entityInRange.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 2, false, false));
            entityInRange.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 1, false, false));
            entityInRange.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 1, false, false));
        }
    }

}
