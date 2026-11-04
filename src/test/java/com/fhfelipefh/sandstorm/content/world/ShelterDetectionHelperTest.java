package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ShelterDetectionHelperTest {

    @Test
    void testNullLevelAndPosReturnFalse() {
        assertFalse(ShelterDetectionHelper.isSheltered(null, null));
        assertFalse(ShelterDetectionHelper.isSheltered(null, new BlockPos(0, 64, 0)));
    }
}
