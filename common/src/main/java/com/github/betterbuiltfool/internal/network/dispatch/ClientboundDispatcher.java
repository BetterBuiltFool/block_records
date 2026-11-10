package com.github.betterbuiltfool.internal.network.dispatch;

import com.github.betterbuiltfool.internal.network.payload.S2CDataSyncPacket;
import com.github.betterbuiltfool.internal.network.payload.DataSync;
import com.github.betterbuiltfool.records.ClientSyncable;
import com.github.betterbuiltfool.records.types.SerializableDataType;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ClientboundDispatcher {
    public static void dispatchSyncPacket(
            ClientSyncable data,
            SerializableDataType<?> dataType,
            ServerPlayer player,
            BlockPos... positions
    ) {
        CompoundTag dataTag = new CompoundTag();
        data.toNbt(dataTag);
        ResourceLocation typeID = BlockRecordsRegistry.SERIALIZABLE_DATA_TYPES.getKey(dataType);
        if (typeID == null) return;
        DataSync payload = new DataSync(typeID, dataTag, List.of(positions));
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        payload.write(buffer);
        NetworkManager.sendToPlayer( player, payload.getID(), buffer);
    }
}
