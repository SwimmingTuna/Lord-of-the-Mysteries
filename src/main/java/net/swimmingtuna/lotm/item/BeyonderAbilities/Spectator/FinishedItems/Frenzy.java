package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Frenzy extends Item {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public Frenzy(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(pSlot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
            attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 12, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 12, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player pPlayer = context.getPlayer();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (holder == null) return InteractionResult.FAIL;
        if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
            pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            return InteractionResult.FAIL;
        }
        if (holder.getSpirituality() < 125) {
            pPlayer.displayClientMessage(Component.literal("You need 125 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            return InteractionResult.FAIL;
        }
        if (pPlayer.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Level level = pPlayer.level();
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        BlockPos positionClicked = context.getClickedPos();
        if (holder.getCurrentSequence() <= 7 && holder.useSpirituality(125)) {
            applyPotionEffectToEntities(pPlayer, level, positionClicked, holder.getCurrentSequence(), (int) dreamIntoReality.getValue());
            if (!pPlayer.getAbilities().instabuild) {
                pPlayer.getCooldowns().addCooldown(this, 300);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void applyPotionEffectToEntities(Player pPlayer, Level level, BlockPos targetPos, int sequence, int dreamIntoRealityValue) {
        double radius = (15.0 - sequence) * dreamIntoRealityValue;
        float damage = (float) (18.0 - (sequence / 2));
        int duration = 250 - (sequence * 12) * dreamIntoRealityValue;

        AABB boundingBox = new AABB(targetPos).inflate(radius);
        level.getEntitiesOfClass(LivingEntity.class, boundingBox, LivingEntity::isAlive).forEach(livingEntity -> {
            if (livingEntity != pPlayer) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.FRENZY.get(), duration, 1, false, false));
                livingEntity.hurt(livingEntity.damageSources().magic(), damage);
            }
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, makes all living entities around the targeted block lose control of their movement\n" +
                    "Spirituality Used: 125\n" +
                    "Cooldown: 15 seconds").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}