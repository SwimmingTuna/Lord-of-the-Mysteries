package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

public class LuckDenial extends SimpleAbilityItem {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public LuckDenial(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 4, 300, 300,35,35);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player);
        giftLuck(interactionTarget, player);
        }
        return InteractionResult.SUCCESS;
    }

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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 20, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 20, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on a living entity, gives you the ability to manipulate them for 30 seconds\n" +
                "Left Click for Manipulate Emotion\n" +
                "Spirituality Used: 50\n" +
                "Cooldown: None").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }


    private static void giftLuck(LivingEntity interactionTarget, Player player) {
        if (!player.level().isClientSide() && !interactionTarget.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            AttributeInstance luck = interactionTarget.getAttribute(ModAttributes.LOTM_LUCK.get());
            AttributeInstance misfortune = interactionTarget.getAttribute(ModAttributes.MISFORTUNE.get());
            CompoundTag tag =  interactionTarget.getPersistentData();
            double misfortuneAmount = misfortune.getBaseValue();
            double luckAmount = luck.getBaseValue();
            if (holder.getCurrentSequence() <= 2) {
                tag.putDouble("luckDenialTimer", 1800 - (holder.getCurrentSequence()) * 150);
                tag.putDouble("luckDenialLuck", luckAmount);
                tag.putDouble("luckDenialMisfortune", misfortuneAmount);
            } else {
                tag.putDouble("luckDenialTimer", 1800 - (holder.getCurrentSequence()) * 150);
                tag.putDouble("luckDenialLuck", luckAmount);
            }
        }
    }
}