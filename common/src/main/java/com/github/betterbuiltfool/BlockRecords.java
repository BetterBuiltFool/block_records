package com.github.betterbuiltfool;

import com.github.betterbuiltfool.internal.SyncedChunkData;
import com.github.betterbuiltfool.internal.network.BlockRecordsNetwork;
import com.github.betterbuiltfool.internal.storage.ChunkDataFetcher;
import com.github.betterbuiltfool.records.BlockData;
import com.github.betterbuiltfool.records.types.BlockDataType;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BlockRecords {
    public static final String MOD_ID = "block_records";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static ChunkDataFetcher fetcherStrategy = chunk -> null;
    
    @Nullable
    public static <T extends BlockData> T get(
            Level level,
            BlockPos pos,
            BlockDataType<T> dataType
    ) {
        SyncedChunkData storage = getSyncedChunkData(level, pos);
        return storage.get(pos, dataType);
    }
    
    public static <T extends BlockData> void set(
            Level level,
            BlockPos pos,
            BlockDataType<T> dataType,
            T data
    ) {
        SyncedChunkData storage = getSyncedChunkData(level, pos);
        storage.set(pos, dataType, data);
    }
    
    public static <T extends BlockData> void clear(
            Level level,
            BlockPos pos,
            BlockDataType<T> dataType
    ) {
        SyncedChunkData storage = getSyncedChunkData(level, pos);
        storage.clear(pos, dataType);
    }
    
    public static void setFetcherStrategy(ChunkDataFetcher fetcher) {
        fetcherStrategy = fetcher;
    }

    public static void init() {
        BlockRecordsRegistry.init();
        BlockRecordsNetwork.init();
    }
    
    public static SyncedChunkData getSyncedChunkData(
            Level level,
            BlockPos pos
    ) {
        LevelChunk chunk = level.getChunkAt(pos);
        return fetcherStrategy.getSyncedData(chunk);
    }
}
