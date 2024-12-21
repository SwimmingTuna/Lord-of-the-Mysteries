package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FalseProphecy extends SimpleAbilityItem {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public FalseProphecy(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 2, 300, 500, 150, 150);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            useSpirituality(player);
            addCooldown(player);
            manipulateMisfortune(interactionTarget, player);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, gives a false prophecy to the entity you interacted with. They will get a contradictory message, if they choose to ignore it, nothing will happen, if they act on it, then the opposite of what they say, and what you chose, will happen."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("300").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("25 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private static void manipulateMisfortune(LivingEntity interactionTarget, Player player) {
        if (!player.level().isClientSide() && !interactionTarget.level().isClientSide()) {
            CompoundTag tag = interactionTarget.getPersistentData();
            CompoundTag playerTag = player.getPersistentData();
            int falseProphecyItem = playerTag.getInt("falseProphecyItem");
            if (falseProphecyItem == 1) {
                tag.putInt("harmfulFalseProphecyShift", 200);
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you shift for a while, you'll recieve great fortune").withStyle(ChatFormatting.GREEN));
                }
            }
            if (falseProphecyItem == 2) {
                tag.putInt("harmfulFalseProphecyStand", 200);
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you stand still for a while, you'll be healed to full health").withStyle(ChatFormatting.GREEN));
                }
            }
            if (falseProphecyItem == 3) {
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you jump a few times, you'll be able to use your abilities instantly").withStyle(ChatFormatting.GREEN));
                }
                tag.putInt("harmfulFalseProphecyJump", 200);
            }
            if (falseProphecyItem == 4) {
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you sprint for a while, you'll become immune to the next damage you take").withStyle(ChatFormatting.GREEN));
                }
                tag.putInt("harmfulFalseProphecySprint", 200);
            }
            if (falseProphecyItem == 5) {
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you attack something soon, your next melee hits will deal extra damage").withStyle(ChatFormatting.GREEN));
                }
                tag.putInt("harmfulFalseProphecyAttack", 200);
            }
            if (falseProphecyItem == 6) {
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you shift for a while, you'll be granted great misfortune").withStyle(ChatFormatting.RED));
                }
                tag.putInt("beneficialFalseProphecyShift", 200);
            }
            if (falseProphecyItem == 7) {
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you stand still for a while, you'll be given a great illness").withStyle(ChatFormatting.RED));
                }
                tag.putInt("beneficialFalseProphecyStand", 200);
            }
            if (falseProphecyItem == 8) {
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you jump a few times, your abilities will be put on cooldown").withStyle(ChatFormatting.RED));
                }
                tag.putInt("beneficialFalseProphecyJump", 200);
            }
            if (falseProphecyItem == 9) {
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you sprint for a while, you'll take extra damage the next times you're hurt").withStyle(ChatFormatting.RED));
                }
                tag.putInt("beneficialFalseProphecySprint", 200);
            }
            if (falseProphecyItem == 10) {
                if (interactionTarget instanceof Player) {
                    interactionTarget.sendSystemMessage(Component.literal("If you attack something in the next 10 seconds, your subsequent melee hits wont deal damage").withStyle(ChatFormatting.RED));
                }
                tag.putInt("beneficialFalseProphecyAttack", 200);
            }
        }

    }

    //make some strings /n
    //harmful cooldown jump is positive

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof FalseProphecy) {
                    player.displayClientMessage(falseProphecyString(player), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    public static Component falseProphecyString(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int falseProphecyString = tag.getInt("falseProphecyItem");
        if (falseProphecyString == 1) {
            return Component.literal("Shifting will give great misfortune").withStyle(ChatFormatting.RED);
        }
        if (falseProphecyString == 2) {
            return Component.literal("Standing still will cause a grave illness").withStyle(ChatFormatting.RED);
        }
        if (falseProphecyString == 3) {
            return Component.literal("Jumping will cause all abilities to go on cooldown").withStyle(ChatFormatting.RED);
        }
        if (falseProphecyString == 4) {
            return Component.literal("Sprinting will cause great damage").withStyle(ChatFormatting.RED);
        }
        if (falseProphecyString == 5) {
            return Component.literal("Attacking a mob will cause the next 5 damage instances the player takes to be doubled as true damage").withStyle(ChatFormatting.RED);
        }
        if (falseProphecyString == 6) {
            return Component.literal("Shifting will give great fortune").withStyle(ChatFormatting.GREEN);
        }
        if (falseProphecyString == 7) {
            return Component.literal("Standing still will give many beneficial effects").withStyle(ChatFormatting.GREEN);
        }
        if (falseProphecyString == 8) {
            return Component.literal("Jumping will cause their ability cooldowns to be reset").withStyle(ChatFormatting.GREEN);
        }
        if (falseProphecyString == 9) {
            return Component.literal("Sprinting will cause the player to ignore the next 5 times they take damage to be nullified").withStyle(ChatFormatting.GREEN);
        }
        if (falseProphecyString == 10) {
            return Component.literal("Attacking a mob will cause the next 5 melee hits to deal double damage as true damage").withStyle(ChatFormatting.GREEN);
        } else {
            return Component.literal("None").withStyle(ChatFormatting.GRAY);
        }
    }

    public static void falseProphecyTick(LivingEntity livingEntity) {
        CompoundTag tag = livingEntity.getPersistentData();
        int harmfulShift = tag.getInt("harmfulFalseProphecyShift");
        int harmfulStand = tag.getInt("harmfulFalseProphecyStand");
        int harmfulSprint = tag.getInt("harmfulFalseProphecySprint");
        int harmfulJump = tag.getInt("harmfulFalseProphecyJump");
        int harmfulAttack = tag.getInt("harmfulFalseProphecyAttack");
        int beneficialShift = tag.getInt("beneficialFalseProphecyShift");
        int beneficialStand = tag.getInt("beneficialFalseProphecyStand");
        int beneficialSprint = tag.getInt("beneficialFalseProphecySprint");
        int beneficialJump = tag.getInt("beneficialFalseProphecyJump");
        int beneficialAttack = tag.getInt("beneficialFalseProphecyAttack");
        if (harmfulShift >= 1) {
            tag.putInt("harmfulFalseProphecyShift", harmfulShift - 1);
            int x = tag.getInt("falseProphecyShiftHarmful");
            if (livingEntity.isShiftKeyDown()) {
                tag.putInt("falseProphecyShiftHarmful", x + 1);
            }
            if (x >= 60) {
                tag.putInt("falseProphecyShiftHarmful", 0);
                tag.putInt("harmfulFalseProphecyShift", 0);
                livingEntity.sendSystemMessage(Component.literal("Shift Harmful Activated"));
                livingEntity.getPersistentData().putDouble("misfortune", livingEntity.getPersistentData().getDouble("misfortune") + 50);
            }
        }
        if (harmfulStand >= 1) {
            if (!BeyonderUtil.isLivingEntityMoving(livingEntity)) {
                tag.putInt("falseProphecyStandHarmful", tag.getInt("falseProphecyStandHarmful") + 1);
            }
            if (tag.getInt("falseProphecyStandHarmful") >= 100) {
                tag.putInt("falseProphecyStandHarmful", 0);
                tag.putInt("harmfulFalseProphecyStand", 0);
                BeyonderUtil.applyMobEffect(livingEntity, ModEffects.BLEEDING.get(), 400, 7, false, false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.BLINDNESS, 400, 5, false, false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.HARM, 400, 10, false, false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.WEAKNESS, 400, 4, false, false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.MOVEMENT_SLOWDOWN, 400, 4, false, false);
            }
            tag.putInt("harmfulFalseProphecyStand", harmfulStand - 1);
        }
        if (harmfulSprint >= 1) {
            int x = tag.getInt("falseProphecySprintHarmful");
            tag.putInt("harmfulFalseProphecySprint", harmfulSprint - 1);
            if (livingEntity.isSprinting()) {
                tag.putInt("falseProphecySprintHarmful", x + 1);
            }
            if (x >= 60) {
                tag.putInt("falseProphecySprintHarmful", 0);
                tag.putInt("harmfulFalseProphecySprint", 0);
                livingEntity.hurt(BeyonderUtil.magicSource(livingEntity), 40);
                livingEntity.sendSystemMessage(Component.literal("Sprint hurt"));
            }
        }
        if (harmfulJump >= 1) {
            tag.putInt("harmfulFalseProphecyJump", harmfulJump - 1);
        }
        if (harmfulJump == 1) {
            tag.putInt("falseProphecyJumpHarmful", 0);
        }
        if (harmfulAttack >= 1) {
            tag.putInt("harmfulFalseProphecyAttack", harmfulAttack - 1);
        }
        if (beneficialShift >= 1) {
            tag.putInt("beneficialFalseProphecyShift", beneficialShift - 1);
            int x = tag.getInt("falseProphecyShiftBeneficial");
            if (livingEntity.isShiftKeyDown()) {
                tag.putInt("falseProphecyShiftBeneficial", x + 1);
            }
            if (x >= 60) {
                tag.putInt("falseProphecyShiftBeneficial", 0);
                tag.putInt("beneficialFalseProphecyShift", 0);
                if (BeyonderUtil.isBeyonderCapable(livingEntity)) {
                    livingEntity.getPersistentData().putDouble("luck", livingEntity.getPersistentData().getDouble("luck") + 50);
                }
            }
        }


        if (beneficialStand >= 1) {
            if (livingEntity.tickCount % 20 == 0) {
                livingEntity.sendSystemMessage(Component.literal("value is " + beneficialStand));
            }
            if (!BeyonderUtil.isLivingEntityMoving(livingEntity)) {
                tag.putInt("falseProphecyStandBeneficial", tag.getInt("falseProphecyStandBeneficial") + 1);
            }

            if (tag.getInt("falseProphecyStandBeneficial") >= 100) {
                livingEntity.sendSystemMessage(Component.literal("Worked"));
                tag.putInt("falseProphecyStandBeneficial", 0);
                tag.putInt("beneficialFalseProphecyStand", 0);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.ABSORPTION, 1200, 20, false, false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.DAMAGE_BOOST, 600, 5, false, false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.DIG_SPEED, 600, 4, false, false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.MOVEMENT_SPEED, 600, 3, false, false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.REGENERATION, 600, 5, false, false);
            }
            tag.putInt("beneficialFalseProphecyStand", beneficialStand - 1);
        }


        if (beneficialSprint >= 1) {
            int x = tag.getInt("falseProphecySprintBeneficial");
            tag.putInt("beneficialFalseProphecySprint", beneficialSprint - 1);
            if (livingEntity.isSprinting()) {
                tag.putInt("falseProphecySprintBeneficial", x + 1);
            }
            if (x >= 60) {
                tag.putInt("falseProphecySprintBeneficial", 0);
                tag.putInt("beneficialFalseProphecySprint", 0);
                tag.putInt("luckIgnoreDamage", 3);
            }
        }
        if (beneficialJump >= 1) {
            tag.putInt("beneficialFalseProphecyJump", beneficialJump - 1);
        }
        if (beneficialAttack >= 1) {
            tag.putInt("beneficialFalseProphecyAttack", beneficialAttack - 1);
        }

        if (tag.getInt("falseProphecyJumpHarmful") >= 5 && livingEntity instanceof Player pPlayer) {
            for (ItemStack stack : pPlayer.getInventory().items) {
                if (stack.getItem() instanceof SimpleAbilityItem simpleAbilityItem) {
                    int currentCooldown = (int) pPlayer.getCooldowns().getCooldownPercent(stack.getItem(), 0);
                    int cooldownToSet = simpleAbilityItem.getCooldown() * (100 - currentCooldown) / 100;

                    if (currentCooldown < cooldownToSet) {
                        pPlayer.getCooldowns().addCooldown(stack.getItem(), cooldownToSet);
                    }
                }
            }
            tag.putInt("falseProphecyJumpHarmful", 0);
            tag.putInt("harmfulFalseProphecyJump", 0);
        }
        if (tag.getInt("falseProphecyJumpBeneficial") >= 5 && livingEntity instanceof Player pPlayer) {
            for (ItemStack stack : pPlayer.getInventory().items) {
                if (stack.getItem() instanceof SimpleAbilityItem) {
                    pPlayer.getCooldowns().addCooldown(stack.getItem(), 0);
                }
            }
            tag.putInt("falseProphecyJumpBeneficial", 0);
            tag.putInt("beneficialFalseProphecyJump", 0);
        }
    }
}