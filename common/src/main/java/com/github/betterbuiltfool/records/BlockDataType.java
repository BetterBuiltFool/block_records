package com.github.betterbuiltfool.records;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class BlockDataType<T extends BlockData> {
    private final Class<T> registeredDataClass;
    private final Function<CompoundTag, T> deserializer;
    
    public BlockDataType(Class<T> registeredDataClass, Function<CompoundTag, T> deserializer) {
        this.registeredDataClass = registeredDataClass;
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
    
    public Class<T> getRegisteredType() {
        return this.registeredDataClass;
    }
    
    public T deserialize(CompoundTag tag) {
        return this.deserializer.apply(tag);
    }
}
