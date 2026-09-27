package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThumperBlockTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldDefinePoweredProperty() {
        assertEquals("powered", ThumperBlock.POWERED.getName());
        assertTrue(ThumperBlock.POWERED.getPossibleValues().contains(true));
        assertTrue(ThumperBlock.POWERED.getPossibleValues().contains(false));
    }
}
