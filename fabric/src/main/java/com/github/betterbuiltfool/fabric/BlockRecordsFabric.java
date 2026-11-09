package com.github.betterbuiltfool.fabric;

import com.github.betterbuiltfool.fabric.components.FabricChunkComponentWrapper;
import com.github.betterbuiltfool.fabric.components.FabricComponentRegistry;
import net.fabricmc.api.ModInitializer;

import com.github.betterbuiltfool.BlockRecords;

public final class BlockRecordsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        BlockRecords.setFetcherStrategy(chunk -> FabricComponentRegistry.CHUNK_DATA_KEY.get(chunk).getCommonStorage());

        // Run our common setup.
        BlockRecords.init();
    }
}
