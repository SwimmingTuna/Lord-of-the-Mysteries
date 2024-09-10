package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningBallEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;

public class LightningBall extends Item {

    public LightningBall(Properties properties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return super.use(level, player, hand);
        }

        // If no block or entity is targeted, proceed with the original functionality
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
            player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (!holder.useSpirituality(800)) {
            player.displayClientMessage(Component.literal("You need 800 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (holder.getCurrentSequence() <= 2) {
            useItem(player);
            if (!player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(this, 400);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons a lightning ball that launches in the direction the player looks after a bit of time\n" +
                "Spirituality Used: 800\n" +
                "Cooldown: 20 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public static void useItem(Player player) {
        if (!player.level().isClientSide()) {
            LightningBallEntity lightningBall = new LightningBallEntity(EntityInit.LIGHTNING_BALL.get(), player.level(), true);
            lightningBall.setSummoned(true);
            lightningBall.setBallXRot((float) ((Math.random() * 20) - 10));
            lightningBall.setBallYRot((float) ((Math.random() * 20) - 10));
            lightningBall.setPos(player.getX(), player.getY() + 1.5, player.getZ());
            lightningBall.setOwner(player);
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(lightningBall);
            scaleData.setScale(10);
            scaleData.markForSync(true);
            player.level().addFreshEntity(lightningBall);
        }
    }
}
