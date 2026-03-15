package net.tier1234.backpack_mod;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.tier1234.backpack_mod.client.menu.BackpackScreen;
import net.tier1234.backpack_mod.init.ModMenuTypes;

public class BackpackModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenuTypes.BACKPACK_MENU.get(), BackpackScreen::new);

    }
}
