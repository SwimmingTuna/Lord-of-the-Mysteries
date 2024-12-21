package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FortuneAppropiation extends SimpleAbilityItem {

    public FortuneAppropiation(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 2, 500, 400);
    }
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        appropiateFortune(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    private void appropiateFortune(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            for (LivingEntity entity : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(100 - (BeyonderUtil.getSequence(livingEntity) * 20)))) {
                if (entity != livingEntity) {
                    CompoundTag tag = entity.getPersistentData();
                    CompoundTag livingTag = livingEntity.getPersistentData();

                    int pIgnoreDamage = tag.getInt("luckIgnoreDamage");
                    int pDiamonds = tag.getInt("luckDiamonds");
                    int pRegeneration = tag.getInt("luckRegeneration");
                    int pMoveProjectiles = tag.getInt("windMovingProjectilesCounter");
                    int pHalveDamage = tag.getInt("luckHalveDamage");
                    int pIgnoreMobs = tag.getInt("luckIgnoreMobs");
                    int pLuckAttackerPoisoned = tag.getInt("luckAttackerPoisoned");

                    int ignoreDamage = tag.getInt("luckIgnoreDamage");
                    int diamonds = tag.getInt("luckDiamonds");
                    int regeneration = tag.getInt("luckRegeneration");
                    int moveProjectiles = tag.getInt("windMovingProjectilesCounter");
                    int halveDamage = tag.getInt("luckHalveDamage");
                    int ignoreMobs = tag.getInt("luckIgnoreMobs");
                    int luckAttackerPoisoned = tag.getInt("luckAttackerPoisoned");
                    if (ignoreDamage >= 1) {
                        livingTag.putInt("luckIgnoreDamage", pIgnoreDamage + ignoreDamage);
                        tag.putInt("luckIgnoreDamage", 0);
                    }
                    if (diamonds >= 1) {
                        livingTag.putInt("luckDiamonds", pDiamonds + diamonds);
                        tag.putInt("luckDiamonds", 0);
                    }
                    if (regeneration >= 1) {
                        livingTag.putInt("luckRegeneration", pRegeneration + regeneration);
                        tag.putInt("luckRegeneration", 0);
                    }
                    if (moveProjectiles >= 1) {
                        livingTag.putInt("windMovingProjectilesCounter", pMoveProjectiles + moveProjectiles);
                        tag.putInt("windMovingProjectilesCounter", 0);
                    }
                    if (halveDamage >= 1) {
                        livingTag.putInt("luckHalveDamage", pHalveDamage + halveDamage);
                        tag.putInt("luckHalveDamage", 0);
                    }
                    if (ignoreMobs >= 1) {
                        livingTag.putInt("luckIgnoreMobs", pIgnoreMobs + ignoreMobs);
                        tag.putInt("luckIgnoreMobs", 0);
                    }
                    if (luckAttackerPoisoned >= 1) {
                        livingTag.putInt("luckAttackerPoisoned", pLuckAttackerPoisoned + luckAttackerPoisoned);
                        tag.putInt("luckAttackerPoisoned", 0);
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, takes all fortunate events from entities around you and gives them to yourself."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("500").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("20 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
