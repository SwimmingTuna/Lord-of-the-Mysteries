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
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Nightmare extends SimpleAbilityItem {

    public Nightmare(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 5, 100, 110,35,35 );
    }

    @Override
    public InteractionResult useAbilityOnBlock(UseOnContext pContext) {
        Player player = pContext.getPlayer();

        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        nightmare(player, pContext.getLevel(), pContext.getClickedPos());
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 35, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 35, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }


    private void nightmare(Player player, Level level, BlockPos targetPos) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
            int sequence = holder.getCurrentSequence();
            int dir = (int) dreamIntoReality.getValue();
            double radius = 25.0 - sequence;
            float damagePlayer = ((float) 120.0 - (sequence * 10)) * dir;
            float damageMob = ((float) (60.0 - (sequence * 3)) / 2) * dir;

            int duration = 200 - (sequence * 20);

            AABB boundingBox = new AABB(targetPos).inflate(radius);
            level.getEntitiesOfClass(LivingEntity.class, boundingBox, entity -> entity.isAlive()).forEach(livingEntity -> {
                AttributeInstance nightmareAttribute = livingEntity.getAttribute(ModAttributes.NIGHTMARE.get());
                String playerName = livingEntity.getDisplayName().getString();
                if (livingEntity != player) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, duration, 1, false, false));
                    if (livingEntity instanceof Player) {
                        if (nightmareAttribute.getValue() < 3) {
                            if (sequence <= 2) {
                                nightmareAttribute.setBaseValue(nightmareAttribute.getValue() + 2);
                            } else {
                                nightmareAttribute.setBaseValue(nightmareAttribute.getValue() + 1);
                            }
                        }
                        if (nightmareAttribute.getValue() == 3) {
                            livingEntity.hurt(livingEntity.damageSources().magic(), damagePlayer);
                            nightmareAttribute.setBaseValue(0);
                        }
                        player.sendSystemMessage(Component.literal(playerName + "'s nightmare value is:" + (int) nightmareAttribute.getValue()).withStyle(BeyonderUtil.getStyle(player)));

                    } else {
                        livingEntity.hurt(livingEntity.damageSources().magic(), damageMob);
                    }
                }
            });
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, makes all entities around the clicked block enter a nightmare, plunging them into darkness. If a player is hit by this three times in 30 seconds, they take immense damage. If a mob is hit, they take damage immediately, but less."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("1500").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("45 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
