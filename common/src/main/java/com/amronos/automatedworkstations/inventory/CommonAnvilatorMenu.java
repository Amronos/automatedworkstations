package com.amronos.automatedworkstations.inventory;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class CommonAnvilatorMenu extends CommonAutomatedWorkstationMenu {
    private final ContainerData containerData;
    private final Container container;
    private final Level level;
    private final int resultSlotIndex;
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int EXPERIENCE_SLOT = 2;
    public static final int RESULT_SLOT = 3;
    private static final int INPUT_SLOT_X_PLACEMENT = 27;
    private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
    private static final int RESULT_SLOT_X_PLACEMENT = 134;
    private static final int SLOT_Y_PLACEMENT = 47;
    private static final int EXPERIENCE_SLOT_X_PLACEMENT = 100;
    private static final int EXPERIENCE_SLOT_Y_PLACEMENT = 100;
    private final DataSlot cost = DataSlot.standalone();
    private static final int COST_FAIL = 0;
    private static final int COST_BASE = 1;
    private static final int COST_ADDED_BASE = 1;
    private static final int COST_REPAIR_MATERIAL = 1;
    private static final int COST_REPAIR_SACRIFICE = 2;
    private static final int COST_INCOMPATIBLE_PENALTY = 1;
    private static final int COST_RENAME = 1;
    private int repairItemCountCost;
    @Nullable
    private String itemName;
    public static final int MAX_NAME_LENGTH = 50;
    private final Player player;

    public CommonAnvilatorMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(menuType, containerId);
        this.containerData = containerData;
        this.container = container;
        checkContainerSize(container, 3);
        this.player = playerInventory.player;
        container.startOpen(this.player);
        this.level = this.player.level();
        ItemCombinerMenuSlotDefinition itemcombinermenuslotdefinition = this.createSlotDefinitions();
        this.resultSlotIndex = itemcombinermenuslotdefinition.getResultSlotIndex();
        this.addSlots(itemcombinermenuslotdefinition, playerInventory);
        this.addDataSlot(this.cost);
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(INPUT_SLOT, INPUT_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, itemStack -> true)
                .withSlot(ADDITIONAL_SLOT, ADDITIONAL_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, itemStack -> true)
                .withSlot(EXPERIENCE_SLOT, EXPERIENCE_SLOT_X_PLACEMENT, EXPERIENCE_SLOT_Y_PLACEMENT, itemStack -> true)
                .withResultSlot(RESULT_SLOT, RESULT_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT)
                .build();
    }

    @Override
    public void createResult() {
        ItemStack inputStack = this.container.getItem(INPUT_SLOT);
        this.cost.set(COST_BASE);
        int i = 0;
        long j = 0L;
        int k = 0;
        if (!inputStack.isEmpty() && EnchantmentHelper.canStoreEnchantments(inputStack)) {
            ItemStack resultStack = inputStack.copy();
            ItemStack additionalStack = this.container.getItem(ADDITIONAL_SLOT);
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(resultStack));
            j += (long) inputStack.getOrDefault(DataComponents.REPAIR_COST, 0)
                    + (long) additionalStack.getOrDefault(DataComponents.REPAIR_COST, 0);
            this.repairItemCountCost = 0;
            if (!additionalStack.isEmpty()) {
                boolean additionalStackHasEnchants = additionalStack.has(DataComponents.STORED_ENCHANTMENTS);
                if (resultStack.isDamageableItem() && resultStack.getItem().isValidRepairItem(inputStack, additionalStack)) {
                    int l2 = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        resultStack = ItemStack.EMPTY;
                        this.cost.set(COST_FAIL);
                    }
                    else {
                        int j3;
                        for (j3 = 0; l2 > 0 && j3 < additionalStack.getCount(); j3++) {
                            int k3 = resultStack.getDamageValue() - l2;
                            resultStack.setDamageValue(k3);
                            i++;
                            l2 = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / 4);
                        }
                        this.repairItemCountCost = j3;
                    }
                }

                else {
                    if (!additionalStackHasEnchants && (!resultStack.is(additionalStack.getItem()) || !resultStack.isDamageableItem())) {
                        resultStack = ItemStack.EMPTY;
                        this.cost.set(COST_FAIL);
                    }

                    else {
                        if (resultStack.isDamageableItem() && !additionalStackHasEnchants) {
                            int l = inputStack.getMaxDamage() - inputStack.getDamageValue();
                            int i1 = additionalStack.getMaxDamage() - additionalStack.getDamageValue();
                            int j1 = i1 + resultStack.getMaxDamage() * 12 / 100;
                            int k1 = l + j1;
                            int l1 = resultStack.getMaxDamage() - k1;
                            if (l1 < 0) {
                                l1 = 0;
                            }

                            if (l1 < resultStack.getDamageValue()) {
                                resultStack.setDamageValue(l1);
                                i += 2;
                            }
                        }

                        ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(additionalStack);
                        boolean flag2 = false;
                        boolean flag3 = false;

                        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
                            Holder<Enchantment> holder = entry.getKey();
                            int i2 = itemenchantments$mutable.getLevel(holder);
                            int j2 = entry.getIntValue();
                            j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                            Enchantment enchantment = holder.value();
                            boolean flag1 = enchantment.canEnchant(inputStack);
                            if (this.player.getAbilities().instabuild || inputStack.is(Items.ENCHANTED_BOOK)) {
                                flag1 = true;
                            }

                            for (Holder<Enchantment> holder1 : itemenchantments$mutable.keySet()) {
                                if (!holder1.equals(holder) && !Enchantment.areCompatible(holder, holder1)) {
                                    flag1 = false;
                                    i++;
                                }
                            }

                            if (!flag1) {
                                flag3 = true;
                            } else {
                                flag2 = true;
                                if (j2 > enchantment.getMaxLevel()) {
                                    j2 = enchantment.getMaxLevel();
                                }

                                itemenchantments$mutable.set(holder, j2);
                                int l3 = enchantment.getAnvilCost();
                                if (additionalStackHasEnchants) {
                                    l3 = Math.max(1, l3 / 2);
                                }

                                i += l3 * j2;
                                if (inputStack.getCount() > 1) {
                                    i = 40;
                                }
                            }
                        }

                        if (flag3 && !flag2) {
                            resultStack = ItemStack.EMPTY;
                            this.cost.set(COST_FAIL);
                        }
                    }
                }
            }

            if (!resultStack.isEmpty()) {
                if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
                    if (!this.itemName.equals(inputStack.getHoverName().getString())) {
                        k = 1;
                        i += k;
                        resultStack.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
                    }
                }
                else if (inputStack.has(DataComponents.CUSTOM_NAME)) {
                    k = 1;
                    i += k;
                    resultStack.remove(DataComponents.CUSTOM_NAME);
                }

                int k2 = (int) Mth.clamp(j + (long)i, 0L, 2147483647L);
                this.cost.set(k2);
                if (i <= 0) {
                    resultStack = ItemStack.EMPTY;
                }

                if (k == i && k > 0 && this.cost.get() >= 40) {
                    this.cost.set(39);
                }

                if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                    resultStack = ItemStack.EMPTY;
                }

                if (!resultStack.isEmpty()) {
                    int i3 = resultStack.getOrDefault(DataComponents.REPAIR_COST, 0);
                    if (i3 < additionalStack.getOrDefault(DataComponents.REPAIR_COST, 0)) {
                        i3 = additionalStack.getOrDefault(DataComponents.REPAIR_COST, 0);
                    }

                    if (k != i || k == 0) {
                        i3 = calculateIncreasedRepairCost(i3);
                    }

                    resultStack.set(DataComponents.REPAIR_COST, i3);
                    EnchantmentHelper.setEnchantments(resultStack, itemenchantments$mutable.toImmutable());
                }

                this.resultContainer.setItem(0, resultStack);
                this.broadcastChanges();
            }
        }
        else {
            this.resultContainer.setItem(0, ItemStack.EMPTY);
            this.cost.set(COST_FAIL);
        }
    }

    public static int calculateIncreasedRepairCost(int oldRepairCost) {
        return (int)Math.min((long)oldRepairCost * 2L + 1L, 2147483647L);
    }

    public boolean setItemName(String itemName) {
        String s = validateName(itemName);
        if (s != null && !s.equals(this.itemName)) {
            this.itemName = s;
            ItemStack resultStack = this.getSlot(RESULT_SLOT).getItem();
            if (!resultStack.isEmpty()) {
                if (StringUtil.isBlank(s)) {
                    resultStack.remove(DataComponents.CUSTOM_NAME);
                }
                else {
                    resultStack.set(DataComponents.CUSTOM_NAME, Component.literal(s));
                }
            }

            this.createResult();
            return true;
        }
        else {
            return false;
        }
    }

    @Nullable
    private static String validateName(String itemName) {
        String s = StringUtil.filterText(itemName);
        return s.length() <= 50 ? s : null;
    }

    public int getCost() {
        return this.cost.get();
    }

    @Override
    public ContainerData getContainerData() {
        return this.containerData;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public int getResultSlot() {
        return this.resultSlotIndex;
    }
}
