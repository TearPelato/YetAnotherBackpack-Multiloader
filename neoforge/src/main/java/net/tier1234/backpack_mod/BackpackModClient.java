package net.tier1234.backpack_mod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class BackpackModClient {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {


    }
}
