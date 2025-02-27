package com.amronos.automatedworkstations.inventory;

import com.amronos.automatedworkstations.registry.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;

public class AnvilatorMenu extends CommonAnvilatorMenu {

    public static final MenuType<AnvilatorMenu> MENU_TYPE = new MenuType<>(AnvilatorMenu::new, FeatureFlags.DEFAULT_FLAGS);

    public AnvilatorMenu(int containerId, Inventory playerInventory){
        this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public AnvilatorMenu(int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(ModMenuTypes.ANVILATOR_MENU, containerId, playerInventory, container, containerData);
    }
}
