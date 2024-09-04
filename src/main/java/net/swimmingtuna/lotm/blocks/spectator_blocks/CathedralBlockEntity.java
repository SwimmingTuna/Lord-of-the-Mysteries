package net.swimmingtuna.lotm.blocks.spectator_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockEntityInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.TickableBlockEntity;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CathedralBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks;

    public CathedralBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.CATHEDRAL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        assert this.level != null;
        if (!this.level.isClientSide()) {
            ticks++;
        }
        if (!this.level.isClientSide() && this.ticks % 20 == 0) {
            for (Player pPlayer : level.players()) {
                CompoundTag compoundTag = pPlayer.getPersistentData();
                double distanceX = pPlayer.getX() - worldPosition.getX();
                double distanceY = pPlayer.getY() - worldPosition.getY();
                double distanceZ = pPlayer.getZ() - worldPosition.getZ();
                AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                    if (holder.getCurrentSequence() == 0 && holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                        if (Math.abs(distanceX) <= 80 && Math.abs(distanceY) <= 100 && Math.abs(distanceZ) <= 110) {
                            compoundTag.putInt("mindscapeAbilities", 25);
                        }
                    }
                });
            }
        }
        if (!this.level.isClientSide() && ticks == 550) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = -5; y <= -1; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 560) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 0; y <= 4; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 570) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 5; y <= 9; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 100 * 100) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 580) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 10; y <= 14; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 590) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 15; y <= 19; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 600) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 20; y <= 24; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 610) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 25; y <= 29; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 620) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 30; y <= 34; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 630) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 35; y <= 39; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 640) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 40; y <= 44; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 650) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 45; y <= 49; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 660) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 50; y <= 54; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 670) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 55; y <= 59; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD
                                    || block1 == Blocks.PLAYER_WALL_HEAD || block1 == Blocks.NETHER_BRICK_FENCE) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 680) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 60; y <= 64; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD || block1 == Blocks.NETHER_BRICK_FENCE
                                    || block1 == Blocks.PLAYER_WALL_HEAD) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 690) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 65; y <= 69; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD || block1 == Blocks.NETHER_BRICK_FENCE
                                    || block1 == Blocks.PLAYER_WALL_HEAD) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 700) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 70; y <= 74; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD || block1 == Blocks.NETHER_BRICK_FENCE
                                    || block1 == Blocks.PLAYER_WALL_HEAD) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 710) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 75; y <= 79; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD || block1 == Blocks.NETHER_BRICK_FENCE
                                    || block1 == Blocks.PLAYER_WALL_HEAD) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 720) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 80; y <= 84; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD
                                    || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD
                                    || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD || block1 == Blocks.NETHER_BRICK_FENCE
                                    || block1 == Blocks.PLAYER_WALL_HEAD) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 730) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 85; y <= 90; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.NETHER_BRICK_FENCE || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD || block1 == Blocks.PLAYER_WALL_HEAD) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks == 740) {
            for (RegistryObject<Block> blockRegObj : BlockInit.BLOCK_LIST) {
                Block block = blockRegObj.get();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = -87; x <= 87; x++) {
                    for (int y = 91; y <= 95; y++) {
                        for (int z = -80; z <= 80; z++) {
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            Block block1 = level.getBlockState(mutablePos).getBlock();
                            if (block1 == block || block1 == Blocks.NETHER_BRICK_FENCE || block1 == Blocks.REDSTONE_WIRE || block1 == Blocks.CANDLE || block1 == Blocks.OAK_FENCE || block1 == Blocks.ZOMBIE_HEAD || block1 == Blocks.ZOMBIE_WALL_HEAD || block1 == Blocks.PIGLIN_HEAD || block1 == Blocks.PIGLIN_WALL_HEAD || block1 == Blocks.CREEPER_HEAD || block1 == Blocks.CREEPER_WALL_HEAD || block1 == Blocks.PLAYER_HEAD || block1 == Blocks.PLAYER_WALL_HEAD) {
                                double distance = worldPosition.distSqr(mutablePos);
                                if (distance <= 500 * 500) {
                                    level.removeBlock(mutablePos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide() && ticks >= 800) {
            level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        }

    }
}
