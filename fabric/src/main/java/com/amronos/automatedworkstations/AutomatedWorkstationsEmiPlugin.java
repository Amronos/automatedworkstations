package com.amronos.automatedworkstations;

import com.amronos.automatedworkstations.registry.ModBlocks;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;

import static dev.emi.emi.api.recipe.VanillaEmiRecipeCategories.ANVIL_REPAIRING;
import static dev.emi.emi.api.recipe.VanillaEmiRecipeCategories.SMITHING;

public class AutomatedWorkstationsEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addWorkstation(ANVIL_REPAIRING, EmiStack.of(ModBlocks.ANVILATOR.asItem()));
        registry.addWorkstation(SMITHING, EmiStack.of(ModBlocks.SMITHER.asItem()));
    }
}
