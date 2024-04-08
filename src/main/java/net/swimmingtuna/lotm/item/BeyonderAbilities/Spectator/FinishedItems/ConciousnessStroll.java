package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConciousnessStroll extends Item {

    public ConciousnessStroll(Properties pProperties) {
        super(pProperties);
    }

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        Player pPlayer = event.getPlayer();
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        String message = event.getMessage().getString().toLowerCase();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (holder.isSpectatorClass() && !pPlayer.level().isClientSide() && pPlayer.getMainHandItem().getItem() instanceof ConciousnessStroll && spectatorSequence.getCurrentSequence() <= 2) {
                Player targetPlayer = null;
                for (Player serverPlayer : level.players()) {
                    if (serverPlayer.getUUID().toString().equals(message)) {
                        targetPlayer = serverPlayer;
                        break;
                    }
                }
                int consciousnessStrollTimer = pPlayer.getPersistentData().getInt("waitStrollTimer");
                if (consciousnessStrollTimer == 0) {
                if (targetPlayer != null) {
                    if (!pPlayer.level().isClientSide()) {
                        if (!holder.isSpectatorClass()) {
                            pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                        }
                        if (holder.getSpirituality() < (int) 500/dreamIntoReality.getValue()) {
                            pPlayer.displayClientMessage(Component.literal("You need 500 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                        }
                    }
                    int x = (int) targetPlayer.getX();
                    int y = (int) targetPlayer.getY();
                    int z = (int) targetPlayer.getZ();
                    pPlayer.teleportTo(x, y, z);
                    int strollTimer = pPlayer.getPersistentData().getInt("StrollTimer");
                    pPlayer.getPersistentData().putInt("StrollTimer", 1);
                    pPlayer.getPersistentData().putInt("waitStrollTimer",1);
                    spectatorSequence.useSpirituality((int) (500/dreamIntoReality.getValue()));
                    event.setCanceled(true);}
                if (consciousnessStrollTimer != 0) {
                    event.getPlayer().sendSystemMessage(Component.literal("Ability on Cooldown for " + (int) ((400 - consciousnessStrollTimer)/20) + " seconds"));
                }
                } else {
                    event.getPlayer().sendSystemMessage(Component.literal("Player:" + message + " not found"), true);
                    event.setCanceled(true);
                }
            }
        });
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Type a player's name in chat to teleport to their location and turn invincible and invulnerable, teleporting back after 3 seconds\n" +
                    "Spirituality Used: 500\n" +
                    "Cooldown: 20 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void tickCounter(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (event.phase == TickEvent.Phase.START && !pPlayer.level().isClientSide()) {
            int strollTimer = pPlayer.getPersistentData().getInt("StrollTimer");
            double currentX = pPlayer.getPersistentData().getDouble("currentX");
            double currentY = pPlayer.getPersistentData().getDouble("currentY");
            double currentZ = pPlayer.getPersistentData().getDouble("currentZ");
            int locationKeeper = pPlayer.getPersistentData().getInt("Location");
            if (strollTimer == 1 && pPlayer instanceof ServerPlayer) {
                int strollCounter = pPlayer.getPersistentData().getInt("StrollCounter");
                strollCounter++;
                ((ServerPlayer) pPlayer).setGameMode(GameType.SPECTATOR);
                pPlayer.getPersistentData().putInt("StrollCounter", strollCounter);
                pPlayer.getPersistentData().putInt("Location", 1);
                if (strollCounter == 60) {
                    ((ServerPlayer) pPlayer).setGameMode(GameType.SURVIVAL);
                    pPlayer.getPersistentData().putInt("StrollTimer", 0);
                    pPlayer.getPersistentData().putInt("StrollCounter", 0);
                    pPlayer.getPersistentData().putInt("Location", 1);
                    pPlayer.teleportTo(currentX,currentY,currentZ);
                }
            }
            if (locationKeeper == 0) {
                currentX = pPlayer.getX();
                currentY = pPlayer.getY();
                currentZ = pPlayer.getZ();
            } else {
                currentX = 0;
                currentY = 0;
                currentZ = 0;
                pPlayer.getPersistentData().putInt("Location", 0);
            }
        }
    }
    @SubscribeEvent
    public static void tickCounter2(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (event.phase == TickEvent.Phase.START && !pPlayer.level().isClientSide()) {
            int waitStrollTimer = pPlayer.getPersistentData().getInt("waitStrollTimer");
            if (waitStrollTimer >= 1) {
                waitStrollTimer++;}
            if (waitStrollTimer >= 400) {
                pPlayer.getPersistentData().putInt("waitStrollTimer", 0);
                waitStrollTimer = 0;
            }
        }
    }
}
