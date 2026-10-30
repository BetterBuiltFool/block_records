package com.github.betterbuiltfool.internal;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.records.BlockData;
import com.github.betterbuiltfool.records.BlockDataType;
import com.github.betterbuiltfool.records.ClientSyncableBlockData;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SyncedChunkData {
    
    public static final String CHUNK_DATA_ID = BlockRecords.MOD_ID + ":chunk_data";
    public static final String TYPE_ID = BlockRecords.MOD_ID + ":type_id";
    public static final String POSITIONS_ID = BlockRecords.MOD_ID + ":positions";
    
    final Map<BlockPos, Map<BlockDataType<?>, BlockData>> spatialMap = new ConcurrentHashMap<>();
    
    final Map<BlockDataType<?>, Set<BlockPos>> typeMap = new Reference2ObjectOpenHashMap<>();
    
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
    
    public synchronized void set(
            @NotNull BlockPos pos,
            @NotNull BlockDataType<?> dataType,
            BlockData data
    ) {
        if (data == null) return;
        
        setRaw(pos, dataType, data);
        
        if (!(data instanceof ClientSyncableBlockData syncableData)) return;
        sendUpdatePacket(syncableData, dataType, pos);
    }
    
    @SuppressWarnings("unchecked")
    private <T extends BlockData> void setRaw(
            @NotNull BlockPos pos,
            @NotNull BlockDataType<?> dataType,
            BlockData data
    ) {
        var dataMap = spatialMap.computeIfAbsent(pos, key -> new ConcurrentHashMap<>());
        
        purgeOldData(pos, dataType, dataMap);
        
        T canonical = BlockDataPool.intern((T) data);
        
        dataMap.put(dataType, canonical);
        
        var typePositions = typeMap.computeIfAbsent(dataType, key -> new HashSet<>());
        
        typePositions.add(pos);
    }
    public synchronized void set(
            @NotNull List<BlockPos> posList,
            @NotNull BlockDataType<?> dataType,
            BlockData data
    ) {
        if (data == null) return;
        
        for (BlockPos pos:posList) {
            
            setRaw(pos, dataType, data);
        }
        if (!(data instanceof ClientSyncableBlockData syncableData)) return;
        sendUpdatePacket(syncableData, dataType, posList.toArray(BlockPos[]::new));
    }
    
    private void sendUpdatePacket(
            ClientSyncableBlockData data,
            BlockDataType<?> dataType,
            BlockPos... array
    ) {
        // Send a packet to the client with the data
    }
    
    public synchronized void clear(BlockPos pos, BlockDataType<?> dataType) {
        var dataMap = spatialMap.get(pos);
        if (dataMap == null) return;
        
        purgeOldData(pos, dataType, dataMap);
        sendClearPacket(pos, dataType);
    }
    
    private void sendClearPacket(
            BlockPos pos,
            BlockDataType<?> dataType
    ) {
        if (!ClientSyncableBlockData.class.isAssignableFrom(dataType.getRegisteredType())) return;
    
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
            ResourceLocation id = BlockRecordsRegistry.DATA_TYPES.getKey(dataType);
            
            if (id == null) continue;
            
            for (var dataEntry:typeEntry.getValue().entrySet()) {
                BlockData uniqueRecord = dataEntry.getKey();
                List<BlockPos> posList = dataEntry.getValue();
                CompoundTag metaData = new CompoundTag();
                
                CompoundTag dataTag = new CompoundTag();
                metaData.putString(TYPE_ID, id.toString());
                
                ListTag tagList = new ListTag();
                for (BlockPos pos:posList) {
                    tagList.add(NbtUtils.writeBlockPos(pos));
                }
                metaData.put(POSITIONS_ID, tagList);
                
                uniqueRecord.toNbt(dataTag);
                CompoundTag compositeData = new CompoundTag();
                compositeData.put("payload", dataTag);
                compositeData.put("metadata", metaData);
                chunkDataTag.add(compositeData);
            }
        }
        root.put(CHUNK_DATA_ID, chunkDataTag);
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
    
    public static SyncedChunkData deserialize(CompoundTag root) {
        SyncedChunkData newChunkData = new SyncedChunkData();
        Map<String, BlockDataType<?>> loadedTypes = new HashMap<>();
        if (!root.contains(CHUNK_DATA_ID)) return newChunkData;
        
        ListTag chunkDataTag = root.getList(CHUNK_DATA_ID, ListTag.TAG_COMPOUND);
        
        for (Tag tag:chunkDataTag) {
            CompoundTag compositeTag = (CompoundTag) tag;
            if (!compositeTag.contains("metadata")) continue;
            if (!compositeTag.contains("payload")) continue;
            
            CompoundTag metaData = compositeTag.getCompound("metadata");
            BlockDataType<?> dataType = getDataType(metaData, loadedTypes);
            if (dataType == null) continue;
            
            ListTag posListTag = metaData.getList(POSITIONS_ID, ListTag.TAG_COMPOUND);
            List<BlockPos> posList = getPosList(posListTag);
            if (posList.isEmpty()) continue;
            
            CompoundTag dataTag = compositeTag.getCompound("payload");
            BlockData blockData = dataType.deserialize(dataTag);
            if (blockData == null) continue;
            newChunkData.set(posList, dataType, blockData);
        }
        
        return newChunkData;
    }
    
    private static List<BlockPos> getPosList(ListTag posListTag) {
        List<BlockPos> posList = new ArrayList<>();
        for (Tag tag:posListTag) {
            CompoundTag posTag = (CompoundTag) tag;
            BlockPos pos = NbtUtils.readBlockPos(posTag);
            posList.add(pos);
        }
        return posList;
    }
    
    private static @Nullable BlockDataType<?> getDataType(
            CompoundTag dataTag,
            Map<String, BlockDataType<?>> loadedTypes
    ) {
        if (!dataTag.contains(TYPE_ID)) return null;
        
        String type_id = dataTag.getString(TYPE_ID);
        return loadedTypes.computeIfAbsent(type_id, key -> {
            ResourceLocation type_location = ResourceLocation.tryParse(key);
            if (type_location == null) return null;
            
            return BlockRecordsRegistry.DATA_TYPES.get(type_location);
        });
    }
    
}
