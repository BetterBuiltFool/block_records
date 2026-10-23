package com.github.betterbuiltfool.records;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.function.BiFunction;

public class BlockDataType<T extends BlockData> {
    private final BiFunction<BlockPos, CompoundTag, T> deserializer;
    
    public BlockDataType(BiFunction<BlockPos, CompoundTag, T> deserializer) {
        this.deserializer = deserializer;
    }
    
    public T deserialize(BlockPos pos, CompoundTag tag) {
        return this.deserializer.apply(pos, tag);
    }
}
