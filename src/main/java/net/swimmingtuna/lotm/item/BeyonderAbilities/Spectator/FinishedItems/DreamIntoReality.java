package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;


public class DreamIntoReality extends SimpleAbilityItem {
    private static final String CAN_FLY = "CanFly";

    public DreamIntoReality(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 2, 300, 300);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        if (player.getPersistentData().getBoolean(CAN_FLY)) {
            addCooldown(player);
        } else {
            useSpirituality(player);
        }
        toggleFlying(player);
        return InteractionResult.SUCCESS;
    }

    private void toggleFlying(Player player) {
        if (!player.level().isClientSide()) {
            boolean canFly = player.getPersistentData().getBoolean(CAN_FLY);
            if (canFly) {
                stopFlying(player);
            } else {
                startFlying(player);
            }
        }
    }

    private void startFlying(Player player) {
        if (!player.level().isClientSide()) {
            AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
            if (dreamIntoReality.getValue() != 3) {
                player.getPersistentData().putBoolean(CAN_FLY, true);
                Abilities playerAbilities = player.getAbilities();
                dreamIntoReality.setBaseValue(4);
                if (!player.isCreative()) {
                    playerAbilities.mayfly = true;
                    playerAbilities.flying = true;
                    playerAbilities.setFlyingSpeed(0.1F);
                }
                ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
                scaleData.setTargetScale(scaleData.getBaseScale() * 12);
                scaleData.markForSync(true);
                player.onUpdateAbilities();
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                }
            }
        }
    }

    public static void stopFlying(Player player) {
        if (!player.level().isClientSide()) {
            AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
            player.getPersistentData().putBoolean(CAN_FLY, false);
            Abilities playerAbilities = player.getAbilities();
            CompoundTag compoundTag = player.getPersistentData();
            int mindscape = compoundTag.getInt("inMindscape");
            if (!playerAbilities.instabuild || mindscape >= 1) {
                playerAbilities.mayfly = false;
                playerAbilities.flying = false;
            }
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
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, turns your dreams into reality, making you a giant with strengthened abilities, quicker regeneration, and stronger melee hits\n" +
                "Spirituality Used: 40 every second\n" +
                "Cooldown: 30 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}