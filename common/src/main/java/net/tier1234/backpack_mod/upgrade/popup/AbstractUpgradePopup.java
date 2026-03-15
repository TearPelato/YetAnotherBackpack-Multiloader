package net.tier1234.backpack_mod.upgrade.popup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractUpgradePopup extends Screen {

    protected final Screen parentScreen;

    protected final ItemStack upgradeStack;
    protected final ItemStack backpackStack;

    protected int popupX;
    protected int popupY;

    protected static final int COLOR_OVERLAY     = 0xAA000000;
    protected static final int COLOR_BG          = 0xFF3C3C3C;
    protected static final int COLOR_BORDER      = 0xFFFFD700;
    protected static final int COLOR_TITLE_BG    = 0xFF1A1A1A;
    protected static final int COLOR_TITLE_TEXT  = 0xFFFFFFFF;

    protected AbstractUpgradePopup(Screen parentScreen, Component title,
                                   ItemStack upgradeStack, ItemStack backpackStack) {
        super(title);
        this.parentScreen  = parentScreen;
        this.upgradeStack  = upgradeStack;
        this.backpackStack = backpackStack;
    }

    public abstract int getPopupWidth();

    public abstract int getPopupHeight();

    protected abstract void initPopupWidgets(int ox, int oy);

    protected abstract void renderPopupContent(GuiGraphics graphics, int mouseX, int mouseY, float pt);

    @Override
    protected final void init() {
        popupX = (width  - getPopupWidth())  / 2;
        popupY = (height - getPopupHeight()) / 2;

        int doneW = 60;
        int doneX = popupX + (getPopupWidth() - doneW) / 2;
        int doneY = popupY + getPopupHeight() - 24;
        addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                btn -> onClose()
        ).bounds(doneX, doneY, doneW, 20).build());

        initPopupWidgets(popupX, popupY);
    }

    protected void saveAndClose() {
        onClose();
    }

    @Override
    public void onClose() {
        // Return to the parent screen (the BackpackScreen)
        Minecraft.getInstance().setScreen(parentScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (parentScreen != null) {
            parentScreen.render(graphics, -1, -1, partialTick);
        }

        graphics.fill(0, 0, width, height, COLOR_OVERLAY);

        graphics.fill(popupX,     popupY,
                popupX + getPopupWidth(), popupY + getPopupHeight(),
                COLOR_BG);

        int pw = getPopupWidth();
        int ph = getPopupHeight();
        graphics.fill(popupX,          popupY,          popupX + pw,     popupY + 1,      COLOR_BORDER);
        graphics.fill(popupX,          popupY + ph - 1, popupX + pw,     popupY + ph,     COLOR_BORDER);
        graphics.fill(popupX,          popupY,          popupX + 1,      popupY + ph,     COLOR_BORDER);
        graphics.fill(popupX + pw - 1, popupY,          popupX + pw,     popupY + ph,     COLOR_BORDER);

        int titleBarBottom = popupY + 16;
        graphics.fill(popupX + 1, popupY + 1, popupX + pw - 1, titleBarBottom, COLOR_TITLE_BG);
        graphics.drawCenteredString(font, title, popupX + pw / 2, popupY + 4, COLOR_TITLE_TEXT);

        renderPopupContent(graphics, mouseX, mouseY, partialTick);

        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
