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
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Hurricane extends Item implements ReachChangeUUIDs {

    public Hurricane(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 1250) {
                pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 4 && sailorSequence.useSpirituality(1250)) {
                    pPlayer.getPersistentData().putInt("sailorHurricane", 600);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 1200);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof Hurricane) {
            CompoundTag tag = pPlayer.getPersistentData();
            boolean x = tag.getBoolean("sailorHurricaneRain");
            if (x) {
                pPlayer.displayClientMessage(Component.literal("Hurricane will only cause rain").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", false);
                x = false;
            }
            if (!x) {
                pPlayer.displayClientMessage(Component.literal("Hurricane cause lightning, tornadoes, and rain").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", true);
                x = true;
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, summons a hurricane that shoots lightning in the sky around the player and generates tornadoes\n" +
                    "Spirituality Used: 1250\n" +
                    "Cooldown: 1 minute").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof Hurricane) {
            CompoundTag tag = pPlayer.getPersistentData();
            boolean x = tag.getBoolean("sailorHurricaneRain");
            if (x) {
                pPlayer.displayClientMessage(Component.literal("Hurricane will only cause rain").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", false);
                x = false;
            }
            if (!x) {
                pPlayer.displayClientMessage(Component.literal("Hurricane cause lightning, tornadoes, and rain").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", true);
                x = true;
            }
        }
    }
}
