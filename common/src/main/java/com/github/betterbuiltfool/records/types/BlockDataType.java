package com.github.betterbuiltfool.records.types;

import com.github.betterbuiltfool.records.BlockData;
import com.github.betterbuiltfool.records.EphemeralData;
import com.github.betterbuiltfool.records.Serializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public sealed abstract class BlockDataType<T extends BlockData> permits EphemeralDataType, SerializableDataType {
    private final Class<T> registeredDataClass;
    
    public BlockDataType(Class<T> registeredDataClass) {
        this.registeredDataClass = registeredDataClass;
    }
    
    public Class<T> getRegisteredType() {
        return this.registeredDataClass;
    }
    
    public static <U extends Serializable> SerializableDataType<U> create(
            Class<U> registeredDataClass,
            Function<CompoundTag,U> deserializer) {
        return new SerializableDataType<>(registeredDataClass, deserializer);
    }
    
    public static <U extends EphemeralData> EphemeralDataType<U> create(
            Class<U> registeredDataClass
    ) {
        return new EphemeralDataType<>(registeredDataClass);
    }
}
