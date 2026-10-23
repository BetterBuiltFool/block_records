package com.github.betterbuiltfool.records;

import net.minecraft.nbt.CompoundTag;

public interface BlockData {
    BlockDataType<?> getType();
    
    /**
     * Allows the record to be saved as NBT data.
     * Do not add the BlockDataType to the tag.
     * @param dataTag A tag for saving the data to.
     * @return The dataTag with all NBT serialization data
     */
    CompoundTag toNbt(CompoundTag dataTag);
}
