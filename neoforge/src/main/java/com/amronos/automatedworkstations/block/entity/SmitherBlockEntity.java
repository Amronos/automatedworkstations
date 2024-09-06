package com.amronos.automatedworkstations.block.entity;

import com.amronos.automatedworkstations.inventory.SmitherMenu;
import com.amronos.automatedworkstations.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class SmitherBlockEntity extends CommonSmitherBlockEntity{

    public SmitherBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SMITHER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new SmitherMenu(containerId, inventory,this, this.containerData);
    }
}
