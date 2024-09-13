package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DreamIntoReality extends Item {
    private static final String CAN_FLY = "CanFly";

    public DreamIntoReality(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 50) {
                player.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 2) {
                toggleFlying(player);
                boolean canFly = player.getPersistentData().getBoolean(CAN_FLY);
                if (canFly && !player.getAbilities().instabuild) {
                    player.getCooldowns().addCooldown(this, 300);
                }
            }
        }
        ItemStack itemStack = player.getItemInHand(hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    private void toggleFlying(Player player) {
        boolean canFly = player.getPersistentData().getBoolean(CAN_FLY);
        if (canFly) {
            stopFlying(player);
        } else {
            startFlying(player);
        }
    }

    private void startFlying(Player player) {
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        if (dreamIntoReality.getValue() != 3) {
            player.getPersistentData().putBoolean(CAN_FLY, true);
            Abilities playerAbilities = player.getAbilities();
            dreamIntoReality.setBaseValue(4);
            if (!player.isCreative()) {
                playerAbilities.mayfly = true;
                playerAbilities.flying = true;
                playerAbilities.setFlyingSpeed(0.2F);
            }
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
            scaleData.setTargetScale(scaleData.getBaseScale() * 4);
            scaleData.markForSync(true);
            player.onUpdateAbilities();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
            }
        }
    }

    public static void stopFlying(Player player) {
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        player.getPersistentData().putBoolean(CAN_FLY, false);
        Abilities playerAbilities = player.getAbilities();
        CompoundTag compoundTag = player.getPersistentData();
        int mindscape = compoundTag.getInt("inMindscape");
        if (!playerAbilities.instabuild || mindscape >= 1) {
        playerAbilities.mayfly = false;
        playerAbilities.flying = false;}
        dreamIntoReality.setBaseValue(1);
        playerAbilities.setFlyingSpeed(0.05F);
        player.onUpdateAbilities();
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
        scaleData.setTargetScale(1);
        scaleData.markForSync(true);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, turns your dreams into reality, making you a giant with strengthened abilities, quicker regeneration, and stronger melee hits\n" +
                "Spirituality Used: 300 every second\n" +
                "Cooldown: 30 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}