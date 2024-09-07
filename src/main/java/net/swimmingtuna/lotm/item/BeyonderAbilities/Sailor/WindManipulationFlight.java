package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
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
import net.swimmingtuna.lotm.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WindManipulationFlight extends Item {
    public WindManipulationFlight(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 7 && sailorSequence.getCurrentSequence() > 4 && sailorSequence.useSpirituality(100)) {
                    flightRegular(pPlayer);
                }
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 4) {
                    toggleFlying(pPlayer);
                }
                CompoundTag tag = pPlayer.getPersistentData();
                boolean x = tag.getBoolean("sailorFlight1");
                if (!pPlayer.getAbilities().instabuild && !x) {
                    pPlayer.getCooldowns().addCooldown(this, 10);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    private void flightRegular(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        tag.putInt("sailorFlight", 1);
        tag.putInt("sailorFlightDamageCancel", 1);
    }

    private void startFlying(Player pPlayer) {
        pPlayer.getPersistentData().putBoolean("sailorFlight1", true);
        Abilities playerAbilities = pPlayer.getAbilities();
        CompoundTag compoundTag = pPlayer.getPersistentData();
        compoundTag.putInt("sailorWindSpiritualityCounter", 1);
        if (!playerAbilities.instabuild) {
            playerAbilities.mayfly = true;
            playerAbilities.flying = true;
            playerAbilities.setFlyingSpeed(0.1F);
        }
        pPlayer.onUpdateAbilities();
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
        }
    }
    private void toggleFlying(Player pPlayer) {
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            boolean canFly = pPlayer.getPersistentData().getBoolean("sailorFlight1");
            if (canFly) {
                stopFlying(pPlayer);
            } else {
                startFlying(pPlayer);
            }
        });
    }
    private void stopFlying(Player pPlayer) {
        pPlayer.getPersistentData().putBoolean("sailorFlight1", false);
        Abilities playerAbilities = pPlayer.getAbilities();
        CompoundTag compoundTag = pPlayer.getPersistentData();
        compoundTag.putInt("sailorWindSpiritualityCounter", 0);
        if (!playerAbilities.instabuild) {
            playerAbilities.mayfly = false;
            playerAbilities.flying = false;}
        playerAbilities.setFlyingSpeed(0.05F);
        pPlayer.onUpdateAbilities();
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
        }
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player) {
            Player pPlayer = (Player) entity;
            int spiritualityUseCounter = pPlayer.getPersistentData().getInt("sailorWindSpiritualityCounter");
            CompoundTag tag = pPlayer.getPersistentData();
            if (!pPlayer.level().isClientSide) {
                boolean canFly = pPlayer.getPersistentData().getBoolean("sailorFlight1");
                if (canFly) {
                    BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                        if (!pPlayer.getAbilities().instabuild) {
                            if (sailorSequence.getSpirituality() > 101) {
                                tag.putInt("sailorWindSpiritualityCounter", spiritualityUseCounter + 1);
                                if (spiritualityUseCounter >= 20) {
                                    sailorSequence.useSpirituality(100);
                                    tag.putInt("sailorWindSpiritualityCounter", 0);
                                }
                            } else {
                                stopFlying(pPlayer);
                            }
                        }
                    });
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, uses the wind to burst forward in the direction the player is looking three times or allow the user to fly, depending on the sequence\n" +
                    "Spirituality Used: 100 every second\n" +
                    "Cooldown: 0.5 seconds").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof WindManipulationFlight) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.WindManipulationCushion.get()));
            heldItem.shrink(1);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof WindManipulationFlight) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.WindManipulationCushion.get()));
            heldItem.shrink(1);
        }
    }
}
