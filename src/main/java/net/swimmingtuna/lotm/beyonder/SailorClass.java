package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.List;

public class SailorClass implements BeyonderClass {
    private int dolhpinsGrace;
    private int speed;
    private int strength;
    private int haste;
    private int resistance;
    private int regeneration;

    @Override
    public List<String> sequenceNames() {
        return List.of(
                "Tyrant",
                "Thunder God",
                "Calamity",
                "Sea King",
                "Cataclysmic Interrer",
                "Ocean Songster",
                "Wind-blessed",
                "Seafarer",
                "Folk of Rage",
                "Sailor"
        );
    }

    @Override
    public List<Integer> spiritualityLevels() {
        return List.of(10000, 5000, 3000, 1800, 1200, 700, 450, 300, 175, 125);
    }

    @Override
    public List<Integer> mentalStrength() {
        return List.of(400, 300, 220, 170, 150, 110, 80, 70, 50, 30);
    }

    @Override
    public List<Integer> spiritualityRegen() {
        return List.of(34, 22, 16, 12, 10, 8, 6, 5, 3, 2);
    }

    @Override
    public List<Double> maxHealth() {
        return List.of(80.0, 65.0, 60.0, 50.0, 45.0, 35.0, 30.0, 30.0, 25.0, 23.0);
    }

