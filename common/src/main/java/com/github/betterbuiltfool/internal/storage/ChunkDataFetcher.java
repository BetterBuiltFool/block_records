package com.github.betterbuiltfool.internal.storage;

import com.github.betterbuiltfool.internal.SyncedChunkData;
import net.minecraft.world.level.chunk.LevelChunk;

@FunctionalInterface
public interface ChunkDataFetcher {
    
    SyncedChunkData getSyncedData(LevelChunk chunk);
}
