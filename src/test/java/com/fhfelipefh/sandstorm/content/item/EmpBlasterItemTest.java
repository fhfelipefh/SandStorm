package com.fhfelipefh.sandstorm.content.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmpBlasterItemTest {

    @Test
    void shouldDefineEmpBlasterSpecifications() {
        assertEquals(400, EmpBlasterItem.COOLDOWN_TICKS);
        assertEquals(48.0, EmpBlasterItem.EMP_RADIUS, 0.001);
        assertEquals(140, EmpBlasterItem.PLAYER_BACKLASH_TICKS);
        assertEquals(1200, EmpBlasterItem.MIN_PARALYSIS_TICKS);
        assertEquals(2400, EmpBlasterItem.MAX_PARALYSIS_TICKS);
    }
}