    @Override
    public void tick(Player player, int sequenceLevel) {
        if (player.level().getGameTime() % 50 != 0) {
            return;
        }
        CompoundTag tag = player.getPersistentData();
        Abilities playerAbilites = player.getAbilities();
        boolean sailorFlight1 = tag.getBoolean("sailorFlight1");
        if (player.isInWater() || player.level().isRaining()) {
            playerAbilites.setFlyingSpeed(0.1F);
            player.onUpdateAbilities();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
            }

            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, dolhpinsGrace + 2, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, speed + 1, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, haste + 1, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, resistance + 1, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, strength + 2, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, regeneration + 2, false, false);
        }
        if (!player.level().isRaining() && !sailorFlight1) {
            playerAbilites.setFlyingSpeed(0.05F);
            player.onUpdateAbilities();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
            }
        }
        if (sequenceLevel == 9) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 0, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            dolhpinsGrace = 0;
            regeneration = -1;
            speed = 0;
            strength = -1;
            resistance = 0;
            haste = -1;
        }
        if (sequenceLevel == 8) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 0, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 0, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            dolhpinsGrace = 0;
            regeneration = -1;
            speed = 0;
            strength = 0;
            resistance = 0;
            haste = 0;
        }
        if (sequenceLevel == 7) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 1, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 1, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 0, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.WATER_BREATHING, 300, 0, false, false);
            dolhpinsGrace = 1;
            regeneration = 0;
            speed = 1;
            strength = 1;
            resistance = 0;
            haste = 0;
        }
        if (sequenceLevel == 6) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 1, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 1, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 1, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.WATER_BREATHING, 300, 0, false, false);
            dolhpinsGrace = 1;
            regeneration = 0;
            speed = 1;
            strength = 1;
            resistance = 0;
            haste = 1;
        }
        if (sequenceLevel == 5) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 1, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 1, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 2, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, 1, false, false);
            applyMobEffect(player,MobEffects.WATER_BREATHING, 300, 1, false, false);
            dolhpinsGrace = 1;
            regeneration = 1;
            speed = 1;
            strength = 2;
            resistance = 1;
            haste = 1;
        }
        if (sequenceLevel == 4) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 2, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 3, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 2, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, 2, false, false);
            applyMobEffect(player,MobEffects.WATER_BREATHING, 300, 2, false, false);
            dolhpinsGrace = 2;
            regeneration = 2;
            speed = 2;
            strength = 3;
            resistance = 2;
            haste = 2;
        }
        if (sequenceLevel == 3) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 3, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 3, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 2, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, 3, false, false);
            applyMobEffect(player,MobEffects.WATER_BREATHING, 300, 2, false, false);
            dolhpinsGrace = 2;
            regeneration = 3;
            speed = 2;
            strength = 3;
            resistance = 2;
            haste = 3;
        }
        if (sequenceLevel == 2) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 3, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 4, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, 3, false, false);
            applyMobEffect(player,MobEffects.WATER_BREATHING, 300, 3, false, false);
            dolhpinsGrace = 2;
            regeneration = 3;
            speed = 3;
            strength = 4;
            resistance = 2;
            haste = 3;

        }
        if (sequenceLevel == 1) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 4, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 4, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, 3, false, false);
            applyMobEffect(player,MobEffects.WATER_BREATHING, 300, 4, false, false);
            dolhpinsGrace = 2;
            regeneration = 3;
            speed = 3;
            strength = 4;
            resistance = 2;
            haste = 4;
        }
        if (sequenceLevel == 0) {
            applyMobEffect(player,MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_BOOST, 300, 5, false, false);
            applyMobEffect(player,MobEffects.DIG_SPEED, 300, 4, false, false);
            applyMobEffect(player,MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
            applyMobEffect(player,MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player,MobEffects.REGENERATION, 300, 4, false, false);
            applyMobEffect(player,MobEffects.WATER_BREATHING, 300, 4, false, false);
            dolhpinsGrace = 2;
            regeneration = 4;
            speed = 3;
            strength = 5;
            resistance = 2;
            haste = 4;
        }
    }

    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(9, ItemInit.BEYONDER_ABILITY_USER.get());
        items.put(8, ItemInit.RAGING_BLOWS.get());
        items.put(8, ItemInit.SAILORPROJECTILECTONROL.get());

        items.put(7, ItemInit.ENABLE_OR_DISABLE_LIGHTNING.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_DROWN.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_PULL.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_PUSH.get());

        items.put(6, ItemInit.WIND_MANIPULATION_BLADE.get());
        items.put(6, ItemInit.WIND_MANIPULATION_CUSHION.get());
        items.put(6, ItemInit.WIND_MANIPULATION_FLIGHT.get());
        items.put(6, ItemInit.WIND_MANIPULATION_SENSE.get());

        items.put(5, ItemInit.SAILOR_LIGHTNING.get());
        items.put(5, ItemInit.SIREN_SONG_HARM.get());
        items.put(5, ItemInit.SIREN_SONG_STRENGTHEN.get());
        items.put(5, ItemInit.SIREN_SONG_WEAKEN.get());
        items.put(5, ItemInit.SIREN_SONG_STUN.get());
        items.put(5, ItemInit.ACIDIC_RAIN.get());
        items.put(5, ItemInit.WATER_SPHERE.get());

        items.put(4, ItemInit.TSUNAMI.get());
        items.put(4, ItemInit.TSUNAMI_SEAL.get());
        items.put(4, ItemInit.HURRICANE.get());
        items.put(4, ItemInit.TORNADO.get());
        items.put(4, ItemInit.EARTHQUAKE.get());
        items.put(4, ItemInit.ROAR.get());

        items.put(3, ItemInit.AQUATIC_LIFE_MANIPULATION.get());
        items.put(3, ItemInit.LIGHTNING_STORM.get());
        items.put(3, ItemInit.LIGHTNING_BRANCH.get());
        items.put(3, ItemInit.SONIC_BOOM.get());
        items.put(3, ItemInit.THUNDER_CLAP.get());

        items.put(2, ItemInit.LIGHTNING_BALL.get());
        items.put(2, ItemInit.VOLCANIC_ERUPTION.get());
        items.put(2, ItemInit.RAIN_EYES.get());
        items.put(2, ItemInit.EXTREME_COLDNESS.get());

        items.put(1, ItemInit.LIGHTNING_BALL_ABSORB.get());
        items.put(1, ItemInit.STAR_OF_LIGHTNING.get());
        items.put(1, ItemInit.SAILOR_LIGHTNING_TRAVEL.get());
        items.put(1, ItemInit.LIGHTNING_REDIRECTION.get());

        items.put(0, ItemInit.STORM_SEAL.get());
        items.put(0, ItemInit.WATER_COLUMN.get());
        items.put(0, ItemInit.MATTER_ACCELERATION_BLOCKS.get());
        items.put(0, ItemInit.MATTER_ACCELERATION_SELF.get());
        items.put(0, ItemInit.MATTER_ACCELERATION_ENTITIES.get());
        items.put(0, ItemInit.TYRANNY.get());


        return items;
    }

    @Override
    public ChatFormatting getColorFormatting() {
        return ChatFormatting.BLUE;
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
