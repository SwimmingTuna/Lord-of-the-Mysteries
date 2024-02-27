package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import java.util.ArrayList;
import java.util.List;

public class PsychologicalInvisibility extends Item {
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
            if ((!storedArmor.isEmpty() || hadArmor)) {
                restoreArmor(pPlayer);
                storedArmor.clear();
            } else if (!pPlayer.getInventory().armor.isEmpty() || !hadArmor) {
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
                    break;
                }
            }
        }
    }
   }