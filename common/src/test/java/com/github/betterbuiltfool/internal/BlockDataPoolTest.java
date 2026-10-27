package com.github.betterbuiltfool.internal;

import com.github.betterbuiltfool.BootstrapRequiredTest;
import com.github.betterbuiltfool.data.TestData;
import com.github.betterbuiltfool.records.BlockDataType;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class BlockDataPoolTest extends BootstrapRequiredTest {
    
    static BlockDataType<TestData> TEST_DATA;
    static ResourceLocation TEST_ID = new ResourceLocation("testmod", "testData");
    
    @BeforeEach
    void setUp() {
        Registry<BlockDataType<?>> registry = BlockRecordsRegistry.DATA_TYPES;
        TEST_DATA = new BlockDataType<>(TestData.class, TestData::deserialize);
        Registry.register(registry, TEST_ID, TEST_DATA);
    }
    
    @Test
    void testInterning() {
        TestData data1 = new TestData(true);
        TestData data2 = new TestData(true);
        TestData data3 = new TestData(false);
        
        TestData canonical = BlockDataPool.intern(data1);
        assertSame(data1, canonical);
        assertNotSame(data2, canonical);
        assertNotSame(data3, canonical);
        
        canonical = BlockDataPool.intern(data2);
        // Should not change the result
        assertSame(data1, canonical);
        assertNotSame(data2, canonical);
        assertNotSame(data3, canonical);
        
        // Data3 has a different field, so it should be a new entry, different from 1 and 2
        TestData canonical2 = BlockDataPool.intern(data3);
        assertSame(data3, canonical2);
        assertNotSame(data2, canonical2);
        assertNotSame(data1, canonical2);
    }
}
