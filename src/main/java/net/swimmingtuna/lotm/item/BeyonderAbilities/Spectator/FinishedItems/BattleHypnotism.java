package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BattleHypnotism extends SimpleAbilityItem {

    public BattleHypnotism(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 6, 150, 300,50,50);
    }

    @Override
    public InteractionResult useAbilityOnBlock(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player);
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        makesEntitiesAttackEachOther(player, player.level(), pContext.getClickedPos(), holder.getCurrentSequence(), (int) player.getAttribute(ModAttributes.DIR.get()).getValue());
        return InteractionResult.SUCCESS;
    }

    private void makesEntitiesAttackEachOther(Player player, Level level, BlockPos targetPos, int sequence, int dir) {
        double radius = 20.0 - sequence * dir;
        float damage = 15 - sequence;
        int duration = 400 - (sequence * 10);
        AABB boundingBox = new AABB(targetPos).inflate(radius);
        level.getEntitiesOfClass(LivingEntity.class, boundingBox, LivingEntity::isAlive).forEach(livingEntity -> {
            livingEntity.hurt(livingEntity.damageSources().magic(), damage);
            if (livingEntity != player) {
                if (livingEntity instanceof Player) {
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.BATTLEHYPNOTISM.get(), duration, (int) radius, false, false));
                } else {
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.BATTLEHYPNOTISM.get(), duration, 0, false, false));
                }
            }
        });
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, makes all living entities around the clicked location target the nearest player if one is present and each other if there isn't one\n" +
                "Spirituality Used: 150\n" +
                "Cooldown: 15 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }
}