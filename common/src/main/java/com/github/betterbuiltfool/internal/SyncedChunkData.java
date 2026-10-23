package com.github.betterbuiltfool.internal;

import com.github.betterbuiltfool.records.BlockData;
import com.github.betterbuiltfool.records.BlockDataType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SyncedChunkData {
    
    private final Map<BlockPos, Map<BlockDataType<?>, BlockData>> spatialMap = new ConcurrentHashMap<>();
    
    private final Map<BlockDataType<?>, Set<BlockPos>> typeMap = new Reference2ObjectOpenHashMap<>();
    
    @SuppressWarnings("unchecked")
    public <T extends BlockData> @Nullable T get(BlockPos pos, BlockDataType<T> dataType) {
        var dataMap = get(pos);
        if (dataMap == null) return null;
        var data = dataMap.get(dataType);
        if (data == null) return null;
        return (T) data;
    }
    
    public Map<BlockDataType<?>, BlockData> get(BlockPos pos) {
        return spatialMap.get(pos);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized <T extends BlockData> void set(
            @NotNull BlockPos pos,
            @NotNull BlockDataType<?> dataType,
            BlockData data
    ) {
        if (data == null) return;
        
        var dataMap = spatialMap.computeIfAbsent(pos, key -> new ConcurrentHashMap<>());
        
        purgeOldData(pos, dataType, dataMap);
        
        T canonical = BlockDataPool.intern((T) data);
        
        dataMap.put(dataType, canonical);
        
        var typePositions = typeMap.get(dataType);
        if (typePositions == null) return;
        
        typePositions.add(pos);
    }
    
    public synchronized void clear(BlockPos pos, BlockDataType<?> dataType) {
        var dataMap = spatialMap.get(pos);
        if (dataMap == null) return;
        
        purgeOldData(pos, dataType, dataMap);
    }
    
    public synchronized <T extends BlockData> void collectPositionsForType(
            BlockDataType<T> dataType,
            Map<BlockPos, T> outMap
    ) {
        var typePositions = typeMap.get(dataType);
        if (typePositions == null) return;
        
        for (var pos:typePositions) {
            var data = get(pos, dataType);
            if (data == null) continue;
            outMap.put(pos, data);
        }
        
    }
    
    private void purgeOldData(@NotNull BlockPos pos,
                           @NotNull BlockDataType<?> dataType,
                           Map<BlockDataType<?>, BlockData> dataMap
    ) {
        var oldData = dataMap.get(dataType);
        if (oldData == null)  return;
        
        var typePositions = typeMap.get(dataType);
        if (typePositions == null) return;
        
        dataMap.remove(dataType);
        typePositions.remove(pos);
    }
    
}
