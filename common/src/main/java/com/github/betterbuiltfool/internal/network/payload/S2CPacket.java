package com.github.betterbuiltfool.internal.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface S2CPacket<T extends S2CPacket<T>> {
    void write(FriendlyByteBuf buffer);
    
    ResourceLocation getID();
}
