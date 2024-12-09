package net.swimmingtuna.lotm.item.OtherItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.entity.LuckBottleEntity;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class LuckBottleItem extends Item {

    public LuckBottleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!pLevel.isClientSide() && pPlayer.getMainHandItem().getItem() instanceof LuckBottleItem) {
            if (!pPlayer.isShiftKeyDown()) {
                LuckBottleEntity luckBottleEntity = new LuckBottleEntity(pLevel, pPlayer);
                luckBottleEntity.setLuck(5);
                luckBottleEntity.setItem(stack);
                luckBottleEntity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0f, 0.5f, 1.0f);
                pLevel.addFreshEntity(luckBottleEntity);
            } else {
                AttributeInstance luck = pPlayer.getAttribute(ModAttributes.LOTM_LUCK.get());
                luck.setBaseValue(Math.min(100, luck.getBaseValue() + getLuckAmount(stack)));
            }
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            if (!pPlayer.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

    public static void setLuckAmount(ItemStack stack, int amount) {
        stack.getOrCreateTag().putInt("luckAmount", amount);
    }

    public static int getLuckAmount(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt("luckAmount") : 0;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Luck Amount:" + getLuckAmount(stack) + "\n" +
                "Shift to break in your hand and give all luck to yourself").withStyle(ChatFormatting.GREEN));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
