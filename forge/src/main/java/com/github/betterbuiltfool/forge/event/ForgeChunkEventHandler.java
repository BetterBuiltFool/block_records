package com.github.betterbuiltfool.forge.event;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.internal.network.dispatch.ClientboundDispatcher;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockRecords.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeChunkEventHandler {
    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event) {
        ClientboundDispatcher.syncChunk(event.getChunk(), event.getPlayer());
    }
}
