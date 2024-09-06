package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    private static Item registerModItems(String name, Item item){
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), item);
    }
    public static void registerModItems() {
    }
}
