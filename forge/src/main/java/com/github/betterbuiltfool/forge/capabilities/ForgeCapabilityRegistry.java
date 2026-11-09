package com.github.betterbuiltfool.forge.capabilities;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.internal.SyncedChunkData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeCapabilityRegistry {
    
    public static final ResourceLocation CHUNK_DATA_KEY = ResourceLocation.fromNamespaceAndPath(BlockRecords.MOD_ID, "chunk_data");
    
    public static final Capability<SyncedChunkData> CHUNK_DATA_CAPABILITY = CapabilityManager.get(
            new CapabilityToken<>() {});
    
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(SyncedChunkData.class);
    }
}
