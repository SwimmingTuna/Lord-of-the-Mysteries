package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class LightningStorm extends SimpleAbilityItem {

    public LightningStorm(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 3, 1000, 600);
    }



    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        lightningStorm(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public static void lightningStorm(Player player) { //add logic to add persitatent data of targetX,
        if (!player.level().isClientSide()) {
            int sailorStormVec = player.getPersistentData().getInt("sailorStormVec");
            Vec3 lookVec = player.getLookAngle();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            double targetX = player.getX() + sailorStormVec * lookVec.x();
            double targetY = player.getY() + sailorStormVec * lookVec.y();
            double targetZ = player.getZ() + sailorStormVec * lookVec.z();
            player.getPersistentData().putDouble("sailorStormVecX", targetX);
            player.getPersistentData().putDouble("sailorStormVecY", targetY);
            player.getPersistentData().putDouble("sailorStormVecZ", targetZ);
            CompoundTag persistentData = player.getPersistentData();
            persistentData.putInt("sailorLightningStorm", 500 - (sequence * 80));
            if (sequence <= 0) {
                persistentData.putInt("sailorLightningStormTyrant", 500);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons a lightning storm, leaving mass destruction\n" +
                "Left Click to Increase Distance Spawned At\n" +
                "Shift to Increase Storm Radius\n" +
                "Spirituality Used: 1000\n" +
                "Cooldown: 30 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}