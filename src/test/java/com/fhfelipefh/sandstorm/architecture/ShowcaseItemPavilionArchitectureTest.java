package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowcaseItemPavilionArchitectureTest {

    private static final Path SHOWCASE_AUTOMATION_JAVA = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java"
    );

    @Test
    void itemPavilionMustHaveCategorizedStationsAndPortals() throws IOException {
        assertTrue(Files.exists(SHOWCASE_AUTOMATION_JAVA), "ShowcaseAutomation.java must exist");
        String content = Files.readString(SHOWCASE_AUTOMATION_JAVA);

        assertTrue(content.contains("ALA OESTE: ARSENAL & EQUIPAMENTOS"), "West wing portal must be identified");
        assertTrue(content.contains("ALA LESTE: QUÂNTICA & ROBÓTICA"), "East wing portal must be identified");

        List<String> requiredStations = List.of(
                "[TRAJES ESPACIAIS & VIDA]",
                "[ARSENAL TÁTICO & ARMAS]",
                "[MEGAZORD & MOBILIDADE]",
                "[SENSORES & FERRAMENTAS]",
                "[CHAVES, UPLINKS & RELÍQUIAS]",
                "[BIOLOGIA DO DESERTO & FIBRAS]",
                "[REDE & DISCOS QUÂNTICOS]",
                "[SATÉLITES & CARGAS ORBITAIS]",
                "[ELETRÔNICA & NANOTECNOLOGIA]",
                "[OVOS DE ANDROIDES & MÁQUINAS]",
                "[CARTUCHOS DE TERRAFORMAÇÃO]",
                "[LIGAS, MINERAIS & FLUIDOS]"
        );

        for (String station : requiredStations) {
            assertTrue(content.contains(station), "Item pavilion must contain station: " + station);
        }
    }

    @Test
    void itemPavilionMustIncludeHorizonSpawnEggsAndModernItems() throws IOException {
        assertTrue(Files.exists(SHOWCASE_AUTOMATION_JAVA), "ShowcaseAutomation.java must exist");
        String content = Files.readString(SHOWCASE_AUTOMATION_JAVA);

        assertTrue(content.contains("GRAZER_SPAWN_EGG"), "Must include Grazer spawn egg");
        assertTrue(content.contains("FANGHORN_SPAWN_EGG"), "Must include Fanghorn spawn egg");
        assertTrue(content.contains("FROSTCLAW_SPAWN_EGG"), "Must include Frostclaw spawn egg");
        assertTrue(content.contains("GLINTHAWK_SPAWN_EGG"), "Must include Glinthawk spawn egg");
        assertTrue(content.contains("WATERWING_SPAWN_EGG"), "Must include Waterwing spawn egg");

        assertTrue(content.contains("SURVIVAL_DATAPAD"), "Must include Survival Datapad");
        assertTrue(content.contains("SANDBOARD"), "Must include Sandboard");
        assertTrue(content.contains("MEGAZORD_FLIGHT_MODULE"), "Must include Megazord flight module");
        assertTrue(content.contains("QUANTUM_STORAGE_CARTRIDGE_64K"), "Must include Quantum Cartridges");
        assertTrue(content.contains("WEATHER_RECON_SATELLITE"), "Must include Weather Satellite");
    }

    @Test
    void itemPavilionWalkwaysMustConnectToCentralAvenue() throws IOException {
        assertTrue(Files.exists(SHOWCASE_AUTOMATION_JAVA), "ShowcaseAutomation.java must exist");
        String content = Files.readString(SHOWCASE_AUTOMATION_JAVA);

        assertTrue(content.contains("int[] transverseZ = new int[]{8, 15, 22, 29, 36, 43, 50};"),
                "Item pavilion must connect transversely to central avenue across all stations");
        assertTrue(content.contains("SandStormBlocks.SEISMIC_DAMPENER_PAVING"),
                "Item pavilion walkways must use seismic dampener paving");
    }

    @Test
    void itemPavilionTextsMustNotExceedFiftyCharactersOrContainRawNewlines() throws IOException {
        assertTrue(Files.exists(SHOWCASE_AUTOMATION_JAVA), "ShowcaseAutomation.java must exist");
        String content = Files.readString(SHOWCASE_AUTOMATION_JAVA);

        int start = content.indexOf("void buildItemWing(");
        int end = content.indexOf("void buildEntityWing(", start);
        assertTrue(start >= 0 && end > start);

        String wingCode = content.substring(start, end);
        assertFalse(wingCode.contains("\\n"), "Item wing must not contain raw newline escape sequences");
        assertFalse(wingCode.contains("\\r"), "Item wing must not contain raw carriage return escape sequences");
    }
}

