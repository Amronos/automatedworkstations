package com.amronos.automatedworkstations.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class CommonSmitherMenu extends CommonAutomatedWorkstationMenu {
    public static final int TEMPLATE_SLOT = 0;
    public static final int BASE_SLOT = 1;
    public static final int ADDITIONAL_SLOT = 2;
    public static final int RESULT_SLOT = 3;
    public static final int TEMPLATE_SLOT_X_PLACEMENT = 8;
    public static final int BASE_SLOT_X_PLACEMENT = 26;
    public static final int ADDITIONAL_SLOT_X_PLACEMENT = 44;
    private static final int RESULT_SLOT_X_PLACEMENT = 102;
    public static final int SLOT_Y_PLACEMENT = 48;
    private final ContainerData containerData;
    private final Container container;
    private final Level level;
    private final int resultSlotIndex;
    private final List<RecipeHolder<SmithingRecipe>> recipes;

    public CommonSmitherMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(menuType, containerId);
        this.containerData = containerData;
        this.container = container;
        checkContainerSize(container, 3);
        container.startOpen(playerInventory.player);
        this.level = playerInventory.player.level();
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
        ItemCombinerMenuSlotDefinition itemcombinermenuslotdefinition = this.createSlotDefinitions();
        this.resultSlotIndex = itemcombinermenuslotdefinition.getResultSlotIndex();
        this.addSlots(itemcombinermenuslotdefinition, playerInventory);
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(TEMPLATE_SLOT, TEMPLATE_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, itemStack -> this.recipes.stream().anyMatch(recipeHolder -> recipeHolder.value().isTemplateIngredient(itemStack)))
                .withSlot(BASE_SLOT, BASE_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, itemStack -> this.recipes.stream().anyMatch(recipeHolder -> recipeHolder.value().isBaseIngredient(itemStack)))
                .withSlot(ADDITIONAL_SLOT, ADDITIONAL_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, itemStack -> this.recipes.stream().anyMatch(recipeHolder -> recipeHolder.value().isAdditionIngredient(itemStack)))
                .withResultSlot(RESULT_SLOT, RESULT_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT)
                .build();
    }

    private SmithingRecipeInput createRecipeInput() {
        return new SmithingRecipeInput(this.container.getItem(TEMPLATE_SLOT), this.container.getItem(BASE_SLOT), this.container.getItem(ADDITIONAL_SLOT));
    }

    @Override
    public void createResult() {
        SmithingRecipeInput smithingrecipeinput = this.createRecipeInput();
        List<RecipeHolder<SmithingRecipe>> list = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, smithingrecipeinput, this.level);
        if (list.isEmpty()) {
            this.resultContainer.setItem(0, ItemStack.EMPTY);
        }
        else {
            RecipeHolder<SmithingRecipe> recipeholder = list.getFirst();
            ItemStack itemstack = recipeholder.value().assemble(smithingrecipeinput, this.level.registryAccess());
            this.resultContainer.setRecipeUsed(recipeholder);
            this.resultContainer.setItem(0, itemstack);
        }
    }

    @Override
    public int getResultSlot() {
        return this.resultSlotIndex;
    }

    @Override
    public ContainerData getContainerData() {
        return this.containerData;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }
}
