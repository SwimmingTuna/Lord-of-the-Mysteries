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
    public BeyonderResetPotion(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        ItemStack itemStack = pPlayer.getItemInHand(hand);
        if (!level.isClientSide()) {

            level.playSound(null, pPlayer.getOnPos(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 0.5f, level.random.nextFloat() * 0.1F + 0.9F);
            pPlayer.sendSystemMessage(Component.literal("You are no longer a Beyonder").withStyle(ChatFormatting.BLACK).withStyle(ChatFormatting.BOLD));
            pPlayer.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            BeyonderHolderAttacher.getHolderUnwrap(pPlayer).removeClass();
            if (!pPlayer.getAbilities().instabuild) {
                itemStack.shrink(1);
                pPlayer.hurt(pPlayer.damageSources().magic(), 1.0f);

            }
        }
        return super.use(level, pPlayer, hand);
    }
}
