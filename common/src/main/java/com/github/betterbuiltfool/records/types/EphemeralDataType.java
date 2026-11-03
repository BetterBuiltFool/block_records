package com.github.betterbuiltfool.records.types;

import com.github.betterbuiltfool.records.BlockData;

public final class EphemeralDataType<T extends BlockData> extends BlockDataType<T>{
    public EphemeralDataType(Class<T> registeredDataClass) {
        super(registeredDataClass);
    }
}
