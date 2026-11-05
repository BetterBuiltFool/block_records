package com.github.betterbuiltfool;

import com.github.betterbuiltfool.internal.network.BlockRecordsNetwork;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BlockRecords {
    public static final String MOD_ID = "block_records";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        BlockRecordsRegistry.init();
        BlockRecordsNetwork.init();
    }
}
