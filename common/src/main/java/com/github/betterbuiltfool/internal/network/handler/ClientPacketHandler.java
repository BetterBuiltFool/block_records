package com.github.betterbuiltfool.internal.network.handler;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.internal.SyncedChunkData;
import com.github.betterbuiltfool.internal.network.payload.DataClear;
import com.github.betterbuiltfool.internal.network.payload.DataSync;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class ClientPacketHandler {
    public static void handleSyncPacket(DataSync payload, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        
        context.queue(() -> {
            Level clientLevel = context.getPlayer()
                                       .level();
            if (!clientLevel.isClientSide()) return;
            
            List<BlockPos> posList = payload.posList();
            if (posList.isEmpty()) {
                BlockRecords.LOGGER.error("Attempted to sync client data of type {} with no positions", payload.typeID());
                return;
            }
            // Assumption: All positions in a given packet will share a chunk, so we can just use the first position
            SyncedChunkData storage = BlockRecords.getSyncedChunkData(clientLevel, posList.get(0));
            storage.receiveSyncPacket(payload);
        });
    }
    
    public static void handleClearPacket(DataClear payload, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        
        context.queue(() -> {
            Level clientLevel = context.getPlayer()
                                       .level();
            if (!clientLevel.isClientSide()) return;
            
            
            List<BlockPos> posList = payload.posList();
            if (posList.isEmpty()) {
                BlockRecords.LOGGER.error("Attempted to clear client data of type {} with no positions", payload.typeID());
                return;
            }
            SyncedChunkData storage = BlockRecords.getSyncedChunkData(clientLevel, posList.get(0));
            storage.receiveClearPacket(payload);
        });
    }
}
