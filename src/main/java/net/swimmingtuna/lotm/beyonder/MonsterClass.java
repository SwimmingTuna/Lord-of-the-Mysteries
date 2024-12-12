package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.List;

public class MonsterClass implements BeyonderClass {
    private int speed;
    private int resistance;
    private int strength;
    private int regen;

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
        CompoundTag tag = player.getPersistentData();
        if (player.level().getGameTime() % 20 == 0) {
            if (sequenceLevel == 8 || sequenceLevel == 7) {
                if (player.getMainHandItem().getItem() instanceof SwordItem) {
                    applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 60,speed + 1, true,true);
                }


                if (player.getMainHandItem().getItem() instanceof AxeItem) {
                    applyMobEffect(player, MobEffects.DAMAGE_BOOST, 60,strength + 1, true,true);
                }


                if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                    applyMobEffect(player, MobEffects.DIG_SPEED, 60,1, true,true);
                }


                if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                    applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 60,speed + 1, true,true);

                }


            }
            if (sequenceLevel == 6 || sequenceLevel == 5) {
                if (player.getMainHandItem().getItem() instanceof SwordItem) {
                    applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 60,speed + 1, true,true);
                    applyMobEffect(player, MobEffects.DIG_SPEED, 60,0, true,true);

                }


                if (player.getMainHandItem().getItem() instanceof AxeItem) {
                    applyMobEffect(player, MobEffects.DAMAGE_BOOST, 60,strength + 1, true,true);
                    applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 60,resistance + 1, true,true);
                }


                if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                    applyMobEffect(player, MobEffects.DIG_SPEED, 60,2, true,true);
                }


                if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                    applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 60,speed + 1, true,true);
                    applyMobEffect(player, MobEffects.REGENERATION, 60,regen + 1, true,true);


                }
            }
            if (sequenceLevel <= 4) {
                if (player.getMainHandItem().getItem() instanceof SwordItem) {
                    applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 60,speed + 2, true,true);
                    applyMobEffect(player, MobEffects.DIG_SPEED, 60,0, true,true);
                }


                if (player.getMainHandItem().getItem() instanceof AxeItem) {
                    applyMobEffect(player, MobEffects.DAMAGE_BOOST, 60,strength + 1, true,true);
                    applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 60,resistance + 1, true, true);
                }


                if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                    applyMobEffect(player, MobEffects.DIG_SPEED, 60,3, true,true);
                }


                if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                    applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 60,speed + 2, true,true);
                    applyMobEffect(player, MobEffects.REGENERATION, 60,regen + 1, true,true);


                }
            }
        }
        if (player.level().getGameTime() % 50 == 0) {
            if (sequenceLevel == 9) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 0, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 0, false, false);
                speed = 0;
                resistance = 0;
                regen = -1;
                strength = -1;
            }
            if (sequenceLevel == 8) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 0, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.JUMP, 3000, 0, false, false);
                speed = 1;
                resistance = 0;
                regen = -1;
                strength = 0;
            }
            if (sequenceLevel == 7) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300300, 1, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 0, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, 0, false, false);
                speed = 1;
                resistance = 1;
                regen = 0;
                strength = 1;
            }
            if (sequenceLevel == 6) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 1, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.FIRE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, 1, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 1, false, false);
                speed = 1;
                resistance = 1;
                regen = 1;
                strength = 1;
            }
            if (sequenceLevel == 5) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 2, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 2, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.FIRE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, 1, false, false);
                applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 1, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 1, false, false);
                speed = 2;
                resistance = 1;
                regen = 1;
                strength = 2;
            }
            if (sequenceLevel == 4) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 2, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 3, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.FIRE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, 2, false, false);
                applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 2, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 1, false, false);
                speed = 2;
                resistance = 1;
                regen = 2;
                strength = 3;
            }
            if (sequenceLevel == 3) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 2, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 3, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.FIRE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, 3, false, false);
                applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 2, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 1, false, false);
                speed = 2;
                resistance = 1;
                regen = 3;
                strength = 3;
            }
            if (sequenceLevel == 2) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 3, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.FIRE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, 3, false, false);
                applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 2, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 1, false, false);
                speed = 3;
                resistance = 2;
                regen = 3;
                strength = 3;
            }
            if (sequenceLevel == 1) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 4, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.FIRE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, 4, false, false);
                applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 2, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 1, false, false);
                speed = 3;
                resistance = 2;
                regen = 4;
                strength = 4;
            }
            if (sequenceLevel == 0) {
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 4, false, false);
                applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
                applyMobEffect(player, MobEffects.FIRE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, 5, false, false);
                applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 2, false, false);
                applyMobEffect(player, MobEffects.JUMP, 300, 1, false, false);
                speed = 3;
                resistance = 3;
                regen = 5;
                strength = 4;
            }
        }
    }

    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(9, ItemInit.SPIRITVISION.get());
        items.put(9, ItemInit.MONSTERDANGERSENSE.get());
        items.put(8, ItemInit.MONSTERPROJECTILECONTROL.get());
        items.put(7, ItemInit.LUCKPERCEPTION.get());
        items.put(6, ItemInit.PSYCHESTORM.get());
        items.put(5, ItemInit.LUCK_MANIPULATION.get());
        items.put(5, ItemInit.LUCKDEPRIVATION.get());
        items.put(5, ItemInit.LUCKGIFTING.get());
        items.put(5, ItemInit.MISFORTUNEBESTOWAL.get());
        items.put(5, ItemInit.LUCKFUTURETELLING.get());
        items.put(4, ItemInit.DECAYDOMAIN.get());
        items.put(4, ItemInit.PROVIDENCEDOMAIN.get());
        items.put(4, ItemInit.LUCKCHANNELING.get());
        items.put(4, ItemInit.LUCKDENIAL.get());
        items.put(4, ItemInit.MISFORTUNEMANIPULATION.get());
        items.put(4, ItemInit.MONSTERCALAMITYATTRACTION.get());
        items.put(3, ItemInit.CALAMITYINCARNATION.get());
        items.put(3, ItemInit.ENABLEDISABLERIPPLE.get());
        items.put(3, ItemInit.AURAOFCHAOS.get());
        items.put(3, ItemInit.CHAOSWALKERCOMBAT.get());
        items.put(3, ItemInit.MISFORTUNEREDIRECTION.get());
        items.put(3, ItemInit.MONSTERDOMAINTELEPORATION.get());
        items.put(2, ItemInit.WHISPEROFCORRUPTION.get());
        items.put(2, ItemInit.FORTUNEAPPROPIATION.get());
        items.put(2, ItemInit.FALSEPROPHECY.get());
        items.put(2, ItemInit.MISFORTUNEIMPLOSION.get());
        items.put(1, ItemInit.MONSTERREBOOT.get());
        items.put(1, ItemInit.FATEREINCARNATION.get());
        items.put(1, ItemInit.CYCLEOFFATE.get());
        items.put(1, ItemInit.CHAOSAMPLIFICATION.get());
        items.put(1, ItemInit.FATEDCONNECTION.get());
        items.put(1, ItemInit.REBOOTSELF.get());
        items.put(0, ItemInit.PROBABILITYMISFORTUNE.get());
        items.put(0, ItemInit.PROBABILITYFORTUNE.get());
        items.put(0, ItemInit.PROBABILITYFORTUNEINCREASE.get());
        items.put(0, ItemInit.PROBABILITYMISFORTUNEINCREASE.get());
        items.put(0, ItemInit.PROBABILITYWIPE.get());
        items.put(0, ItemInit.PROBABILITYEFFECT.get());
        items.put(0, ItemInit.PROBABILITYINFINITEFORTUNE.get());
        items.put(0, ItemInit.PROBABILITYINFINITEMISFORTUNE.get());

        return items;
    }

    @Override
    public ChatFormatting getColorFormatting() {
        return ChatFormatting.WHITE;
    }

    public void applyMobEffect(Player pPlayer, MobEffect mobEffect, int duration, int amplifier, boolean ambient, boolean visible) {
        MobEffectInstance currentEffect = pPlayer.getEffect(mobEffect);
        MobEffectInstance newEffect = new MobEffectInstance(mobEffect, duration, amplifier, ambient, visible);
        if (currentEffect == null) {
            pPlayer.addEffect(newEffect);
        } else if (currentEffect.getAmplifier() < amplifier) {
            pPlayer.addEffect(newEffect);
        } else if (currentEffect.getAmplifier() == amplifier && duration >= currentEffect.getDuration()) {
            pPlayer.addEffect(newEffect);
        }
    }

}
