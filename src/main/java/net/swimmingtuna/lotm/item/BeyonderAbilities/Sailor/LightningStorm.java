package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.LeftClickC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LightningStorm extends Item {
    public LightningStorm(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 1000) {
                player.displayClientMessage(Component.literal("You need 1000 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 3 && holder.useSpirituality(1000)) {
                useItem(player);
            }
            if (!player.getAbilities().instabuild)
                player.getCooldowns().addCooldown(this, 600);
        }
        return super.use(level, player, hand);
    }

    public static void useItem(Player player) { //add logic to add persitatent data of targetX,
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

    @SubscribeEvent
    public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        Style style = BeyonderUtil.getStyle(player);
        if (player.getMainHandItem().getItem() instanceof LightningStorm) {
            LOTMNetworkHandler.sendToServer(new LeftClickC2S());
        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Style style = BeyonderUtil.getStyle(player);
        if (!player.level().isClientSide()) {
            if (player.getMainHandItem().getItem() instanceof LightningStorm) {
                CompoundTag tag = player.getPersistentData();
                double distance = tag.getDouble("sailorLightningStormDistance");
                tag.putDouble("sailorLightningStormDistance", (int) (distance + 30));
                player.sendSystemMessage(Component.literal("Storm Radius Is" + distance).withStyle(style));
                if (distance > 300) {
                    tag.putDouble("sailorLightningStormDistance", 0);
                }
            }
        }
    }
}