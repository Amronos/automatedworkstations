package com.amronos.automatedworkstations.inventory;

import com.amronos.automatedworkstations.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class CommonAutomatedWorkstationScreen<T extends CommonAutomatedWorkstationMenu> extends AbstractContainerScreen<T> implements ContainerListener {
    protected static final ResourceLocation DISABLED_SLOT_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/disabled_slot");
    protected static final ResourceLocation POWERED_REDSTONE_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/powered_redstone");
    protected static final ResourceLocation UNPOWERED_REDSTONE_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/unpowered_redstone");
    private final Player player;
    protected static final Component DISABLE_SLOT_TOOLTIP = Component.translatable("gui." + Constants.MOD_ID + ".disable_slot_tooltip");

    public CommonAutomatedWorkstationScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
    }

    @Override
    protected void init() {
        super.init();
        this.menu.addSlotListener(this);
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot instanceof CommonAutomatedWorkstationSlot && !slot.hasItem() && !this.player.isSpectator()) {
            switch (type) {
                case PICKUP:
                    if (this.menu.isSlotDisabled(slotId)) {
                        this.enableSlot(slotId);
                    }
                    else if (this.menu.getCarried().isEmpty()) {
                        this.disableSlot(slotId);
                    }
                    break;
                case SWAP:
                    ItemStack itemstack = this.player.getInventory().getItem(mouseButton);
                    if (this.menu.isSlotDisabled(slotId) && !itemstack.isEmpty()) {
                        this.enableSlot(slotId);
                    }
            }
        }

        super.slotClicked(slot, slotId, mouseButton, type);
    }

    protected void enableSlot(int slot) {
        this.updateSlotState(slot, true);
    }

    protected void disableSlot(int slot) {
        this.updateSlotState(slot, false);
    }

    protected void updateSlotState(int slot, boolean state) {
        this.menu.setSlotState(slot, state);
        super.handleSlotStateChanged(slot, this.menu.containerId, state);
        float f = state ? 1.0F : 0.75F;
        this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, f);
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (slot instanceof CommonAutomatedWorkstationSlot commonautomatedworkstationslot && this.menu.isSlotDisabled(slot.index)) {
            this.renderDisabledSlot(guiGraphics, commonautomatedworkstationslot);
            return;
        }

        super.renderSlot(guiGraphics, slot);
    }

    protected void renderDisabledSlot(GuiGraphics guiGraphics, CommonAutomatedWorkstationSlot slot) {
        guiGraphics.blitSprite(DISABLED_SLOT_LOCATION_SPRITE, slot.x - 1, slot.y - 1, 18, 18);
    }

    protected abstract void renderRedstone(GuiGraphics guiGraphics);

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param guiGraphics the GuiGraphics object used for rendering.
     * @param mouseX      the x-coordinate of the mouse cursor.
     * @param mouseY      the y-coordinate of the mouse cursor.
     * @param partialTick the partial tick time.
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderRedstone(guiGraphics);
        this.renderOnboardingTooltips(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(this.getMenuResource(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected abstract void renderOnboardingTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY);

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual contents of that slot.
     */
    @Override
    public abstract void slotChanged(AbstractContainerMenu containerToSend, int slotIndex, ItemStack stack);

    @Override
    public abstract void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value);

    protected abstract ResourceLocation getMenuResource();
}
