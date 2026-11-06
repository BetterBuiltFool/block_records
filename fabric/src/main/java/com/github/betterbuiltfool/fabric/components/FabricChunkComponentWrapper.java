package com.github.betterbuiltfool.fabric.components;

import com.github.betterbuiltfool.internal.SyncedChunkData;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

public class FabricChunkComponentWrapper implements Component {
    private SyncedChunkData commonStorage;
    
    public FabricChunkComponentWrapper(SyncedChunkData commonStorage) {
        this.commonStorage = commonStorage;
    }
    
    @Override
    public void readFromNbt(CompoundTag tag) {
        this.commonStorage = SyncedChunkData.deserialize(tag);
    }
    
    @Override
    public void writeToNbt(CompoundTag tag) {
        this.commonStorage.serialize(tag);
    }
    
    public SyncedChunkData getCommonStorage() {
        return commonStorage;
    }
}
