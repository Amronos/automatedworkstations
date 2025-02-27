package com.amronos.automatedworkstations.block.entity;

import com.amronos.automatedworkstations.inventory.AnvilatorMenu;
import com.amronos.automatedworkstations.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class AnvilatorBlockEntity extends CommonAnvilatorBlockEntity {

    public AnvilatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ANVILATOR_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AnvilatorMenu(containerId, inventory, this, this.containerData);
    }
}
