package net.tier1234.backpack_mod.init;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.tier1234.backpack_mod.Constants;

@RegistryContainer
public class ModCreativeTab {

    public static final RegistryEntry<CreativeModeTab> MAIN = RegistryEntry.creativeModeTab(Constants.id("creative_tab"), builder -> {
        builder.title(Component.translatable("itemGroup." + Constants.MOD_ID));
        builder.icon(() -> new ItemStack(ModItems.BACKPACK_LEATHER.get()));
        builder.displayItems((params, output) -> {
            output.accept(ModItems.BACKPACK_LEATHER.get());
            output.accept(ModItems.BACKPACK_IRON.get());
            output.accept(ModItems.BACKPACK_GOLD.get());
            output.accept(ModItems.BACKPACK_DIAMOND.get());


        });
    });
}
