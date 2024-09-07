package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
public class WindManipulationSense extends Item {
    public WindManipulationSense(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 100) {
                pPlayer.displayClientMessage(Component.literal("You need 100 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }

            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 7 && tyrantSequence.useSpirituality(10)) {
                    shootLight(pPlayer, level);
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 10);
            });
        }
        return super.use(level, pPlayer, hand);
    }

    public static void shootLight(Player pPlayer, Level level) {
        if (!level.isClientSide()) {
           CompoundTag tag = pPlayer.getPersistentData();
           boolean x = tag.getBoolean("windManipulationSense");
           if (x) {
               tag.putBoolean("windManipulationSense", false);
               pPlayer.displayClientMessage(Component.literal("Wind Sense Turned Off").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);

           }
           if (!x) {
               tag.putBoolean("windManipulationSense", true);
               pPlayer.displayClientMessage(Component.literal("Wind Sense Turned On").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);

           }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, controls the wind around you to extend your senses, alerting you of players around youn and where they are\n" +
                    "Spirituality Used: 4 every second\n" +
                    "Cooldown: 0.5 seconds").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof WindManipulationSense) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.WindManipulationFlight.get()));
            heldItem.shrink(1);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof WindManipulationSense) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.AqueousLightDrown.get()));
            heldItem.shrink(1);
        }
    }
}