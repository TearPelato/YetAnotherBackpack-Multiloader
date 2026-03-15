package net.tier1234.backpack_mod.upgrade.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.tier1234.backpack_mod.item.UpgradeItem;
import net.tier1234.backpack_mod.upgrade.UpgradeInteractionType;
import net.tier1234.backpack_mod.upgrade.popup.AbstractUpgradePopup;
import net.tier1234.backpack_mod.upgrade.widget.AbstractUpgradeWidget;


public class UpgradeButton {

    public static final int WIDTH  = 18;
    public static final int HEIGHT = 18;


    private static final int COLOR_BORDER          = 0xFFFFD700;
    private static final int COLOR_BORDER_PASSIVE  = 0xFF888888;
    private static final int COLOR_BG_IDLE         = 0xFF2A2000;
    private static final int COLOR_BG_HOVER        = 0xFF3D3000;
    private static final int COLOR_BG_ACTIVE       = 0xFF1A3A00;
    private static final int COLOR_BG_PASSIVE      = 0xFF1E1E1E;
    private static final int COLOR_LABEL           = 0xFFFFD700;
    private static final int COLOR_LABEL_PASSIVE   = 0xFF888888;
    private static final int COLOR_TYPE_WIDGET     = 0xFF00FF88;
    private static final int COLOR_TYPE_POPUP      = 0xFF88AAFF;


    private int x;
    private int y;

    private final int slotIndex;

   // @Nullable
    private UpgradeItem upgradeItem;
    //@Nullable
    private ItemStack upgradeStack;
   // @Nullable
    private ItemStack   backpackStack;


   // @Nullable
    private AbstractUpgradeWidget boundWidget;

    private boolean visible = false;

    public UpgradeButton(int x, int y, int slotIndex) {
        this.x = x;
        this.y = y;
        this.slotIndex = slotIndex;
    }

    public void bindUpgrade(UpgradeItem item, ItemStack upgradeStack, ItemStack backpackStack) {
        this.upgradeItem   = item;
        this.upgradeStack  = upgradeStack;
        this.backpackStack = backpackStack;
        this.boundWidget   = null;
        this.visible       = true;
    }

    public void clearUpgrade() {
        if (boundWidget != null) {
            boundWidget.setVisible(false);
        }
        this.upgradeItem   = null;
        this.upgradeStack  = null;
        this.backpackStack = null;
        this.boundWidget   = null;
        this.visible       = false;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float pt) {
        if (!visible || upgradeItem == null) return;

        UpgradeInteractionType type = upgradeItem.getInteractionType();
        boolean hovered = isHovered(mouseX, mouseY);
        boolean active  = isActive();
        boolean passive = (type == UpgradeInteractionType.NONE);

        int bg;
        if      (passive) bg = COLOR_BG_PASSIVE;
        else if (active)  bg = COLOR_BG_ACTIVE;
        else if (hovered) bg = COLOR_BG_HOVER;
        else              bg = COLOR_BG_IDLE;
        graphics.fill(x, y, x + WIDTH, y + HEIGHT, bg);

        int borderColor = passive ? COLOR_BORDER_PASSIVE : COLOR_BORDER;
        graphics.fill(x,           y,            x + WIDTH, y + 1,       borderColor);
        graphics.fill(x,           y + HEIGHT-1, x + WIDTH, y + HEIGHT,  borderColor);
        graphics.fill(x,           y,            x + 1,     y + HEIGHT,  borderColor);
        graphics.fill(x + WIDTH-1, y,            x + WIDTH, y + HEIGHT,  borderColor);

        Font font = Minecraft.getInstance().font;

        String abbrev = abbreviate(upgradeItem.getDisplayName().getString());
        int labelColor = passive ? COLOR_LABEL_PASSIVE : COLOR_LABEL;
        graphics.drawString(font, abbrev, x + 5, y + 5, labelColor, false);

        if (!passive) {
            int dotColor = (type == UpgradeInteractionType.WIDGET)
                    ? COLOR_TYPE_WIDGET : COLOR_TYPE_POPUP;
            graphics.fill(x + WIDTH - 4, y + 1, x + WIDTH - 1, y + 4, dotColor);
        }

        if (hovered) {
            graphics.renderTooltip(font,
                    java.util.List.of(
                            upgradeItem.getDisplayName(),
                            Component.translatable("tooltip.backpackmod.upgrade.click_hint."
                                    + type.name().toLowerCase())
                    ),
                    java.util.Optional.empty(),
                    mouseX, mouseY);
        }
    }

