package net.tier1234.backpack_mod.init;

import com.mojang.serialization.Codec;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.tier1234.backpack_mod.Constants;
import net.tier1234.backpack_mod.api.data.BackpackContents;

@RegistryContainer
public class ModDataComponents {

    public static final RegistryEntry<DataComponentType<BackpackContents>> BACKPACK_CONTENTS = RegistryEntry.dataComponentType(Constants.id("backpack_contents"), builder -> {
        return builder.persistent(BackpackContents.CODEC).networkSynchronized(BackpackContents.STREAM_CODEC);
    });

    public static final RegistryEntry<DataComponentType<Integer>> BACKPACK_TIER = RegistryEntry.dataComponentType(Constants.id("backpack_tier"), builder -> {
        return builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT);
    });

}
