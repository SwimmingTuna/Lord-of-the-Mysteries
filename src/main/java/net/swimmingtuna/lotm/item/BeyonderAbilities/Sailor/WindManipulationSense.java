package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
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
            if (holder.getSpirituality() < 50) {
                pPlayer.displayClientMessage(Component.literal("You need 50 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }

            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 7 && tyrantSequence.useSpirituality(50)) {
                    shootLight(pPlayer, level);
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 40);
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
            componentList.add(Component.literal("Upon use, shoots a projectile that upon hit, pushes the target away from the user\n" +
                    "Spirituality Used: 50\n" +
                    "Cooldown: 2 seconds"));
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
            event.setCanceled(true);
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
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void windGlowingTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide && event.phase == TickEvent.Phase.END) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            CompoundTag tag = pPlayer.getPersistentData();
            boolean x = tag.getBoolean("windManipulationSense");
            if (x) {
                double radius = 100 - (holder.getCurrentSequence() * 10);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                    if (entity != pPlayer && entity instanceof Player player) {
                        Vec3 directionToPlayer = entity.position().subtract(pPlayer.position()).normalize();
                        Vec3 lookAngle = pPlayer.getLookAngle();
                        double horizontalAngle = Math.atan2(directionToPlayer.x, directionToPlayer.z) - Math.atan2(lookAngle.x, lookAngle.z);

                        String horizontalDirection;
                        if (Math.abs(horizontalAngle) < Math.PI / 4) {
                            horizontalDirection = "in front of";
                        } else if (horizontalAngle < -Math.PI * 3 / 4 || horizontalAngle > Math.PI * 3 / 4) {
                            horizontalDirection = "behind";
                        } else if (horizontalAngle < 0) {
                            horizontalDirection = "to the right of";
                        } else {
                            horizontalDirection = "to the left of";
                        }

                        String verticalDirection;
                        if (directionToPlayer.y > 0.2) {
                            verticalDirection = "above";
                        } else if (directionToPlayer.y < -0.2) {
                            verticalDirection = "below";
                        } else {
                            verticalDirection = "at the same level as";
                        }

                        String message = player.getName().getString() + " is " + horizontalDirection + " and " + verticalDirection + " you.";
                        pPlayer.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
                    }
                }
            }
        }
    }
}