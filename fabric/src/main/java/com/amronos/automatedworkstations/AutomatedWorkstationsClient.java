package com.amronos.automatedworkstations;

import com.amronos.automatedworkstations.inventory.CommonSmitherScreen;
import com.amronos.automatedworkstations.inventory.SmitherMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;

@Environment(EnvType.CLIENT)
public class AutomatedWorkstationsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(SmitherMenu.MENU_TYPE, CommonSmitherScreen::new);
    }
}
