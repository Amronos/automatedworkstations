package com.amronos.automatedworkstations.block.entity;

import com.amronos.automatedworkstations.Constants;
import com.amronos.automatedworkstations.block.CommonSmitherBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class CommonAnvilatorBlockEntity extends CommonAutomatedWorkstationBlockEntity {
    public static final int CONTAINER_SIZE = 3;
    public static final int SLOT_DISABLED = 1;
    public static final int SLOT_ENABLED = 0;
    public static final int DATA_TRIGGERED = 3;
    public static final int NUM_DATA = 4;
    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int craftingTicksRemaining = 0;
    protected final ContainerData containerData = new ContainerData() {
        private final int[] slotStates = new int[3];
        private int triggered = 0;

        @Override
        public int get(int slot) {
            if (slot == DATA_TRIGGERED){
                return this.triggered;
            }
            else{
                return this.slotStates[slot];
            }
        }

        @Override
        public void set(int slot, int state) {
            if (slot == DATA_TRIGGERED) {
                this.triggered = state;
            }
            else {
                this.slotStates[slot] = state;
            }
        }

        @Override
        public int getCount() {
            return NUM_DATA;
        }
    };

    public CommonAnvilatorBlockEntity(BlockEntityType blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block." + Constants.MOD_ID + ".anvilator");
    }

    @Override
    protected abstract AbstractContainerMenu createMenu(int containerId, Inventory inventory);

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public int getDataTriggered() {
        return DATA_TRIGGERED;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getCraftingTicksRemaining() {
        return this.craftingTicksRemaining;
    }

    @Override
    public void setCraftingTicksRemaining(int craftingTicksRemaining) {
        this.craftingTicksRemaining = craftingTicksRemaining;
    }

    @Override
    public ContainerData getContainerData() {
        return this.containerData;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CommonAnvilatorBlockEntity anvilatorblockentity) {
        int i = anvilatorblockentity.craftingTicksRemaining - 1;
        if (i >= 0) {
            anvilatorblockentity.craftingTicksRemaining = i;
            if (i == 0) {
                level.setBlock(pos, state.setValue(CommonSmitherBlock.CRAFTING, Boolean.FALSE), 3);
            }
        }
    }
}
