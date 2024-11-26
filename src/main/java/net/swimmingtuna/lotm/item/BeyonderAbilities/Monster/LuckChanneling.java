package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.item.OtherItems.LuckBottleItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class LuckChanneling extends SimpleAbilityItem {

    public LuckChanneling(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 4, 300, 20);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        channelLuck(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    private void channelLuck(Player player) {
        if (!player.level().isClientSide()) {
            ItemStack stack = player.getOffhandItem();
            if (stack.getItem() == Items.GLASS_BOTTLE) {
                AttributeInstance luck = player.getAttribute(ModAttributes.LOTM_LUCK.get());
                if (luck != null) {
                    ItemStack luckBottle = new ItemStack(ItemInit.LUCKBOTTLEITEM.get());
                    int sequence = BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence();
                    if (sequence <= 2) {
                        double luckBottleAmount = 0;
                        for (LivingEntity livingEntity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(100 - (sequence * 25)))) {
                            if (livingEntity instanceof Player || livingEntity instanceof PlayerMobEntity) {
                                AttributeInstance newLuck = livingEntity.getAttribute(ModAttributes.LOTM_LUCK.get());
                                if (newLuck != null) {
                                    if (livingEntity == player) {
                                        double originalLuck = newLuck.getBaseValue();
                                        newLuck.setBaseValue(originalLuck / 2);
                                        luckBottleAmount += (originalLuck);
                                    } else {
                                        luckBottleAmount += newLuck.getBaseValue();
                                        newLuck.setBaseValue(0);
                                    }
                                }
                            }
                        }
                        LuckBottleItem.setLuckAmount(luckBottle, (int) luckBottleAmount);
                    }
                    LuckBottleItem.setLuckAmount(luckBottle, (int) luck.getBaseValue());
                    player.displayClientMessage(Component.literal("Channeled " + luck.getValue() + " luck into this bottle").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD), true);
                    stack.shrink(1);
                    luck.setBaseValue(0);
                    if (stack.isEmpty()) {
                        player.setItemInHand(InteractionHand.OFF_HAND, luckBottle);
                    } else {
                        player.getInventory().add(luckBottle);
                    }
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(
                "Upon use, exude an aura of tyranny, not giving any entity permission to move, implanting fear strong enough to not allow them to use their abilities"
        ).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
