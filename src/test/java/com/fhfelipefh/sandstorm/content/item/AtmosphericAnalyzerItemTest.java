package com.fhfelipefh.sandstorm.content.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AtmosphericAnalyzerItemTest {

    @Test
    void shouldDefineAnalyzerSpecifications() {
        assertEquals(20, AtmosphericAnalyzerItem.COOLDOWN_TICKS);
        assertEquals(64.0, AtmosphericAnalyzerItem.SCAN_RADIUS, 0.001);
    }
}
