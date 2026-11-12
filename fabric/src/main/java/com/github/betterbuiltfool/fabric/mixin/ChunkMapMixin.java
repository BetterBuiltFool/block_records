package com.github.betterbuiltfool.fabric.mixin;

import com.github.betterbuiltfool.internal.network.dispatch.ClientboundDispatcher;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
    
    @Inject(
            method="playerLoadedChunk",
            at=@At("TAIL")
    )
    public void block_records$onPlayerLoadedChunk(
            ServerPlayer player,
            MutableObject<ClientboundLevelChunkWithLightPacket> packetCache,
            LevelChunk chunk,
            CallbackInfo callbackInfo
    ) {
        ClientboundDispatcher.syncChunk(chunk, player);
    }
}
