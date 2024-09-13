package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LightningRedirection extends Item {

    public LightningRedirection(Properties properties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 600) {
                player.displayClientMessage(Component.literal("You need 600 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 1 && holder.useSpirituality(900)) {
                useItem(player);
                if (!player.getAbilities().instabuild)
                    player.getCooldowns().addCooldown(this, 100);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 1) {
            for (Entity entity : level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(200))) {
                if (entity instanceof LightningEntity lightning) {
                    lightning.setTargetPos(context.getClickLocation());
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, redirects all lightning into the direction the player is looking \n" +
                "Spirituality Used: 600\n" +
                "Cooldown: 5 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public static void useItem(Player player) {
        if (!player.level().isClientSide()) {
            for (Entity entity : player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(200))) {
                if (entity instanceof LightningEntity lightning) {
                    Vec3 lookVec = player.getLookAngle();
                    lightning.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
                    lightning.hurtMarked = true;
                    lightning.setMaxLength(lightning.getMaxLength() + 10);
                }
            }
        }
    }
}
