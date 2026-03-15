package net.tier1234.backpack_mod.init;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.tier1234.backpack_mod.Constants;
import net.tier1234.backpack_mod.client.menu.BackpackMenu;

@RegistryContainer
public class ModMenuTypes {

    public static final RegistryEntry<MenuType<BackpackMenu>> BACKPACK_MENU =
            RegistryEntry.menuType(Constants.id("backpack_menu"), BackpackMenu::new);
}