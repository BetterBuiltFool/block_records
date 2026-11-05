package com.github.betterbuiltfool.internal.network.dispatch;

import com.github.betterbuiltfool.internal.network.payload.S2CDataSyncPacket;
import com.github.betterbuiltfool.records.ClientSyncable;
import com.github.betterbuiltfool.records.types.SerializableDataType;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientboundDispatcher {
    public static void dispatchSyncPacket(
            ClientSyncable data,
            SerializableDataType<?> dataType,
            BlockPos... positions
    ) {
        CompoundTag dataTag = new CompoundTag();
        data.toNbt(dataTag);
        ResourceLocation typeID = BlockRecordsRegistry.SERIALIZABLE_DATA_TYPES.getKey(dataType);
        if (typeID == null) return;
        S2CDataSyncPacket payload = new S2CDataSyncPacket(typeID, dataTag, List.of(positions));
        // TODO: Send via channel
    }
}
