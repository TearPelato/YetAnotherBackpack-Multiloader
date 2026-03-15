package net.tier1234.backpack_mod.init;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.world.item.Item;
import net.tier1234.backpack_mod.Constants;
import net.tier1234.backpack_mod.api.data.BackpackTier;
import net.tier1234.backpack_mod.item.BackpackItem;

@RegistryContainer
public class ModItems {

    public static final RegistryEntry<Item> BACKPACK_LEATHER = RegistryEntry.item(Constants.id("backpack_leather"),
            () -> new BackpackItem(BackpackTier.TIER_1,new Item.Properties().stacksTo(1)));

    public static final RegistryEntry<Item> BACKPACK_IRON = RegistryEntry.item(Constants.id("backpack_iron"),
            () -> new BackpackItem(BackpackTier.TIER_2,new Item.Properties().stacksTo(1)));

    public static final RegistryEntry<Item> BACKPACK_GOLD = RegistryEntry.item(Constants.id("backpack_gold"),
            () -> new BackpackItem(BackpackTier.TIER_3,new Item.Properties().stacksTo(1)));

    public static final RegistryEntry<Item> BACKPACK_DIAMOND = RegistryEntry.item(Constants.id("backpack_diamond"),
            () -> new BackpackItem(BackpackTier.TIER_4,new Item.Properties().stacksTo(1)));

  /*  public static final RegistryEntry<Item> BACKPACK_NETHERITE = RegistryEntry.item(Constants.id("backpack_netherite"),
            () -> new BackpackItem(BackpackTier.TIER_5,new Item.Properties().stacksTo(1)));
*/




}
