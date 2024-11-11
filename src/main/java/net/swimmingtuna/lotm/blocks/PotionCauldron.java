package net.swimmingtuna.lotm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemStackHandler;
import net.swimmingtuna.lotm.util.TickableBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionCauldron extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PotionCauldron(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PotionCauldronBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, Level pLevel, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (player.level().isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pos);
            if (blockEntity instanceof PotionCauldronBlockEntity potionCauldronBlock) {
                player.sendSystemMessage(Component.literal("Client ticks are " + potionCauldronBlock.getTickCounter()));
            }
        }
        if (!player.level().isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pos);
            if (blockEntity instanceof PotionCauldronBlockEntity potionCauldronBlock) {
                player.sendSystemMessage(Component.literal("Server ticks are " + potionCauldronBlock.getTickCounter()));
            }
        }
        if (!pLevel.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pos);
            if (blockEntity instanceof PotionCauldronBlockEntity potionCauldronBlock) {
                ItemStackHandler inventory = potionCauldronBlock.getInventory();
                ItemStack stack = player.getItemInHand(hand);
                if (player.isCrouching()) {
                    boolean anyItemExtracted = false;
                    for (int i = 0; i < inventory.getSlots(); i++) {
                        ItemStack slotStack = inventory.getStackInSlot(i);
                        if (!slotStack.isEmpty()) {
                            ItemStack extracted = inventory.extractItem(i, inventory.getSlotLimit(i), false);
                            ItemEntity entity = new ItemEntity(pLevel,
                                    pos.getX() + 0.5D,
                                    pos.getY() + 0.5D,
                                    pos.getZ() + 0.5D,
                                    extracted);
                            pLevel.addFreshEntity(entity);
                            anyItemExtracted = true;
                        }
                    }
                    if (!anyItemExtracted) {
                        player.sendSystemMessage(Component.literal("No items in cauldron!"));
                    }
                    return InteractionResult.SUCCESS;
                }
                if (stack.isEmpty()) {
                    boolean extracted = false;
                    for (int i = 0; i < inventory.getSlots(); i++) {
                        if (!inventory.getStackInSlot(i).isEmpty()) {
                            ItemStack extractedStack = inventory.extractItem(i, 1, false);
                            player.setItemInHand(hand, extractedStack);
                            extracted = true;
                            break;
                        }
                    }
                    if (!extracted) {
                        player.sendSystemMessage(Component.literal("No items in cauldron!"));
                    }
                } else {
                    boolean inserted = false;
                    ItemStack toInsert = stack.copy();
                    toInsert.setCount(1);
                    for (int i = 0; i < inventory.getSlots(); i++) {
                        ItemStack result = inventory.insertItem(i, toInsert, true);
                        if (result.isEmpty()) {
                            inventory.insertItem(i, toInsert, false);
                            ItemStack remainder = stack.copy();
                            remainder.shrink(1);
                            player.setItemInHand(hand, remainder);
                            inserted = true;
                            break;
                        }
                    }

                    if (!inserted) {
                        player.sendSystemMessage(Component.literal("Cauldron is full!"));
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public void onRemove(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof PotionCauldronBlockEntity potionCauldronBlock) {
                ItemStackHandler inventory = potionCauldronBlock.getInventory();
                for (int index = 0; index < inventory.getSlots(); index++) {
                    ItemStack stack = inventory.getStackInSlot(index);
                    if (!stack.isEmpty()) {
                        ItemEntity entity = new ItemEntity(pLevel,
                                pPos.getX() + 0.5D,
                                pPos.getY() + 0.5D,
                                pPos.getZ() + 0.5D,
                                stack);
                        pLevel.addFreshEntity(entity);
                    }
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return TickableBlockEntity.getTickerHelper(level);
    }
}