package com.amronos.automatedworkstations;

import com.amronos.automatedworkstations.registry.*;
import net.fabricmc.api.ModInitializer;

public class AutomatedWorkstations implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModBlockEntities.registerBlockEntities();
        ModMenuTypes.registerModMenuTypes();
        ModCreativeModeTabs.registerCreativeModeTabs();
    }
}