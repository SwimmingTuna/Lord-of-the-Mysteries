package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.item.OtherItems.LuckyGoldCoin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class FatedConnection extends SimpleAbilityItem {

    public FatedConnection(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 1, 200, 2400);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        createIncarnation(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public void createIncarnation(Player player) {
        if (!player.level().isClientSide()) {
            ItemStack stack = player.getOffhandItem();
            if (stack.getItem() == Items.GOLD_NUGGET) {
                UUID playerUUID = player.getUUID();
                ItemStack luckyCoin = new ItemStack(ItemInit.LUCKYGOLDCOIN.get());
                LuckyGoldCoin.setUUID(luckyCoin,playerUUID);
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setItemInHand(InteractionHand.OFF_HAND, luckyCoin);
                } else {
                    player.getInventory().add(luckyCoin);
                }
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, if you're holding a gold nugget in your off hand, transforms it into a lucky gold coin. Give this to someone to be able to use /fatedconnection (ability) (player) to use any of your abilities through them. Keep in note, if it's a targeted ability, they will need to look at the target/block."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("200").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("2 Minutes").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
