package com.github.betterbuiltfool.internal.network.payload;

import com.github.betterbuiltfool.BlockRecords;import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;import java.util.List;

public record S2CDataSyncPacket(ResourceLocation typeID, CompoundTag dataTag, List<BlockPos> posList) {
    
    public static final ResourceLocation ID = new ResourceLocation(BlockRecords.MOD_ID, "s2c_sync_position_data");
    
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(typeID);
        buffer.writeNbt(dataTag);
        buffer.writeVarInt(posList.size());
        
        for (var pos:posList) {
            buffer.writeBlockPos(pos);
        }
    }
    
    public static S2CDataSyncPacket read(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        CompoundTag data = buffer.readAnySizeNbt();
        int count = buffer.readVarInt();
        List<BlockPos> positions = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            positions.add(buffer.readBlockPos());
        }
        
        return new S2CDataSyncPacket(id, data, positions);
    }
}
