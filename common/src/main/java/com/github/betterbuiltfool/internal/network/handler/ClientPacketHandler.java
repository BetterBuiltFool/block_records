package com.github.betterbuiltfool.internal.network.handler;

import com.github.betterbuiltfool.internal.network.payload.S2CDataSyncPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.List;

public class ClientPacketHandler {
    public static void handleSyncPacket(FriendlyByteBuf buffer, NetworkManager.PacketContext context) {
        S2CDataSyncPacket payload = S2CDataSyncPacket.read(buffer);
        
        context.queue(() -> {
            Level clientLevel = context.getPlayer()
                                       .level();
            if (!clientLevel.isClientSide()) return;
            // TODO
            // Find or create the SyncedChunkStorage for the positions
            List<BlockPos> posList = payload.posList();
            if (posList.isEmpty()) return;
            // Assumption: All positions in a given packet will share a chunk, so we can just use the first position
            LevelChunk chunk = clientLevel.getChunkAt(posList.get(0));
            // storage = getStorageAt(chunk);
            // storage.receiveSyncPacket(payload);
        });
    }
}
