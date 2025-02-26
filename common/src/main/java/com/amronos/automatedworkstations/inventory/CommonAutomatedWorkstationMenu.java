package com.amronos.automatedworkstations.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public abstract class CommonAutomatedWorkstationMenu extends AbstractContainerMenu implements ContainerListener {
    protected final ResultContainer resultContainer = new ResultContainer();

    public CommonAutomatedWorkstationMenu(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    protected abstract ItemCombinerMenuSlotDefinition createSlotDefinitions();

    protected void addSlots(ItemCombinerMenuSlotDefinition slotDefinition, Inventory inventory) {
        createInputSlots(slotDefinition);
        createResultSlot(slotDefinition);
        createInventorySlots(inventory);
        this.addDataSlots(this.getContainerData());
        this.createResult();
    }

    protected void createInputSlots(ItemCombinerMenuSlotDefinition slotDefinition) {
        for (final ItemCombinerMenuSlotDefinition.SlotDefinition itemcombinermenuslotdefinition$slotdefinition : slotDefinition.getSlots()) {
            this.addSlot(
                    new CommonAutomatedWorkstationSlot(
                            this.getContainer(),
                            itemcombinermenuslotdefinition$slotdefinition.slotIndex(),
                            itemcombinermenuslotdefinition$slotdefinition.x(),
                            itemcombinermenuslotdefinition$slotdefinition.y(),
                            this) {
                        @Override
                        public boolean mayPlace(ItemStack itemStack) {
                            return !this.menu.isSlotDisabled(this.index) && itemcombinermenuslotdefinition$slotdefinition.mayPlace().test(itemStack);
                        }
                    }
            );
        }
    }

    protected void createResultSlot(ItemCombinerMenuSlotDefinition slotDefinition) {
        this.addSlot(new NonInteractiveResultSlot(this.resultContainer, slotDefinition.getResultSlot().slotIndex(), slotDefinition.getResultSlot().x(), slotDefinition.getResultSlot().y()));
    }

    protected void createInventorySlots(Inventory inventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    public abstract void createResult();

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.getInventorySlotStart();
            int j = this.getUseRowEnd();
            if (index < i) {
                if (!this.moveItemStackTo(itemstack1, i, j, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemstack1, 0, i, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is null for the initial slot that was double-clicked.
     */
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
    public boolean stillValid(Player player) {
        return this.getContainer().stillValid(player);
    }

    public void setSlotState(int slot, boolean enabled) {
        CommonAutomatedWorkstationSlot smitherSlot = (CommonAutomatedWorkstationSlot) this.getSlot(slot);
        this.getContainerData().set(smitherSlot.index, enabled ? 0 : 1);
        this.broadcastChanges();
    }

    public boolean isSlotDisabled(int slot) {
        return slot > -1 && slot < 3 && this.getContainerData().get(slot) == 1;
    }

    public boolean isPowered() {
        return this.getContainerData().get(3) == 1;
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void slotsChanged(Container inventory) {
        super.slotsChanged(inventory);
        this.createResult();
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual contents of that slot.
     */
    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
        this.createResult();
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
    }

    protected int getInventorySlotStart() {
        return getResultSlot() + 1;
    }

    protected int getInventorySlotEnd() {
        return this.getInventorySlotStart() + 27;
    }

    protected int getUseRowStart() {
        return this.getInventorySlotEnd();
    }

    protected int getUseRowEnd() {
        return this.getUseRowStart() + 9;
    }

    public abstract ContainerData getContainerData();

    public abstract Container getContainer();

    public abstract int getResultSlot();
}
