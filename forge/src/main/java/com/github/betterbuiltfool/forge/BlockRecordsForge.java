package com.github.betterbuiltfool.forge;

import com.github.betterbuiltfool.forge.capabilities.ForgeCapabilityRegistry;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.github.betterbuiltfool.BlockRecords;

@Mod(BlockRecords.MOD_ID)
public final class BlockRecordsForge {
    public BlockRecordsForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(BlockRecords.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        
        BlockRecords.setFetcherStrategy(chunk -> chunk.getCapability(ForgeCapabilityRegistry.CHUNK_DATA_CAPABILITY).orElse(null));

        // Run our common setup.
        BlockRecords.init();
    }
}
