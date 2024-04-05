package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionLocation extends Item {

    public EnvisionLocation(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("While holding this item, type in three coordinates, e.g. (100, 100, 100) or a player's name, and you'll teleport to that location\n" +
                    "Spirituality Used: 500\n" +
                    "Left Click for Envision Location (Blink)\n" +
                    "Cooldown: 0 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        Player pPlayer = event.getPlayer();
        String message = event.getMessage().getString();
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
            if (!pPlayer.level().isClientSide() && pPlayer.getMainHandItem().getItem() instanceof EnvisionLocation && spectatorSequence.getCurrentSequence() == 0) {
                if (isThreeIntegers(message)) {
                    String[] coordinates = message.split(" ");
                    if (coordinates.length == 3) {
                        int x = Integer.parseInt(coordinates[0]);
                        int y = Integer.parseInt(coordinates[1]);
                        int z = Integer.parseInt(coordinates[2]);
                        pPlayer.teleportTo(x, y, z);
                        event.getPlayer().sendSystemMessage(Component.literal("Teleported to " + x + ", " + y + ", " + z), true);
                        spectatorSequence.useSpirituality((int) (500 / dreamIntoReality.getValue()));
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
                        pPlayer.teleportTo(x,y,z);
                        spectatorSequence.useSpirituality(500);
                        event.setCanceled(true);
                    }
                    else {
                        event.getPlayer().sendSystemMessage(Component.literal("Player:" + message + " not found"), true);
                        event.setCanceled(true);
                    }
                }

            }
        });
    }
    private static boolean isThreeIntegers(String message) {
        return message.matches("\\d+ \\d+ \\d+");
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionLocation) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionLocationBlink.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionLocation) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionLocationBlink.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}
