package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final Supplier<CreativeModeTab> AUTOMATED_WORKSTATIONS = CREATIVE_MODE_TABS.register("automated_workstations", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Constants.MOD_ID + ".automated_workstations"))
                    .icon(() -> new ItemStack(ModBlocks.SMITHER))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.SMITHER);
                    })
                    .build());
}
