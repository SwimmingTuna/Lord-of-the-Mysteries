package net.swimmingtuna.lotm.events;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventsKingdom {

    @SubscribeEvent
    public static void generateCathedrals(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        CompoundTag tag = pPlayer.getPersistentData();
        if(!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            int mindScape = tag.getInt("inMindscape");
            int x = tag.getInt("mindscapePlayerLocationX");
            int y = tag.getInt("mindscapePlayerLocationY");
            int z = tag.getInt("mindscapePlayerLocationZ");
            Level level = pPlayer.level();
            ServerLevel serverLevel = (ServerLevel) level;
            StructureTemplate[] parts = new StructureTemplate[48];
            if (mindScape == 6) {
                pPlayer.teleportTo(pPlayer.getX() + 77, pPlayer.getY() + 8, pPlayer.getZ() + 206);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(250))) {
                    if (entity != pPlayer) {
                        entity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ() - 10);
                    }}
            }
            for (int i = 0; i < 48; i++) {
                parts[i] = serverLevel.getStructureManager().getOrCreate(new ResourceLocation(LOTM.MOD_ID, "corpse_cathedral_" + (i + 1)));
            }
            BlockPos[] tagPos = new BlockPos[48];
            for (int i = 0; i < 48; i++) {
                tagPos[i] = new BlockPos(x, y + (i * 2), z);
            }
            StructurePlaceSettings settings = BeyonderUtil.getStructurePlaceSettings(new BlockPos(x, y, z));
            for (int i = 0; i < 48; i++) {
                if (mindScape == (i + 2)) {
                    parts[i].placeInWorld(serverLevel, tagPos[i], tagPos[i], settings, null, 3);
                }
            }
        }
    }



    @SubscribeEvent
    public static void mindscapeCounter(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if(!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {
            CompoundTag compoundTag = pPlayer.getPersistentData();
            int mindscape = compoundTag.getInt("inMindscape");
            if (mindscape >= 1) {
                compoundTag.putInt("inMindscape", mindscape + 1);
            }
            if (mindscape >= 1200) {
                compoundTag.putInt("inMindscape", 0);
            }
        }
    }
    @SubscribeEvent
    public static void mindscapeAbilityCounter(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag compoundTag = pPlayer.getPersistentData();
            int mindscape = compoundTag.getInt("mindscapeAbilities");
            AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            double maxSpirituality = holder.getMaxSpirituality();
            Abilities playerAbilities = pPlayer.getAbilities();
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (holder.getCurrentSequence() == 0 && holder.isSpectatorClass()) {
                    if (mindscape >= 1) {
                        compoundTag.putInt("mindscapeAbilities", mindscape - 1);
                        holder.setSpirituality((int) maxSpirituality);
                        if (!compoundTag.getBoolean("CAN_FLY")) {
                            dreamIntoReality.setBaseValue(3);
                            playerAbilities.setFlyingSpeed(0.15F);
                            playerAbilities.mayfly  = true;
                            pPlayer.onUpdateAbilities();
                            if (pPlayer instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                            }
                        }
                    }
                    if (mindscape == 0) {
                        if (!compoundTag.getBoolean("CAN_FLY")) {
                            dreamIntoReality.setBaseValue(1);
                            playerAbilities.setFlyingSpeed(0.05F);
                            if (!playerAbilities.instabuild) {
                                playerAbilities.mayfly = false;
                            }
                            pPlayer.onUpdateAbilities();
                            if (pPlayer instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                            }
                        }
                    }
                }
            });
        }
    }
}
