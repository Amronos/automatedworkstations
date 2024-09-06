package com.amronos.automatedworkstations.block.entity;

import com.amronos.automatedworkstations.Constants;
import com.amronos.automatedworkstations.block.CommonSmitherBlock;
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
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CommonSmitherBlockEntity extends RandomizableContainerBlockEntity implements Container {
    public static final int CONTAINER_SIZE = 3;
    public static final int SLOT_DISABLED = 1;
    public static final int SLOT_ENABLED = 0;
    public static final int DATA_TRIGGERED = 3;
    public static final int NUM_DATA = 4;
    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int smithingTicksRemaining = 0;
    protected final ContainerData containerData = new ContainerData() {
        private final int[] slotStates = new int[3];
        private int triggered = 0;

        @Override
        public int get(int slot) {
            if (slot == 3){
                return this.triggered;
            }
            else{
                return this.slotStates[slot];
            }
        }

        @Override
        public void set(int slot, int state) {
            if (slot == 3) {
                this.triggered = state;
            }
            else {
                this.slotStates[slot] = state;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public CommonSmitherBlockEntity(BlockEntityType blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block." + Constants.MOD_ID + ".smither");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    public void setSlotState(int slot, boolean state) {
        if (this.slotCanBeDisabled(slot)) {
            this.containerData.set(slot, state ? 0 : 1);
            this.setChanged();
        }
    }

    public boolean isSlotDisabled(int slot) {
        return (slot >= 0) && (slot < 3) && (this.containerData.get(slot) == 1);
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
            ItemStack itemstack = this.items.get(slot);
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
        for (int i = slot + 1; i < 3; i++) {
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
        tag.putInt("smithing_ticks_remaining", this.smithingTicksRemaining);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }

        this.addDisabledSlots(tag);
        this.addTriggered(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.smithingTicksRemaining = tag.getInt("smithing_ticks_remaining");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }

        int[] is = tag.getIntArray("disabled_slots");

        for (int i = 0; i < 3; i++) {
            this.containerData.set(i, 0);
        }
        for (int j : is) {
            if (this.slotCanBeDisabled(j)) {
                this.containerData.set(j, 1);
            }
        }

        this.containerData.set(3, tag.getInt("triggered"));
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (this.isSlotDisabled(index)) {
            this.setSlotState(index, true);
        }
        super.setItem(index, stack);
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private void addDisabledSlots(CompoundTag tag) {
        IntList intlist = new IntArrayList();
        for (int i = 0; i < 3; i++) {
            if (this.isSlotDisabled(i)) {
                intlist.add(i);
            }
        }
        tag.putIntArray("disabled_slots", intlist);
    }

    private void addTriggered(CompoundTag tag) {
        tag.putInt("triggered", this.containerData.get(3));
    }

    public void setTriggered(boolean triggered) {
        this.containerData.set(3, triggered ? 1 : 0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CommonSmitherBlockEntity smither) {
        int i = smither.smithingTicksRemaining - 1;
        if (i >= 0) {
            smither.smithingTicksRemaining = i;
            if (i == 0) {
                level.setBlock(pos, state.setValue(CommonSmitherBlock.SMITHING, Boolean.FALSE), 3);
            }
        }
    }

    public void setSmithingTicksRemaining(int smithingTicksRemaining) {
        this.smithingTicksRemaining = smithingTicksRemaining;
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
        return slot > -1 && slot < 3 && this.items.get(slot).isEmpty();
    }

    public SmithingRecipeInput createRecipeInput() {
        return new SmithingRecipeInput(this.getItem(0), this.getItem(1), this.getItem(2));
    }
}
