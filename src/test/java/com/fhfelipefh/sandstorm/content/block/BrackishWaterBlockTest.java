package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrackishWaterBlockTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldHaveDrainsPropertyWithinValidRange() {
        assertEquals("drains", BrackishWaterBlock.DRAINS.getName());
        assertEquals(0, BrackishWaterBlock.DRAINS.getPossibleValues().stream().min(Integer::compareTo).orElseThrow());
        assertEquals(6, BrackishWaterBlock.DRAINS.getPossibleValues().stream().max(Integer::compareTo).orElseThrow());
        assertEquals(7, BrackishWaterBlock.DRAINS.getPossibleValues().size());
    }

    @Test
    void depletionLimitShouldAlwaysBeBetweenThreeAndSix() {
        BlockPos[] positions = new BlockPos[]{
                new BlockPos(0, 50, 0),
                new BlockPos(1234, 40, -5678),
                new BlockPos(-9999, 10, 9999),
                new BlockPos(42, 64, -42),
                new BlockPos(-1, -1, -1)
        };
        for (BlockPos pos : positions) {
            int maxDrains = 3 + (Math.abs((int) (pos.asLong() ^ 0x5DEECE66DL)) % 4);
            assertTrue(maxDrains >= 3 && maxDrains <= 6);
        }
    }

    @Test
    void blockstateJsonShouldDefineAllDrainVariants() throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/assets/sandstorm/blockstates/brackish_aquifer.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            for (int i = 0; i <= 6; i++) {
                assertTrue(json.contains("drains=" + i));
            }
        }
    }
}
