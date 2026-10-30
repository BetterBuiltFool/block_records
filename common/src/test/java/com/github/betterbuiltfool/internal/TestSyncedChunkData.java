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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        
        BlockPos nullPos = new BlockPos(1, 1, 1);
        
        chunkData.set(nullPos, testDataBlockDataType, null);
        
        assertFalse(spatialMap.containsKey(nullPos));
    }
    
    @Test
    void testGet() {
        TestData testData = new TestData(true);
        BlockPos testPos = new BlockPos(0, 0, 0);
        
        chunkData.set(testPos, testDataBlockDataType, testData);
        
        var expected = chunkData.get(testPos, testDataBlockDataType);
        
        assertSame(expected, testData);
        
        var resultMap = chunkData.get(testPos);
        
        assertEquals(1, resultMap.size());
        assertTrue(resultMap.containsKey(testDataBlockDataType));
        assertTrue(resultMap.containsValue(testData));
    }
    
    @Test
    void testGetPositionsForType() {
        TestData testData = new TestData(true);
        BlockPos testPos = new BlockPos(0, 0, 0);
        BlockPos testPos2 = new BlockPos(1, 1, 1);
        Map<BlockPos, TestData> outMap = new HashMap<>();
        
        chunkData.set(List.of(testPos, testPos2), testDataBlockDataType, testData);
        
        chunkData.collectPositionsForType(testDataBlockDataType, outMap);
        
        assertEquals(2, outMap.size());
        assertTrue(outMap.containsKey(testPos));
        assertTrue(outMap.containsKey(testPos2));
        assertSame(outMap.get(testPos), testData);
        assertSame(outMap.get(testPos2), testData);
    }
    
}
