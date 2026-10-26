package com.github.betterbuiltfool.registry;

import com.github.betterbuiltfool.data.TestData;
import com.github.betterbuiltfool.records.BlockData;
import com.github.betterbuiltfool.records.BlockDataType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class RegistrationTest {
    
    @Test
    void testRegister() {
        Registry<BlockDataType<?>> registry = BlockRecordsRegistry.DATA_TYPES;
        ResourceLocation testLocation = new ResourceLocation("testmod", "testData");
        BlockDataType<TestData> TEST_DATA = new BlockDataType<>(TestData.class, TestData::deserialize);
        Registry.register(registry, testLocation, TEST_DATA);
        
        assertSame(TEST_DATA, registry.get(testLocation));
        assertEquals(testLocation, registry.getKey(TEST_DATA));
    }
}
