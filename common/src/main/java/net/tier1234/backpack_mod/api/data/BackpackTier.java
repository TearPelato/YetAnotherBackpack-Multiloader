package net.tier1234.backpack_mod.api.data;

public enum BackpackTier {

    TIER_1("leather", 27,  1),
    TIER_2("iron",   45,  2),
    TIER_3("gold",   66,  3),
    TIER_4("diamond",88, 4);

    private final String name;
    private final int inventorySlots;
    private final int upgradeSlots;

    BackpackTier(String name, int inventorySlots, int upgradeSlots) {
        this.name = name;
        this.inventorySlots = inventorySlots;
        this.upgradeSlots = upgradeSlots;
    }

    public String getName() {
        return name;
    }

    public int getInventorySlots() {
        return inventorySlots;
    }

    public int getUpgradeSlots() {
        return upgradeSlots;
    }

    public int getTotalSlots() {
        return inventorySlots + upgradeSlots;
    }

    public static BackpackTier fromIndex(int index) {
        BackpackTier[] values = values();
        if (index < 0 || index >= values.length) return TIER_1;
        return values[index];
    }

    public static BackpackTier fromName(String name) {
        for (BackpackTier tier : values()) {
            if (tier.name.equalsIgnoreCase(name)) return tier;
        }
        return TIER_1;
    }

    public int getIndex() {
        return ordinal();
    }
}

