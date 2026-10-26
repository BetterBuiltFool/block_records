package com.github.betterbuiltfool.registry;

import com.github.betterbuiltfool.records.BlockDataType;
import com.github.betterbuiltfool.BlockRecords;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BlockRecordsRegistry {
    public static final ResourceKey<Registry<BlockDataType<?>>> BLOCK_DATA_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
            new ResourceLocation(BlockRecords.MOD_ID, "data_types")
    );
    
    public static final Registry<BlockDataType<?>> DATA_TYPES = new MappedRegistry<>(BLOCK_DATA_TYPE_REGISTRY_KEY, Lifecycle.stable(), false);
}
