package net.swimmingtuna.lotm.item.Blocks;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BlockEntityInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.TickableBlockEntity;
import org.joml.Matrix3dStack;

import java.util.List;
@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CathedralBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks;
    public CathedralBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.CATHEDRAL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide())
            return;
        if (this.ticks++ % 20 == 0) {
            for (Player pPlayer : level.players()) {
                CompoundTag compoundTag = pPlayer.getPersistentData();
                double distanceX = pPlayer.getX() - worldPosition.getX();
                double distanceY = pPlayer.getY() - worldPosition.getY();
                double distanceZ = pPlayer.getZ() - worldPosition.getZ();
                AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                    if (spectatorSequence.getCurrentSequence() == 0) {
                        if (Math.abs(distanceX) <= 180 && Math.abs(distanceY) <= 100 && Math.abs(distanceZ) <= 260) {
                            double maxSpirituality = spectatorSequence.getMaxSpirituality();
                            spectatorSequence.setSpirituality((int) maxSpirituality);
                            Abilities playerAbilities = pPlayer.getAbilities();
                            if (!compoundTag.getBoolean("CAN_FLY")) {
                                dreamIntoReality.setBaseValue(3);
                            }
                            playerAbilities.mayfly = true;
                            playerAbilities.setFlyingSpeed(0.25F);
                            pPlayer.onUpdateAbilities();
                            if (pPlayer instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                            }
                        }
                        if (Math.abs(distanceX) > 180 || Math.abs(distanceY) > 100 || Math.abs(distanceZ) > 260) {
                            pPlayer.sendSystemMessage(Component.literal("working!"));
                            Abilities playerAbilities = pPlayer.getAbilities();
                            if (!compoundTag.getBoolean("CAN_FLY")) {
                                playerAbilities.setFlyingSpeed(0.05F);
                                dreamIntoReality.setBaseValue(1);
                            }
                            if (!pPlayer.getAbilities().instabuild || !compoundTag.getBoolean("CAN_FLY")) {
                                playerAbilities.mayfly = false;
                            }
                        } else {
                            pPlayer.sendSystemMessage(Component.literal("distanceX: " + distanceX + ", distanceY: " + distanceY + ", distanceZ: " + distanceZ));
                        }
                    }
                });
            }
        }
    }
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        if (event.getState().getBlock() == BlockInit.CATHEDRAL_BLOCK.get()) {
            List<Player> pPlayers = (List<Player>) level.players();
            for (Player pPlayer : pPlayers) {
                CompoundTag compoundTag = pPlayer.getPersistentData();

                    AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                    Abilities playerAbilities = pPlayer.getAbilities();
                    if (compoundTag.getInt("inMindscape") >= 1) {
                        dreamIntoReality.setBaseValue(1);
                        if (!playerAbilities.instabuild) {
                        playerAbilities.mayfly = false;}
                        playerAbilities.setFlyingSpeed(0.05F);
                        pPlayer.onUpdateAbilities();
                        if (pPlayer instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                        }
                    }
            }
        }
    }
    @SubscribeEvent
    public static void resetMindscape(PlayerEvent.PlayerLoggedOutEvent event) {
        Player pPlayer = event.getEntity();
        CompoundTag compoundTag = pPlayer.getPersistentData();
        if (compoundTag.getInt("inMindscape") >= 1) {
            Abilities playerAbilites = pPlayer.getAbilities();
            AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
            if (!playerAbilites.instabuild) {
                playerAbilites.mayfly = false;
                playerAbilites.flying = false;
            }
            playerAbilites.setFlyingSpeed(0.05F);
            dreamIntoReality.setBaseValue(1);
        }
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(pPlayer.getAbilities()));
        }
    }
}
