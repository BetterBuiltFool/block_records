package com.github.betterbuiltfool.records;

import net.minecraft.nbt.CompoundTag;

public sealed interface Serializable extends BlockData permits ClientSyncable, PersistentData {
    /**
     * Allows the record to be saved as NBT data.
     * Do not add the BlockDataType to the tag.
     * @param dataTag A tag for saving the data to.
     */
    void toNbt(CompoundTag dataTag);
}
