package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EnvisionDeath extends Item {

    public EnvisionDeath(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() == 0 && spectatorSequence.useSpirituality(2000)) {
                AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                envisionDeath(pPlayer, (int) dreamIntoReality.getValue());
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 2400);
            }
        });
        return super.use(level, pPlayer, hand);
    }

    private void envisionDeath(Player pPlayer, int dir) {
        double radius = 300 * dir;
        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
            if (entity != pPlayer) {
                int entityHealth = (int) entity.getHealth();
                if (entityHealth <= 100) {
                    entity.hurt(entity.damageSources().magic(),100);
                }
        }}
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, all living around you with less than 100 health die\n" +
                    "Left Click for Envision Health\n" +
                    "Spirituality Used: 2000\n" +
                    "Cooldown: 2 minutes"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionDeath) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionHealth.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionDeath) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionHealth.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}
