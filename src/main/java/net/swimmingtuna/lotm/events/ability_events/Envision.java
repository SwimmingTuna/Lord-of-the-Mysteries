package net.swimmingtuna.lotm.events.ability_events;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.EnvisionBarrier;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;

public class Envision {
    protected static void envisionBarrier(BeyonderHolder holder, Player player, Style style) {
        //ENVISION BARRIER
        if (holder.getCurrentSequence() != 0) {
            return;
        }
        int barrierRadius = player.getPersistentData().getInt("BarrierRadius");
        if (player.isShiftKeyDown() && player.getMainHandItem().getItem() instanceof EnvisionBarrier) {
            barrierRadius++;
            player.displayClientMessage(Component.literal("Barrier Radius: " + barrierRadius).withStyle(style), true);
        }
        if (barrierRadius > 101) {
            barrierRadius = 0;
            player.displayClientMessage(Component.literal("Barrier Radius: 0").withStyle(style), true);
        }
        player.getPersistentData().putInt("BarrierRadius", barrierRadius);
    }

    protected static void envisionLife(Player player) {
        //ENVISION LIFE
        int waitMakeLifeCounter = player.getPersistentData().getInt("waitMakeLifeTimer");
        if (waitMakeLifeCounter >= 1) {
            waitMakeLifeCounter++;
        }
        if (waitMakeLifeCounter >= 600) {
            waitMakeLifeCounter = 0;
        }
        player.getPersistentData().putInt("waitMakeLifeTimer", waitMakeLifeCounter);
    }

    protected static void envisionKingdom(CompoundTag playerPersistentData, Player player, BeyonderHolder holder, ServerLevel serverLevel) {
        //ENVISION KINGDOM

        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        int mindScape = playerPersistentData.getInt("inMindscape");
        if (mindScape < 1) return;
        Abilities playerAbilities = player.getAbilities();
        playerPersistentData.putInt("inMindscape", mindScape + 1);
        if (mindScape >= 1200) {
            playerPersistentData.putInt("inMindscape", 0);
        }
        int mindscapeAbilities = playerPersistentData.getInt("mindscapeAbilities");
        if (mindscapeAbilities >= 1) {
            holder.setSpirituality(holder.getMaxSpirituality());
            if (!playerPersistentData.getBoolean("CAN_FLY")) {
                dreamIntoReality.setBaseValue(3);
                playerAbilities.setFlyingSpeed(0.1F);
                playerAbilities.mayfly = true;
                player.onUpdateAbilities();
                playerPersistentData.putInt("mindscapeAbilities", mindscapeAbilities - 1);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                }
            }
        }
        if (mindscapeAbilities == 1 && !playerPersistentData.getBoolean("CAN_FLY")) {
            dreamIntoReality.setBaseValue(1);
            playerAbilities.setFlyingSpeed(0.05F);
            playerAbilities.mayfly = false;
            player.onUpdateAbilities();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
            }
        }

        int partIndex = mindScape - 2;
        if (partIndex < 0) return;

        int mindScape1 = playerPersistentData.getInt("inMindscape");
        int x = playerPersistentData.getInt("mindscapePlayerLocationX");
        int y = playerPersistentData.getInt("mindscapePlayerLocationY");
        int z = playerPersistentData.getInt("mindscapePlayerLocationZ");
        if (mindScape1 < 1) return;
        if (mindScape1 == 11) {
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(250))) {
                if (entity != player) {
                    if (entity instanceof Player) {
                        entity.teleportTo(player.getX(), player.getY() + 1, player.getZ() - 10);
                    } else if (entity.getMaxHealth() >= 50) {
                        entity.teleportTo(player.getX(), player.getY() + 1, player.getZ() - 10);
                    }
                }
            }
        }
        if (mindScape == 2 || mindScape == 4 || mindScape == 6 || mindScape == 8 || mindScape == 10) {
            player.teleportTo(player.getX(), player.getY() + 4.5, player.getZ());
        }
        StructureTemplate part = serverLevel.getStructureManager().getOrCreate(new ResourceLocation(LOTM.MOD_ID, "corpse_cathedral_" + (partIndex + 1)));
        BlockPos tagPos = new BlockPos(x, y + (partIndex * 2), z);
        StructurePlaceSettings settings = BeyonderUtil.getStructurePlaceSettings(new BlockPos(x, y, z));
        part.placeInWorld(serverLevel, tagPos, tagPos, settings, null, Block.UPDATE_ALL);
        playerPersistentData.putInt("inMindscape", mindScape + 1);
    }
}
