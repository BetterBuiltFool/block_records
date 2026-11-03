package com.github.betterbuiltfool.registry;

import com.github.betterbuiltfool.records.EphemeralData;
import com.github.betterbuiltfool.records.Serializable;
import com.github.betterbuiltfool.records.types.BlockDataType;
import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.records.types.EphemeralDataType;
import com.github.betterbuiltfool.records.types.SerializableDataType;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BlockRecordsRegistry {
    public static final ResourceKey<Registry<BlockDataType<?>>> BLOCK_DATA_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
            new ResourceLocation(BlockRecords.MOD_ID, "data_types")
    );
    public static final ResourceKey<Registry<SerializableDataType<?>>> SERIALIZABLE_DATA_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
            new ResourceLocation(BlockRecords.MOD_ID, "serial_data_types")
    );
    
    public static final Registry<BlockDataType<?>> DATA_TYPES = new MappedRegistry<>(
            BLOCK_DATA_TYPE_REGISTRY_KEY,
            Lifecycle.stable(),
            false);
    
    public static final Registry<SerializableDataType<?>> SERIALIZABLE_DATA_TYPES = new MappedRegistry<>(
            SERIALIZABLE_DATA_TYPE_REGISTRY_KEY,
            Lifecycle.stable(),
            false
    );
    
    public static <T extends Serializable> SerializableDataType<T> register(
            ResourceLocation id,
            SerializableDataType<T> type
    ) {
        Registry.register(DATA_TYPES, id, type);
        Registry.register(SERIALIZABLE_DATA_TYPES, id, type);
        return type;
    }
    
    public static <T extends EphemeralData> EphemeralDataType<T> register(
            ResourceLocation id,
            EphemeralDataType<T> type
    ) {
        Registry.register(DATA_TYPES, id, type);
        return type;
    }
    
    public static void init() {
        @SuppressWarnings("unchecked")
        Registry<Registry<?>> rootRegistry = (Registry<Registry<?>>) BuiltInRegistries.REGISTRY;
        
        Registry.register(rootRegistry, BLOCK_DATA_TYPE_REGISTRY_KEY.location(), DATA_TYPES);
        Registry.register(rootRegistry, SERIALIZABLE_DATA_TYPE_REGISTRY_KEY.location(), SERIALIZABLE_DATA_TYPES);
    }
}
