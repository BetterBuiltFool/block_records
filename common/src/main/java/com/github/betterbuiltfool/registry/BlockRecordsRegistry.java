package com.github.betterbuiltfool.registry;

import com.github.betterbuiltfool.BlockData;
import com.github.betterbuiltfool.BlockDataType;
import com.github.betterbuiltfool.BlockRecords;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class BlockRecordsRegistry {
    public static final ResourceKey<Registry<BlockDataType<?>>> BLOCK_DATA_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
            new ResourceLocation(BlockRecords.MOD_ID, "data_types")
    );
    
    public static final DeferredRegister<BlockDataType<?>> BLOCK_DATA_TYPE_REGISTER = DeferredRegister.create(
            BlockRecords.MOD_ID, BLOCK_DATA_TYPE_REGISTRY_KEY
    );
    
    public static final Registrar<BlockDataType<?>> DATA_TYPES = RegistrarManager.get(BlockRecords.MOD_ID).<BlockDataType<?>>builder(
            BLOCK_DATA_TYPE_REGISTRY_KEY.location()
    ).syncToClients().build();
    
    public static void init() {
        BLOCK_DATA_TYPE_REGISTER.register();
    }
}
