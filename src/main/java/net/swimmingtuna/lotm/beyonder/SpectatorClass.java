package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.List;

public class SpectatorClass implements BeyonderClass {
    @Override
    public List<String> sequenceNames() {
        return List.of(
                "Visionary",
                "Author",
                "Discerner",
                "Dream Weaver",
                "Manipulator",
                "Dreamwalker",
                "Hypnotist",
                "Psychiatrist",
                "Telepathist",
                "Spectator"
        );
    }

    @Override
    public List<Integer> spiritualityLevels() {
        return List.of(10000, 5000, 3000, 1800, 1200, 700, 450, 300, 175, 125);
    }

    @Override
    public List<Integer> mentalStrength() {
        return List.of(630, 420, 320, 270, 220, 145, 110, 95, 70, 45);
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
        if (!player.level().isClientSide() && player.isCrouching()) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, -1, false, false));
        }
        if (player.level().getGameTime() % 80 == 0) {
            if (sequenceLevel >= 0) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 30 * 20, -1, false, false));
            }

            if (sequenceLevel == 6) {
               applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 0, false, false);
            }
            if (sequenceLevel == 5) {
                applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 0, false, false);
                applyMobEffect(player,MobEffects.REGENERATION, 300, 0, false, false);

            }
            if (sequenceLevel == 4) {
                applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 1, false, false);
                applyMobEffect(player,MobEffects.REGENERATION, 300, 0, false, false);
                applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);

            }
            if (sequenceLevel == 3) {
                applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 1, false, false);
                applyMobEffect(player,MobEffects.REGENERATION, 300, 1, false, false);
                applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
            }
            if (sequenceLevel == 2) {
                applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 2, false, false);
                applyMobEffect(player,MobEffects.REGENERATION, 300, 1, false, false);
                applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false);
                applyMobEffect(player,MobEffects.FIRE_RESISTANCE, 300, 0, false, false);

            }
            if (sequenceLevel == 1) {
                applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 2, false, false);
                applyMobEffect(player,MobEffects.REGENERATION, 300, 1, false, false);
                applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
                applyMobEffect(player,MobEffects.FIRE_RESISTANCE, 300, 0, false, false);

            }
            if (sequenceLevel == 0) {
                applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 3, false, false);
                applyMobEffect(player,MobEffects.REGENERATION, 300, 2, false, false);
                applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
                applyMobEffect(player,MobEffects.FIRE_RESISTANCE, 300, 0, false, false);
            }
        }
    }
    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(9, ItemInit.BEYONDER_ABILITY_USER.get());
        items.put(8, ItemInit.MIND_READING.get());
        items.put(7, ItemInit.AWE.get());
        items.put(7, ItemInit.FRENZY.get());
        items.put(7, ItemInit.PLACATE.get());
        items.put(6, ItemInit.BATTLE_HYPNOTISM.get());
        items.put(6, ItemInit.PSYCHOLOGICAL_INVISIBILITY.get());
        items.put(5, ItemInit.DREAM_WALKING.get());
        items.put(5, ItemInit.NIGHTMARE.get());
        items.put(4, ItemInit.APPLY_MANIPULATION.get());
        items.put(4, ItemInit.MANIPULATE_EMOTION.get());
        items.put(4, ItemInit.MANIPULATE_FONDNESS.get());
        items.put(4, ItemInit.MANIPULATE_MOVEMENT.get());
        items.put(4, ItemInit.DRAGON_BREATH.get());
        items.put(4, ItemInit.MENTAL_PLAGUE.get());
        items.put(3, ItemInit.MIND_STORM.get());
        items.put(3, ItemInit.PLAGUE_STORM.get());
        items.put(3, ItemInit.CONSCIOUSNESS_STROLL.get());
        items.put(3, ItemInit.DREAM_WEAVING.get());
        items.put(2, ItemInit.DISCERN.get());
        items.put(2, ItemInit.DREAM_INTO_REALITY.get());
        items.put(1, ItemInit.PROPHESIZE_DEMISE.get());
        items.put(1, ItemInit.PROPHESIZE_TELEPORT_BLOCK.get());
        items.put(1, ItemInit.PROPHESIZE_TELEPORT_PLAYER.get());
        items.put(1, ItemInit.METEOR_SHOWER.get());
        items.put(1, ItemInit.METEOR_NO_LEVEL_SHOWER.get());
        items.put(0, ItemInit.ENVISION_BARRIER.get());
        items.put(0, ItemInit.ENVISION_LIFE.get());
        items.put(0, ItemInit.ENVISION_DEATH.get());
        items.put(0, ItemInit.ENVISIONHEALTH.get());
        items.put(0, ItemInit.ENVISION_LOCATION.get());
        items.put(0, ItemInit.ENVISION_LOCATION_BLINK.get());
        items.put(0, ItemInit.ENVISION_WEATHER.get());
        items.put(0, ItemInit.ENVISION_KINGDOM.get());

        return items;
    }

    @Override
    public ChatFormatting getColorFormatting() {
        return ChatFormatting.AQUA;
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
