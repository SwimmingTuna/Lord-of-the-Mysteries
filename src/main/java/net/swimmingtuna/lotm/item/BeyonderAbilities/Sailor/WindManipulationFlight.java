package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class WindManipulationFlight extends SimpleAbilityItem {


    public WindManipulationFlight(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 7, 0, 10);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        if (holder.getCurrentSequence() <= 4) {
            toggleFlying(player);
        } else {
            useSpirituality(player,40);
            flightRegular(player);
        }
        CompoundTag tag = player.getPersistentData();
        boolean sailorFlight1 = tag.getBoolean("sailorFlight1");
        if (!player.isCreative() && !sailorFlight1) {
            addCooldown(player);
        }
        return InteractionResult.SUCCESS;
    }

    public static void flightRegular(Player player) {
        CompoundTag tag = player.getPersistentData();
        tag.putInt("sailorFlight", 1);
        tag.putInt("sailorFlightDamageCancel", 1);
    }

    public static void startFlying(Player player) {
        player.getPersistentData().putBoolean("sailorFlight1", true);
        Abilities playerAbilities = player.getAbilities();
        if (!playerAbilities.instabuild) {
            playerAbilities.mayfly = true;
            playerAbilities.flying = true;
            playerAbilities.setFlyingSpeed(0.1F);
        }
        player.onUpdateAbilities();
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
        }
    }

    public static void toggleFlying(Player player) {
        boolean canFly = player.getPersistentData().getBoolean("sailorFlight1");
        if (canFly) {
            stopFlying(player);
        } else {
            startFlying(player);
        }
    }

    public static void stopFlying(Player player) {
        player.getPersistentData().putBoolean("sailorFlight1", false);
        Abilities playerAbilities = player.getAbilities();
        if (!player.isCreative() && !player.isSpectator()) {
            playerAbilities.mayfly = false;
            playerAbilities.flying = false;
        }
        playerAbilities.setFlyingSpeed(0.05F);
        player.onUpdateAbilities();
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Upon use, uses the wind to burst forward in the direction the player is looking three times or allow the user to fly, depending on the sequence"));
        tooltipComponents.add(Component.literal("Activation Cost: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("40 per second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("after disabling, 0.5 seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
