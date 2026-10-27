package com.github.betterbuiltfool;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;

public class BootstrapRequiredTest {
    
    @BeforeAll
    static void minecraftInit() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }
}
