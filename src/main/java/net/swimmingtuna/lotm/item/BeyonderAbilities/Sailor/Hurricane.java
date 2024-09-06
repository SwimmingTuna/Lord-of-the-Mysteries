package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Hurricane extends Item {

    public Hurricane(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return super.use(level, player, hand);
        }

        // If no block or entity is targeted, proceed with the original functionality
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder == null) return InteractionResultHolder.pass(player.getItemInHand(hand));
        if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
            player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (!holder.useSpirituality(1250)) {
            player.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (holder.getCurrentSequence() <= 4) {
            player.getPersistentData().putInt("sailorHurricane", 600);
            if (!player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(this, 1200);
            }
        }
        return super.use(level, player, hand);
    }
    
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof Hurricane) {
            CompoundTag tag = pPlayer.getPersistentData();
            boolean sailorHurricaneRain = tag.getBoolean("sailorHurricaneRain");
            if (sailorHurricaneRain) {
                pPlayer.displayClientMessage(Component.literal("Hurricane will only cause rain").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", false);
            } else {
                pPlayer.displayClientMessage(Component.literal("Hurricane cause lightning, tornadoes, and rain").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", true);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, summons a hurricane that shoots lightning in the sky around the player and generates tornadoes\n" +
                    "Spirituality Used: 1250\n" +
                    "Cooldown: 1 minute").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof Hurricane) {
            CompoundTag tag = pPlayer.getPersistentData();
            boolean sailorHurricaneRain = tag.getBoolean("sailorHurricaneRain");
            if (sailorHurricaneRain) {
                pPlayer.displayClientMessage(Component.literal("Hurricane will only cause rain").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", false);
            } else {
                pPlayer.displayClientMessage(Component.literal("Hurricane cause lightning, tornadoes, and rain").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", true);
            }
        }
    }
}
