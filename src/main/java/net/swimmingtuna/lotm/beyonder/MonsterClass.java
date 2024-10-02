package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
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
        if (player.level().getGameTime() % 20 == 0) {
            MobEffectInstance speedEffect = player.getEffect(MobEffects.MOVEMENT_SPEED);
            MobEffectInstance hasteEffect = player.getEffect(MobEffects.DIG_SPEED);
            MobEffectInstance resistanceEffect = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            MobEffectInstance strengthEffect = player.getEffect(MobEffects.DAMAGE_BOOST);
            MobEffectInstance regenEffect = player.getEffect(MobEffects.REGENERATION);
            if (sequenceLevel == 8 || sequenceLevel == 7) {
                if (player.getMainHandItem().getItem() instanceof SwordItem) {
                    if (speedEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 1, false, false));
                    } else if (speedEffect.getAmplifier() < speed + 2) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 1, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof AxeItem) {
                    if (strengthEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 45, strength + 1, false, false));
                    } else if (strengthEffect.getAmplifier() < strength + 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 45, strength + 1, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                    if (hasteEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 1, false, false));
                    } else if (hasteEffect.getAmplifier() < 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 1, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                    if (speedEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 1, false, false));
                    } else if (speedEffect.getAmplifier() < speed + 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 1, false, false));
                    }
                }


            }
            if (sequenceLevel == 6 || sequenceLevel == 5) {
                if (player.getMainHandItem().getItem() instanceof SwordItem) {
                    if (speedEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 1, false, false));
                    } else if (speedEffect.getAmplifier() < speed + 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 1, false, false));
                    }
                    if (hasteEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 1, false, false));
                    } else if (hasteEffect.getAmplifier() < 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 1, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof AxeItem) {
                    if (strengthEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 45, strength + 1, false, false));
                    } else if (strengthEffect.getAmplifier() < strength + 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 45, strength + 1, false, false));
                    }
                    if (resistanceEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 45, resistance + 1, false, false));
                    } else if (resistanceEffect.getAmplifier() < resistance + 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 45, resistance + 1, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                    if (hasteEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 2, false, false));
                    } else if (hasteEffect.getAmplifier() < 2) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 2, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                    if (speedEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 2, false, false));
                    } else if (speedEffect.getAmplifier() < speed + 2) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 2, false, false));
                    }
                }


            }
            if (sequenceLevel <= 4) {
                if (player.getMainHandItem().getItem() instanceof SwordItem) {
                    if (speedEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 2, false, false));
                    } else if (speedEffect.getAmplifier() < speed + 2) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 2, false, false));
                    }
                    if (hasteEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 1, false, false));
                    } else if (hasteEffect.getAmplifier() < 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 1, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof AxeItem) {
                    if (strengthEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 45, strength + 2, false, false));
                    } else if (strengthEffect.getAmplifier() < strength + 2) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 45, strength + 2, false, false));
                    }
                    if (resistanceEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 45, resistance + 1, false, false));
                    } else if (resistanceEffect.getAmplifier() < resistance) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 45, resistance + 1, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                    if (hasteEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 3, false, false));
                    } else if (hasteEffect.getAmplifier() < 3) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 45, 3, false, false));
                    }
                }


                if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                    if (speedEffect == null) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 3, false, false));
                    } else if (speedEffect.getAmplifier() < speed + 3) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 45, speed + 3, false, false));
                    }
                }


            }
        }
        if (player.level().getGameTime() % 50 == 0) {
            if (sequenceLevel == 9) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
                speed = 0;
                resistance = 1;
                regen = 0;
                strength = 0;
            }
            if (sequenceLevel == 8) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
                speed = 1;
                resistance = 1;
                regen = 0;
                strength = 0;
            }
            if (sequenceLevel == 7) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 0, false, false));
                speed = 1;
                resistance = 1;
                regen = 0;
                strength = 1;
            }
            if (sequenceLevel == 6) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 1, false, false));
                speed = 1;
                resistance = 2;
                regen = 1;
                strength = 1;
            }
            if (sequenceLevel == 5) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 1, false, false));
                speed = 2;
                resistance = 2;
                regen = 1;
                strength = 2;
            }
            if (sequenceLevel == 4) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 1, false, false));
                speed = 2;
                resistance = 3;
                regen = 2;
                strength = 3;
            }
            if (sequenceLevel == 3) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 1, false, false));
                speed = 2;
                resistance = 3;
                regen = 3;
                strength = 3;
            }
            if (sequenceLevel == 2) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 1, false, false));
                speed = 3;
                resistance = 3;
                regen = 3;
                strength = 3;
            }
            if (sequenceLevel == 1) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 4, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 4, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 1, false, false));
                speed = 3;
                resistance = 3;
                regen = 4;
                strength = 4;
            }
            if (sequenceLevel == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 4, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 5, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 15 * 20, 1, false, false));
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
        items.put(0, ItemInit.PLACATE.get());
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
        }
    }

}
