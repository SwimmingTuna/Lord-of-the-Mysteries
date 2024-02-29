package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PsychologicalInvisibility extends Item  {
    private int spiritualityUseCounter = 0; // Counter for tracking ticks

    public PsychologicalInvisibility(Properties pProperties) {
        super(pProperties);
    }

    private boolean hadArmor = false;

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        ItemStack itemStack = pPlayer.getItemInHand(hand);
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 7) {
            if (!pPlayer.level().isClientSide) {
            if ((!storedArmor.isEmpty() || hadArmor)) { //if the player has armor in the "inventory", or if hadArmor is true, when this is clicked, hadArmor is made false
                restoreArmor(pPlayer);
                storedArmor.clear();
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 200);
            } else if (!pPlayer.getInventory().armor.isEmpty() || !hadArmor) { //turns off invisibility, hadArmor is now true
                storeArmor(pPlayer);
            }
        }}});
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }
    public List<ItemStack> storedArmor = new ArrayList<>();

    private void storeArmor(Player pPlayer) {
        hadArmor = true;
        pPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,10000,-1,false,false));
        storedArmor.clear();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armorStack = pPlayer.getItemBySlot(slot);
                if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem) {
                    storedArmor.add(armorStack.copy());
                    pPlayer.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
        }
    }

    private void restoreArmor(Player pPlayer) {
        hadArmor = false;
        pPlayer.removeEffect(MobEffects.INVISIBILITY);
        for (ItemStack storedStack : storedArmor) {
            if (!storedStack.isEmpty() && storedStack.getItem() instanceof ArmorItem) {
                EquipmentSlot slot = ((ArmorItem) storedStack.getItem()).getEquipmentSlot();
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    pPlayer.setItemSlot(slot, storedStack.copy());
                }
            }
        }
        storedArmor.clear();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, takes off your armor and turns you invisible while draining your spirituality while you're invisible, upon use again, turns you visible and gives you back your armor\n" +
                    "Spirituality Used: 40 every second\n" +
                    "Cooldown: 10 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player) {
            Player pPlayer = (Player) entity;
            if (!pPlayer.level().isClientSide) {
                if (hadArmor) {
                    BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                        if (!pPlayer.getAbilities().instabuild) {
                            if (spectatorSequence.getSpirituality() > 41) {
                                spiritualityUseCounter++;
                                if (spiritualityUseCounter >= 20) {
                                    spectatorSequence.useSpirituality(40);
                                    spiritualityUseCounter = 0;
                                }
                            } else {
                                restoreArmor(pPlayer);
                                spiritualityUseCounter = 0;
                            }
                        }
                    });
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    @SubscribeEvent
    public static void resetArmor(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player pPlayer = (Player) event.getEntity();
            PsychologicalInvisibility itemInstance = getPlayerHeldItem(pPlayer);
        if (itemInstance != null) {
            itemInstance.clearStoredArmor(pPlayer);
        }
        }
    }

    private void clearStoredArmor(Player pPlayer) {
        storedArmor.clear();
    }

    private static PsychologicalInvisibility getPlayerHeldItem(Player pPlayer) {
        for (ItemStack stack : pPlayer.getInventory().items) {
            if (stack.getItem() instanceof PsychologicalInvisibility) {
                return (PsychologicalInvisibility) stack.getItem();
            }
        }
        return null;
    }
}