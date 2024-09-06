package net.swimmingtuna.lotm.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;

public abstract class Ability1 extends Item {
    private final BeyonderClassInit requiredClass;
    private final int requiredSequence;
    private final int requiredSpirituality;

    public Ability1(Properties pProperties, BeyonderClass requiredClass, int requiredSequence, int requiredSpirituality) {
        super(pProperties);
        this.requiredClass = (BeyonderClassInit) requiredClass;
        this.requiredSequence = requiredSequence;
        this.requiredSpirituality = requiredSpirituality;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);

            if (holder == null) {
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }

            if (!checkClass(holder)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the " + requiredClass.toString() + " pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }

            if (!checkSequence(holder)) {
                pPlayer.displayClientMessage(Component.literal("You need to be sequence " + requiredSequence + " or higher to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }

            if (!checkSpirituality(holder)) {
                pPlayer.displayClientMessage(Component.literal("You need " + requiredSpirituality + " spirituality to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }

            if (holder.useSpirituality(requiredSpirituality)) {
                return useAbility(level, pPlayer, hand);
            }
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
    }

    protected abstract InteractionResultHolder<ItemStack> useAbility(Level level, Player pPlayer, InteractionHand hand);

    private boolean checkClass(BeyonderHolder holder) {
        return holder.getCurrentClass() == requiredClass && holder.getCurrentClass() != null;
    }

    private boolean checkSequence(BeyonderHolder holder) {
        return holder.getCurrentSequence() <= requiredSequence;
    }

    private boolean checkSpirituality(BeyonderHolder holder) {
        return holder.getSpirituality() >= requiredSpirituality;
    }
}
