package com.github.betterbuiltfool.internal;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.records.BlockData;
import com.github.betterbuiltfool.records.BlockDataType;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
        
        var typePositions = typeMap.computeIfAbsent(dataType, key -> new HashSet<>());
        
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
    
    /**
     * Converts the chunk data into serialized NBT
     * @param root The root tag the chunk is being added to.
     */
    public void serialize(CompoundTag root) {
        ListTag chunkDataTag = new ListTag();
        Map<BlockDataType<?>, Map<BlockData, List<BlockPos>>> uniqueRecords = collectUniqueRecords();
        for (var typeEntry:uniqueRecords.entrySet()) {
            BlockDataType<?> dataType = typeEntry.getKey();
            ResourceLocation id = BlockRecordsRegistry.DATA_TYPES.getId(dataType);
            
            if (id == null) continue;
            
            for (var dataEntry:typeEntry.getValue().entrySet()) {
                BlockData uniqueRecord = dataEntry.getKey();
                List<BlockPos> posList = dataEntry.getValue();
                
                CompoundTag dataTag = new CompoundTag();
                dataTag.putString(BlockRecords.MOD_ID + ":type_id", id.toString());
                
                ListTag tagList = new ListTag();
                for (BlockPos pos:posList) {
                    tagList.add(NbtUtils.writeBlockPos(pos));
                }
                dataTag.put(BlockRecords.MOD_ID + ":positions", tagList);
                
                uniqueRecord.toNbt(dataTag);
                chunkDataTag.add(dataTag);
            }
        }
        root.put(BlockRecords.MOD_ID + ":chunk_data", chunkDataTag);
    }
    
    /**
     * Collapses the record data into a map with each unique record only appearing once, to save disk space.
     * The unique records will have a list of shared positions
     * @return A Map that contains the unique record instances with their positions, mapped to their type.
     */
    private Map<BlockDataType<?>, Map<BlockData, List<BlockPos>>> collectUniqueRecords() {
        Map<BlockDataType<?>, Map<BlockData, List<BlockPos>>> uniquesMap = new HashMap<>();
        for (var entry:spatialMap.entrySet()) {
            var pos = entry.getKey();
            var typeMap = entry.getValue();
            for (var typeEntry:typeMap.entrySet()) {
                Map<BlockData, List<BlockPos>> map = uniquesMap.computeIfAbsent(typeEntry.getKey(), key -> new HashMap<>());
                List<BlockPos> posList = map.computeIfAbsent(typeEntry.getValue(), key -> new ArrayList<>());
                posList.add(pos);
            }
        }
        return uniquesMap;
    }
    
}
