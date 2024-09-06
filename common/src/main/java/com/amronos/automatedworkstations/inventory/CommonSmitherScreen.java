package com.amronos.automatedworkstations.inventory;

import com.amronos.automatedworkstations.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class CommonSmitherScreen extends AbstractContainerScreen<CommonSmitherMenu> implements ContainerListener {
    private final ResourceLocation menuResource = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/smither.png");
    private static final ResourceLocation DISABLED_SLOT_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/disabled_slot");
    private static final ResourceLocation POWERED_REDSTONE_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/powered_redstone");
    private static final ResourceLocation UNPOWERED_REDSTONE_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/unpowered_redstone");
    private final Player player;
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "error");
    private static final Component ERROR_TOOLTIP = Component.translatable("gui." + Constants.MOD_ID + ".smither.upgrade.error_tooltip");
    private static final Component MISSING_TEMPLATE_TOOLTIP = Component.translatable("gui." + Constants.MOD_ID + ".smither.missing_template_tooltip");
    private static final Component DISABLE_SLOT_TOOLTIP = Component.translatable("gui." + Constants.MOD_ID + ".disable_slot_tooltip");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM = ResourceLocation.withDefaultNamespace("item/empty_slot_smithing_template_armor_trim");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE = ResourceLocation.withDefaultNamespace("item/empty_slot_smithing_template_netherite_upgrade");
    private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES = List.of(
            EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE
    );
    private static final Vector3f ARMOR_STAND_TRANSLATION = new Vector3f();
    private static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, (float) Math.PI);
    private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
    @Nullable
    private ArmorStand armorStandPreview;

    public CommonSmitherScreen(CommonSmitherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = 44;
        this.titleLabelY = 15;
        this.player = playerInventory.player;
    }

    protected void subInit() {
        this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0, 0.0, 0.0);
        this.armorStandPreview.setNoBasePlate(true);
        this.armorStandPreview.setShowArms(true);
        this.armorStandPreview.yBodyRot = 210.0F;
        this.armorStandPreview.setXRot(25.0F);
        this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
        this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
        this.updateArmorStandPreview(this.menu.getSlot(3).getItem());
    }

    @Override
    protected void init() {
        super.init();
        this.subInit();
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
        if (slot instanceof CommonSmitherSlot && !slot.hasItem() && !this.player.isSpectator()) {
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

    private void enableSlot(int slot) {
        this.updateSlotState(slot, true);
    }

    private void disableSlot(int slot) {
        this.updateSlotState(slot, false);
    }

    private void updateSlotState(int slot, boolean state) {
        this.menu.setSlotState(slot, state);
        super.handleSlotStateChanged(slot, this.menu.containerId, state);
        float f = state ? 1.0F : 0.75F;
        this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, f);
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (slot instanceof CommonSmitherSlot smitherslot && this.menu.isSlotDisabled(slot.index)) {
            this.renderDisabledSlot(guiGraphics, smitherslot);
            return;
        }

        super.renderSlot(guiGraphics, slot);
    }

    private void renderDisabledSlot(GuiGraphics guiGraphics, CommonSmitherSlot slot) {
        guiGraphics.blitSprite(DISABLED_SLOT_LOCATION_SPRITE, slot.x - 1, slot.y - 1, 18, 18);
    }

    private void renderRedstone(GuiGraphics guiGraphics) {
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
    public void containerTick() {
        super.containerTick();
        Optional<SmithingTemplateItem> optional = this.getTemplateItem();
        this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.baseIcon.tick(optional.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
        this.additionalIcon.tick(optional.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
    }

    private Optional<SmithingTemplateItem> getTemplateItem() {
        ItemStack itemstack = this.menu.getSlot(0).getItem();
        return !itemstack.isEmpty() && itemstack.getItem() instanceof SmithingTemplateItem smithingtemplateitem
                ? Optional.of(smithingtemplateitem)
                : Optional.empty();
    }

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
        this.renderFg(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderRedstone(guiGraphics);
        this.renderOnboardingTooltips(guiGraphics, mouseX, mouseY);
    }

    protected void renderFg(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(this.menuResource, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.renderErrorIcon(guiGraphics, this.leftPos, this.topPos);
        this.templateIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        this.baseIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        this.additionalIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        InventoryScreen.renderEntityInInventory(
                guiGraphics, (float)(this.leftPos + 141), (float)(this.topPos + 75), 25.0F, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, null, this.armorStandPreview
        );
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual contents of that slot.
     */
    public void slotChanged(AbstractContainerMenu containerToSend, int slotIndex, ItemStack stack) {
        if (slotIndex == 3) {
            this.updateArmorStandPreview(stack);
        }
    }

    private void updateArmorStandPreview(ItemStack stack) {
        if (this.armorStandPreview != null) {
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                this.armorStandPreview.setItemSlot(equipmentslot, ItemStack.EMPTY);
            }

            if (!stack.isEmpty()) {
                ItemStack itemstack = stack.copy();
                if (stack.getItem() instanceof ArmorItem armoritem) {
                    this.armorStandPreview.setItemSlot(armoritem.getEquipmentSlot(), itemstack);
                }
                else {
                    this.armorStandPreview.setItemSlot(EquipmentSlot.OFFHAND, itemstack);
                }
            }
        }
    }

    protected void renderErrorIcon(GuiGraphics guiGraphics, int X, int Y) {
        if (this.hasRecipeError()) {
            guiGraphics.blitSprite(ERROR_SPRITE, X + 65, Y + 45, 28, 21);
        }
    }

    private void renderOnboardingTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Optional<Component> optional = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(65, 46, 28, 21, (double)mouseX, (double)mouseY) && this.hoveredSlot instanceof CommonSmitherSlot && !this.menu.isSlotDisabled(this.hoveredSlot.index) && this.menu.getCarried().isEmpty() && !this.hoveredSlot.hasItem() && !this.player.isSpectator()) {
            optional = Optional.of(ERROR_TOOLTIP);
        }


        if (this.hoveredSlot != null) {
            ItemStack itemstack = this.menu.getSlot(0).getItem();
            ItemStack itemstack1 = this.hoveredSlot.getItem();
            if (itemstack.isEmpty()) {
                if (this.hoveredSlot.index == 0 && this.hoveredSlot instanceof CommonSmitherSlot && !this.menu.isSlotDisabled(this.hoveredSlot.index) && this.menu.getCarried().isEmpty() && !this.hoveredSlot.hasItem() && !this.player.isSpectator()) {
                    optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
                }
            }
            else if (itemstack.getItem() instanceof SmithingTemplateItem smithingtemplateitem && itemstack1.isEmpty()) {
                if (this.hoveredSlot.index == 1) {
                    optional = Optional.of(smithingtemplateitem.getBaseSlotDescription());
                }
                else if (this.hoveredSlot.index == 2) {
                    optional = Optional.of(smithingtemplateitem.getAdditionSlotDescription());
                }
            }
            else if (((this.hoveredSlot.index == 1) || (this.hoveredSlot.index == 2)) && this.hoveredSlot instanceof CommonSmitherSlot && !this.menu.isSlotDisabled(this.hoveredSlot.index) && this.menu.getCarried().isEmpty() && !this.hoveredSlot.hasItem() && !this.player.isSpectator()) {
                optional = Optional.of(DISABLE_SLOT_TOOLTIP);
            }
        }

        optional.ifPresent(p_280863_ -> guiGraphics.renderTooltip(this.font, this.font.split(p_280863_, 115), mouseX, mouseY));
    }

    private boolean hasRecipeError() {
        return this.menu.getSlot(0).hasItem()
                && this.menu.getSlot(1).hasItem()
                && this.menu.getSlot(2).hasItem()
                && !this.menu.getSlot(this.menu.getResultSlot()).hasItem();
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
    }
}
