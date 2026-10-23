package com.github.betterbuiltfool.internal;

import com.github.betterbuiltfool.records.BlockData;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Static pool of record instances. Interning the record allows the same instance to be used across all cases with
 * identical data, saving memory. This works because records are immutable and two instances with the same values hash
 * out to the same value (unless they have a standard array).
 */
public class BlockDataPool {
    public static final Map<BlockData, BlockData> POOL = Collections.synchronizedMap(new WeakHashMap<>());
    
    @SuppressWarnings("unchecked")
    public static <T extends BlockData> T intern(T data) {
        if (data == null) return null;
        
        return (T) POOL.computeIfAbsent(data, key -> key);
    }
}
