package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static void registerCreativeModeTabs(){
    }

    public static final CreativeModeTab AUTOMATED_WORKSTATIONS = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "automated_workstations"),
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
                    .title(Component.translatable("itemGroup." + Constants.MOD_ID + ".automated_workstations"))
                    .icon(() -> new ItemStack(ModBlocks.SMITHER))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.SMITHER);
                    })
                    .build());
}
