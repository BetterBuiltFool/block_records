package com.github.betterbuiltfool.fabric.components;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.internal.SyncedChunkData;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.resources.ResourceLocation;

public class FabricComponentRegistry implements ChunkComponentInitializer {
    public static final ComponentKey<FabricChunkComponentWrapper> CHUNK_DATA_KEY =
            ComponentRegistry.getOrCreate(
                    new ResourceLocation(BlockRecords.MOD_ID, "chunk_data"),
                    FabricChunkComponentWrapper.class
            );
    
    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(CHUNK_DATA_KEY, chunkAccess -> new FabricChunkComponentWrapper(new SyncedChunkData()));
    }
}
