package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.List;

public class SailorClass implements BeyonderClass {
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
        return List.of(350.0, 250.0, 186.0, 136.0, 96.0, 66.0, 54.0, 48.0, 28.0, 22.0);
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
            playerAbilites.setFlyingSpeed(0.2F);
            player.onUpdateAbilities();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
            }
            MobEffectInstance dolphinsGrace = player.getEffect(MobEffects.DOLPHINS_GRACE);
            MobEffectInstance speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
            MobEffectInstance haste = player.getEffect(MobEffects.DIG_SPEED);
            MobEffectInstance resistance = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            MobEffectInstance strength = player.getEffect(MobEffects.DAMAGE_BOOST);
            MobEffectInstance regen = player.getEffect(MobEffects.REGENERATION);
            if (player.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, dolphinsGrace.getAmplifier() + 2, false, false));
            }
            if (player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, speed.getAmplifier() + 1, false, false));
            }
            if (player.hasEffect(MobEffects.DIG_SPEED)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, haste.getAmplifier() + 1, false, false));
            }
            if (player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, resistance.getAmplifier() + 1, false, false));
            }
            if (player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, strength.getAmplifier() + 2, false, false));
            }
            if (player.hasEffect(MobEffects.REGENERATION)) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, regen.getAmplifier() + 2, false, false));
            }
        }
        if (!player.level().isRaining() && !sailorFlight1) {
            playerAbilites.setFlyingSpeed(0.05F);
            player.onUpdateAbilities();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
            }
        }
        if (sequenceLevel == 9) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 0, false, false));
        }
        if (sequenceLevel == 8) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 0, false, false));
        }
        if (sequenceLevel == 7) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 0, false, false));
        }
        if (sequenceLevel == 6) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 0, false, false));
        }
        if (sequenceLevel == 5) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 1, false, false));
        }
        if (sequenceLevel == 4) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
        }
        if (sequenceLevel == 3) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
        }
        if (sequenceLevel == 2) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 4, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 3, false, false));
        }
        if (sequenceLevel == 1) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 4, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 4, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 4, false, false));
        }
        if (sequenceLevel == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 5, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 4, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 4, false, false));
        }
    }

    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(8, ItemInit.RAGING_BLOWS.get());

        items.put(7, ItemInit.ENABLE_OR_DISABLE_LIGHTNING.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_DROWN.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_PULL.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_PUSH.get());
        items.put(4, ItemInit.SAILORPROJECTILECTONROL.get());

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
}
