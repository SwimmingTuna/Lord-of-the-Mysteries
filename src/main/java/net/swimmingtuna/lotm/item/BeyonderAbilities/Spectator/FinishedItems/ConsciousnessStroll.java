package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
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
public class ConsciousnessStroll extends Item {

    public ConsciousnessStroll(Properties pProperties) {
        super(pProperties);
    }

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        Player pPlayer = event.getPlayer();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (pPlayer.getMainHandItem().getItem() instanceof ConsciousnessStroll) {
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 300) {
                pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
        }
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !pPlayer.level().isClientSide()) {
            String message = event.getMessage().getString();
            for (ServerPlayer onlinePlayer : pPlayer.getServer().getPlayerList().getPlayers()) {
                if (message.equalsIgnoreCase(onlinePlayer.getName().getString())) {
                    pPlayer.getPersistentData().putInt("consciousnessStrollActivated", 60);
                    pPlayer.getPersistentData().putInt("consciousnessStrollActivatedX", (int) pPlayer.getX());
                    pPlayer.getPersistentData().putInt("consciousnessStrollActivatedY", (int) pPlayer.getY());
                    pPlayer.getPersistentData().putInt("consciousnessStrollActivatedZ", (int) pPlayer.getZ());
                    ((ServerPlayer) pPlayer).setGameMode(GameType.SPECTATOR);
                    holder.useSpirituality(300);
                    pPlayer.teleportTo(onlinePlayer.getX(), onlinePlayer.getY(), onlinePlayer.getZ());
                }
            }
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Type a player's name in chat to teleport to their location and turn invincible and invulnerable, teleporting back after 3 seconds\n" +
                    "Spirituality Used: 500\n" +
                    "Cooldown: 20 seconds").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}
