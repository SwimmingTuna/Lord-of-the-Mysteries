package net.swimmingtuna.lotm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.BlockEntityInit;
import net.swimmingtuna.lotm.util.TickableBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int tickCounter = 0;

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
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        var lotmModData = new CompoundTag();
        lotmModData.putInt("TickCounter", this.tickCounter);
        lotmModData.put("Inventory", this.inventory.serializeNBT());
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

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ItemStack getItemInSlot(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    public void setItemInSlot(ItemStack stack, int slot) {
        this.inventory.setStackInSlot(slot, stack);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        this.tickCounter++;
        setChanged();
        this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public int getTickCounter() {
        return this.tickCounter;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
