package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonCeilingAndLadderIntegrityArchitectureTest {

    @Test
    void cargoHoistShaftMustNotCarveCeilingOrBreakLadder() throws IOException {
        Path catalogPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "MillennialRepositoryCatalog.java");
        assertTrue(Files.exists(catalogPath), "MillennialRepositoryCatalog.java must exist");
        String content = Files.readString(catalogPath);

        assertFalse(content.contains("ly <= 5; ly++) {\n                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR"),
                "CargoHoistShaft must never carve ceiling ly=5 to AIR");
        assertFalse(content.contains("setLocalBlock(level, origin, rotation, lx, 1, 1, size, Blocks.IRON_BARS"),
                "Iron bars must not overwrite ladder column at (2, 1, 1)");
        assertTrue(content.contains("Blocks.IRON_BLOCK"),
                "CargoHoistShaft must mount an iron block winch motor for hoist chain");
        assertTrue(content.contains("SandStormBlocks.REINFORCED_CONCRETE"),
                "CargoHoistShaft must reinforce ceiling with reinforced concrete");
    }

    @Test
    void showcaseMustBuildRoofCapOverDungeons() throws IOException {
        Path showcasePath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java");
        assertTrue(Files.exists(showcasePath), "ShowcaseAutomation.java must exist");
        String content = Files.readString(showcasePath);

        assertTrue(content.contains("buildRoofCap("),
                "ShowcaseAutomation must seal dungeons with buildRoofCap");
    }

    @Test
    void repositoryDungeonMustFilterVerticalTransitionsPerFloor() throws IOException {
        Path dungeonPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "MillennialRepositoryDungeon.java");
        assertTrue(Files.exists(dungeonPath), "MillennialRepositoryDungeon.java must exist");
        String content = Files.readString(dungeonPath);

        assertTrue(content.contains("repository_stairwell_up"),
                "MillennialRepositoryDungeon must filter out repository_stairwell_up on floor 0");
        assertTrue(content.contains("repository_stairwell_down"),
                "MillennialRepositoryDungeon must filter out repository_stairwell_down on bottom floor");
    }

    @Test
    void militaryLabDungeonMustFilterVerticalTransitionsPerFloor() throws IOException {
        Path dungeonPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "AbandonedMilitaryLabDungeon.java");
        assertTrue(Files.exists(dungeonPath), "AbandonedMilitaryLabDungeon.java must exist");
        String content = Files.readString(dungeonPath);

        assertTrue(content.contains("military_lab_stairwell_up"),
                "AbandonedMilitaryLabDungeon must filter out military_lab_stairwell_up on floor 0");
        assertTrue(content.contains("military_lab_stairwell_down"),
                "AbandonedMilitaryLabDungeon must filter out military_lab_stairwell_down on bottom floor");
    }

    @Test
    void militaryLabMustExecutePostGenerationVerticalShaftLinkage() throws IOException {
        Path dungeonPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "AbandonedMilitaryLabDungeon.java");
        assertTrue(Files.exists(dungeonPath), "AbandonedMilitaryLabDungeon.java must exist");
        String content = Files.readString(dungeonPath);

        assertTrue(content.contains("connectAllVerticalShafts("),
                "AbandonedMilitaryLabDungeon must connect all shafts after all floors generate");
        assertTrue(content.contains("ensureDoorwaysOpen("),
                "AbandonedMilitaryLabDungeon must ensure stairwell doorways are open to corridors");
    }

    @Test
    void excavationHiveMustSupportLaddersWithSolidPillars() throws IOException {
        Path catalogPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "ExcavationHiveCatalog.java");
        assertTrue(Files.exists(catalogPath), "ExcavationHiveCatalog.java must exist");
        String catalogContent = Files.readString(catalogPath);

        assertTrue(catalogContent.contains("SandStormBlocks.WEATHERED_REINFORCED_CONCRETE.defaultBlockState()"),
                "ExcavationHiveCatalog must place weathered reinforced concrete pillars for ladders");
        assertTrue(catalogContent.contains("rotation.rotateDirection(Direction.SOUTH)"),
                "ExcavationHiveCatalog must rotate ladder horizontal facing with component rotation");

        Path dungeonPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "AbandonedExcavationHiveDungeon.java");
        assertTrue(Files.exists(dungeonPath), "AbandonedExcavationHiveDungeon.java must exist");
        String dungeonContent = Files.readString(dungeonPath);

        assertTrue(dungeonContent.contains("SandStormBlocks.WEATHERED_REINFORCED_CONCRETE.defaultBlockState()"),
                "AbandonedExcavationHiveDungeon must place weathered reinforced concrete pillars in shafts");
        assertTrue(dungeonContent.contains("Direction.SOUTH"),
                "AbandonedExcavationHiveDungeon must orient shaft ladders to attach to solid pillar");
    }

    @Test
    void excavationHiveMustOnlyUseGlowLichenForIllumination() throws IOException {
        Path catalogPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "ExcavationHiveCatalog.java");
        assertTrue(Files.exists(catalogPath), "ExcavationHiveCatalog.java must exist");
        String catalogContent = Files.readString(catalogPath);

        assertTrue(catalogContent.contains("Blocks.GLOW_LICHEN"),
                "ExcavationHiveCatalog must use glow lichen for atmospheric lighting");
        assertFalse(catalogContent.contains("Blocks.LANTERN"),
                "ExcavationHiveCatalog must not use lanterns");
        assertFalse(catalogContent.contains("Blocks.SEA_LANTERN"),
                "ExcavationHiveCatalog must not use sea lanterns");
        assertFalse(catalogContent.contains("Blocks.SHROOMLIGHT"),
                "ExcavationHiveCatalog must not use shroomlights");
        assertFalse(catalogContent.contains("Blocks.TORCH"),
                "ExcavationHiveCatalog must not use torches");

        Path dungeonPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "AbandonedExcavationHiveDungeon.java");
        assertTrue(Files.exists(dungeonPath), "AbandonedExcavationHiveDungeon.java must exist");
        String dungeonContent = Files.readString(dungeonPath);

        assertFalse(dungeonContent.contains("Blocks.LANTERN"),
                "AbandonedExcavationHiveDungeon must not use lanterns");
        assertFalse(dungeonContent.contains("Blocks.SEA_LANTERN"),
                "AbandonedExcavationHiveDungeon must not use sea lanterns");
        assertFalse(dungeonContent.contains("Blocks.SHROOMLIGHT"),
                "AbandonedExcavationHiveDungeon must not use shroomlights");

        Path clutterPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "ExcavationHiveClutterPass.java");
        assertTrue(Files.exists(clutterPath), "ExcavationHiveClutterPass.java must exist");
        String clutterContent = Files.readString(clutterPath);

        assertTrue(clutterContent.contains("Blocks.GLOW_LICHEN"),
                "ExcavationHiveClutterPass must grow organic glow lichen on solid walls");
        assertFalse(clutterContent.contains("Blocks.LANTERN"),
                "ExcavationHiveClutterPass must not use lanterns");
    }

    @Test
    void allGeneratorsMustBackLaddersWithSolidBlocks() throws IOException {
        Path castlePath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "structure", "ColossalCastleGenerator.java");
        assertTrue(Files.exists(castlePath), "ColossalCastleGenerator.java must exist");
        String castleContent = Files.readString(castlePath);

        assertTrue(castleContent.contains("Blocks.SPRUCE_LOG.defaultBlockState()"),
                "ColossalCastleGenerator must back tower ladder with solid spruce log pillar");
        assertFalse(castleContent.contains("level.setBlock(mpos, Blocks.LADDER.defaultBlockState(), 2);"),
                "ColossalCastleGenerator must never place unbacked floating ladders");

        Path showcasePath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java");
        assertTrue(Files.exists(showcasePath), "ShowcaseAutomation.java must exist");
        String showcaseContent = Files.readString(showcasePath);

        assertTrue(showcaseContent.contains("SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState()"),
                "ShowcaseAutomation must back observation tube ladder with solid concrete");

        Path militaryCatalogPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "MilitaryLabCatalog.java");
        assertTrue(Files.exists(militaryCatalogPath), "MilitaryLabCatalog.java must exist");
        String militaryContent = Files.readString(militaryCatalogPath);

        assertTrue(militaryContent.contains("rotation.rotateDirection(Direction.SOUTH)"),
                "MilitaryLabCatalog must rotate ladder horizontal facing with component rotation");

        Path repositoryCatalogPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "MillennialRepositoryCatalog.java");
        assertTrue(Files.exists(repositoryCatalogPath), "MillennialRepositoryCatalog.java must exist");
        String repositoryContent = Files.readString(repositoryCatalogPath);

        assertTrue(repositoryContent.contains("rotation.rotateDirection(Direction.SOUTH)"),
                "MillennialRepositoryCatalog must rotate ladder horizontal facing with component rotation");

        Path cryoCatalogPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "CryoVaultCatalog.java");
        assertTrue(Files.exists(cryoCatalogPath), "CryoVaultCatalog.java must exist");
        String cryoContent = Files.readString(cryoCatalogPath);

        assertTrue(cryoContent.contains("SandStormBlocks.CRYO_INSULATION.defaultBlockState()"),
                "CryoVaultCatalog must place cryo insulation pillars for ladders");
        assertTrue(cryoContent.contains("rotation.rotateDirection(Direction.SOUTH)"),
                "CryoVaultCatalog must rotate ladder horizontal facing with component rotation");

        Path cryoDungeonPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "SubglacialCryoVaultDungeon.java");
        assertTrue(Files.exists(cryoDungeonPath), "SubglacialCryoVaultDungeon.java must exist");
        String cryoDungeonContent = Files.readString(cryoDungeonPath);

        assertTrue(cryoDungeonContent.contains("SandStormBlocks.CRYO_INSULATION.defaultBlockState()"),
                "SubglacialCryoVaultDungeon must place cryo insulation pillars in shafts");
        assertTrue(cryoDungeonContent.contains("Direction.SOUTH"),
                "SubglacialCryoVaultDungeon must orient shaft ladders to attach to solid pillar");
    }

    @Test
    void cryoVaultMustOnlyUseGlowLichenForIllumination() throws IOException {
        Path catalogPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "CryoVaultCatalog.java");
        assertTrue(Files.exists(catalogPath), "CryoVaultCatalog.java must exist");
        String catalogContent = Files.readString(catalogPath);

        assertFalse(catalogContent.contains("Blocks.LANTERN"),
                "CryoVaultCatalog must not use lanterns");
        assertFalse(catalogContent.contains("Blocks.SEA_LANTERN"),
                "CryoVaultCatalog must not use sea lanterns");
        assertFalse(catalogContent.contains("Blocks.SHROOMLIGHT"),
                "CryoVaultCatalog must not use shroomlights");
        assertFalse(catalogContent.contains("Blocks.TORCH"),
                "CryoVaultCatalog must not use torches");

        Path dungeonPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "SubglacialCryoVaultDungeon.java");
        assertTrue(Files.exists(dungeonPath), "SubglacialCryoVaultDungeon.java must exist");
        String dungeonContent = Files.readString(dungeonPath);

        assertFalse(dungeonContent.contains("Blocks.LANTERN"),
                "SubglacialCryoVaultDungeon must not use lanterns");
        assertFalse(dungeonContent.contains("Blocks.SEA_LANTERN"),
                "SubglacialCryoVaultDungeon must not use sea lanterns");
        assertFalse(dungeonContent.contains("Blocks.SHROOMLIGHT"),
                "SubglacialCryoVaultDungeon must not use shroomlights");

        Path clutterPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "CryoVaultClutterPass.java");
        assertTrue(Files.exists(clutterPath), "CryoVaultClutterPass.java must exist");
        String clutterContent = Files.readString(clutterPath);

        assertTrue(clutterContent.contains("Blocks.GLOW_LICHEN"),
                "CryoVaultClutterPass must grow organic glow lichen on solid walls");
        assertFalse(clutterContent.contains("Blocks.LANTERN"),
                "CryoVaultClutterPass must not use lanterns");
    }
}
