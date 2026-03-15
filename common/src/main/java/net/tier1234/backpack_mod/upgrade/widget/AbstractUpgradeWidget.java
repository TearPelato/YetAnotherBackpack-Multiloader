package net.tier1234.backpack_mod.upgrade.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractUpgradeWidget {

    protected int x;
    protected int y;

    protected final ItemStack upgradeStack;
    protected final ItemStack backpackStack;

    private boolean visible = false;

    protected static final int COLOR_BG         = 0xFF2D2D2D;
    protected static final int COLOR_BORDER      = 0xFFFFD700;  // gold
    protected static final int COLOR_TITLE_BG    = 0xFF1A1A1A;
    protected static final int COLOR_TITLE_TEXT  = 0xFFFFD700;
    protected static final int COLOR_CLOSE_BTN   = 0xFFAA0000;
    protected static final int COLOR_CLOSE_HOVER = 0xFFFF3333;


    public AbstractUpgradeWidget(int x, int y, ItemStack upgradeStack, ItemStack backpackStack) {
        this.x = x;
        this.y = y;
        this.upgradeStack = upgradeStack;
        this.backpackStack = backpackStack;
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract Component getTitle();

    public void onOpen() {}

    public void onClose() {}

    public void tick() {}

    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        graphics.fill(x,             y,              x + getWidth(),     y + getHeight(),     COLOR_BG);
        graphics.fill(x,             y,              x + getWidth(),     y + 1,               COLOR_BORDER);
        graphics.fill(x,             y + getHeight() - 1, x + getWidth(), y + getHeight(),    COLOR_BORDER);
        graphics.fill(x,             y,              x + 1,              y + getHeight(),     COLOR_BORDER);
        graphics.fill(x + getWidth() - 1, y,         x + getWidth(),     y + getHeight(),     COLOR_BORDER);

        int titleBarBottom = y + 12;
        graphics.fill(x + 1, y + 1, x + getWidth() - 1, titleBarBottom, COLOR_TITLE_BG);

        Font font = font();
        graphics.drawString(font, getTitle(), x + 4, y + 2, COLOR_TITLE_TEXT, false);

        int closeX = x + getWidth() - 11;
        int closeY = y + 1;
        boolean hoverClose = mouseX >= closeX && mouseX < closeX + 10
                && mouseY >= closeY && mouseY < closeY + 10;
        graphics.fill(closeX, closeY, closeX + 10, closeY + 10,
                hoverClose ? COLOR_CLOSE_HOVER : COLOR_CLOSE_BTN);
        graphics.drawString(font, "×", closeX + 2, closeY + 1, 0xFFFFFFFF, false);

        // Subclass content (clipped to content area)
        graphics.enableScissor(x + 1, titleBarBottom, x + getWidth() - 1, y + getHeight() - 1);
        renderContents(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();
    }

    protected abstract void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        int closeX = x + getWidth() - 11;
        int closeY = y + 1;
        if (mouseX >= closeX && mouseX < closeX + 10
                && mouseY >= closeY && mouseY < closeY + 10) {
            setVisible(false);
            return true;
        }

        return isInside(mouseX, mouseY)
                && onMouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return visible && isInside(mouseX, mouseY)
                && onMouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        return visible && isInside(mouseX, mouseY)
                && onMouseDragged(mouseX, mouseY, button, dx, dy);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return visible && isInside(mouseX, mouseY)
                && onMouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return visible && onKeyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return visible && onCharTyped(codePoint, modifiers);
    }


    protected boolean onMouseClicked(double mouseX, double mouseY, int button)                          { return false; }
    protected boolean onMouseReleased(double mouseX, double mouseY, int button)                         { return false; }
    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double dx, double dy)    { return false; }
    protected boolean onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)     { return false; }
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers)                            { return false; }
    protected boolean onCharTyped(char codePoint, int modifiers)                                        { return false; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isVisible() { return visible; }

    public void setVisible(boolean visible) {
        boolean wasVisible = this.visible;
        this.visible = visible;
        if (!wasVisible && visible)  onOpen();
        if (wasVisible  && !visible) onClose();
    }

    public void toggle() { setVisible(!visible); }

    protected boolean isInside(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + getWidth()
                && mouseY >= y && mouseY < y + getHeight();
    }

    protected Font font() {
        return Minecraft.getInstance().font;
    }
}
