package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MeteorShower extends Item {

    public MeteorShower(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 2500) {
                pPlayer.displayClientMessage(Component.literal("You need 2500 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }

        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (holder.isSpectatorClass() && spectatorSequence.getCurrentSequence() <= 1 && spectatorSequence.useSpirituality(2500)) {
                meteorShower(pPlayer);
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 900);
            }
        });
            }
        return super.use(level, pPlayer, hand);
    }

    public static void meteorShower(Player pPlayer) {
        Vec3 eyePosition = pPlayer.getEyePosition(1.0f);
        Vec3 direction = pPlayer.getViewVector(1.0f).scale(-1);
        MeteorEntity.summonMultipleMeteors(direction, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer, 4);
        MeteorEntity.summonMultipleMeteors(direction, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer, 4);
        MeteorEntity.summonMultipleMeteors(direction, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer, 4);
        MeteorEntity.summonMultipleMeteors(direction, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer, 4);
        MeteorEntity.summonMultipleMeteors(direction, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer, 4);
        MeteorEntity.summonMultipleMeteors(direction, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer, 4);
        MeteorEntity.summonMultipleMeteors(direction, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer, 4);
        MeteorEntity.summonMultipleMeteors(direction, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer, 4);

    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, summons a meteor shower\n" +
                    "Spirituality Used: 1500\n" +
                    "Left Click for a version that deals no block destruction\n" +
                    "Cooldown: 45 secondss"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof MeteorShower) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MeteorNoLevelShower.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof MeteorShower) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MeteorNoLevelShower.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}
