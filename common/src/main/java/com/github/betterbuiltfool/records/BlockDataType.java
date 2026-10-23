package com.github.betterbuiltfool.records;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;

public class BlockDataType<T extends BlockData> {
    private final Function<CompoundTag, T> deserializer;
    
    public BlockDataType(Function<CompoundTag, T> deserializer) {
        this.deserializer = deserializer;
    }
    
    public T deserialize(CompoundTag tag) {
        return this.deserializer.apply(tag);
    }
}
