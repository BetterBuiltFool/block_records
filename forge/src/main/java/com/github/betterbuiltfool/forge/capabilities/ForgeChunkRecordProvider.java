package com.github.betterbuiltfool.forge.capabilities;

import com.github.betterbuiltfool.internal.SyncedChunkData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeChunkRecordProvider implements ICapabilitySerializable<CompoundTag> {
    private SyncedChunkData commonStorage;
    private LazyOptional<SyncedChunkData> holder;
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                                                      @Nullable Direction arg
    ) {
        if (capability == ForgeCapabilityRegistry.CHUNK_DATA_CAPABILITY) {
            return this.holder.cast();
        }
        return LazyOptional.empty();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag dataTag = new CompoundTag();
        commonStorage.serialize(dataTag);
        return dataTag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag arg) {
        commonStorage = SyncedChunkData.deserialize(arg);
        this.holder = LazyOptional.of(() -> this.commonStorage);
    }
    
    public void invalidate() {
        this.holder.invalidate();
    }
}
