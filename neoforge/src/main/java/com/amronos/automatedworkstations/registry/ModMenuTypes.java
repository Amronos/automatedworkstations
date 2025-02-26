package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import com.amronos.automatedworkstations.inventory.AnvilatorMenu;
import com.amronos.automatedworkstations.inventory.SmitherMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;
import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Constants.MOD_ID);

    public static final Supplier<MenuType<AnvilatorMenu>> ANVILATOR_MENU = MENU_TYPES.register("anvilator_menu", () -> new MenuType<>(AnvilatorMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<SmitherMenu>> SMITHER_MENU = MENU_TYPES.register("smither_menu", () -> new MenuType<>(SmitherMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