    private static String abbreviate(String name) {
        return name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button,
                                Screen parentScreen) {
        if (!visible || upgradeItem == null) return false;
        if (button != 0) return false;                       // left-click only
        if (!isHovered(mouseX, mouseY))      return false;

        UpgradeInteractionType type = upgradeItem.getInteractionType();

        switch (type) {
            case NONE -> { /* passive — consume click silently */ return true; }

            case WIDGET -> {
                ensureWidget(parentScreen);
                if (boundWidget != null) boundWidget.toggle();
                return true;
            }

            case POPUP -> {
                AbstractUpgradePopup popup = upgradeItem.createPopup(
                        parentScreen, upgradeStack, backpackStack);
                Minecraft.getInstance().setScreen(popup);
                return true;
            }
        }
        return false;
    }

    // ----------------------------------------------------------------
    //  Widget delegation (BackpackScreen forwards these)
    // ----------------------------------------------------------------

    public boolean widgetMouseClicked(double mx, double my, int btn) {
        return boundWidget != null && boundWidget.mouseClicked(mx, my, btn);
    }
    public boolean widgetMouseReleased(double mx, double my, int btn) {
        return boundWidget != null && boundWidget.mouseReleased(mx, my, btn);
    }
    public boolean widgetMouseDragged(double mx, double my, int btn, double dx, double dy) {
        return boundWidget != null && boundWidget.mouseDragged(mx, my, btn, dx, dy);
    }
    public boolean widgetMouseScrolled(double mx, double my, double sx, double sy) {
        return boundWidget != null && boundWidget.mouseScrolled(mx, my, sx, sy);
    }
    public boolean widgetKeyPressed(int kc, int sc, int mod) {
        return boundWidget != null && boundWidget.keyPressed(kc, sc, mod);
    }
    public boolean widgetCharTyped(char c, int mod) {
        return boundWidget != null && boundWidget.charTyped(c, mod);
    }

    /** Render the bound widget (call from BackpackScreen.render(), after super). */
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pt) {
        if (boundWidget != null) boundWidget.render(graphics, mouseX, mouseY, pt);
    }

    public void tickWidget() {
        if (boundWidget != null && boundWidget.isVisible()) boundWidget.tick();
    }

    // ----------------------------------------------------------------
    //  Helpers
    // ----------------------------------------------------------------

    private boolean isHovered(double mx, double my) {
        return mx >= x && mx < x + WIDTH && my >= y && my < y + HEIGHT;
    }

    private boolean isActive() {
        return boundWidget != null && boundWidget.isVisible();
    }

    private void ensureWidget(Screen parentScreen) {
        if (boundWidget == null && upgradeItem != null) {
            // Position widget just to the right of this button
            int wX = x + WIDTH + 2;
            int wY = y;
            boundWidget = upgradeItem.createWidget(slotIndex, wX, wY, upgradeStack, backpackStack);
        }
    }

    // ----------------------------------------------------------------
    //  Position update (call if screen is resized)
    // ----------------------------------------------------------------

    public void setPosition(int x, int y) {
        int dx = x - this.x;
        int dy = y - this.y;
        this.x = x;
        this.y = y;
        if (boundWidget != null) {
            boundWidget.setPosition(boundWidget.getX() + dx, boundWidget.getY() + dy);
        }
    }

    // ----------------------------------------------------------------
    //  Getters
    // ----------------------------------------------------------------

    public int getSlotIndex() { return slotIndex; }
    public boolean isVisible() { return visible; }
   // @Nullable
    public AbstractUpgradeWidget getBoundWidget() { return boundWidget; }
}

