package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.List;

public class MonsterClass implements BeyonderClass {
    @Override
    public List<String> sequenceNames() {
        return List.of(
                "Wheel of Fortune",
                "Snake of Mercury",
                "Soothsayer",
                "Chaoswalker",
                "Misfortune Mage",
                "Winner",
                "Calamity Priest",
                "Lucky One",
                "Robot",
                "Monster"
        );
    }

    @Override
    public List<Integer> spiritualityLevels() {
        return List.of(10000, 5000, 3000, 1800, 1200, 700, 450, 300, 175, 125);
    }

    @Override
    public List<Integer> mentalStrength() {
        return List.of(450, 320, 210, 175, 150, 110, 80, 70, 50, 33);
    }

    @Override
    public List<Integer> spiritualityRegen() {
        return List.of(34, 22, 16, 12, 10, 8, 6, 5, 3, 2);
    }

    @Override
    public List<Double> maxHealth() {
        return List.of(350.0, 250.0, 186.0, 136.0, 96.0, 66.0, 54.0, 48.0, 28.0, 22.0);
    }

    @Override
    public void tick(Player player, int sequenceLevel) {
        if (player.level().getGameTime() % 20 == 0) {
            MobEffectInstance speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
            MobEffectInstance haste = player.getEffect(MobEffects.DIG_SPEED);
            MobEffectInstance resistance = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            MobEffectInstance strength = player.getEffect(MobEffects.DAMAGE_BOOST);
            if (sequenceLevel <= 8) {
                if (speed != null && strength != null && player.getMainHandItem().getItem() instanceof SwordItem) {
                    int x = speed.getAmplifier();
                    int y = strength.getAmplifier();
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, x + 1, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, y + 1, true, true));
                }
                if (strength != null && resistance != null && player.getMainHandItem().getItem() instanceof AxeItem) {
                    int x = resistance.getAmplifier();
                    int y = strength.getAmplifier();
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, x + 1, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, y + 1, true, true));
                }
                if (haste != null) {
                    if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                        int x = haste.getAmplifier();
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, x + 2, true, true));
                    }
                }
                if (speed != null) {
                    if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                        int x = speed.getAmplifier();
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, x + 2  , true, true));
                    }
                }
            }
        }
        if (player.level().getGameTime() % 50 == 0) {
            if (sequenceLevel == 9) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 8) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 1, false, false));
            }
            if (sequenceLevel == 7) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 6) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 5) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 4) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 3) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 2) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 1) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 4, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 4, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 4, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 5, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
            }
        }
    }

    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(0, ItemInit.PLACATE.get());
        return items;
    }

    @Override
    public ChatFormatting getColorFormatting() {
        return ChatFormatting.WHITE;
    }


}
