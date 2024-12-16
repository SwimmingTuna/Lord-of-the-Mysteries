package net.swimmingtuna.lotm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.swimmingtuna.lotm.init.BlockEntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.screen.PotionCauldronMenu;
import net.swimmingtuna.lotm.world.worlddata.BeyonderRecipeData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PotionCauldronBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(6);
    private static final int OUTPUT_SLOT = 0;
    private static final int INPUT_SLOT_1 = 1;
    private static final int INPUT_SLOT_2 = 2;
    private static final int INPUT_SLOT_3 = 3;
    private static final int INPUT_SLOT_4 = 4;
    private static final int INPUT_SLOT_5 = 5;
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final ContainerData data;

    public PotionCauldronBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.POTION_CAULDRON_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return 0;
            }

            @Override
            public void set(int pIndex, int pValue) {
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    private void spawnAshParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.8;
            double z = pos.getZ() + 0.5;
            double offsetX = (Math.random() - 0.5) * 0.7;
            double offsetZ = (Math.random() - 0.5) * 0.7;
            serverLevel.sendParticles(ParticleTypes.ASH, x + offsetX, y, z + offsetZ, 0, 0, -0.5, 0, 0.1);
        }
    }
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.lotm.potion_cauldron");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new PotionCauldronMenu(i, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
    }


    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level instanceof ServerLevel serverLevel) {
            boolean hasItems = false;
            for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_5; i++) {
                if (!itemHandler.getStackInSlot(i).isEmpty()) {
                    hasItems = true;
                    break;
                }
            }

            if (this.getBlockState().getBlock() instanceof PotionCauldron potionCauldron) {
                potionCauldron.updateLitState(level, pos, hasItems);
            }
            if (hasItems) {
                spawnAshParticles(level, pos);
            }
            if (hasRecipe(serverLevel)) {
                setChanged(level, pos, blockState);
                    craftItem(serverLevel);
                    int random = (int) ((Math.random() * 5) - 10);
                    serverLevel.playSound(null, pos, SoundEvents.BLAZE_DEATH, SoundSource.BLOCKS, 1.0f, random);
            }
        }
    }

    private boolean hasRecipe(ServerLevel level) {
        ItemStack outputSlot = this.itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (!outputSlot.is(Items.GLASS_BOTTLE)) {
            return false;
        }
        if (level == null) {
            return false;
        }
        BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(level);
        List<ItemStack> inputIngredients = new ArrayList<>();
        for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_5; i++) {
            ItemStack ingredient = this.itemHandler.getStackInSlot(i);
            if (!ingredient.isEmpty()) {
                inputIngredients.add(ingredient);
            }
        }
        for (Map.Entry<ItemStack, List<ItemStack>> recipeEntry : recipeData.getBeyonderRecipes().entrySet()) {
            List<ItemStack> recipeIngredients = recipeEntry.getValue();
            if (ingredientsMatch(inputIngredients, recipeIngredients)) {
                return true;
            }
        }

        return false;
    }

    private boolean ingredientsMatch(List<ItemStack> inputIngredients, List<ItemStack> recipeIngredients) {
        if (inputIngredients.size() != recipeIngredients.size()) {
            return false;
        }
        List<ItemStack> remainingRecipeIngredients = new ArrayList<>(recipeIngredients);
        for (ItemStack inputIngredient : inputIngredients) {
            boolean ingredientMatched = false;
            for (int i = 0; i < remainingRecipeIngredients.size(); i++) {
                if (ItemStack.isSameItemSameTags(inputIngredient, remainingRecipeIngredients.get(i))) {
                    remainingRecipeIngredients.remove(i);
                    ingredientMatched = true;
                    break;
                }
            }
            if (!ingredientMatched) {
                return false;
            }
        }
        return true;
    }

    private void craftItem(ServerLevel level) {
        if (level == null) {
            return;
        }
        BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(level);
        List<ItemStack> inputIngredients = new ArrayList<>();
        for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_5; i++) {
            ItemStack ingredient = this.itemHandler.getStackInSlot(i);
            if (!ingredient.isEmpty()) {
                inputIngredients.add(ingredient);
            }
        }
        for (Map.Entry<ItemStack, List<ItemStack>> recipeEntry : recipeData.getBeyonderRecipes().entrySet()) {
            List<ItemStack> recipeIngredients = recipeEntry.getValue();
            if (ingredientsMatch(inputIngredients, recipeIngredients)) {
                this.itemHandler.setStackInSlot(OUTPUT_SLOT, recipeEntry.getKey().copy());
                for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_5; i++) {
                    this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
                return;
            }
        }
        ejectAllItems();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private void craftItem() {
        ItemStack result = new ItemStack(ItemInit.SPECTATOR_0_POTION.get(), 1);
        this.itemHandler.extractItem(INPUT_SLOT_1, 1, false);
        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private void ejectAllItems() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropContents(this.level, this.worldPosition, new SimpleContainer(stack));
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }
}
