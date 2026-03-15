package net.tier1234.backpack_mod.client.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.tier1234.backpack_mod.api.data.BackpackTier;
import net.tier1234.backpack_mod.api.inventory.BackpackInventory;
import net.tier1234.backpack_mod.api.inventory.BackpackUpgradeInventory;
import net.tier1234.backpack_mod.init.ModMenuTypes;

public class BackpackMenu extends AbstractContainerMenu {

    private final BackpackInventory backpackInventory;
    private final BackpackUpgradeInventory upgradeInventory;
    private final BackpackTier tier;

    private static final int SLOT_SIZE = 18;
    private static final int INVENTORY_COLS = 9;
    private static final int TIER4_VISIBLE_ROWS = 6;
    private static final int TIER4_EXTRA_ROWS = 2;

    private final int inventoryRows;
    private final int guiHeight;
    private int scrollOffset = 0;

    public BackpackMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory,
                new BackpackInventory(ItemStack.EMPTY, BackpackTier.TIER_1),
                new BackpackUpgradeInventory(ItemStack.EMPTY, BackpackTier.TIER_1));
    }

    public BackpackMenu(int containerId, Inventory playerInventory,
                        BackpackInventory backpackInventory,
                        BackpackUpgradeInventory upgradeInventory) {
        super(ModMenuTypes.BACKPACK_MENU.get(), containerId);
        this.backpackInventory = backpackInventory;
        this.upgradeInventory = upgradeInventory;
        this.tier = backpackInventory.getTier();
        this.inventoryRows = (int) Math.ceil((double) tier.getInventorySlots() / INVENTORY_COLS);

        addBackpackSlots();
        addUpgradeSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.guiHeight = computeGuiHeight();
    }

    public BackpackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(ModMenuTypes.BACKPACK_MENU.get(), containerId);

        int tierIndex = extraData.readInt();
        int slotIndex = extraData.readInt();

        BackpackTier resolvedTier = BackpackTier.fromIndex(tierIndex);
        ItemStack stack = playerInventory.player.getInventory().getItem(slotIndex);

        this.backpackInventory = new BackpackInventory(stack, resolvedTier);
        this.upgradeInventory = new BackpackUpgradeInventory(stack, resolvedTier);
        this.tier = resolvedTier;
        this.inventoryRows = (int) Math.ceil((double) tier.getInventorySlots() / INVENTORY_COLS);

        addBackpackSlots();
        addUpgradeSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.guiHeight = computeGuiHeight();
    }

    private int computeGuiHeight() {
        // Per tier 4 usiamo TIER4_VISIBLE_ROWS come altezza visiva, non inventoryRows
        int visibleRows = (tier == BackpackTier.TIER_4) ? TIER4_VISIBLE_ROWS : inventoryRows;
        return 18 + visibleRows * SLOT_SIZE + 4 + 3 * SLOT_SIZE + 4 + SLOT_SIZE;
    }

    private void addBackpackSlots() {
        if (tier == BackpackTier.TIER_4) {
            // Usa ScrollableSlot per tutti gli 88 slot, inizialmente posizionati
            for (int i = 0; i < tier.getInventorySlots(); i++) {
                int col = i % INVENTORY_COLS;
                int row = i / INVENTORY_COLS;
                // I primi TIER4_VISIBLE_ROWS*9 slot sono visibili, gli altri nascosti
                int guiRow = row; // verrà corretto da rebuildBackpackSlots
                addSlot(new ScrollableSlot(backpackInventory, i,
                        8 + col * SLOT_SIZE,
                        18 + guiRow * SLOT_SIZE));
            }
            rebuildBackpackSlots(); // posiziona correttamente fin da subito
        } else {
            for (int i = 0; i < tier.getInventorySlots(); i++) {
                int col = i % INVENTORY_COLS;
                int row = i / INVENTORY_COLS;
                addSlot(new Slot(backpackInventory, i,
                        8 + col * SLOT_SIZE,
                        18 + row * SLOT_SIZE));
            }
        }
    }

    private void addUpgradeSlots() {
        // Gli slot upgrade stanno a destra del pannello principale.
        // La X è calcolata uguale a upgradeColX nella Screen:
        // mainPanelWidth + UPGRADE_SIDE_GAP + centro del slot (4px per centrare in 26px)
        int mainPanelW = mainPanelWidth();
        int upgradeGap = 4;
        int upgradeSlotSize = 26;
        int upgradeX = mainPanelW + upgradeGap + (upgradeSlotSize - SLOT_SIZE) / 2; // centra il slot 18px dentro il bg 26px
        int upgradeStartY = 18 + 4; // piccolo margine dal bordo superiore

        for (int i = 0; i < tier.getUpgradeSlots(); i++) {
            int slotY = upgradeStartY + i * (upgradeSlotSize + 2) + (upgradeSlotSize - SLOT_SIZE) / 2;
            addSlot(new Slot(upgradeInventory, i, upgradeX, slotY));
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        int visibleRows = (tier == BackpackTier.TIER_4) ? TIER4_VISIBLE_ROWS : inventoryRows;
        int playerInvStartY = 18 + visibleRows * SLOT_SIZE + 8 + SLOT_SIZE + 8;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        8 + col * SLOT_SIZE,
                        playerInvStartY + row * SLOT_SIZE));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        int visibleRows = (tier == BackpackTier.TIER_4) ? TIER4_VISIBLE_ROWS : inventoryRows;
        int hotbarY = 18 + visibleRows * SLOT_SIZE + 8 + SLOT_SIZE + 8 + 3 * SLOT_SIZE + 4;
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory,
                    col,
                    8 + col * SLOT_SIZE,
                    hotbarY));
        }
    }

    private int mainPanelWidth() {
        return switch (tier) {
            case TIER_1 -> 175;
            case TIER_2 -> 175;
            case TIER_3, TIER_4 -> 211;
        };
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            int backpackSlots = tier.getInventorySlots();
            int upgradeSlots = tier.getUpgradeSlots();
            int totalBackpack = backpackSlots + upgradeSlots;
            int totalSlots = slots.size();
            int playerSlotStart = totalBackpack;

            if (index < backpackSlots) {
                if (!moveItemStackTo(slotStack, playerSlotStart, totalSlots, true))
                    return ItemStack.EMPTY;
            } else if (index < totalBackpack) {
                if (!moveItemStackTo(slotStack, playerSlotStart, totalSlots, true))
                    return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(slotStack, backpackSlots, totalBackpack, false))
                    if (!moveItemStackTo(slotStack, 0, backpackSlots, false))
                        return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }

        return returnStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void setScrollOffset(int newOffset) {
        if (tier != BackpackTier.TIER_4) return;
        this.scrollOffset = Math.max(0, Math.min(TIER4_EXTRA_ROWS, newOffset));
        rebuildBackpackSlots();
    }

    private void rebuildBackpackSlots() {
        int startRow = scrollOffset;

        for (int i = 0; i < tier.getInventorySlots(); i++) {
            Slot slot = slots.get(i);
            if (!(slot instanceof ScrollableSlot scrollable)) continue;

            int visibleIndex = i - startRow * INVENTORY_COLS;
            int visibleRow = visibleIndex / INVENTORY_COLS;
            int visibleCol = visibleIndex % INVENTORY_COLS;
            boolean isVisible = visibleIndex >= 0 && visibleRow < TIER4_VISIBLE_ROWS;

            if (isVisible) {
                scrollable.setGuiPos(8 + visibleCol * SLOT_SIZE, 18 + visibleRow * SLOT_SIZE);
            } else {
                scrollable.setGuiPos(-2000, -2000);
            }
        }
    }

    public int getScrollOffset()                          { return scrollOffset; }
    public int getGuiHeight()                             { return guiHeight; }
    public int getInventoryRows()                         { return inventoryRows; }
    public BackpackTier getTier()                         { return tier; }
    public BackpackInventory getBackpackInventory()       { return backpackInventory; }
    public BackpackUpgradeInventory getUpgradeInventory() { return upgradeInventory; }
    public ItemStack getBackpackStack()                   { return backpackInventory.getBackpackStack(); }
}