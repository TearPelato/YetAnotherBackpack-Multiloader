package net.tier1234.backpack_mod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.tier1234.backpack_mod.api.data.BackpackContents;
import net.tier1234.backpack_mod.api.data.BackpackTier;
import net.tier1234.backpack_mod.api.inventory.BackpackInventory;
import net.tier1234.backpack_mod.api.inventory.BackpackUpgradeInventory;
import net.tier1234.backpack_mod.client.menu.BackpackMenu;
import net.tier1234.backpack_mod.init.ModDataComponents;

import java.util.List;

public class BackpackItem extends Item {

    private final BackpackTier tier;

    public BackpackItem(BackpackTier tier, Properties properties) {
        super(properties.stacksTo(1));
        this.tier = tier;
    }

    public BackpackTier getTier() {
        return tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ensureComponents(stack);

            BackpackInventory inventory = new BackpackInventory(stack, tier);
            BackpackUpgradeInventory upgradeInventory = new BackpackUpgradeInventory(stack, tier);

            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return stack.getHoverName();
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
                    return new BackpackMenu(id, playerInventory, inventory, upgradeInventory);
                }
            });
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /** Makes sure a new stack has its tier component initialised. */
    private void ensureComponents(ItemStack stack) {
        if (!stack.has(ModDataComponents.BACKPACK_TIER.get())) {
            stack.set(ModDataComponents.BACKPACK_TIER.get(), tier.getIndex());
        }
        if (!stack.has(ModDataComponents.BACKPACK_CONTENTS.get())) {
            stack.set(ModDataComponents.BACKPACK_CONTENTS.get(), BackpackContents.empty(tier));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.backpackmod.tier",
                Component.translatable("tier.backpackmod." + tier.getName())));
        tooltipComponents.add(Component.translatable("tooltip.backpackmod.slots", tier.getInventorySlots()));
        tooltipComponents.add(Component.translatable("tooltip.backpackmod.upgrade_slots", tier.getUpgradeSlots()));
    }
}