package net.swimmingtuna.lotm.item.BeyonderPotions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;

public class BeyonderResetPotion extends Item {
    public BeyonderResetPotion(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide()) {

            level.playSound(null, player.getOnPos(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 0.5f, level.random.nextFloat() * 0.1F + 0.9F);
            player.sendSystemMessage(Component.literal("You are no longer a Beyonder").withStyle(ChatFormatting.BLACK).withStyle(ChatFormatting.BOLD));
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            BeyonderHolderAttacher.getHolderUnwrap(player).removeClass();
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
                player.hurt(player.damageSources().magic(), 1.0f);

            }
        }
        return super.use(level, player, hand);
    }
}
