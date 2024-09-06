package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

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
import net.swimmingtuna.lotm.entity.AqueousLightEntityPull;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AqueousLightPull extends Item {
    public AqueousLightPull(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return super.use(level, player, hand);
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder == null) return InteractionResultHolder.pass(player.getItemInHand(hand));
        if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
            player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (!holder.useSpirituality(50)) {
            player.displayClientMessage(Component.literal("You need 50 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        if (holder.getCurrentSequence() <= 7) {
            useItem(player);
        }
        if (!player.getAbilities().instabuild) {
            player.getCooldowns().addCooldown(this, 60);
        }
        
        return super.use(level, player, hand);
    }

    public static void useItem(Player pPlayer) {
        Vec3 eyePosition = pPlayer.getEyePosition(1.0f);
        Vec3 direction = pPlayer.getViewVector(1.0f);
        Vec3 initialVelocity = direction.scale(2.0);
        AqueousLightEntityPull.summonEntityWithSpeed(direction, initialVelocity, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, shoots a water bubble that upon hit, pulls the target towards the user\n" +
                    "Spirituality Used: 50\n" +
                    "Cooldown: 3 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof AqueousLightPull) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.AQUEOUS_LIGHT_DROWN.get()));
            heldItem.shrink(1);
        }
    }
    
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof AqueousLightPull) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.AQUEOUS_LIGHT_DROWN.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}