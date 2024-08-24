package net.swimmingtuna.lotm.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.ArrayList;
import java.util.List;

public class BeyonderUtil {

    public static Projectile getProjectiles(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            for (Projectile projectile : pPlayer.level().getEntitiesOfClass(Projectile.class, pPlayer.getBoundingBox().inflate(30))) {
                if (projectile.getOwner() == pPlayer) {
                    if (projectile.tickCount > 8 && projectile.tickCount < 50) {
                        return projectile;
                    }
                }
            }
        }
        return null;
    }

    public static StructurePlaceSettings getStructurePlaceSettings(BlockPos pos) {
        BoundingBox boundingBox = new BoundingBox(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX() + 160,
                pos.getY() + 97,
                pos.getZ() + 265
        );
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setRotation(Rotation.NONE);
        settings.setMirror(Mirror.NONE);
        settings.setRotationPivot(pos);
        settings.setBoundingBox(boundingBox);
        return settings;
    }

    public static List<String> getAbilities(Player pPlayer) {
        List<String> abilityNames = new ArrayList<>();
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            int sequence = holder.getCurrentSequence();
            if (holder.isSpectatorClass()) {
                if (sequence <= 8) {
                    abilityNames.add(getItemName(ItemInit.MindReading.get()));
                }
                if (sequence <= 7) {
                    abilityNames.add(getItemName(ItemInit.Awe.get()));
                    abilityNames.add(getItemName(ItemInit.Frenzy.get()));
                    abilityNames.add(getItemName(ItemInit.Placate.get()));
                }
                if (sequence <= 6) {
                    abilityNames.add(getItemName(ItemInit.PsychologicalInvisibility.get()));
                    abilityNames.add(getItemName(ItemInit.BattleHypnotism.get()));
                }
                if (sequence <= 5) {
                    abilityNames.add(getItemName(ItemInit.Guidance.get()));
                    abilityNames.add(getItemName(ItemInit.Alteration.get()));
                    abilityNames.add(getItemName(ItemInit.DreamWalking.get()));
                    abilityNames.add(getItemName(ItemInit.Nightmare.get()));
                }
                if (sequence <= 4) {
                    abilityNames.add(getItemName(ItemInit.ApplyManipulation.get()));
                    abilityNames.add(getItemName(ItemInit.ManipulateMovement.get()));
                    abilityNames.add(getItemName(ItemInit.ManipulateFondness.get()));
                    abilityNames.add(getItemName(ItemInit.ManipulateEmotion.get()));
                    abilityNames.add(getItemName(ItemInit.MentalPlague.get()));
                    abilityNames.add(getItemName(ItemInit.MindStorm.get()));
                    abilityNames.add(getItemName(ItemInit.DragonBreath.get()));
                }
                if (sequence <= 3) {
                    abilityNames.add(getItemName(ItemInit.ConsciousnessStroll.get()));
                    abilityNames.add(getItemName(ItemInit.PlagueStorm.get()));
                    abilityNames.add(getItemName(ItemInit.DreamWeaving.get()));
                }
                if (sequence <= 2) {
                    abilityNames.add(getItemName(ItemInit.Discern.get()));
                    abilityNames.add(getItemName(ItemInit.DreamIntoReality.get()));
                }
                if (sequence <= 1) {
                    abilityNames.add(getItemName(ItemInit.ProphesizeDemise.get()));
                    abilityNames.add(getItemName(ItemInit.ProphesizeTeleportPlayer.get()));
                    abilityNames.add(getItemName(ItemInit.MeteorShower.get()));
                    abilityNames.add(getItemName(ItemInit.MeteorNoLevelShower.get()));
                }
                if (sequence <= 0) {
                    abilityNames.add(getItemName(ItemInit.EnvisionBarrier.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionDeath.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionHealth.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionKingdom.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionLife.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionLocation.get()));
                    abilityNames.add(getItemName(ItemInit.EnvisionLocationBlink.get()));
                    abilityNames.add(getItemName(ItemInit.DreamWeaving.get()));
                }
            }

            if (holder.isSailorClass()) {
                if (sequence <= 8) {
                    abilityNames.add(getItemName(ItemInit.RagingBlows.get()));
                }
                if (sequence <= 7) {
                    abilityNames.add(getItemName(ItemInit.EnableOrDisableLightning.get()));
                    abilityNames.add(getItemName(ItemInit.AqueousLightPush.get()));
                    abilityNames.add(getItemName(ItemInit.AqueousLightPull.get()));
                    abilityNames.add(getItemName(ItemInit.AqueousLightDrown.get()));
                }
                if (sequence <= 6) {
                    abilityNames.add(getItemName(ItemInit.WindManipulationBlade.get()));
                    abilityNames.add(getItemName(ItemInit.WindManipulationCushion.get()));
                    abilityNames.add(getItemName(ItemInit.WindManipulationFlight.get()));
                    abilityNames.add(getItemName(ItemInit.WindManipulationSense.get()));
                }
                if (sequence <= 5) {
                    abilityNames.add(getItemName(ItemInit.SailorLightning.get()));
                    abilityNames.add(getItemName(ItemInit.SirenSongHarm.get()));
                    abilityNames.add(getItemName(ItemInit.SirenSongStrengthen.get()));
                    abilityNames.add(getItemName(ItemInit.SirenSongWeaken.get()));
                    abilityNames.add(getItemName(ItemInit.SirenSongStun.get()));
                    abilityNames.add(getItemName(ItemInit.AcidicRain.get()));
                    abilityNames.add(getItemName(ItemInit.WaterSphere.get()));
                }
                if (sequence <= 4) {
                    abilityNames.add(getItemName(ItemInit.Tsunami.get()));
                    abilityNames.add(getItemName(ItemInit.TsunamiSeal.get()));
                    abilityNames.add(getItemName(ItemInit.Hurricane.get()));
                    abilityNames.add(getItemName(ItemInit.Tornado.get()));
                    abilityNames.add(getItemName(ItemInit.Earthquake.get()));
                    abilityNames.add(getItemName(ItemInit.Roar.get()));
                }
                if (sequence <= 3) {
                    abilityNames.add(getItemName(ItemInit.AquaticLifeManipulation.get()));
                    abilityNames.add(getItemName(ItemInit.LightningStorm.get()));
                    abilityNames.add(getItemName(ItemInit.LightningBranch.get()));
                    abilityNames.add(getItemName(ItemInit.SonicBoom.get()));
                    abilityNames.add(getItemName(ItemInit.ThunderClap.get()));
                }
                if (sequence <= 2) {
                    abilityNames.add(getItemName(ItemInit.VolcanicEruption.get()));
                    abilityNames.add(getItemName(ItemInit.ExtremeColdness.get()));
                    abilityNames.add(getItemName(ItemInit.LightningBall.get()));
                }
                if (sequence <= 1) {
                    abilityNames.add(getItemName(ItemInit.LightningBallAbsorb.get()));
                    abilityNames.add(getItemName(ItemInit.SailorLightningTravel.get()));
                    abilityNames.add(getItemName(ItemInit.StarOfLightning.get()));
                }
                if (sequence <= 0) {
                    abilityNames.add(getItemName(ItemInit.StormSeal.get()));
                    abilityNames.add(getItemName(ItemInit.WaterColumn.get()));
                    abilityNames.add(getItemName(ItemInit.MatterAccelerationSelf.get()));
                    abilityNames.add(getItemName(ItemInit.MatterAccelerationBlocks.get()));
                    abilityNames.add(getItemName(ItemInit.MatterAccelerationEntities.get()));
                    abilityNames.add(getItemName(ItemInit.Tyranny.get()));
                    abilityNames.add(getItemName(ItemInit.TyrantTornado.get()));
                }
            }
        }
        return abilityNames;
    }

    private static String getItemName(Item item) {
        return I18n.get(item.getDescriptionId()).toLowerCase();
    }
}
//make a command that is like: /abilityput (Item Name) (LLRLL or something like that)