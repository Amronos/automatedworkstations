package com.amronos.automatedworkstations.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;

public class CommonSmitherMenu extends AbstractContainerMenu implements ContainerListener{
    public static final int TEMPLATE_SLOT = 0;
    public static final int BASE_SLOT = 1;
    public static final int ADDITIONAL_SLOT = 2;
    public static final int RESULT_SLOT = 3;
    public static final int TEMPLATE_SLOT_X_PLACEMENT = 8;
    public static final int BASE_SLOT_X_PLACEMENT = 26;
    public static final int ADDITIONAL_SLOT_X_PLACEMENT = 44;
    private static final int RESULT_SLOT_X_PLACEMENT = 98;
    public static final int SLOT_Y_PLACEMENT = 48;
    private final ContainerData containerData;
    private final Container container;
    private final Player player;
    private final Level level;
    private final ResultContainer resultContainer = new ResultContainer();
    private final List<Integer> inputSlotIndexes;
    private final int resultSlotIndex;
    @Nullable
    private RecipeHolder<SmithingRecipe> selectedRecipe;
    private final List<RecipeHolder<SmithingRecipe>> recipes;

    public CommonSmitherMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(menuType, containerId);
        this.containerData = containerData;
        this.container = container;
        this.player = playerInventory.player;
        checkContainerSize(container, 3);
        container.startOpen(playerInventory.player);
        this.level = playerInventory.player.level();
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
        ItemCombinerMenuSlotDefinition itemcombinermenuslotdefinition = this.createSlotDefinitions();
        this.inputSlotIndexes = itemcombinermenuslotdefinition.getInputSlotIndexes();
        this.resultSlotIndex = itemcombinermenuslotdefinition.getResultSlotIndex();
        this.addSlots(itemcombinermenuslotdefinition, playerInventory);
    }

    private ItemCombinerMenuSlotDefinition createSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 8, 48, p_266643_ -> this.recipes.stream().anyMatch(p_300804_ -> p_300804_.value().isTemplateIngredient(p_266643_)))
                .withSlot(1, 26, 48, p_286208_ -> this.recipes.stream().anyMatch(p_300802_ -> p_300802_.value().isBaseIngredient(p_286208_)))
                .withSlot(2, 44, 48, p_286207_ -> this.recipes.stream().anyMatch(p_300798_ -> p_300798_.value().isAdditionIngredient(p_286207_)))
                .withResultSlot(3, 102, 48)
                .build();
    }

    private void addSlots(ItemCombinerMenuSlotDefinition slotDefinition, Inventory inventory){
        createInputSlots(slotDefinition);
        createResultSlot(slotDefinition);
        createInventorySlots(inventory);
        this.addDataSlots(this.containerData);
        this.createResult();
    }

    private void createInputSlots(ItemCombinerMenuSlotDefinition slotDefinition) {
        for (final ItemCombinerMenuSlotDefinition.SlotDefinition itemcombinermenuslotdefinition$slotdefinition : slotDefinition.getSlots()) {
            this.addSlot(
                    new CommonSmitherSlot(
                            this.container,
                            itemcombinermenuslotdefinition$slotdefinition.slotIndex(),
                            itemcombinermenuslotdefinition$slotdefinition.x(),
                            itemcombinermenuslotdefinition$slotdefinition.y(),
                            this) {
                        @Override
                        public boolean mayPlace(ItemStack p_267156_) {
                            return !this.menu.isSlotDisabled(this.index) && itemcombinermenuslotdefinition$slotdefinition.mayPlace().test(p_267156_);
                        }
                    }
            );
        }
    }

    private void createResultSlot(ItemCombinerMenuSlotDefinition slotDefinition) {
        this.addSlot(new NonInteractiveResultSlot(this.resultContainer, slotDefinition.getResultSlot().slotIndex(), slotDefinition.getResultSlot().x(), slotDefinition.getResultSlot().y()));
    }

    private void createInventorySlots(Inventory inventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    private SmithingRecipeInput createRecipeInput() {
        return new SmithingRecipeInput(this.container.getItem(0), this.container.getItem(1), this.container.getItem(2));
    }

    public void createResult() {
        SmithingRecipeInput smithingrecipeinput = this.createRecipeInput();
        List<RecipeHolder<SmithingRecipe>> list = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, smithingrecipeinput, this.level);
        if (list.isEmpty()) {
            this.resultContainer.setItem(0, ItemStack.EMPTY);
        }
        else {
            RecipeHolder<SmithingRecipe> recipeholder = list.get(0);
            ItemStack itemstack = recipeholder.value().assemble(smithingrecipeinput, this.level.registryAccess());
            this.selectedRecipe = recipeholder;
            this.resultContainer.setRecipeUsed(recipeholder);
            this.resultContainer.setItem(0, itemstack);
        }
    }

    private static OptionalInt findSlotMatchingIngredient(SmithingRecipe recipe, ItemStack stack) {
        if (recipe.isTemplateIngredient(stack)) {
            return OptionalInt.of(0);
        }
        else if (recipe.isBaseIngredient(stack)) {
            return OptionalInt.of(1);
        }
        else if (recipe.isAdditionIngredient(stack)){
            return OptionalInt.of(2);
        }
        else {
            return OptionalInt.empty();
        }
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.getInventorySlotStart();
            int j = this.getUseRowEnd();
            if (pIndex < i) {
                if (!this.moveItemStackTo(itemstack1, i, j, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemstack1, 0, i, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }


    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is null for the initial slot that was double-clicked.
     */
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
    /**
     * For shift-click.
     */
    private OptionalInt findSlotToQuickMoveTo(ItemStack stack) {
        return this.recipes
                .stream()
                .flatMapToInt(p_300800_ -> findSlotMatchingIngredient(p_300800_.value(), stack).stream())
                .filter(p_294045_ -> !this.getSlot(p_294045_).hasItem())
                .findFirst();
    }

    public int getResultSlot() {
        return this.resultSlotIndex;
    }

    private int getInventorySlotStart() {
        return this.resultSlotIndex + 1;
    }

    private int getInventorySlotEnd() {
        return this.getInventorySlotStart() + 27;
    }

    private int getUseRowStart() {
        return this.getInventorySlotEnd();
    }

    private int getUseRowEnd() {
        return this.getUseRowStart() + 9;
    }

    public void setSlotState(int slot, boolean enabled) {
        CommonSmitherSlot smitherSlot = (CommonSmitherSlot) this.getSlot(slot);
        this.containerData.set(smitherSlot.index, enabled ? 0 : 1);
        this.broadcastChanges();
    }

    public boolean isSlotDisabled(int slot) {
        return slot > -1 && slot < 3 && this.containerData.get(slot) == 1;
    }

    public boolean isPowered() {
        return this.containerData.get(3) == 1;
    }

    public Container getContainer() {
        return this.container;
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void slotsChanged(Container inventory) {
        super.slotsChanged(inventory);
        if (inventory == this.container) {
            this.createResult();
        }
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual contents of that slot.
     */
    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
        this.createResult();
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
    }
}
