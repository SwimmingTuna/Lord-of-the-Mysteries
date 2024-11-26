package net.swimmingtuna.lotm.events.ability_events;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.WindManipulationCushion;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.WindManipulationFlight;

public class WindManipulation {
    protected static void windManipulationFlight(Player player, CompoundTag tag) {
        Vec3 lookVector = player.getLookAngle();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (tag.getBoolean("sailorFlight1")) {
            if (holder.getSpirituality() >= 3) {
                holder.useSpirituality(3);
            } else {
                WindManipulationFlight.stopFlying(player);
            }
        }
        int flightCancel = tag.getInt("sailorFlightDamageCancel");
        if (flightCancel >= 1) {
            player.fallDistance = 0;
            tag.putInt("sailorFlightDamageCancel", flightCancel + 1);
            if (flightCancel >= 300) {
                tag.putInt("sailorFlightDamageCancel", 0);
            }
        }
        int flight = tag.getInt("sailorFlight");
        if (flight >= 1) {
            tag.putInt("sailorFlight", flight + 1);
            if (flight <= 45 && flight % 15 == 0) {
                player.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
                player.hurtMarked = true;
            }
            if (flight > 45) {
                tag.putInt("sailorFlight", 0);
            }
        }
    }

    protected static void windManipulationFlight1(Player player, CompoundTag playerPersistentData) {
        //WIND MANIPULATION FLIGHT
        Vec3 lookVector = player.getLookAngle();
        if (!playerPersistentData.getBoolean("sailorFlight1")) {
            return;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!holder.useSpirituality(2)) {
            return;
        }
        int flight = playerPersistentData.getInt("sailorFlight");
        int flightCancel = playerPersistentData.getInt("sailorFlightDamageCancel");
        if (flightCancel >= 1) {
            playerPersistentData.putInt("sailorFlightDamageCancel", flightCancel + 1);
        }
        if (flightCancel >= 300) {
            playerPersistentData.putInt("sailorFlightDamageCancel", 0);
        }
        if (flight >= 1) {
            playerPersistentData.putInt("sailorFlight", flight + 1);
        }
        if (flight <= 60 && flight % 20 == 0) {
            player.setDeltaMovement(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);
            player.hurtMarked = true;
        }
        if (flight > 60) {
            playerPersistentData.putInt("sailorFlight", 0);
        }
    }

    protected static void windManipulationSense(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //WIND MANIPULATION SENSE
        boolean windManipulationSense = playerPersistentData.getBoolean("windManipulationSense");
        if (!windManipulationSense) {
            return;
        }
        if (!holder.useSpirituality(2)) return;
        double radius = 100 - (holder.getCurrentSequence() * 10);
        for (Player otherPlayer : player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(radius))) {
            if (otherPlayer == player) {
                continue;
            }
            Vec3 directionToPlayer = otherPlayer.position().subtract(player.position()).normalize();
            Vec3 lookAngle = player.getLookAngle();
            double horizontalAngle = Math.atan2(directionToPlayer.x, directionToPlayer.z) - Math.atan2(lookAngle.x, lookAngle.z);

            String message = AbilityEventsUtil.getString(otherPlayer, horizontalAngle, directionToPlayer);
            if (player.tickCount % 200 == 0) {
                player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
            }
        }
    }

    protected static void windManipulationCushion(CompoundTag playerPersistentData, Player player) {
        //WIND MANIPULATION CUSHION
        int cushion = playerPersistentData.getInt("windManipulationCushion");
        if (cushion >= 1) {
            WindManipulationCushion.summonWindCushionParticles(player);
            playerPersistentData.putInt("windManipulationCushion", cushion - 1);
            player.resetFallDistance();
        }
        if (cushion >= 20 && player.getDeltaMovement().y <= 0) {
            player.setDeltaMovement(player.getDeltaMovement().x(), player.getDeltaMovement().y() * 0.7, player.getDeltaMovement().z());
            player.hurtMarked = true;
        }
        if (cushion == 1) {
            player.setDeltaMovement(player.getLookAngle().scale(2.0f));
            player.hurtMarked = true;
            player.resetFallDistance();
        }
    }

    protected static void windManipulationGuide(CompoundTag playerPersistentData, BeyonderHolder holder, Player player) {
        //WIND MANIPULATION GLIDE
        int regularFlight = playerPersistentData.getInt("sailorFlight");
        boolean enhancedFlight = playerPersistentData.getBoolean("sailorFlight1");
        if (
                holder.currentClassMatches(BeyonderClassInit.SAILOR) &&
                        holder.getCurrentSequence() <= 7 &&
                        player.isShiftKeyDown() &&
                        player.getDeltaMovement().y() < 0 &&
                        !player.getAbilities().instabuild &&
                        !enhancedFlight &&
                        regularFlight == 0
        ) {
            Vec3 movement = player.getDeltaMovement();
            double deltaX = Math.cos(Math.toRadians(player.getYRot() + 90)) * 0.06;
            double deltaZ = Math.sin(Math.toRadians(player.getYRot() + 90)) * 0.06;
            player.setDeltaMovement(movement.x + deltaX, -0.05, movement.z + deltaZ);
            player.resetFallDistance();
            player.hurtMarked = true;
        }
    }
}
