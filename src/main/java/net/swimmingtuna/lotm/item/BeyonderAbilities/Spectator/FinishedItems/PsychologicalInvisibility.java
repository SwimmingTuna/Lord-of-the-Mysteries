package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PsychologicalInvisibility extends Item  {
    private static final String HAD_ARMOR_TAG = "HadArmor";
    private AtomicInteger spiritualityUseCounter;


    public PsychologicalInvisibility(Properties pProperties) {
        super(pProperties);
        this.spiritualityUseCounter = new AtomicInteger(0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        ItemStack itemStack = pPlayer.getItemInHand(hand);
        if (!pPlayer.level().isClientSide) {
        boolean hadArmor = pPlayer.getPersistentData().getBoolean("HAD_ARMOR_TAG");
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 7) {
                if (hadArmor || !storedArmor.isEmpty()) {
                    restoreArmor(pPlayer);
                    storedArmor.clear();
                } else {
                    storeArmor(pPlayer);
                }
                pPlayer.getCooldowns().addCooldown(this,20);
            }
            });
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    public List<ItemStack> storedArmor = new ArrayList<>();

    private void storeArmor(Player pPlayer) {
        AttributeInstance armorInvisAttribute = pPlayer.getAttribute(ModAttributes.ARMORINVISIBLITY.get());
        pPlayer.getPersistentData().putBoolean(HAD_ARMOR_TAG, true);
        pPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,10000,-1,false,false));
        storedArmor.clear();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armorStack = pPlayer.getItemBySlot(slot);
                if (!armorStack.isEmpty()) {
                    storedArmor.add(armorStack.copy());
                    pPlayer.setItemSlot(slot, ItemStack.EMPTY);
                }
                if (armorStack.isEmpty()) {
                    storedArmor.add(Items.LEATHER_BOOTS.getDefaultInstance());
                    armorInvisAttribute.setBaseValue(1);
                }
            }
        }
    }

    private void restoreArmor(Player pPlayer) {
        pPlayer.getPersistentData().putBoolean(HAD_ARMOR_TAG, false);
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
            AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
            if (!pPlayer.level().isClientSide) {
                boolean hadArmor = pPlayer.getPersistentData().getBoolean(HAD_ARMOR_TAG);

                final int[] counterValue = {spiritualityUseCounter.get()};

                if (hadArmor) {
                    BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                        if (!pPlayer.getAbilities().instabuild) {
                            if (spectatorSequence.getSpirituality() > 41) {
                                counterValue[0]++;
                                if (counterValue[0] >= 20) {
                                    spectatorSequence.useSpirituality((int) (40 / dreamIntoReality.getValue()));
                                    counterValue[0] = 0;
                                }
                            } else {
                                restoreArmor(pPlayer);
                                counterValue[0] = 0;
                            }
                        }
                    });
                }
                spiritualityUseCounter.set(counterValue[0]);
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