package com.amronos.automatedworkstations;

import com.amronos.automatedworkstations.inventory.AnvilatorMenu;
import com.amronos.automatedworkstations.inventory.SmitherMenu;
import com.amronos.automatedworkstations.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class AutomatedWorkstationsJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ANVILATOR), RecipeTypes.ANVIL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SMITHER), RecipeTypes.SMITHING);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AnvilatorMenu.class, AnvilatorMenu.MENU_TYPE, RecipeTypes.ANVIL, 0, 3, 4, 36);
        registration.addRecipeTransferHandler(SmitherMenu.class, SmitherMenu.MENU_TYPE, RecipeTypes.SMITHING, 0, 3, 4, 36);
    }
}
