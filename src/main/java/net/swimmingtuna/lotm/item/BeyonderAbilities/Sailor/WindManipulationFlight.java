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
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WindManipulationFlight extends SimpleAbilityItem {
    public WindManipulationFlight(Item.Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 7, 0, 0);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!checkAll(player)) return InteractionResult.FAIL;
        if (holder.getCurrentSequence() <= 4) {
            toggleFlying(player);
        } else {
            flightRegular(player);
        }
        CompoundTag tag = player.getPersistentData();
        boolean sailorFlight1 = tag.getBoolean("sailorFlight1");
        if (!player.isCreative() && !sailorFlight1) {
            player.getCooldowns().addCooldown(this, 10);
        }
        return InteractionResult.SUCCESS;
    }

    private void flightRegular(Player player) {
        CompoundTag tag = player.getPersistentData();
        tag.putInt("sailorFlight", 1);
        tag.putInt("sailorFlightDamageCancel", 1);
    }

    private void startFlying(Player player) {
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

    private void toggleFlying(Player player) {
        boolean canFly = player.getPersistentData().getBoolean("sailorFlight1");
        if (canFly) {
            stopFlying(player);
        } else {
            startFlying(player);
        }
    }

    private void stopFlying(Player player) {
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

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        int activeSlot = player.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof WindManipulationFlight) {
            player.getInventory().setItem(activeSlot, new ItemStack(ItemInit.WIND_MANIPULATION_CUSHION.get()));
            heldItem.shrink(1);
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        int activeSlot = player.getInventory().selected;
        if (!player.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof WindManipulationFlight) {
            player.getInventory().setItem(activeSlot, new ItemStack(ItemInit.WIND_MANIPULATION_CUSHION.get()));
            heldItem.shrink(1);
        }
    }
}
