package com.amronos.automatedworkstations.inventory;

import com.amronos.automatedworkstations.registry.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class SmitherMenu extends CommonSmitherMenu{
    public SmitherMenu(int containerId, Inventory playerInventory){
        this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public SmitherMenu(int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(ModMenuTypes.SMITHER_MENU.get(), containerId, playerInventory, container, containerData);
    }
}
