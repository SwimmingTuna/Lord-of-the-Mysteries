package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Nightmare extends Item {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public Nightmare(Properties properties) {
        super(properties);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 35, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 35, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 100) {
                player.displayClientMessage(Component.literal("You need 100 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
        }
        Level level = player.level();
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        BlockPos positionClicked = context.getClickedPos();
        if (!context.getLevel().isClientSide) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 5 &&  BeyonderHolderAttacher.getHolderUnwrap(player).useSpirituality(100)) {
                useNightmare(player, level, positionClicked, holder.getCurrentSequence(), (int) dreamIntoReality.getValue());
                if (!player.getAbilities().instabuild) {
                    player.getCooldowns().addCooldown(this, 110);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void useNightmare(Player player, Level level, BlockPos targetPos, int sequence, int dir) {
        double radius = 25.0 - sequence;
        float damagePlayer = ((float) 120.0 - (sequence * 10)) * dir;
        float damageMob = ((float) (50.0 - (sequence * 3)) / 2) * dir;

        int duration = 200 - (sequence * 20);

        AABB boundingBox = new AABB(targetPos).inflate(radius);
        level.getEntitiesOfClass(LivingEntity.class, boundingBox, entity -> entity.isAlive()).forEach(livingEntity -> {
            AttributeInstance nightmareAttribute = livingEntity.getAttribute(ModAttributes.NIGHTMARE.get());
            String playerName = livingEntity.getDisplayName().getString();
            if (livingEntity != player) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, duration, 1, false, false));
                if (livingEntity instanceof Player) {
                    if (nightmareAttribute.getValue() < 3) {
                        nightmareAttribute.setBaseValue(nightmareAttribute.getValue() + 1);}
                if (nightmareAttribute.getValue() == 3) {
                    livingEntity.hurt(livingEntity.damageSources().magic(), damagePlayer);
                    nightmareAttribute.setBaseValue(0);
                }
                    player.sendSystemMessage(Component.literal(playerName + "'s nightmare value is:" + (int) nightmareAttribute.getValue()).withStyle(BeyonderUtil.getStyle(player)));

                }
                else {
                    livingEntity.hurt(livingEntity.damageSources().magic(), damageMob);
                }
            }
        });
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, makes all players around the target enter a nightmare, plunging them into darkness. If this is used 3 times on a player within 30 seconds, they take immense damage. If it's used on a mob, they take less the damage without having to be hit multiple times.\n" +
                "Spirituality Used: 100\n" +
                "Cooldown: 5.5 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
