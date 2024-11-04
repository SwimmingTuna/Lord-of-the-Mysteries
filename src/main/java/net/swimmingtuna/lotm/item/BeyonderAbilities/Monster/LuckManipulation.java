package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.beyonder.SailorClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class LuckManipulation extends SimpleAbilityItem {
    public LuckManipulation(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 5, 150, 60);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        luckManipulation(player);
        return InteractionResult.SUCCESS;
    }

    public static void luckManipulation(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            AttributeInstance luck = player.getAttribute(ModAttributes.LOTM_LUCK.get());
            double lotmLuckValue = luck.getValue();
            int luckManipulation = tag.getInt("luckManipulationItem");
            if (luckManipulation == 1) { //regen
                BeyonderUtil.applyMobEffect(player, MobEffects.REGENERATION, 300 - (holder.getCurrentSequence() * 30), 2, true, true );
                luck.setBaseValue(Math.max(0, lotmLuckValue - 4));
            }
            if (luckManipulation == 2) { //diamonds
                ItemStack diamonds = new ItemStack(Items.DIAMOND,3);
                ItemStack diamondBlock = new ItemStack(Items.DIAMOND_BLOCK,2);
                ItemEntity diamondEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), diamonds);
                ItemEntity diamondBlockEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), diamondBlock);
                if (holder.getCurrentSequence() >= 2) {
                    player.level().addFreshEntity(diamondEntity);
                } else player.level().addFreshEntity(diamondBlockEntity);
                luck.setBaseValue(Math.max(0, lotmLuckValue - 4));
            }
            if (luckManipulation == 3) { //wind moving projectiles
                int x = tag.getInt("windMovingProjectilesCounter");
                tag.putInt("windMovingProjectilesCounter", x + 1);
                luck.setBaseValue(Math.max(0, lotmLuckValue - 8));
            }
            if (luckManipulation == 4) { //halve next damage
                tag.putInt("luckHalveDamage", tag.getInt("luckHalveDamage") + 1);
                luck.setBaseValue(Math.max(0, lotmLuckValue - 10));
            }
            if (luckManipulation == 5) { //mobs distracted from you
                tag.putInt("luckIgnoreMobs", tag.getInt("luckIgnoreMobs") + 1);
                luck.setBaseValue(Math.max(0, lotmLuckValue - 3));
            }
            if (luckManipulation == 6) { //players that hurt you recently will get poison
                tag.putInt("luckAttackerPoisoned", tag.getInt("luckAttackerPoisoned") + 1);
                luck.setBaseValue(Math.max(0, lotmLuckValue - 12));
            }
            if (luckManipulation == 7) { //ignore next damage
                tag.putInt("luckIgnoreDamage", tag.getInt("luckIgnoreDamage") + 1);
                luck.setBaseValue(Math.max(0, lotmLuckValue - 15));
            }
        }
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 5 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof LuckManipulation) {
                    player.displayClientMessage(Component.literal("Current Luck Manipulation is: " + luckManipulationString(player)), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }
    public static String luckManipulationString(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int luckManipulation = tag.getInt("luckManipulationItem");
        if (luckManipulation == 1) {
            return "Regeneration";
        }
        if (luckManipulation == 2) {
            return "Diamonds";
        }
        if (luckManipulation == 3) {
            return "Wind Moving Projectiles";
        }
        if (luckManipulation == 4) {
            return "Halve Next Damage";
        }
        if (luckManipulation == 5) {
            return "Mobs will get Distracted from you";
        }
        if (luckManipulation == 6) {
            return "Players that hurt you recently will get poisoned and stunned";
        }
        if (luckManipulation == 7) {
            return "Ignore the next damage";
        }
        return null;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Upon use, activates your danger sense, alerting you of players around you and where they are"));
        tooltipComponents.add(Component.literal("Activation Cost: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("40 per second").withStyle(ChatFormatting.YELLOW)));
        Component.literal("Cooldown: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}