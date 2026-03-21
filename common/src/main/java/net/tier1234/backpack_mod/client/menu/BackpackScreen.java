package net.tier1234.backpack_mod.client.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.tier1234.backpack_mod.Constants;
import net.tier1234.backpack_mod.api.data.BackpackTier;
import net.tier1234.backpack_mod.item.UpgradeItem;
import net.tier1234.backpack_mod.upgrade.gui.UpgradeButton;

import java.util.ArrayList;
import java.util.List;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

    private static final ResourceLocation TEX_LEATHER =
            Constants.id("textures/gui/backpack/backpack_tier_1.png");
    private static final ResourceLocation TEX_IRON =
            Constants.id("textures/gui/backpack/backpack_tier_2.png");
    private static final ResourceLocation TEX_GOLD =
            Constants.id("textures/gui/backpack/backpack_tier_3.png");
    private static final ResourceLocation TEX_UPGRADE_SLOT =
            Constants.id("textures/gui/backpack/upgrade_slot.png");

    private static final int T1_W = 175, T1_H = 165;
    private static final int T2_W = 175, T2_H = 204;
    private static final int T3_TOP_W = 211, T3_TOP_H = 133;
    private static final int T3_BOT_W = 174, T3_BOT_H = 82;
    private static final int T3_BOT_TEX_X = 19, T3_BOT_TEX_Y = 135;
    private static final int UPGRADE_SLOT_SIZE = 26;
    private static final int UPGRADE_SIDE_GAP = 4;
    private static final int SLOT_SIZE = 18;
    private static final int INVENTORY_COLS = 9;
    private static final int TIER4_VISIBLE_ROWS = 6;
    private static final int TIER4_TOTAL_ROWS = 10;
    private static final int TIER4_EXTRA_ROWS = 2;

    private final List<UpgradeButton> upgradeButtons = new ArrayList<>();
    private int invStartX, invStartY, upgradeColX, upgradeColY;
    private boolean isScrolling = false;

    public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        BackpackTier tier = menu.getTier();
        int upgradeSlots = tier.getUpgradeSlots();

        int mainPanelW = mainPanelWidth(tier);
        int sideW = upgradeSlots > 0 ? UPGRADE_SIDE_GAP + UPGRADE_SLOT_SIZE : 0;

        this.imageWidth = mainPanelW + sideW;
        this.imageHeight = menu.getGuiHeight();

        int visibleRows = (tier == BackpackTier.TIER_4) ? TIER4_VISIBLE_ROWS : menu.getInventoryRows();
        this.titleLabelY = 6;
        this.inventoryLabelY = 18 + visibleRows * SLOT_SIZE + 3;
    }

    @Override
    protected void init() {
        super.init();

        BackpackTier tier = menu.getTier();
        int rows = menu.getInventoryRows();

        invStartX = leftPos + 8;
        invStartY = topPos + 18;

        int mainW = mainPanelWidth(tier);
        upgradeColX = leftPos + mainW + UPGRADE_SIDE_GAP;
        upgradeColY = topPos + 18 + 4;

        upgradeButtons.clear();
        for (int i = 0; i < tier.getUpgradeSlots(); i++) {
            int by = upgradeColY + i * (UPGRADE_SLOT_SIZE + 2);
            UpgradeButton btn = new UpgradeButton(upgradeColX, by, i);
            upgradeButtons.add(btn);
        }

        syncUpgradeButtons();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        syncUpgradeButtons();
        upgradeButtons.forEach(UpgradeButton::tickWidget);
    }

    private void syncUpgradeButtons() {
        var upgradeInv = menu.getUpgradeInventory();
        ItemStack backpackStack = menu.getBackpackStack();

        for (int i = 0; i < upgradeButtons.size(); i++) {
            UpgradeButton btn = upgradeButtons.get(i);
            ItemStack slotStack = upgradeInv.getItem(i);

            if (!slotStack.isEmpty() && slotStack.getItem() instanceof UpgradeItem upgradeItem) {
                if (!btn.isVisible()) btn.bindUpgrade(upgradeItem, slotStack, backpackStack);
            } else {
                if (btn.isVisible()) btn.clearUpgrade();
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        BackpackTier tier = menu.getTier();
        int upgradeSlots = tier.getUpgradeSlots();

        switch (tier) {
            case TIER_1 -> g.blit(TEX_LEATHER, leftPos, topPos, 0, 0, T1_W, T1_H, 256, 256);
            case TIER_2 -> g.blit(TEX_IRON, leftPos, topPos, 0, 0, T2_W, T2_H, 256, 256);
            case TIER_3 -> {
                g.blit(TEX_GOLD, leftPos, topPos, 0, 0, T3_TOP_W, T3_TOP_H, 256, 256);
                g.blit(TEX_GOLD,
                        leftPos + T3_BOT_TEX_X, topPos + T3_TOP_H,
                        T3_BOT_TEX_X, T3_BOT_TEX_Y,
                        T3_BOT_W, T3_BOT_H, 256, 256);
            }
            case TIER_4 -> {
                g.blit(TEX_GOLD, leftPos, topPos, 0, 0, T3_TOP_W, T3_TOP_H, 256, 256);
                g.blit(TEX_GOLD,
                        leftPos + T3_BOT_TEX_X, topPos + T3_TOP_H,
                        T3_BOT_TEX_X, T3_BOT_TEX_Y,
                        T3_BOT_W, T3_BOT_H, 256, 256);
                renderTier4Scrollbar(g);
            }
        }

        if (upgradeSlots > 0) {
            for (int i = 0; i < upgradeSlots; i++) {
                int sy = upgradeColY + i * (UPGRADE_SLOT_SIZE + 2);
                g.blit(TEX_UPGRADE_SLOT,
                        upgradeColX, sy,
                        0, 0,
                        UPGRADE_SLOT_SIZE, UPGRADE_SLOT_SIZE,
                        UPGRADE_SLOT_SIZE, UPGRADE_SLOT_SIZE);
            }
        }

        for (UpgradeButton btn : upgradeButtons) {
            btn.render(g, mouseX, mouseY, partialTick);
        }
    }

    private void renderTier4Scrollbar(GuiGraphics g) {
        int scrollbarX = leftPos + T3_TOP_W - 8;
        int scrollbarY = invStartY;
        int scrollbarH = TIER4_VISIBLE_ROWS * SLOT_SIZE;

        g.fill(scrollbarX, scrollbarY, scrollbarX + 4, scrollbarY + scrollbarH, 0xFF555555);

        int thumbH = Math.max(10, scrollbarH * TIER4_VISIBLE_ROWS / TIER4_TOTAL_ROWS);
        int maxScroll = TIER4_EXTRA_ROWS;
        int offset = menu.getScrollOffset();
        int thumbY = scrollbarY + (maxScroll == 0 ? 0 : (offset * (scrollbarH - thumbH)) / maxScroll);

        g.fill(scrollbarX, thumbY, scrollbarX + 4, thumbY + thumbH, 0xFFAAAAAA);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);

        for (UpgradeButton btn : upgradeButtons) {
            btn.renderWidget(g, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu.getTier() == BackpackTier.TIER_4 && button == 0 && isOverScrollbar(mouseX, mouseY)) {
            isScrolling = true;
            return true;
        }
        for (UpgradeButton btn : upgradeButtons)
            if (btn.widgetMouseClicked(mouseX, mouseY, button)) return true;
        for (UpgradeButton btn : upgradeButtons)
            if (btn.mouseClicked(mouseX, mouseY, button, this)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isScrolling = false;
        for (UpgradeButton btn : upgradeButtons)
            if (btn.widgetMouseReleased(mouseX, mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (isScrolling && menu.getTier() == BackpackTier.TIER_4) {
            int scrollbarH = TIER4_VISIBLE_ROWS * SLOT_SIZE;
            int thumbH = Math.max(10, scrollbarH * TIER4_VISIBLE_ROWS / TIER4_TOTAL_ROWS);
            float ratio = (float) (mouseY - invStartY - thumbH / 2.0) / (scrollbarH - thumbH);
            int newOffset = Math.round(ratio * TIER4_EXTRA_ROWS);
            menu.setScrollOffset(Math.max(0, Math.min(TIER4_EXTRA_ROWS, newOffset)));
            return true;
        }
        for (UpgradeButton btn : upgradeButtons)
            if (btn.widgetMouseDragged(mouseX, mouseY, button, dx, dy)) return true;
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (menu.getTier() == BackpackTier.TIER_4 && isOverInventoryArea(mouseX, mouseY)) {
            int newOffset = menu.getScrollOffset() + (scrollY < 0 ? 1 : -1);
            menu.setScrollOffset(Math.max(0, Math.min(TIER4_EXTRA_ROWS, newOffset)));
            return true;
        }
        for (UpgradeButton btn : upgradeButtons)
            if (btn.widgetMouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (UpgradeButton btn : upgradeButtons)
            if (btn.widgetKeyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (UpgradeButton btn : upgradeButtons)
            if (btn.widgetCharTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        int scrollbarX = leftPos + T3_TOP_W - 8;
        int scrollbarH = TIER4_VISIBLE_ROWS * SLOT_SIZE;
        return mouseX >= scrollbarX && mouseX <= scrollbarX + 4
                && mouseY >= invStartY && mouseY <= invStartY + scrollbarH;
    }

    private boolean isOverInventoryArea(double mouseX, double mouseY) {
        return mouseX >= invStartX && mouseX <= invStartX + INVENTORY_COLS * SLOT_SIZE
                && mouseY >= invStartY && mouseY <= invStartY + TIER4_VISIBLE_ROWS * SLOT_SIZE;
    }

    private static int mainPanelWidth(BackpackTier tier) {
        return switch (tier) {
            case TIER_1 -> T1_W;
            case TIER_2 -> T2_W;
            case TIER_3, TIER_4 -> T3_TOP_W;
        };
    }
}