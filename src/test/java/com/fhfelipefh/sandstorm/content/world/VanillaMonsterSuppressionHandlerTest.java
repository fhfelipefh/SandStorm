package com.fhfelipefh.sandstorm.content.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class VanillaMonsterSuppressionHandlerTest {

    @Test
    void nonMonsterEntityShouldNotBeSuppressed() {
        assertFalse(VanillaMonsterSuppressionHandler.shouldSuppressEntity(null));
    }
}
