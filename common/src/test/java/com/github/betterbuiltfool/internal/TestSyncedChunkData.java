package com.github.betterbuiltfool.internal;

import com.github.betterbuiltfool.BootstrapRequiredTest;
import com.github.betterbuiltfool.data.TestData;
import com.github.betterbuiltfool.records.BlockDataType;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSyncedChunkData extends BootstrapRequiredTest {
    public static Registry<BlockDataType<?>> REGISTRY;
    public static BlockDataType<TestData> testDataBlockDataType;
    public static final ResourceLocation testLocation = new ResourceLocation("testmod", "test_data");
    public static SyncedChunkData chunkData;
    
    @BeforeEach
    void setUp() {
        REGISTRY = new MappedRegistry<>(BlockRecordsRegistry.BLOCK_DATA_TYPE_REGISTRY_KEY, Lifecycle.stable(), false);
        testDataBlockDataType = Registry.register(REGISTRY, testLocation, new BlockDataType<>(TestData.class, TestData::deserialize));
        
        REGISTRY.freeze();
        
        chunkData = new SyncedChunkData();
    }
    
    @Test
    void testSet() {
        TestData testData = new TestData(true);
        BlockPos testPos = new BlockPos(0, 0, 0);
        
        chunkData.set(testPos, testDataBlockDataType, testData);
        
        var spatialMap = chunkData.spatialMap;
        assertTrue(spatialMap.containsKey(testPos));
        var dataMap = spatialMap.get(testPos);
        assertTrue(dataMap.containsKey(testDataBlockDataType));
        assertSame(dataMap.get(testDataBlockDataType), testData);
        
        var typeMap = chunkData.typeMap;
        assertTrue(typeMap.containsKey(testDataBlockDataType));
    }
}
