package com.github.betterbuiltfool.data;


import com.github.betterbuiltfool.records.BlockData;
import net.minecraft.nbt.CompoundTag;

public record TestData(boolean isTrue) implements BlockData {
    
    @Override
    public void toNbt(CompoundTag dataTag) {
        //No need to actually do anything here
    }
    
    public static TestData deserialize(CompoundTag root) {
        return new TestData(true);
    }
}
