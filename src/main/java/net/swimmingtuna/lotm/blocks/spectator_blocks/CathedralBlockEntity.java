package net.swimmingtuna.lotm.blocks.spectator_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockEntityInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.TickableBlockEntity;

import java.util.List;

public class CathedralBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks;

    public CathedralBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.CATHEDRAL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            return;
        }
        System.out.println("ticks are " + ticks);
        ticks++;
        if (this.ticks % 20 == 0) {
            for (Player player : level.players()) {
                CompoundTag compoundTag = player.getPersistentData();
                double distanceX = player.getX() - worldPosition.getX();
                double distanceY = player.getY() - worldPosition.getY();
                double distanceZ = player.getZ() - worldPosition.getZ();
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (holder.getCurrentSequence() == 0 && holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                    if (Math.abs(distanceX) <= 80 && Math.abs(distanceY) <= 100 && Math.abs(distanceZ) <= 110) {
                        compoundTag.putInt("mindscapeAbilities", 25);
                    }
                }
            }
        }
        if (ticks >= 550 && ticks <= 740 && ticks % 10 == 0) {
            List<Block> blockList = BlockInit.BLOCK_LIST.stream().map(RegistryObject::get).toList();
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            int yStart = (ticks - 560) / 2;
            int yEnd = yStart + 4;
            if (ticks == 740) {
                yStart = 91;
            }
            for (int x = -89; x <= 89; x++) {
                for (int y = yStart; y <= yEnd + 3; y++) {
                    for (int z = -82; z <= 82; z++) {
                        mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                        Block blockInPos = level.getBlockState(mutablePos).getBlock();
                        if (blockList.contains(blockInPos) || blockInPos == Blocks.REDSTONE_WIRE || blockInPos == Blocks.CANDLE || blockInPos == Blocks.OAK_FENCE || blockInPos == Blocks.ZOMBIE_HEAD
                                || blockInPos == Blocks.ZOMBIE_WALL_HEAD || blockInPos == Blocks.PIGLIN_HEAD || blockInPos == Blocks.PIGLIN_WALL_HEAD
                                || blockInPos == Blocks.CREEPER_HEAD || blockInPos == Blocks.CREEPER_WALL_HEAD || blockInPos == Blocks.PLAYER_HEAD
                                || blockInPos == Blocks.PLAYER_WALL_HEAD || blockInPos == Blocks.NETHER_BRICK_FENCE || blockInPos == Blocks.WHITE_CANDLE || blockInPos == Blocks.SKELETON_SKULL || blockInPos == Blocks.WITHER_SKELETON_SKULL || blockInPos == Blocks.STONE_BRICK_STAIRS) {
                            double distance = worldPosition.distSqr(mutablePos);
                            if (distance <= 250 * 250) {
                                level.removeBlock(mutablePos, false);
                            }
                        }
                    }
                }
            }
        }
        if (ticks >= 800) {
            int radius = 300;
            level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class,
                    new AABB(worldPosition.offset(-radius, -radius, -radius), worldPosition.offset(radius, radius, radius)));

            for (ItemEntity itemEntity : items) {
                if (itemEntity.getItem().getItem() == Items.REDSTONE || itemEntity.getItem().getItem() == Items.TWISTING_VINES) {
                    itemEntity.discard(); // Remove the item entity from the world
                }
            }
        }

    }
}
