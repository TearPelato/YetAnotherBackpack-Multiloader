package net.tier1234.backpack_mod.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.tier1234.backpack_mod.upgrade.UpgradeInteractionType;
import net.tier1234.backpack_mod.upgrade.popup.AbstractUpgradePopup;
import net.tier1234.backpack_mod.upgrade.widget.AbstractUpgradeWidget;

import java.util.List;

public abstract class UpgradeItem extends Item {

    public UpgradeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public abstract String getUpgradeType();

    public abstract Component getDisplayName();

    public UpgradeInteractionType getInteractionType() {
        return UpgradeInteractionType.NONE;
    }

    public void applyPassiveEffect(ItemStack upgradeStack, ItemStack backpackStack) {

    }

    public boolean isIrremovable() {
        return false;
    }

    public AbstractUpgradeWidget createWidget(
            int slotIndex,
            int screenX, int screenY,
            ItemStack upgradeStack, ItemStack backpackStack) {
        throw new UnsupportedOperationException(
                getUpgradeType() + " declares WIDGET but did not override createWidget()");
    }

    public AbstractUpgradePopup createPopup(
            Screen parentScreen,
            ItemStack upgradeStack, ItemStack backpackStack) {
        throw new UnsupportedOperationException(
                getUpgradeType() + " declares POPUP but did not override createPopup()");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.backpackmod.upgrade." + getUpgradeType()));
        tooltip.add(Component.translatable(
                "tooltip.backpackmod.upgrade.interaction."
                        + getInteractionType().name().toLowerCase()));
    }
}

