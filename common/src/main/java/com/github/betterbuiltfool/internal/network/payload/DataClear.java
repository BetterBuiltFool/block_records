package com.github.betterbuiltfool.internal.network.payload;

import com.github.betterbuiltfool.BlockRecords;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record DataClear(ResourceLocation typeID, List<BlockPos> posList) implements S2CPacket<DataClear> {
    public static final ResourceLocation ID = new ResourceLocation(BlockRecords.MOD_ID, "s2c_clear_position_data");
    
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(typeID);
        buffer.writeVarInt(posList.size());
        
        for (var pos:posList) {
            buffer.writeBlockPos(pos);
        }
    }
    
    @Override
    public ResourceLocation getID() {
        return ID;
    }
    
    public static DataClear read(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        int count = buffer.readVarInt();
        List<BlockPos> positions = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            positions.add(buffer.readBlockPos());
        }
        
        return new DataClear(id, positions);
    }
}
