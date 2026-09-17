package com.fhfelipefh.sandstorm.content.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SonicCannonItemTest {

    @Test
    void shouldDefineCannonSpecifications() {
        assertEquals(25, SonicCannonItem.COOLDOWN_TICKS);
        assertEquals(24.0, SonicCannonItem.CANNON_RANGE, 0.001);
        assertEquals(20.0f, SonicCannonItem.DAMAGE_AMOUNT, 0.001f);
    }
}
