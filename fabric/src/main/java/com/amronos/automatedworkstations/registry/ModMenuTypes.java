package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import com.amronos.automatedworkstations.inventory.SmitherMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    private static MenuType<?> registerModMenuTypes(String name, MenuType<?> menuType){
        return Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), menuType);
    }
    public static void registerModMenuTypes() {
    }

    public static final MenuType<?> SMITHER_MENU = registerModMenuTypes("smither_menu", SmitherMenu.MENU_TYPE);
}
