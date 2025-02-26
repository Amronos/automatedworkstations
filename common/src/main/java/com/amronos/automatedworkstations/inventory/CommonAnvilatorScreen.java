package com.amronos.automatedworkstations.inventory;

import com.amronos.automatedworkstations.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CommonAnvilatorScreen extends CommonAutomatedWorkstationScreen<CommonAnvilatorMenu> {
    private final ResourceLocation menuResource = ResourceLocation.withDefaultNamespace("textures/gui/container/anvil.png");
    private final Player player;
    private EditBox name;
    private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    private static final ResourceLocation TEXT_FIELD_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field");
    private static final ResourceLocation TEXT_FIELD_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field_disabled");
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "error");

    public CommonAnvilatorScreen(CommonAnvilatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
        this.titleLabelX = 60;
    }

    protected void subInit() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, i + 62, j + 24, 103, 12, Component.translatable("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(50);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addWidget(this.name);
        this.name.setEditable(this.menu.getSlot(0).hasItem());
    }

    @Override
    protected void init() {
        super.init();
        this.subInit();
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.name);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.name.getValue();
        this.init(minecraft, width, height);
        this.name.setValue(s);
    }

    /**
     * Called when a keyboard key is pressed within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     *
     * @param keyCode   the key code of the pressed key.
     * @param scanCode  the scan code of the pressed key.
     * @param modifiers the keyboard modifiers.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }

        return this.name.keyPressed(keyCode, scanCode, modifiers) || this.name.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onNameChanged(String name) {
        Slot slot = this.menu.getSlot(CommonAnvilatorMenu.INPUT_SLOT);
        if (slot.hasItem()) {
            String s = name;
            if (!slot.getItem().has(DataComponents.CUSTOM_NAME) && name.equals(slot.getItem().getHoverName().getString())) {
                s = "";
            }

            if (this.menu.setItemName(s)) {
                this.minecraft.player.connection.send(new ServerboundRenameItemPacket(s));
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        int i = this.menu.getCost();
        if (i > 0) {
            int j = 8453920;
            Component component;
            if (i >= 40 && !this.minecraft.player.getAbilities().instabuild) {
                component = TOO_EXPENSIVE_TEXT;
                j = 16736352;
            } else if (!this.menu.getSlot(2).hasItem()) {
                component = null;
            } else {
                component = Component.translatable("container.repair.cost", i);
                if (!this.menu.getSlot(2).mayPickup(this.player)) {
                    j = 16736352;
                }
            }

            if (component != null) {
                int k = this.imageWidth - 8 - this.font.width(component) - 2;
                int l = 69;
                guiGraphics.fill(k - 2, 67, this.imageWidth - 8, 79, 1325400064);
                guiGraphics.drawString(this.font, component, k, l, j);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        this.renderErrorIcon(guiGraphics);
        guiGraphics.blitSprite(this.menu.getSlot(CommonAnvilatorMenu.INPUT_SLOT).hasItem() ? TEXT_FIELD_SPRITE : TEXT_FIELD_DISABLED_SPRITE, this.leftPos + 59, this.topPos + 20, 110, 16);
        this.name.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void renderErrorIcon(GuiGraphics guiGraphics) {
        if ((this.menu.getSlot(CommonAnvilatorMenu.INPUT_SLOT).hasItem() || this.menu.getSlot(CommonAnvilatorMenu.ADDITIONAL_SLOT).hasItem()) && !this.menu.getSlot(this.menu.getResultSlot()).hasItem()) {
            guiGraphics.blitSprite(ERROR_SPRITE, this.leftPos + 99, this.topPos + 45, 28, 21);
        }
    }

    @Override
    protected void renderRedstone(GuiGraphics guiGraphics) {
        int i = this.width / 2 - 18;
        int j = this.height / 2 - 37;
        ResourceLocation resourcelocation;
        if (this.menu.isPowered()) {
            resourcelocation = POWERED_REDSTONE_LOCATION_SPRITE;
        } else {
            resourcelocation = UNPOWERED_REDSTONE_LOCATION_SPRITE;
        }

        guiGraphics.blitSprite(resourcelocation, i, j, 16, 16);
    }

    @Override
    protected void renderOnboardingTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual contents of that slot.
     */
    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int slotInd, ItemStack stack) {
        if (slotInd == CommonAnvilatorMenu.INPUT_SLOT) {
            this.name.setValue(stack.isEmpty() ? "" : stack.getHoverName().getString());
            this.name.setEditable(!stack.isEmpty());
            this.setFocused(this.name);
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {

    }

    @Override
    protected ResourceLocation getMenuResource() {
        return this.menuResource;
    }
}
