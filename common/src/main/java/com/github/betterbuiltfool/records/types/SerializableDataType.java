package com.github.betterbuiltfool.records.types;

import com.github.betterbuiltfool.records.Serializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class SerializableDataType<T extends Serializable> extends BlockDataType<T>{
    private final Function<CompoundTag, T> deserializer;
    
    public SerializableDataType(
            Class<T> registeredDataClass,
            Function<CompoundTag, T> deserializer
    ) {
        super(registeredDataClass);
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
