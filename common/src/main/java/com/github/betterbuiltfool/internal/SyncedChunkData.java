package com.github.betterbuiltfool.internal;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.internal.network.dispatch.ClientboundDispatcher;
import com.github.betterbuiltfool.internal.network.payload.DataClear;
import com.github.betterbuiltfool.internal.network.payload.DataSync;
import com.github.betterbuiltfool.records.BlockData;
import com.github.betterbuiltfool.records.PersistentData;
import com.github.betterbuiltfool.records.types.BlockDataType;
import com.github.betterbuiltfool.records.ClientSyncable;
import com.github.betterbuiltfool.records.Serializable;
import com.github.betterbuiltfool.records.types.SerializableDataType;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SyncedChunkData {
    
    public static final String CHUNK_DATA_ID = BlockRecords.MOD_ID + ":chunk_data";
    public static final String TYPE_ID = BlockRecords.MOD_ID + ":type_id";
    public static final String POSITIONS_ID = BlockRecords.MOD_ID + ":positions";
    
    @Nullable
    private transient ServerLevel level;
    
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
        
        if (!(data instanceof ClientSyncable syncableData)) return;
        if (!(dataType instanceof SerializableDataType<?> serialType)) return;
        sendUpdatePacket(syncableData, serialType, pos);
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
        if (!(data instanceof ClientSyncable syncableData)) return;
        if (!(dataType instanceof SerializableDataType<?> serialType)) return;
        sendUpdatePacket(syncableData, serialType, posList.toArray(BlockPos[]::new));
    }
    
    private void sendUpdatePacket(
            ClientSyncable data,
            SerializableDataType<?> dataType,
            BlockPos... array
    ) {
        if (this.level == null) return;
        
        if (array.length == 0) {
            BlockRecords.LOGGER.error("Cannot send data sync for zero positions");
            return;
        }
        
        List<ServerPlayer> players = level.players();
        
        for (var player:players) {
            ClientboundDispatcher.dispatchSyncPacket(data, dataType, player, array);
        }
    }
    
    public void sendUpdatePackets(
            ServerPlayer player
    ) {
        for (var entry: collectSyncableData().entrySet()) {
            SerializableDataType<?> dataType = entry.getKey();
            for (var value : entry.getValue().entrySet()) {
                ClientSyncable data = value.getKey();
                List<BlockPos> posList = value.getValue();
                ClientboundDispatcher.dispatchSyncPacket(data, dataType, player, posList.toArray(BlockPos[]::new));
            }
        }
    }
    
    public void receiveSyncPacket(DataSync payload) {
        ResourceLocation typeID = payload.typeID();
        List<BlockPos> posList = payload.posList();
        CompoundTag dataTag = payload.dataTag();
        
        SerializableDataType<?> dataType = BlockRecordsRegistry.SERIALIZABLE_DATA_TYPES.get(typeID);
        if (dataType == null) {
            warnUnregisteredType(typeID);
            return;
        }
        
        BlockData data = BlockDataPool.intern(dataType.deserialize(dataTag));
        
        for (BlockPos pos:posList) {
            setRaw(pos, dataType, data);
        }
    }
    
    private static void warnUnregisteredType(ResourceLocation typeID) {
        BlockRecords.LOGGER.error("Received packet with unregistered type {}", typeID);
    }
    
    public synchronized void clear(BlockPos pos, BlockDataType<?> dataType) {
        var dataMap = spatialMap.get(pos);
        if (dataMap == null) return;
        
        purgeOldData(pos, dataType, dataMap);
        if (!(dataType instanceof SerializableDataType<?> serialType)) return;
        sendClearPacket(pos, serialType);
    }
    
    private void sendClearPacket(
            BlockPos pos,
            SerializableDataType<?> dataType
    ) {
        if (this.level == null) return;
        
        List<ServerPlayer> players = level.players();
        
        for (var player:players) {
            ClientboundDispatcher.dispatchClearPacket(dataType, player, pos);
        }
    
    }
    
    public void receiveClearPacket(DataClear payload) {
        ResourceLocation typeID = payload.typeID();
        List<BlockPos> posList = payload.posList();
        
        SerializableDataType<?> dataType = BlockRecordsRegistry.SERIALIZABLE_DATA_TYPES.get(typeID);
        if (dataType == null) {
            warnUnregisteredType(typeID);
            return;
        }
        for (BlockPos pos:posList) {
            var dataMap = spatialMap.get(pos);
            if (dataMap == null) return;
            
            purgeOldData(pos, dataType, dataMap);
        }
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
    
    public void setServerLevel(ServerLevel level) {
        this.level = level;
    }
    
    public void clearServerLevel(ServerLevel level) {
        if (this.level == level) {
            this.level = null;
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
        if (typePositions.isEmpty()) {
            typeMap.remove(dataType);
        }
    }
    
    /**
     * Converts the chunk data into serialized NBT
     * Ephemeral data is ignored
     * @param root The root tag the chunk is being added to.
     */
    public void serialize(CompoundTag root) {
        ListTag chunkDataTag = new ListTag();
        Map<SerializableDataType<?>, Map<PersistentData, List<BlockPos>>> uniqueRecords = collectUniqueRecords();
        for (var typeEntry:uniqueRecords.entrySet()) {
            SerializableDataType<?> dataType = typeEntry.getKey();
            
            ResourceLocation id = BlockRecordsRegistry.SERIALIZABLE_DATA_TYPES.getKey(dataType);
            
            if (id == null) continue;
            
            for (var dataEntry:typeEntry.getValue().entrySet()) {
                Serializable uniqueRecord = dataEntry.getKey();
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
    
    private Map<SerializableDataType<?>, Map<ClientSyncable, List<BlockPos>>> collectSyncableData() {
        Map<SerializableDataType<?>, Map<ClientSyncable, List<BlockPos>>> syncablesMap = new HashMap<>();
        for (var entry:spatialMap.entrySet()) {
            var pos = entry.getKey();
            var typeMap = entry.getValue();
            for (var typeEntry:typeMap.entrySet()) {
                if (!(typeEntry.getValue() instanceof ClientSyncable dataRecord)) continue;
                if (!((typeEntry.getKey()) instanceof SerializableDataType<?> dataType)) continue;
                Map<ClientSyncable, List<BlockPos>> map = syncablesMap.computeIfAbsent(dataType, key -> new HashMap<>());
                List<BlockPos> posList = map.computeIfAbsent(dataRecord, key -> new ArrayList<>());
                posList.add(pos);
            }
        }
        return syncablesMap;
    }
    
    /**
     * Collapses the record data into a map with each unique record only appearing once, to save disk space.
     * The unique records will have a list of shared positions
     * Only records tagged to be saved to disk will be included
     * @return A Map that contains the unique record instances with their positions, mapped to their type.
     */
    private Map<SerializableDataType<?>, Map<PersistentData, List<BlockPos>>> collectUniqueRecords() {
        Map<SerializableDataType<?>, Map<PersistentData, List<BlockPos>>> uniquesMap = new HashMap<>();
        for (var entry:spatialMap.entrySet()) {
            var pos = entry.getKey();
            var typeMap = entry.getValue();
            for (var typeEntry:typeMap.entrySet()) {
                if (!(typeEntry.getValue() instanceof PersistentData dataRecord)) continue;
                if (!((typeEntry.getKey()) instanceof SerializableDataType<?> dataType)) continue;
                Map<PersistentData, List<BlockPos>> map = uniquesMap.computeIfAbsent(dataType, key -> new HashMap<>());
                List<BlockPos> posList = map.computeIfAbsent(dataRecord, key -> new ArrayList<>());
                posList.add(pos);
            }
        }
        return uniquesMap;
    }
    
    public static SyncedChunkData deserialize(CompoundTag root) {
        SyncedChunkData newChunkData = new SyncedChunkData();
        Map<String, SerializableDataType<?>> loadedTypes = new HashMap<>();
        if (!root.contains(CHUNK_DATA_ID)) return newChunkData;
        
        ListTag chunkDataTag = root.getList(CHUNK_DATA_ID, ListTag.TAG_COMPOUND);
        
        for (Tag tag:chunkDataTag) {
            CompoundTag compositeTag = (CompoundTag) tag;
            if (!compositeTag.contains("metadata")) continue;
            if (!compositeTag.contains("payload")) continue;
            
            CompoundTag metaData = compositeTag.getCompound("metadata");
            SerializableDataType<?> dataType = getDataType(metaData, loadedTypes);
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
    
    private static @Nullable SerializableDataType<?> getDataType(
            CompoundTag dataTag,
            Map<String, SerializableDataType<?>> loadedTypes
    ) {
        if (!dataTag.contains(TYPE_ID)) return null;
        
        String type_id = dataTag.getString(TYPE_ID);
        return loadedTypes.computeIfAbsent(type_id, key -> {
            ResourceLocation type_location = ResourceLocation.tryParse(key);
            if (type_location == null) return null;
            
            return BlockRecordsRegistry.SERIALIZABLE_DATA_TYPES.get(type_location);
        });
    }
}
