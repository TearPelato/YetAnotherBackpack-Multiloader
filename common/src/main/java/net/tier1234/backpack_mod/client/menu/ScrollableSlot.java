package net.tier1234.backpack_mod.client.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

// common module
public class ScrollableSlot extends Slot {

    private int guiX;
    private int guiY;

    public ScrollableSlot(Container container, int slotIndex, int x, int y) {
        super(container, slotIndex, x, y);
        this.guiX = x;
        this.guiY = y;
    }

    public void setGuiPos(int x, int y) {
       /* this.x = x;
        this.y = y;*/
        this.guiX = x;
        this.guiY = y;
    }

    @Override
    public boolean isActive() {
        return guiX > -1000;
    }

    public int getGuiX() { return guiX; }
    public int getGuiY() { return guiY; }
}