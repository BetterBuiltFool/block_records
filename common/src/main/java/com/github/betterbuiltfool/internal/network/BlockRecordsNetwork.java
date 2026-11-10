package com.github.betterbuiltfool.internal.network;

import com.github.betterbuiltfool.BlockRecords;
import com.github.betterbuiltfool.internal.network.handler.ClientPacketHandler;
import com.github.betterbuiltfool.internal.network.payload.S2CDataSyncPacket;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class BlockRecordsNetwork {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(
            new ResourceLocation(BlockRecords.MOD_ID, "network")
    );
    public static void init() {
        CHANNEL.register(
                S2CDataSyncPacket.class,
                S2CDataSyncPacket::write,
                S2CDataSyncPacket::read,
                ClientPacketHandler::handleSyncPacket
        );
    }
}
