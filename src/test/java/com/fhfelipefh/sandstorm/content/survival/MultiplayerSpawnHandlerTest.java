package com.fhfelipefh.sandstorm.content.survival;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MultiplayerSpawnHandlerTest {

    @Test
    void shouldInitializeWithoutException() {
        assertDoesNotThrow(MultiplayerSpawnHandler::initialize);
    }
}
