package net.swimmingtuna.lotm.item.Blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
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
        if (this.ticks++ % 40 == 0) {
            for (Player pPlayer : level.players()) {
                double distance = pPlayer.distanceToSqr(worldPosition.getCenter());
                if (distance <= 25) {
                    BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                        if (spectatorSequence.getCurrentSequence() == 0) {
                            double maxSpirituality = spectatorSequence.getMaxSpirituality();
                            spectatorSequence.setSpirituality((int) maxSpirituality);
                            AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                            Abilities playerAbilities = pPlayer.getAbilities();
                            dreamIntoReality.setBaseValue(3);

                            playerAbilities.mayfly = true;
                            playerAbilities.flying = true;
                            playerAbilities.setFlyingSpeed(0.25F);
                            pPlayer.onUpdateAbilities();
                            if (pPlayer instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                            }
                        }
                    });
                }
            }
        }
    }
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        if (event.getState().getBlock() == BlockInit.CATHEDRAL_BLOCK.get()) {
            List<Player> pPlayers = (List<Player>) level.players();
            for (Player pPlayer : pPlayers) {
                pPlayer.sendSystemMessage(Component.literal("working111111"));
                double distance = pPlayer.distanceToSqr(Vec3.atCenterOf(pos));
                if (distance <= 25) {
                    pPlayer.sendSystemMessage(Component.literal("working111111"));
                    AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                    Abilities playerAbilities = pPlayer.getAbilities();
                    if (dreamIntoReality.getValue() == 3) {
                        dreamIntoReality.setBaseValue(1);
                        if (!playerAbilities.instabuild) {
                        playerAbilities.mayfly = false;
                        playerAbilities.flying = false;}
                        playerAbilities.setFlyingSpeed(0.05F);
                        pPlayer.onUpdateAbilities();
                        if (pPlayer instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                        }
                    }
                }
            }
        }
    }
}
