package net.tier1234.backpack_mod.api.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.tier1234.backpack_mod.api.data.BackpackContents;
import net.tier1234.backpack_mod.api.data.BackpackTier;
import net.tier1234.backpack_mod.init.ModDataComponents;
import net.tier1234.backpack_mod.item.UpgradeItem;


public class BackpackUpgradeInventory extends SimpleContainer {

    private final ItemStack backpackStack;
    private final BackpackTier tier;

    public BackpackUpgradeInventory(ItemStack backpackStack, BackpackTier tier) {
        super(tier.getUpgradeSlots());
        this.backpackStack = backpackStack;
        this.tier = tier;

        // Load existing upgrade contents
        BackpackContents contents = backpackStack.get(ModDataComponents.BACKPACK_CONTENTS.get());
        if (contents != null) {
            NonNullList<ItemStack> loaded = contents.getMutableUpgrades(tier);
            for (int i = 0; i < Math.min(loaded.size(), getContainerSize()); i++) {
                setItem(i, loaded.get(i));
            }
        }

        addListener(container -> save());
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        // Only allow upgrade items, max 1 per slot
        return stack.getItem() instanceof UpgradeItem;
    }

    @Override
    public int getMaxStackSize() {
        return 1; // Applies to all slots
    }

    public void save() {
        BackpackContents existing = backpackStack.get(ModDataComponents.BACKPACK_CONTENTS.get());
        NonNullList<ItemStack> inventory;

        if (existing != null) {
            inventory = existing.getMutableInventory(tier);
        } else {
            inventory = NonNullList.withSize(tier.getInventorySlots(), ItemStack.EMPTY);
        }

        NonNullList<ItemStack> upgrades = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < getContainerSize(); i++) {
            upgrades.set(i, getItem(i));
        }

        backpackStack.set(ModDataComponents.BACKPACK_CONTENTS.get(),
                BackpackContents.of(inventory, upgrades));
    }

    public BackpackTier getTier() {
        return tier;
    }
}