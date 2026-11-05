package com.github.betterbuiltfool.internal.network;

import com.github.betterbuiltfool.internal.network.handler.ClientPacketHandler;
import com.github.betterbuiltfool.internal.network.payload.S2CDataSyncPacket;
import dev.architectury.networking.NetworkManager;

public class BlockRecordsNetwork {
    public static void init() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                S2CDataSyncPacket.ID,
                ClientPacketHandler::handleSyncPacket
        );
    }
}
