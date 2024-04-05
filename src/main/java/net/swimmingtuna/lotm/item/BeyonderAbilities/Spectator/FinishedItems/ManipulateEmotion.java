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
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)

public class ManipulateEmotion extends Item implements ReachChangeUUIDs {
    public ManipulateEmotion(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
            if (spectatorSequence.getCurrentSequence() <= 4 && spectatorSequence.useSpirituality(500)) {
                manipulateEmotion(pPlayer, spectatorSequence.getCurrentSequence());
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, (int) (1200/ dreamIntoReality.getValue()));
            }
        });
        return super.use(level, pPlayer, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, all living entities able to be manipulated within 250 blocks fall into despair and harm themselves.\n" +
                    "Left Click for Manipulate Fondness\n" +
                    "Spirituality Used: 500\n" +
                    "Cooldown: 1 minute"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    private static void manipulateEmotion(Player pPlayer, int sequence) {
            float damage = 100 - (sequence * 10);
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(250))) {
                if (entity != pPlayer && entity.hasEffect(ModEffects.MANIPULATION.get())) {
                    entity.hurt(entity.damageSources().magic(), damage);
                    entity.removeEffect(ModEffects.MANIPULATION.get());
                }
            }
        }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ManipulateEmotion) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ManipulateFondness.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ManipulateEmotion) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ManipulateFondness.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}
