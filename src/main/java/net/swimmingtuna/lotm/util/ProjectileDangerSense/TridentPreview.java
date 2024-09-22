package net.swimmingtuna.lotm.util.ProjectileDangerSense;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class TridentPreview extends BowArrowPreview {

    public TridentPreview(Level level) {
        super(level);
    }

    @Override
    public List<AbstractArrow> initializeEntities(Player player, ItemStack associatedItem, EquipmentSlot hand) {
        if (associatedItem.getItem() instanceof TridentItem) {
            int timeLeft = player.getUseItemRemainingTicks();
            if (timeLeft > 0) {
                int maxDuration = 0;
                if (hand == EquipmentSlot.MAINHAND)
                    maxDuration = player.getMainHandItem().getUseDuration();
                else
                    maxDuration = player.getOffhandItem().getUseDuration();
                int difference = maxDuration - timeLeft;
                if (difference >= 10) {
                    ThrownTrident trident = new ThrownTrident(level(), player, associatedItem);
                    trident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + EnchantmentHelper.getRiptide(associatedItem) * 0.5F, 0);
                    return Collections.singletonList(trident);
                }
            }
        }
        return null;
    }
}
