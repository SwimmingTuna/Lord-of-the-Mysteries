package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionWeather extends Item {

    public EnvisionWeather(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("While holding this item, say either Clear, Rain, or Thunder, to change the weather at your disposal\n" +
                    "Spirituality Used: 500\n" +
                    "Cooldown: 0 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {

        Level level = event.getPlayer().serverLevel();
        Player pPlayer = event.getPlayer();
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (!pPlayer.level().isClientSide() && pPlayer.getMainHandItem().getItem() instanceof EnvisionWeather) {
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < (int) 500 / dreamIntoReality.getValue()) {
                pPlayer.displayClientMessage(Component.literal("You need " + ((int) 500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            String message = event.getMessage().getString().toLowerCase();
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (holder.isSpectatorClass() && pPlayer.getMainHandItem().getItem() instanceof EnvisionWeather && spectatorSequence.getCurrentSequence() == 0) {
                    if (message.equals("clear") && spectatorSequence.useSpirituality((int) (500 / dreamIntoReality.getValue()))) {
                        setWeatherClear(level);
                        event.getPlayer().sendSystemMessage(Component.literal("Set Weather to Clear"), true);
                        spectatorSequence.useSpirituality((int) (500 / dreamIntoReality.getValue()));
                        event.setCanceled(true);
                    }
                    if (message.equals("rain") && spectatorSequence.useSpirituality((int) (500 / dreamIntoReality.getValue()))) {
                        event.getPlayer().sendSystemMessage(Component.literal("Set Weather to Rain"), true);
                        setWeatherRain(level);
                        event.setCanceled(true);
                    }
                    if (message.equals("thunder") && spectatorSequence.useSpirituality((int) (500 / dreamIntoReality.getValue()))) {
                        event.getPlayer().sendSystemMessage(Component.literal("Set Weather to Thunder"), true);
                        setWeatherThunder(level);
                        event.setCanceled(true);
                    }
                }
            });
        }
        if (!pPlayer.level().isClientSide()) {
            String message = event.getMessage().getString().toLowerCase();
            for (Player otherPlayer : level.players()) {
                if (message.contains(otherPlayer.getName().getString().toLowerCase())) {
                    BeyonderHolder otherHolder = BeyonderHolderAttacher.getHolder(otherPlayer).orElse(null);
                    if (otherHolder != null && otherHolder.isSpectatorClass() && otherHolder.getCurrentSequence() <= 2 && !otherPlayer.level().isClientSide()) {
                        otherPlayer.sendSystemMessage(Component.literal(pPlayer.getName().getString() + " mentioned you in chat. Their coordinates are: " + (int) pPlayer.getX() + " ," + (int) pPlayer.getY() + " ," + (int) pPlayer.getZ()).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
                    }
                    if (otherHolder != null && otherHolder.isSailorClass() && otherHolder.getCurrentSequence() <= 1 && !otherPlayer.level().isClientSide()) {
                        otherPlayer.getPersistentData().putInt("tyrantMentionedInChat", 200);
                        otherPlayer.sendSystemMessage(Component.literal(pPlayer.getName().getString() + " mentioned you in chat. DO you want to summon a lightning storm on them? Type Yes if so, you have 10 seconds").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
                        otherPlayer.getPersistentData().putInt("sailorStormVecX1", (int) pPlayer.getX());
                        otherPlayer.getPersistentData().putInt("sailorStormVecY1", (int) pPlayer.getY());
                        otherPlayer.getPersistentData().putInt("sailorStormVecZ1", (int) pPlayer.getZ());
                    }
                }
            }
            if (pPlayer.getPersistentData().getInt("tyrantMentionedInChat") >= 1 && message.contains("yes")) {
                pPlayer.getPersistentData().putInt("sailorLightningStorm1", 300);
                event.setCanceled(true);
            }
        }
    }

    private static void setWeatherClear(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.setWeatherParameters(8000, 0, false, false);
        }
    }

    private static void setWeatherRain(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.setWeatherParameters(40, 8000, true, true);
        }
    }

    private static void setWeatherThunder(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.setWeatherParameters(40, 8000, true, true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionWeather) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionBarrier.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionWeather) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionBarrier.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}
