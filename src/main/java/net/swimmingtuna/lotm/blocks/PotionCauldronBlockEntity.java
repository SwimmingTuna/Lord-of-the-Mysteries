package net.swimmingtuna.lotm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.BlockEntityInit;
import net.swimmingtuna.lotm.item.BeyonderPotions.BeyonderPotion;
import net.swimmingtuna.lotm.util.BeyonderRecipes;
import net.swimmingtuna.lotm.util.TickableBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int tickCounter = 0;
    private boolean lit = false;

    private final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            PotionCauldronBlockEntity.this.setChanged();
        }
    };

    private final LazyOptional<ItemStackHandler> optional = LazyOptional.of(() -> this.inventory);

    public PotionCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.POTION_CAULDRON_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag modId = pTag.getCompound(LOTM.MOD_ID);
        this.inventory.deserializeNBT(modId.getCompound("Inventory"));
        this.tickCounter = modId.getInt("TickCounter");
        this.lit = modId.getBoolean("HasPotion"); // Load recipe flag

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        var lotmModData = new CompoundTag();
        lotmModData.putInt("TickCounter", this.tickCounter);
        lotmModData.put("Inventory", this.inventory.serializeNBT());
        lotmModData.putBoolean("HasPotion", this.lit);
        pTag.put(LOTM.MOD_ID, lotmModData);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.optional.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.optional.invalidate();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }



    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        // Count non-empty slots
        int nonEmptySlots = 0;
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                nonEmptySlots++;
            }
        }

        // Update lit state if it's different
        boolean shouldBeLit = nonEmptySlots >= 2;
        if (this.lit != shouldBeLit) {
            setHasPotion(shouldBeLit);
        }

        tickCounter++;
        if (this.tickCounter % 20 == 0) {
            System.out.println("Slot 0 is " + inventory.getStackInSlot(0));
            System.out.println("Slot 1 is " + inventory.getStackInSlot(1));
            System.out.println("Slot 2 is " + inventory.getStackInSlot(2));
            System.out.println("Slot 3 is " + inventory.getStackInSlot(3));
            System.out.println("Slot 4 is " + inventory.getStackInSlot(4));
        }
    }

    public void setHasPotion(boolean value) {
        this.lit = value;
        this.setChanged();
    }

    public boolean getHasPotion() {
        return this.lit;
    }


    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ItemStack getItemInSlot(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    public void setItemInSlot(ItemStack stack, int slot) {
        this.inventory.setStackInSlot(slot, stack);
    }

    public void clearInventory() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        this.setChanged(); // Mark the block entity as changed
    }
}
