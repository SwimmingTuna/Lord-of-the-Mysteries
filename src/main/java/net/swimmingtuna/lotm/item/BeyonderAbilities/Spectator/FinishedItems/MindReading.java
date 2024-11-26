package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class MindReading extends SimpleAbilityItem {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public MindReading(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 8, 20, 60,12,12);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        mindRead(player, interactionTarget, stack);
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

        //reach should be___
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 12, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 12, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    public static boolean usedHand(Player player) {
        ItemStack mainHandStack = player.getMainHandItem();
        return mainHandStack.getItem() instanceof MindReading;
    }

    public static void mindRead(Player player, LivingEntity interactionTarget, ItemStack stack) {
        if (!player.level().isClientSide()) {
            if (interactionTarget instanceof Player playerInteractionTarget) {
                AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
                StringBuilder inventoryMessage = new StringBuilder();
                boolean hasItems = false;

                for (int i = 0; i < playerInteractionTarget.getInventory().getContainerSize(); i++) {
                    ItemStack itemStack = playerInteractionTarget.getInventory().getItem(i);
                    if (!itemStack.isEmpty()) {
                        hasItems = true;
                        inventoryMessage.append("\n- ").append(itemStack.getDisplayName().getString());
                    }
                }

                if (hasItems) {
                    String playerName = interactionTarget.getName().getString();
                    player.sendSystemMessage(Component.literal(playerName + "'s inventory contains:").withStyle(ChatFormatting.BOLD)
                            .append(Component.literal(inventoryMessage.toString()).withStyle(ChatFormatting.AQUA)));
                } else {
                    player.sendSystemMessage(Component.literal("The target player's inventory is empty.").withStyle(ChatFormatting.AQUA));
                }

                if (dreamIntoReality.getValue() == 2) {
                    interactionTarget.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1, false, false));
                }
            } else {
                Style style = BeyonderUtil.getStyle(player);
                player.sendSystemMessage(Component.literal("Interaction target isn't a player").withStyle(style));
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, tells you the inventory of the target player\n" +
                "Spirituality Used: 20\n" +
                "Cooldown: 3 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}

