package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
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
    public List<Integer> spiritualityRegen() {
        return List.of(34, 22, 16, 12, 10, 8, 6, 5, 3, 2);
    }

    @Override
    public List<Double> maxHealth() {
        return List.of(350.0, 250.0, 186.0, 136.0, 96.0, 66.0, 54.0, 48.0, 28.0, 22.0);
    }

    @Override
    public void tick(Player pPlayer, int sequenceLevel) {
        if (pPlayer.level().getGameTime() % 50 == 0) {
            CompoundTag tag = pPlayer.getPersistentData();
            Abilities playerAbilites = pPlayer.getAbilities();
            boolean x = tag.getBoolean("sailorFlight1");
            if (pPlayer.isInWater() || pPlayer.level().isRaining()) {
                playerAbilites.setFlyingSpeed(0.2F);
                pPlayer.onUpdateAbilities();
                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
                }
                MobEffectInstance dolphinsGrace = pPlayer.getEffect(MobEffects.DOLPHINS_GRACE);
                MobEffectInstance speed = pPlayer.getEffect(MobEffects.MOVEMENT_SPEED);
                MobEffectInstance haste = pPlayer.getEffect(MobEffects.DIG_SPEED);
                MobEffectInstance resistance = pPlayer.getEffect(MobEffects.DAMAGE_RESISTANCE);
                MobEffectInstance strength = pPlayer.getEffect(MobEffects.DAMAGE_BOOST);
                MobEffectInstance regen = pPlayer.getEffect(MobEffects.REGENERATION);
                if (pPlayer.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, dolphinsGrace.getAmplifier() + 2, false, false));}
                if (pPlayer.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, speed.getAmplifier() + 1, false, false));}
                if (pPlayer.hasEffect(MobEffects.DIG_SPEED)) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, haste.getAmplifier() + 1, false, false));}
                if (pPlayer.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, resistance.getAmplifier() + 1, false, false));}
                if (pPlayer.hasEffect(MobEffects.DAMAGE_BOOST)) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, strength.getAmplifier() + 2, false, false));}
                if (pPlayer.hasEffect(MobEffects.REGENERATION)) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, regen.getAmplifier() + 2, false, false));}
            }
            if (!pPlayer.level().isRaining() && !x) {
                playerAbilites.setFlyingSpeed(0.05F);
                pPlayer.onUpdateAbilities();
                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
                }
            }
            if (sequenceLevel == 9) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 8) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 7) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 6) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 0, false, false));
            }
            if (sequenceLevel == 5) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 1, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 1, false, false));
            }
            if (sequenceLevel == 4) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
            }
            if (sequenceLevel == 3) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 2, false, false));
            }
            if (sequenceLevel == 2) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 4, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 3, false, false));
            }
            if (sequenceLevel == 1) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 4, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 4, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 4, false, false));
            }
            if (sequenceLevel == 0) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 20, 2, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 5, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 4, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 15 * 20, 3, false, false));
                pPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 15 * 20, 4, false, false));
            }
        }
    }
    @Override
    public HashMultimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(8, ItemInit.RagingBlows.get());

        items.put(7, ItemInit.EnableOrDisableLightning.get());
        items.put(7, ItemInit.AqueousLightDrown.get());
        items.put(7, ItemInit.AqueousLightPull.get());
        items.put(7, ItemInit.AqueousLightPush.get());

        items.put(6, ItemInit.WindManipulationBlade.get());
        items.put(6, ItemInit.WindManipulationCushion.get());
        items.put(6, ItemInit.WindManipulationFlight.get());
        items.put(6, ItemInit.WindManipulationSense.get());

        items.put(5, ItemInit.SailorLightning.get());
        items.put(5, ItemInit.SirenSongHarm.get());
        items.put(5, ItemInit.SirenSongStrengthen.get());
        items.put(5, ItemInit.SirenSongWeaken.get());
        items.put(5, ItemInit.SirenSongStun.get());
        items.put(5, ItemInit.AcidicRain.get());
        items.put(5, ItemInit.WaterSphere.get());

        items.put(4, ItemInit.Tsunami.get());
        items.put(4, ItemInit.TsunamiSeal.get());
        items.put(4, ItemInit.Hurricane.get());
        items.put(4, ItemInit.Tornado.get());
        items.put(4, ItemInit.Earthquake.get());
        items.put(4, ItemInit.Roar.get());

        items.put(3, ItemInit.AquaticLifeManipulation.get());
        items.put(3, ItemInit.LightningStorm.get());
        items.put(3, ItemInit.LightningBranch.get());
        items.put(3, ItemInit.SonicBoom.get());
        items.put(3, ItemInit.ThunderClap.get());

        items.put(2, ItemInit.LightningBall.get());
        items.put(2, ItemInit.VolcanicEruption.get());
        items.put(2, ItemInit.RainEyes.get());
        items.put(2, ItemInit.ExtremeColdness.get());

        items.put(1, ItemInit.LightningBallAbsorb.get());
        items.put(1, ItemInit.StarOfLightning.get());
        items.put(1, ItemInit.SailorLightningTravel.get());
        items.put(1, ItemInit.LightningRedirection.get());

        items.put(0, ItemInit.StormSeal.get());
        items.put(0, ItemInit.WaterColumn.get());
        items.put(0, ItemInit.MatterAccelerationBlocks.get());
        items.put(0, ItemInit.MatterAccelerationSelf.get());
        items.put(0, ItemInit.MatterAccelerationEntities.get());
        items.put(0, ItemInit.Tyranny.get());


        return items;
    }
}
