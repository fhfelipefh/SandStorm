package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SentinelDensityArchitectureTest {

    @Test
    void defectiveSentinelMustEnforceDensityLimits() throws IOException {
        Path defectiveSentinelFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "DefectiveSentinelEntity.java");
        assertTrue(Files.exists(defectiveSentinelFile), "DefectiveSentinelEntity.java must exist");
        String content = Files.readString(defectiveSentinelFile);

        assertTrue(content.contains("DENSITY_RADIUS_WIDE"), "Must define DENSITY_RADIUS_WIDE");
        assertTrue(content.contains("MAX_SENTINELS_WIDE"), "Must define MAX_SENTINELS_WIDE");
        assertTrue(content.contains("DENSITY_RADIUS_TIGHT"), "Must define DENSITY_RADIUS_TIGHT");
        assertTrue(content.contains("MAX_SENTINELS_TIGHT"), "Must define MAX_SENTINELS_TIGHT");
        assertTrue(content.contains("canSpawnInArea"), "Must implement canSpawnInArea");
        assertTrue(content.contains("checkSpawnObstruction"), "Must override checkSpawnObstruction");
        assertTrue(content.contains("enforceDensityCap"), "Must implement enforceDensityCap");
        assertTrue(content.contains("discard()"), "Must safely discard overcrowded sentinels");
    }

    @Test
    void scrapSentinelMustEnforceDensityLimits() throws IOException {
        Path scrapSentinelFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "ScrapSentinelEntity.java");
        assertTrue(Files.exists(scrapSentinelFile), "ScrapSentinelEntity.java must exist");
        String content = Files.readString(scrapSentinelFile);

        assertTrue(content.contains("DENSITY_RADIUS_WIDE"), "Must define DENSITY_RADIUS_WIDE");
        assertTrue(content.contains("MAX_SENTINELS_WIDE"), "Must define MAX_SENTINELS_WIDE");
        assertTrue(content.contains("DENSITY_RADIUS_TIGHT"), "Must define DENSITY_RADIUS_TIGHT");
        assertTrue(content.contains("MAX_SENTINELS_TIGHT"), "Must define MAX_SENTINELS_TIGHT");
        assertTrue(content.contains("canSpawnInArea"), "Must implement canSpawnInArea");
        assertTrue(content.contains("checkSpawnObstruction"), "Must override checkSpawnObstruction");
        assertTrue(content.contains("enforceDensityCap"), "Must implement enforceDensityCap");
        assertTrue(content.contains("discard()"), "Must safely discard overcrowded sentinels");
    }

    @Test
    void structureGeneratorsMustGuardSentinelSpawns() throws IOException {
        Path bunkerCatalogFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "AncientBunkerCatalog.java");
        Path outpostGeneratorFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "structure", "AbandonedOutpostGenerator.java");
        Path fuelSiloGeneratorFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "structure", "FuelSiloGenerator.java");

        assertTrue(Files.exists(bunkerCatalogFile), "AncientBunkerCatalog.java must exist");
        assertTrue(Files.exists(outpostGeneratorFile), "AbandonedOutpostGenerator.java must exist");
        assertTrue(Files.exists(fuelSiloGeneratorFile), "FuelSiloGenerator.java must exist");

        String bunkerContent = Files.readString(bunkerCatalogFile);
        assertTrue(bunkerContent.contains("DefectiveSentinelEntity.canSpawnInArea"), "AncientBunkerCatalog must guard sentinel spawn");

        String outpostContent = Files.readString(outpostGeneratorFile);
        assertTrue(outpostContent.contains("ScrapSentinelEntity.canSpawnInArea"), "AbandonedOutpostGenerator must guard sentinel spawn");

        String fuelSiloContent = Files.readString(fuelSiloGeneratorFile);
        assertTrue(fuelSiloContent.contains("ScrapSentinelEntity.canSpawnInArea"), "FuelSiloGenerator must guard sentinel spawn");
    }
}
