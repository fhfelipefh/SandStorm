package com.fhfelipefh.sandstorm.content.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmpParalysisHandlerTest {

    @Test
    void shouldHandleNullLivingEntityGracefully() {
        assertFalse(EmpParalysisHandler.isAndroidOrAutomaton(null));
        assertFalse(EmpParalysisHandler.isParalyzed(null));
        assertNotNull(EmpParalysisHandler.getParalyzedCount());
    }
}
