package com.amronos.automatedworkstations.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CommonAutomatedWorkstationSlot extends Slot {
    protected final CommonAutomatedWorkstationMenu menu;

    public CommonAutomatedWorkstationSlot(Container container, int slot, int x, int y, CommonAutomatedWorkstationMenu menu) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    /**
     * Check if the stack is allowed to be placed in this slot.
     */
    @Override
    public boolean mayPlace(ItemStack stack) {
        return !this.menu.isSlotDisabled(this.index) && super.mayPlace(stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.menu.slotsChanged(this.container);
    }
}
