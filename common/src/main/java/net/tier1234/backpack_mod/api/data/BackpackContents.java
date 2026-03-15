package net.tier1234.backpack_mod.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record BackpackContents(List<ItemStack> inventoryItems,List<ItemStack> upgradeItems) {

    public static final BackpackContents EMPTY = new BackpackContents(List.of(), List.of());

    private record SlotEntry(int slot, ItemStack stack) {
        static final Codec<SlotEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("slot").forGetter(SlotEntry::slot),
                        ItemStack.STRICT_CODEC.fieldOf("item").forGetter(SlotEntry::stack)
                ).apply(instance, SlotEntry::new)
        );
    }

    public static final Codec<BackpackContents> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(SlotEntry.CODEC).fieldOf("inventory").forGetter(bc -> toEntries(bc.inventoryItems)),
                    Codec.list(SlotEntry.CODEC).fieldOf("upgrades").forGetter(bc -> toEntries(bc.upgradeItems))
            ).apply(instance, (invEntries, upgEntries) ->
                    new BackpackContents(fromEntries(invEntries), fromEntries(upgEntries))
            )
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackContents> STREAM_CODEC =
            StreamCodec.of(
                    (buf, contents) -> {
                        ByteBufCodecs.fromCodec(CODEC).encode(buf, contents);
                    },
                    buf -> ByteBufCodecs.fromCodec(CODEC).decode(buf)
            );

    private static List<SlotEntry> toEntries(List<ItemStack> stacks) {
        List<SlotEntry> entries = new ArrayList<>();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (!stack.isEmpty()) {
                entries.add(new SlotEntry(i, stack.copy()));
            }
        }
        return entries;
    }

    private static List<ItemStack> fromEntries(List<SlotEntry> entries) {
        if (entries.isEmpty()) return List.of();

        int maxSlot = entries.stream().mapToInt(SlotEntry::slot).max().orElse(0);
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(maxSlot + 1, ItemStack.EMPTY));
        for (SlotEntry e : entries) {
            if (e.slot() >= 0 && e.slot() < stacks.size()) {
                stacks.set(e.slot(), e.stack().copy());
            }
        }
        return stacks;
    }

    public static BackpackContents empty(BackpackTier tier) {
        List<ItemStack> inv = new ArrayList<>();
        for (int i = 0; i < tier.getInventorySlots(); i++) inv.add(ItemStack.EMPTY);

        List<ItemStack> upg = new ArrayList<>();
        for (int i = 0; i < tier.getUpgradeSlots(); i++) upg.add(ItemStack.EMPTY);

        return new BackpackContents(inv, upg);
    }

    public static BackpackContents of(NonNullList<ItemStack> inventory, NonNullList<ItemStack> upgrades) {
        return new BackpackContents(
                inventory.stream().map(ItemStack::copy).toList(),
                upgrades.stream().map(ItemStack::copy).toList()
        );
    }

    public NonNullList<ItemStack> getMutableInventory(BackpackTier tier) {
        NonNullList<ItemStack> list = NonNullList.withSize(tier.getInventorySlots(), ItemStack.EMPTY);
        for (int i = 0; i < Math.min(inventoryItems.size(), list.size()); i++) {
            ItemStack s = inventoryItems.get(i);
            list.set(i, s.isEmpty() ? ItemStack.EMPTY : s.copy());
        }
        return list;
    }

    public NonNullList<ItemStack> getMutableUpgrades(BackpackTier tier) {
        NonNullList<ItemStack> list = NonNullList.withSize(tier.getUpgradeSlots(), ItemStack.EMPTY);
        for (int i = 0; i < Math.min(upgradeItems.size(), list.size()); i++) {
            ItemStack s = upgradeItems.get(i);
            list.set(i, s.isEmpty() ? ItemStack.EMPTY : s.copy());
        }
        return list;
    }
}