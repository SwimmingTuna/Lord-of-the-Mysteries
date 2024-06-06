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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
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
                    pPlayer.getCooldowns().addCooldown(this, 240);
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
                                pPlayer.sendSystemMessage(Component.literal("sailor value is" + spiritualityUseCounter));
                                pPlayer.sendSystemMessage(Component.literal("sailor boolean is" + canFly));
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
            componentList.add(Component.literal("Upon use, uses the wind to burst forward in the direction the player is looking three times\n" +
                    "Spirituality Used: 120\n" +
                    "Cooldown: 6 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void tickEvent(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {
            Vec3 lookVector = pPlayer.getLookAngle();
            double x = pPlayer.getX() + 5 * lookVector.x();
            double y = pPlayer.getY() + 5 * lookVector.y();
            double z = pPlayer.getZ() + 5 * lookVector.z();

            CompoundTag tag = pPlayer.getPersistentData();
            int flight = tag.getInt("sailorFlight");
            int flightCancel = tag.getInt("sailorFlightDamageCancel");
            if (flightCancel >= 1) {
                tag.putInt("sailorFlightDamageCancel", flightCancel + 1);
            }
            if (flightCancel >= 300) {
                tag.putInt("sailorFlightDamageCancel", 0);
            }
            if (flight >= 1) {
                tag.putInt("sailorFlight", flight + 1);
            }
            if (flight == 20) {
                pPlayer.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
                pPlayer.hurtMarked = true;
            }
            if (flight == 40) {
                pPlayer.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
                pPlayer.hurtMarked = true;
            }
            if (flight == 60) {
                pPlayer.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
                pPlayer.hurtMarked = true;
            }
            if (flight > 60) {
                tag.putInt("sailorFlight", 0);
            }
        }

    }
    @SubscribeEvent
    public static void sailorFallCanceler(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player pPlayer) {
            CompoundTag tag = pPlayer.getPersistentData();
            int flightCancel = tag.getInt("sailorFlightDamageCancel");
            if (!pPlayer.level().isClientSide()) {
                if (flightCancel != 0 && event.getSource() == pPlayer.damageSources().fall()) {
                    event.setCanceled(true);
                    tag.putInt("sailorFlightDamageCancel", 0);
                }
            }
        }
    }
}
