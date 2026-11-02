package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

class HardcoreParityArchitectureTest {

    @Test
    void spawnManagerMustNotDivergeBetweenSurvivalAndHardcore() throws IOException {
        Path spawnManagerPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "DesertMobSpawnManager.java");
        String content = Files.readString(spawnManagerPath);

        assertFalse(content.contains("isHardcore"));
        assertFalse(content.contains("Difficulty"));
        assertFalse(content.contains("GameType"));
        assertFalse(content.contains("getGameMode"));
    }

    @Test
    void worldGenAndWeatherMustNotDivergeBetweenSurvivalAndHardcore() throws IOException {
        Path predationPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "DesertPredationHandler.java");
        Path weatherPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "SandstormWeatherHandler.java");

        String predationContent = Files.readString(predationPath);
        String weatherContent = Files.readString(weatherPath);

        assertFalse(predationContent.contains("isHardcore"));
        assertFalse(predationContent.contains("Difficulty"));
        assertFalse(weatherContent.contains("isHardcore"));
        assertFalse(weatherContent.contains("Difficulty"));
    }
}

