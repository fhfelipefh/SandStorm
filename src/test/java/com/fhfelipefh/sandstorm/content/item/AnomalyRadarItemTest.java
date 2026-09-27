package com.fhfelipefh.sandstorm.content.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnomalyRadarItemTest {

    @Test
    void shouldDefineRadarSpecifications() {
        assertEquals(20, AnomalyRadarItem.COOLDOWN_TICKS);
        assertEquals(96.0, AnomalyRadarItem.SCAN_RADIUS, 0.001);
    }
}
