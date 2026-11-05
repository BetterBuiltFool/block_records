package com.github.betterbuiltfool.internal.network.handler;

import com.github.betterbuiltfool.internal.BlockDataPool;
import com.github.betterbuiltfool.internal.network.payload.S2CDataSyncPacket;
import com.github.betterbuiltfool.records.BlockData;
import com.github.betterbuiltfool.records.types.SerializableDataType;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class ClientPacketHandler {
    public static void handleSyncPacket(FriendlyByteBuf buffer, NetworkManager.PacketContext context) {
        S2CDataSyncPacket payload = S2CDataSyncPacket.read(buffer);
        
        context.queue(() -> {
            Level clientLevel = context.getPlayer()
                                       .level();
            SerializableDataType<?> dataType = BlockRecordsRegistry.SERIALIZABLE_DATA_TYPES.get(payload.typeID());
            
            if (dataType == null) return;
            
            BlockData data = BlockDataPool.intern(dataType.deserialize(payload.dataTag()));
            
            for (var pos:payload.posList()) {
                // TODO: set pos data on client
            }
        });
    }
}
