package net.swimmingtuna.lotm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import net.swimmingtuna.lotm.util.BeyonderRecipes;
import net.swimmingtuna.lotm.util.TickableBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionCauldron extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PotionCauldron(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LIT, false));
    }

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 15, 15);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PotionCauldronBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(LIT);
    }

    @Override
    public InteractionResult use(BlockState state, Level pLevel, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!pLevel.isClientSide() && hand == InteractionHand.MAIN_HAND && !player.level().isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pos);
            if (blockEntity instanceof PotionCauldronBlockEntity potionCauldronBlock) {
                ItemStackHandler inventory = potionCauldronBlock.getInventory();
                ItemStack itemInHand = player.getItemInHand(hand);
                if (player.isShiftKeyDown()) {
                    for (int i = 0; i < inventory.getSlots(); i++) {
                        ItemStack stackInSlot = inventory.getStackInSlot(i);
                        if (!stackInSlot.isEmpty()) {
                            player.sendSystemMessage(Component.literal("Shift click check"));
                            player.drop(stackInSlot.copy(), true);
                            inventory.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                }
                if (!itemInHand.isEmpty()) {
                    if (itemInHand.getItem() == Items.GLASS_BOTTLE) {
                        player.sendSystemMessage(Component.literal("Glass bottle check"));
                        BeyonderRecipes.checkForRecipes(inventory, potionCauldronBlock);
                        itemInHand.shrink(1);
                        player.addItem(inventory.getStackInSlot(0));
                        inventory.setStackInSlot(0, ItemStack.EMPTY);
                    } else {
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack stackInSlot = inventory.getStackInSlot(i);
                            if (ItemStack.isSameItemSameTags(stackInSlot, itemInHand) && stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
                                stackInSlot.grow(1);
                                itemInHand.shrink(1);
                                player.sendSystemMessage(Component.literal("Added existing item"));
                                return InteractionResult.SUCCESS;
                            }
                        }
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            if (inventory.getStackInSlot(i).isEmpty()) {
                                ItemStack newStack = itemInHand.copy();
                                newStack.setCount(1);
                                inventory.setStackInSlot(i, newStack);
                                player.sendSystemMessage(Component.literal("Added non-existent item"));
                                itemInHand.shrink(1);
                                return InteractionResult.SUCCESS;

                            }
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
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
                        ItemStack droppedStack = inventory.extractItem(index, stack.getCount(), false);
                        ItemEntity entity = new ItemEntity(pLevel,
                                pPos.getX() + 0.5D,
                                pPos.getY() + 0.5D,
                                pPos.getZ() + 0.5D,
                                droppedStack);
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