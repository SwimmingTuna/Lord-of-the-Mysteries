package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
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

    public ConsciousnessStroll(Properties properties) {
        super(properties);
    }

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        Player player = event.getPlayer();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (player.getMainHandItem().getItem() instanceof ConsciousnessStroll) {
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 300) {
                player.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
        }
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide()) {
            String message = event.getMessage().getString();
            for (ServerPlayer onlinePlayer : player.getServer().getPlayerList().getPlayers()) {
                if (message.equalsIgnoreCase(onlinePlayer.getName().getString())) {
                    player.getPersistentData().putInt("consciousnessStrollActivated", 60);
                    player.getPersistentData().putInt("consciousnessStrollActivatedX", (int) player.getX());
                    player.getPersistentData().putInt("consciousnessStrollActivatedY", (int) player.getY());
                    player.getPersistentData().putInt("consciousnessStrollActivatedZ", (int) player.getZ());
                    ((ServerPlayer) player).setGameMode(GameType.SPECTATOR);
                    holder.useSpirituality(300);
                    player.teleportTo(onlinePlayer.getX(), onlinePlayer.getY(), onlinePlayer.getZ());
                }
            }
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Type a player's name in chat to teleport to their location and turn invincible and invulnerable, teleporting back after 3 seconds\n" +
                "Spirituality Used: 500\n" +
                "Cooldown: 20 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
