package net.tier1234.backpack_mod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.tier1234.backpack_mod.client.menu.BackpackScreen;
import net.tier1234.backpack_mod.init.ModMenuTypes;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class BackpackModClient {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.BACKPACK_MENU.get(), BackpackScreen::new);
    }

}
