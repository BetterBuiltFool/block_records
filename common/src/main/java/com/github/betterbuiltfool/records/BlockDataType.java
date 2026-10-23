package com.github.betterbuiltfool.records;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class BlockDataType<T extends BlockData> {
    private final Function<CompoundTag, T> deserializer;
    
    public BlockDataType(Function<CompoundTag, T> deserializer) {
        this.deserializer = deserializer;
    }
    
    public void serialize(
            CompoundTag root,
            ResourceLocation id,
            T data
    ) {
        CompoundTag tag = new CompoundTag();
        data.toNbt(tag);
        root.put(id.toString(), tag);
    }
    
    public T deserialize(CompoundTag tag) {
        return this.deserializer.apply(tag);
    }
}
