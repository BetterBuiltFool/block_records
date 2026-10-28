package com.github.betterbuiltfool.registry;

import com.github.betterbuiltfool.BootstrapRequiredTest;
import com.github.betterbuiltfool.data.TestData;
import com.github.betterbuiltfool.records.BlockDataType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationTest extends BootstrapRequiredTest {
    
    @Test
    void testRegister() {
        Registry<BlockDataType<?>> registry = BlockRecordsRegistry.DATA_TYPES;
        ResourceLocation testLocation = new ResourceLocation("testmod", "test_data");
        BlockDataType<TestData> expected = new BlockDataType<>(TestData.class, TestData::deserialize);
        BlockDataType<TestData> registered = Registry.register(registry, testLocation, expected);
        
        registry.freeze();
        
        assertSame(expected, registered);
        assertSame(expected, registry.get(testLocation));
        assertEquals(testLocation, registry.getKey(expected));
    }
}
