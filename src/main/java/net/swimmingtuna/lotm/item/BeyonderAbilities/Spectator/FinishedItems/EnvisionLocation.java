package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionLocation extends Item {

    public EnvisionLocation(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("While holding this item, type in three coordinates, e.g. (100, 100, 100) or a player's name, and you'll teleport to that location\n" +
                "Spirituality Used: 500\n" +
                "Left Click for Envision Location (Blink)\n" +
                "Cooldown: 0 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        Player player = event.getPlayer();
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (player.getMainHandItem().getItem() instanceof EnvisionLocation && !player.level().isClientSide()) {
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
            player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
        }
        if (holder.getSpirituality() < (int) (500 / dreamIntoReality.getValue())) {
            player.displayClientMessage(Component.literal("You need " + (int) (500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
        }
    }
        String message = event.getMessage().getString();
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide() && player.getMainHandItem().getItem() instanceof EnvisionLocation && holder.getCurrentSequence() == 0) {
            if (isThreeIntegers(message)) {
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
            } else {
                Player targetPlayer = null;
                for (Player serverPlayer : level.players()) {
                    if (serverPlayer.getUUID().toString().equals(message)) {
                        targetPlayer = serverPlayer;
                        break;
                    }
                }
                if (targetPlayer != null) {
                    int x = (int) targetPlayer.getX();
                    int y = (int) targetPlayer.getY();
                    int z = (int) targetPlayer.getZ();
                    player.teleportTo(x, y, z);
                    holder.useSpirituality(500);
                } else {
                    event.getPlayer().displayClientMessage(Component.literal("Player:" + message + " not found").withStyle(BeyonderUtil.getStyle(player)), true);
                }
                event.setCanceled(true);
            }

        }
    }
    private static boolean isThreeIntegers(String message) {
        return message.matches("\\d+ \\d+ \\d+");
    }

}
