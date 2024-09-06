package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.RoarEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Roar extends Item {

    public Roar(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 500) {
                pPlayer.displayClientMessage(Component.literal("You need 500 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && sailorSequence.getCurrentSequence() <= 4 && sailorSequence.useSpirituality(500)) {
                    useItem(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 100);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    public static void useItem(Player pPlayer) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (holder == null) {
            return;
        }
        int sequence = holder.getCurrentSequence();
        RoarEntity roarEntity = new RoarEntity(EntityInit.ROAR_ENTITY.get(), pPlayer.level());
        roarEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
        Vec3 lookVec = pPlayer.getLookAngle();
        roarEntity.setDeltaMovement(lookVec.scale(10 - holder.getCurrentSequence()).x,lookVec.scale(10 - holder.getCurrentSequence()).y,lookVec.scale(10 - holder.getCurrentSequence()).z);
        roarEntity.hurtMarked = true;
        pPlayer.level().addFreshEntity(roarEntity);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, lets out a devastating roar\n" +
                    "Spirituality Used: 500\n" +
                    "Cooldown: 5 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}
