package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FalseProphecy extends SimpleAbilityItem {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public FalseProphecy(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 4, 350, 200, 50, 50);
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

    private static void manipulateMisfortune(LivingEntity interactionTarget, Player player) {
        if (!player.level().isClientSide() && !interactionTarget.level().isClientSide()) {
            CompoundTag tag = interactionTarget.getPersistentData();
            CompoundTag playerTag = player.getPersistentData();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int falseProphecyItem = playerTag.getInt("falseProphecyItem");
            if (falseProphecyItem == 1) {
                tag.putInt("harmfulFalseProphecyShift", 200); //not done
            }
            if (falseProphecyItem == 2) {
                tag.putInt("harmfulFalseProphecyStand", 200); //not done
            }
            if (falseProphecyItem == 3) {
                tag.putInt("harmfulFalseProphecyJump", 200);
            }
            if (falseProphecyItem == 4) {
                tag.putInt("harmfulFalseProphecySprint", 200);
            }
            if (falseProphecyItem == 5) {
                tag.putInt("harmfulFalseProphecyAttack", 200);
            }
            if (falseProphecyItem == 6) {
                tag.putInt("beneficialFalseProphecyShift", 200); //not done
            }
            if (falseProphecyItem == 7) {
                tag.putInt("beneficialFalseProphecyStand", 200); //not done
            }
            if (falseProphecyItem == 8) {
                tag.putInt("beneficialFalseProphecyJump", 200);
            }
            if (falseProphecyItem == 9) {
                tag.putInt("beneficialFalseProphecySprint", 200);
            }
            if (falseProphecyItem == 10) {
                tag.putInt("beneficialFalseProphecyAttack", 200);
            }
        }

    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof FalseProphecy) {
                    player.displayClientMessage(Component.literal("Current False Prophecy  is: " + falseProphecyString(player)), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    public static String falseProphecyString(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int falseProphecyString = tag.getInt("falseProphecyItem");
        if (falseProphecyString == 1) {
            return "Harmful: Shifting will give great misfortune";
        }
        if (falseProphecyString == 2) {
            return "Harmful: Standing still for a Meteor to strike";
        }
        if (falseProphecyString == 3) {
            return "Harmful: Jumping will cause all abilities to go on cooldown";
        }
        if (falseProphecyString == 4) {
            return "Harmful: Sprinting will give great misfortune";
        }
        if (falseProphecyString == 5) {
            return "Harmful: Attacking a mob will attract a sudden storm";
        }
        if (falseProphecyString == 6) {
            return "Beneficial: Shifting will give great fortune";
        }
        if (falseProphecyString == 7) {
            return "Beneficial: Standing still will reset ability cooldowns";
        }
        if (falseProphecyString == 8) {
            return "Beneficial: Jumping will cause their ability cooldowns to be reset";
        }
        if (falseProphecyString == 9) {
            return "Beneficial: Sprinting will give great fortune";
        }
        if (falseProphecyString == 10) {
            return "Beneficial: Attacking a mob will cause the next 5 melee hits to deal double damage as true damage";
        }
        return "None";
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
                if (BeyonderUtil.isBeyonderCapable(livingEntity)) {
                    AttributeInstance misfortune = livingEntity.getAttribute(ModAttributes.MISFORTUNE.get());
                    misfortune.setBaseValue(misfortune.getBaseValue() + 50);
                }
            }
        }
        if (harmfulStand >= 1) {
            int tickCounter = tag.getInt("falseProphecyTickCounter");
            double prevX = tag.getDouble("falseProphecyPrevX");
            double prevY = tag.getDouble("falseProphecyPrevY");
            double prevZ = tag.getDouble("falseProphecyPrevZ");
            double currentX = tag.getDouble("falseProphecyCurrentX");
            double currentY = tag.getDouble("falseProphecyCurrentY");
            double currentZ = tag.getDouble("falseProphecyCurrentZ");
            if (tickCounter == 0) {
                prevX = livingEntity.getX();
                tag.putDouble("prevX", prevX);
                prevY = livingEntity.getY();
                tag.putDouble("prevY", prevY);
                prevZ = livingEntity.getZ();
                tag.putDouble("prevZ", prevZ);
                tag.putInt("tickCounter", 1);
            } else if (tickCounter == 1) {
                currentX = livingEntity.getX();
                tag.putDouble("currentX", currentX);
                currentY = livingEntity.getY();
                tag.putDouble("currentY", currentY);
                currentZ = livingEntity.getZ();
                tag.putDouble("currentZ", currentZ);
                tag.putInt("tickCounter", 0);
            }
            if (Math.abs(prevX - currentX) < 0.0023 || Math.abs(prevY - currentY) < 0.0023 || Math.abs(prevZ - currentZ) < 0.0023) {
                tag.putInt("falseProphecyStandHarmful", tag.getInt("falseProphecyStandHarmful") + 1);
            }
            if (tag.getInt("falseProphecyStandHarmful") >= 100) {
                tag.putInt("falseProphecyStandHarmful", 0);
                tag.putInt("harmfulFalseProphecyStand", 0);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.WITHER, 400,7,false,false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.BLINDNESS, 400,5,false,false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.HARM, 400,10,false,false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.WEAKNESS, 400,4,false,false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.MOVEMENT_SLOWDOWN, 400,4,false,false);
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
                livingEntity.hurt(BeyonderUtil.magicSource(livingEntity), 75);
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
                    AttributeInstance luck = livingEntity.getAttribute(ModAttributes.LOTM_LUCK.get());
                    luck.setBaseValue(luck.getBaseValue() + 50);
                }
            }
        }


        if (beneficialStand >= 1) {
            int tickCounter = tag.getInt("falseProphecyTickCounter");
            double prevX = tag.getDouble("falseProphecyPrevX");
            double prevY = tag.getDouble("falseProphecyPrevY");
            double prevZ = tag.getDouble("falseProphecyPrevZ");
            double currentX = tag.getDouble("falseProphecyCurrentX");
            double currentY = tag.getDouble("falseProphecyCurrentY");
            double currentZ = tag.getDouble("falseProphecyCurrentZ");
            if (tickCounter == 0) {
                prevX = livingEntity.getX();
                tag.putDouble("prevX", prevX);
                prevY = livingEntity.getY();
                tag.putDouble("prevY", prevY);
                prevZ = livingEntity.getZ();
                tag.putDouble("prevZ", prevZ);
                tag.putInt("tickCounter", 1);
            } else if (tickCounter == 1) {
                currentX = livingEntity.getX();
                tag.putDouble("currentX", currentX);
                currentY = livingEntity.getY();
                tag.putDouble("currentY", currentY);
                currentZ = livingEntity.getZ();
                tag.putDouble("currentZ", currentZ);
                tag.putInt("tickCounter", 0);
            }
            if (Math.abs(prevX - currentX) < 0.0023 || Math.abs(prevY - currentY) < 0.0023 || Math.abs(prevZ - currentZ) < 0.0023) {
                tag.putInt("falseProphecyStandBeneficial", tag.getInt("falseProphecyStandBeneficial") + 1);
            }
            if (tag.getInt("falseProphecyStandBeneficial") >= 100) {
                tag.putInt("falseProphecyStandBeneficial", 0);
                tag.putInt("beneficialFalseProphecyStand", 0);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.ABSORPTION, 1200,20,false,false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.DAMAGE_BOOST, 600,5,false,false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.DIG_SPEED, 600,4,false,false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.MOVEMENT_SPEED, 600,3,false,false);
                BeyonderUtil.applyMobEffect(livingEntity, MobEffects.REGENERATION, 600,5,false,false);
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
                if (stack.getItem() instanceof SimpleAbilityItem) {
                    pPlayer.getCooldowns().addCooldown(stack.getItem(), 0);
                }
            }
            tag.putInt("falseProphecyJumpHarmful", 0);
            tag.putInt("harmfulFalseProphecyJump", 0);
        }
        if (tag.getInt("falseProphecyJumpBeneficial") >= 5 && livingEntity instanceof Player pPlayer) {
            for (ItemStack stack : pPlayer.getInventory().items) {
                if (stack.getItem() instanceof SimpleAbilityItem simpleAbilityItem) {
                    int currentCooldown = (int) pPlayer.getCooldowns().getCooldownPercent(stack.getItem(), 0);
                    int cooldownToSet = simpleAbilityItem.getCooldown() * (100 - currentCooldown);
                    if (currentCooldown < cooldownToSet) {
                        pPlayer.getCooldowns().addCooldown(stack.getItem(), cooldownToSet);
                    }
                }
            }
            tag.putInt("falseProphecyJumpBeneficial", 0);
            tag.putInt("beneficialFalseProphecyJump", 0);
        }
    }
}