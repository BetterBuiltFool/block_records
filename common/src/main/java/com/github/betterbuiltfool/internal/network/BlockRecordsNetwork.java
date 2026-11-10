package com.github.betterbuiltfool.internal.network;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.internal.network.handler.ClientPacketHandler;
import com.github.betterbuiltfool.internal.network.payload.DataSync;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class BlockRecordsNetwork {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(
            new ResourceLocation(BlockRecords.MOD_ID, "network")
    );
    public static void init() {
        CHANNEL.register(
                DataSync.class,
                DataSync::write,
                DataSync::read,
                ClientPacketHandler::handleSyncPacket
        );
    }
}
