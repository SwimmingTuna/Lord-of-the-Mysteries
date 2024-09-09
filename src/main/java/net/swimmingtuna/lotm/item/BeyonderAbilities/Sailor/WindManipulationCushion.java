package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WindManipulationCushion extends SimpleAbilityItem {
    public WindManipulationCushion(Properties pProperties) {
        super(pProperties, BeyonderClassInit.SAILOR, 7, 150, 120);
    }

    @Override
    public void useAbility(Level level, Player player, InteractionHand hand) {
        cushion(player);
    }

    public static void cushion(Player player) {
        player.getPersistentData().putInt("windManipulationCushion", 100);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        componentList.add(Component.literal("Upon use, create a cushion of wind that absorbs your fall then sends you in the direction you're looking"));
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof WindManipulationCushion) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.WIND_MANIPULATION_BLADE.get()));
            heldItem.shrink(1);
        }
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player pPlayer) {
            double cushionParticles = pPlayer.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER3.get());
            if (cushionParticles == 1) {
                double x = pPlayer.getX() - pPlayer.getLookAngle().x * 2;
                double y = pPlayer.getY() + 1.5; // Slightly above the player's feet
                double z = pPlayer.getZ() - pPlayer.getLookAngle().z * 2;

                // Add 10 wind particles behind the player
                for (int i = 0; i < 10; i++) {
                    level.addParticle(ParticleTypes.CLOUD,
                            x + (level.random.nextDouble() - 0.5),
                            y + (level.random.nextDouble() - 0.5),
                            z + (level.random.nextDouble() - 0.5),
                            0, 0, 0);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof WindManipulationCushion) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.WIND_MANIPULATION_BLADE.get()));
            heldItem.shrink(1);
        }
    }
}