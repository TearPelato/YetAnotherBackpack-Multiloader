package net.tier1234.backpack_mod.api.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.tier1234.backpack_mod.api.data.BackpackContents;
import net.tier1234.backpack_mod.api.data.BackpackTier;
import net.tier1234.backpack_mod.init.ModDataComponents;

public class BackpackInventory extends SimpleContainer {

    private final ItemStack backpackStack;
    private final BackpackTier tier;

    public BackpackInventory(ItemStack backpackStack, BackpackTier tier) {
        super(tier.getInventorySlots());
        this.backpackStack = backpackStack;
        this.tier = tier;

        // Load existing contents from component
        BackpackContents contents = backpackStack.get(ModDataComponents.BACKPACK_CONTENTS.get());
        if (contents != null) {
            NonNullList<ItemStack> loaded = contents.getMutableInventory(tier);
            for (int i = 0; i < Math.min(loaded.size(), getContainerSize()); i++) {
                setItem(i, loaded.get(i));
            }
        }

        // Register save-on-change listener
        addListener(container -> save());
    }

    /** Persist current inventory into the backpack's data component. */
    public void save() {
        BackpackContents existing = backpackStack.get(ModDataComponents.BACKPACK_CONTENTS.get());
        NonNullList<ItemStack> upgrades;

        if (existing != null) {
            upgrades = existing.getMutableUpgrades(tier);
        } else {
            upgrades = NonNullList.withSize(tier.getUpgradeSlots(), ItemStack.EMPTY);
        }

        // Build inventory snapshot from SimpleContainer
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < getContainerSize(); i++) {
            items.set(i, getItem(i));
        }

        backpackStack.set(ModDataComponents.BACKPACK_CONTENTS.get(),
                BackpackContents.of(items, upgrades));
    }

    public BackpackTier getTier() {
        return tier;
    }

    /** Returns the ItemStack this inventory reads/writes to. */
    public ItemStack getBackpackStack() {
        return backpackStack;
    }
}