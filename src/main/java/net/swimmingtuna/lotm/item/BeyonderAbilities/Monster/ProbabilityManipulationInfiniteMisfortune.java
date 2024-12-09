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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ProbabilityManipulationInfiniteMisfortune extends SimpleAbilityItem {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public ProbabilityManipulationInfiniteMisfortune(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 5, 0, 30, 150, 150);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with blocks, pretty much useless for this item
        return attributeBuilder.build();
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Shoots out a lightning bolt in the direction you look"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("200 for blocks/entities, 120 when using on air").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("~1 second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(BeyonderClassInit.SAILOR.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(5, BeyonderClassInit.SAILOR.get()));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack pStack, Player player, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        giveInfiniteMisfortune(pInteractionTarget);
        addCooldown(player, this, 10 + holder.getCurrentSequence());
        useSpirituality(player, 200);
        return InteractionResult.SUCCESS;
    }
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) { //add if cursor is on a projectile, lightning goes to projectile and pwoers it
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        giveInfiniteMisfortune(player);
        addCooldown(player, this, 10 + holder.getCurrentSequence());
        useSpirituality(player, 200);
        return InteractionResult.SUCCESS;
    }

    public static void giveInfiniteMisfortune(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            livingEntity.getPersistentData().putInt("probabilityManipulationInfiniteMisfortune", 3600);
        }
    }

    public static void infiniteFortuneMisfortuneTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            CompoundTag tag = entity.getPersistentData();
            int x = tag.getInt("probabilityManipulationInfiniteMisfortune");
            int y = tag.getInt("probabilityManipulationInfiniteFortune");
            if (BeyonderUtil.isBeyonderCapable(entity)) {
                if (x >= 1) {
                    tag.putInt("probabilityManipulationInfiniteMisfortune", x -1);
                    entity.getAttribute(ModAttributes.MISFORTUNE.get()).setBaseValue(777);
                }
                if (y >= 1) {
                    tag.putInt("probabilityManipulationInfiniteFortune", y - 1);
                    entity.getAttribute(ModAttributes.LOTM_LUCK.get()).setBaseValue(777);
                }
            }
        }
    }
}
