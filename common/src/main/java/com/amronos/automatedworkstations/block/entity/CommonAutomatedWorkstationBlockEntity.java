package com.amronos.automatedworkstations.block.entity;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CommonAutomatedWorkstationBlockEntity extends RandomizableContainerBlockEntity implements Container {
    /**
     * List of methods that should be added to the extending classes and are not included in this class:
     * <p>
     * serverTick()
     */

    public static final int SLOT_DISABLED = 1;
    public static final int SLOT_ENABLED = 0;

    public CommonAutomatedWorkstationBlockEntity(BlockEntityType blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    protected abstract Component getDefaultName();

    @Override
    protected abstract AbstractContainerMenu createMenu(int containerId, Inventory inventory);

    public void setSlotState(int slot, boolean state) {
        if (this.slotCanBeDisabled(slot)) {
            this.getContainerData().set(slot, state ? 0 : 1);
            this.setChanged();
        }
    }

    public boolean isSlotDisabled(int slot) {
        return (slot >= 0) && (slot < 3) && (this.getContainerData().get(slot) == 1);
    }

    /**
     * Returns {@code true} if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For guis use {@code Slot.isItemValid}
     */
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (this.isSlotDisabled(slot)) {
            return false;
        }
        else {
            ItemStack itemstack = this.getItems().get(slot);
            int i = itemstack.getCount();
            if (i >= itemstack.getMaxStackSize()) {
                return false;
            }
            else {
                return itemstack.isEmpty() || !this.smallerStackExist(i, itemstack, slot);
            }
        }
    }

    private boolean smallerStackExist(int currentSize, ItemStack stack, int slot) {
        for (int i = slot + 1; i < this.getContainerSize(); i++) {
            if (!this.isSlotDisabled(i)) {
                ItemStack itemstack = this.getItem(i);
                if (itemstack.isEmpty() || itemstack.getCount() < currentSize && ItemStack.isSameItemSameComponents(itemstack, stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("crafting_ticks_remaining", this.getCraftingTicksRemaining());
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.getItems(), registries);
        }

        this.addDisabledSlots(tag);
        this.addTriggered(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.setCraftingTicksRemaining(tag.getInt("crafting_ticks_remaining"));
        this.setItems(NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY));
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.getItems(), registries);
        }

        int[] is = tag.getIntArray("disabled_slots");

        for (int i = 0; i < this.getContainerSize(); i++) {
            this.getContainerData().set(i, 0);
        }
        for (int j : is) {
            if (this.slotCanBeDisabled(j)) {
                this.getContainerData().set(j, 1);
            }
        }

        this.getContainerData().set(this.getDataTriggered(), tag.getInt("triggered"));
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.getItems()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    private void addDisabledSlots(CompoundTag tag) {
        IntList intlist = new IntArrayList();
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (this.isSlotDisabled(i)) {
                intlist.add(i);
            }
        }
        tag.putIntArray("disabled_slots", intlist);
    }

    private void addTriggered(CompoundTag tag) {
        tag.putInt("triggered", this.getContainerData().get(getDataTriggered()));
    }

    public void setTriggered(boolean triggered) {
        this.getContainerData().set(getDataTriggered(), triggered ? 1 : 0);
    }

    public int getRedstoneSignal() {
        int i = 0;
        for (int j = 0; j < this.getContainerSize(); j++) {
            ItemStack itemstack = this.getItem(j);
            if (!itemstack.isEmpty() || this.isSlotDisabled(j)) {
                i++;
            }
        }
        return i;
    }

    private boolean slotCanBeDisabled(int slot) {
        return slot > -1 && slot < getContainerSize() && this.getItems().get(slot).isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.getItems().get(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (this.isSlotDisabled(index)) {
            this.setSlotState(index, true);
        }
        super.setItem(index, stack);
    }

    @Override
    public abstract int getContainerSize();

    public abstract int getDataTriggered();

    @Override
    public abstract NonNullList<ItemStack> getItems();

    @Override
    protected abstract void setItems(NonNullList<ItemStack> items);

    public abstract int getCraftingTicksRemaining();

    public abstract void setCraftingTicksRemaining(int craftingTicksRemaining);

    public abstract ContainerData getContainerData();
}
