package com.github.betterbuiltfool.forge.event;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.registry.BlockRecordsRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;

@Mod.EventBusSubscriber(modid = BlockRecords.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeRegistryEventHandler {
    
    @SubscribeEvent
    public void onNewRegistry(NewRegistryEvent event) {
        BlockRecordsRegistry.init();
    }
}
