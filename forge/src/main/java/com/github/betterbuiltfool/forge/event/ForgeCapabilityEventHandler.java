package com.github.betterbuiltfool.forge.event;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.forge.capabilities.ForgeCapabilityRegistry;
import com.github.betterbuiltfool.forge.capabilities.ForgeChunkRecordProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockRecords.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCapabilityEventHandler {
    
    @SubscribeEvent
    public void onAttachChunkCapabilities(AttachCapabilitiesEvent<LevelChunk> event) {
        ForgeChunkRecordProvider provider = new ForgeChunkRecordProvider();
        LevelChunk chunk = event.getObject();
        Level level = chunk.getLevel();
        
        event.addCapability(ForgeCapabilityRegistry.CHUNK_DATA_KEY, provider);
        if (level instanceof ServerLevel serverLevel) {
            provider.getCommonStorage().setServerLevel(serverLevel);
        }
        
        event.addListener(provider::invalidate);
    }
}
