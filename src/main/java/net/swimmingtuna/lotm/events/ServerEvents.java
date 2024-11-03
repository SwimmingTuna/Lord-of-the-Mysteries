package net.swimmingtuna.lotm.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.ConsciousnessStroll;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionLife;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionLocation;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionWeather;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import static net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionLife.spawnMob;
import static net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionLocation.isThreeIntegers;
import static net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionWeather.*;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        ServerPlayer player = event.getPlayer();
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        Style style = BeyonderUtil.getStyle(player);
        if (!player.level().isClientSide() && player.getMainHandItem().getItem() instanceof EnvisionWeather) {
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < (int) 500 / dreamIntoReality.getValue()) {
                player.displayClientMessage(Component.literal("You need " + ((int) 500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            String message = event.getMessage().getString().toLowerCase();
            if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && player.getMainHandItem().getItem() instanceof EnvisionWeather && holder.getCurrentSequence() == 0) {
                if (message.equals("clear") && holder.useSpirituality((int) (500 / dreamIntoReality.getValue()))) {
                    setWeatherClear(level);
                    event.getPlayer().displayClientMessage(Component.literal("Set Weather to Clear").withStyle(style), true);
                    holder.useSpirituality((int) (500 / dreamIntoReality.getValue()));
                    event.setCanceled(true);
                }
                if (message.equals("rain") && holder.useSpirituality((int) (500 / dreamIntoReality.getValue()))) {
                    event.getPlayer().displayClientMessage(Component.literal("Set Weather to Rain").withStyle(style), true);
                    setWeatherRain(level);
                    event.setCanceled(true);
                }
                if (message.equals("thunder") && holder.useSpirituality((int) (500 / dreamIntoReality.getValue()))) {
                    event.getPlayer().displayClientMessage(Component.literal("Set Weather to Thunder").withStyle(style), true);
                    setWeatherThunder(level);
                    event.setCanceled(true);
                }
            }
        }
        if (!player.level().isClientSide()) {
            String message = event.getMessage().getString().toLowerCase();
            for (Player otherPlayer : level.players()) {
                if (message.contains(otherPlayer.getName().getString().toLowerCase())) {
                    BeyonderHolder otherHolder = BeyonderHolderAttacher.getHolderUnwrap(otherPlayer);
                    if (otherHolder.currentClassMatches(BeyonderClassInit.SPECTATOR) && otherHolder.getCurrentSequence() <= 2 && !otherPlayer.level().isClientSide()) {
                        otherPlayer.sendSystemMessage(Component.literal(player.getName().getString() + " mentioned you in chat. Their coordinates are: " + (int) player.getX() + " ," + (int) player.getY() + " ," + (int) player.getZ()).withStyle(style));
                    }
                    if (otherHolder.currentClassMatches(BeyonderClassInit.SAILOR) && otherHolder.getCurrentSequence() <= 1 && !otherPlayer.level().isClientSide()) {
                        otherPlayer.getPersistentData().putInt("tyrantMentionedInChat", 200);
                        otherPlayer.sendSystemMessage(Component.literal(player.getName().getString() + " mentioned you in chat. Do you want to summon a lightning storm on them? Type Yes if so, you have 10 seconds").withStyle(style));
                        otherPlayer.getPersistentData().putInt("sailorStormVecX1", (int) player.getX());
                        otherPlayer.getPersistentData().putInt("sailorStormVecY1", (int) player.getY());
                        otherPlayer.getPersistentData().putInt("sailorStormVecZ1", (int) player.getZ());
                    }
                }
            }
            if (player.getPersistentData().getInt("tyrantMentionedInChat") >= 1 && message.contains("yes") && holder.getSpirituality() >= 1200) {
                holder.useSpirituality(1200);
                player.getPersistentData().putInt("sailorLightningStorm1", 300);
                event.setCanceled(true);
            }
        }
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide() && player.getMainHandItem().getItem() instanceof ConsciousnessStroll && holder.getCurrentSequence() <= 3 && !player.getCooldowns().isOnCooldown(ItemInit.CONSCIOUSNESS_STROLL.get())) {
            String message = event.getMessage().getString();
            for (ServerPlayer onlinePlayer : player.getServer().getPlayerList().getPlayers()) {
                if (message.equalsIgnoreCase(onlinePlayer.getName().getString())) {
                    if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                        player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                    } else if (holder.getSpirituality() < 300) {
                        player.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                    } else {
                        player.getPersistentData().putInt("consciousnessStrollActivated", 60);
                        PlayerMobEntity playerMobEntity = new PlayerMobEntity(EntityInit.PLAYER_MOB_ENTITY.get(), player.level());
                        AttributeInstance playerMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
                        AttributeInstance playerMobMaxHealth = playerMobEntity.getAttribute(Attributes.MAX_HEALTH);
                        playerMobMaxHealth.setBaseValue(playerMaxHealth.getValue());
                        playerMobEntity.setHealth(player.getHealth());
                        playerMobEntity.teleportTo(player.getX(), player.getY(), player.getZ());
                        playerMobEntity.setOwner(player);
                        playerMobEntity.getPersistentData().putInt("CSlifetime", 60);
                        playerMobEntity.setUsername(player.getName().getString());
                        player.level().addFreshEntity(playerMobEntity);
                        player.getCooldowns().addCooldown(ItemInit.CONSCIOUSNESS_STROLL.get(), 400);
                        player.getPersistentData().putInt("consciousnessStrollActivatedX", (int) player.getX());
                        player.getPersistentData().putInt("consciousnessStrollActivatedY", (int) player.getY());
                        player.getPersistentData().putInt("consciousnessStrollActivatedZ", (int) player.getZ());
                        ((ServerPlayer) player).setGameMode(GameType.SPECTATOR);
                        holder.useSpirituality(300);
                        player.teleportTo(onlinePlayer.getX(), onlinePlayer.getY(), onlinePlayer.getZ());
                        event.setCanceled(true);
                    }
                }
            }
        }
        if (player.getMainHandItem().getItem() instanceof EnvisionLife && !player.level().isClientSide() && !player.getCooldowns().isOnCooldown(ItemInit.ENVISION_LIFE.get())) {
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }

            if (holder.getSpirituality() < 1500) {
                player.displayClientMessage(Component.literal("You need " + (int) (1500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
        }
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide() && player.getMainHandItem().getItem() instanceof EnvisionLife && holder.getCurrentSequence() == 0) {
            String message = event.getMessage().getString().toLowerCase();
            spawnMob(player, message);
            holder.useSpirituality((int) (1500 / dreamIntoReality.getValue()));
            event.setCanceled(true);
        }
        String message = event.getMessage().getString();
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide() && player.getMainHandItem().getItem() instanceof EnvisionLocation && holder.getCurrentSequence() == 0) {
            if (isThreeIntegers(message)) {
                if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                    player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                } else if (holder.getSpirituality() < (int) (500 / dreamIntoReality.getValue())) {
                    player.displayClientMessage(Component.literal("You need " + (int) (500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                } else {
                    String[] coordinates = message.split(" ");
                    if (coordinates.length == 3) {
                        int x = Integer.parseInt(coordinates[0]);
                        int y = Integer.parseInt(coordinates[1]);
                        int z = Integer.parseInt(coordinates[2]);
                        player.teleportTo(x, y, z);
                        event.getPlayer().displayClientMessage(Component.literal("Teleported to " + x + ", " + y + ", " + z).withStyle(BeyonderUtil.getStyle(player)), true);
                        holder.useSpirituality((int) (500 / dreamIntoReality.getValue()));
                        event.setCanceled(true);
                    }
                }
            } else {
                Player targetPlayer = null;
                for (Player serverPlayer : level.players()) {
                    if (serverPlayer.getUUID().toString().equals(message)) {
                        targetPlayer = serverPlayer;
                        break;
                    }
                }
                if (targetPlayer != null) {
                    if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                        player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                    }
                    if (holder.getSpirituality() < (int) (500 / dreamIntoReality.getValue())) {
                        player.displayClientMessage(Component.literal("You need " + (int) (500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                    } else {
                        int x = (int) targetPlayer.getX();
                        int y = (int) targetPlayer.getY();
                        int z = (int) targetPlayer.getZ();
                        player.teleportTo(x, y, z);
                        holder.useSpirituality(500);
                    }
                }else {
                    event.getPlayer().displayClientMessage(Component.literal("Player:" + message + " not found").withStyle(BeyonderUtil.getStyle(player)), true);
                }
                event.setCanceled(true);
            }
        }
    }
}
