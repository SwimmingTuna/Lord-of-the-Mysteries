package net.swimmingtuna.lotm.blocks.spectator_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        assert this.level != null;
        if (!this.level.isClientSide() &&this.ticks++ % 20 == 0) { //make this in a way to give the player a tag that decreases and is replaced every second and the tag gives abilities
            for (Player pPlayer : level.players()) {
                CompoundTag compoundTag = pPlayer.getPersistentData();
                double distanceX = pPlayer.getX() - worldPosition.getX();
                double distanceY = pPlayer.getY() - worldPosition.getY();
                double distanceZ = pPlayer.getZ() - worldPosition.getZ();
                AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                    if (holder.getCurrentSequence() == 0 && holder.isSpectatorClass()) {
                        if (Math.abs(distanceX) <= 80 && Math.abs(distanceY) <= 100 && Math.abs(distanceZ) <= 110) {
                            compoundTag.putInt("mindscapeAbilities", 25);
                            pPlayer.sendSystemMessage(Component.literal("flying speed is " + pPlayer.getAbilities().getFlyingSpeed()));
                            pPlayer.sendSystemMessage(Component.literal("DIR is " + pPlayer.getAttribute(ModAttributes.DIR.get()).getBaseValue()));
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
            }
        }
    }
}
