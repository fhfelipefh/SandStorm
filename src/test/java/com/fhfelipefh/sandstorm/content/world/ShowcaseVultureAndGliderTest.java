package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.entity.ScavengerVultureEntity;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowcaseVultureAndGliderTest {

    @BeforeAll
    static void bootstrapRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void vultureAndGliderSectorMustBeRegisteredInShowcaseFootprints() {
        List<ShowcaseAutomation.ShowcaseFootprint> footprints = ShowcaseAutomation.getShowcaseAttractionFootprints();
        ShowcaseAutomation.ShowcaseFootprint vultureSector = footprints.stream()
                .filter(footprint -> footprint.name().equals("Campo do Abutre & Exo-Planador"))
                .findFirst()
                .orElse(null);

        assertNotNull(vultureSector);
        assertEquals(140, vultureSector.minX());
        assertEquals(180, vultureSector.maxX());
        assertEquals(15, vultureSector.minZ());
        assertEquals(65, vultureSector.maxZ());
    }

    @Test
    void vultureAndGliderSectorMustNeverOverlapOtherAttractions() {
        List<ShowcaseAutomation.ShowcaseFootprint> footprints = ShowcaseAutomation.getShowcaseAttractionFootprints();
        ShowcaseAutomation.ShowcaseFootprint vultureSector = footprints.stream()
                .filter(footprint -> footprint.name().equals("Campo do Abutre & Exo-Planador"))
                .findFirst()
                .orElseThrow();

        for (ShowcaseAutomation.ShowcaseFootprint other : footprints) {
            if (!other.name().equals(vultureSector.name())) {
                assertFalse(vultureSector.intersects(other), () -> vultureSector.name() + " intersects " + other.name());
            }
        }
    }

    @Test
    void layoutValidationMustAcceptVultureAndGliderSector() {
        assertDoesNotThrow(ShowcaseAutomation::validateShowcaseLayout);
    }

    @Test
    void vultureBehaviorStateConstantsMustBeConsistent() {
        assertEquals(0, ScavengerVultureEntity.STATE_CIRCLING);
        assertEquals(1, ScavengerVultureEntity.STATE_DIVING);
        assertEquals(2, ScavengerVultureEntity.STATE_SCRAPPING);
        assertEquals(3, ScavengerVultureEntity.STATE_FLEEING);
        assertEquals(4, ScavengerVultureEntity.STATE_ATTACKING);
        assertEquals(5, ScavengerVultureEntity.STATE_ESCORTING);
        assertEquals(6, ScavengerVultureEntity.STATE_SCOUT_DIVE);
        assertEquals(7, ScavengerVultureEntity.STATE_BEACON);
    }

    @Test
    void vultureAndGliderShowcaseItemsMustBeDeclaredInSource() throws IOException {
        Path itemsSource = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
        assertTrue(Files.exists(itemsSource));
        String code = Files.readString(itemsSource);

        assertTrue(code.contains("SCAVENGER_VULTURE_SPAWN_EGG"));
        assertTrue(code.contains("SUIT_GLIDER_MODULE"));
        assertTrue(code.contains("CARBON_FIBER_SERVO_FEATHER"));
        assertTrue(code.contains("AVIONICS_OPTICAL_SENSOR"));
    }

    @Test
    void showcaseAutomationMustDeclareVultureAndGliderCommandsAndStations() throws IOException {
        Path showcaseSource = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java");
        assertTrue(Files.exists(showcaseSource));
        String code = Files.readString(showcaseSource);

        assertTrue(code.contains("executeVultureCommand"));
        assertTrue(code.contains("buildScavengerVultureShowcase"));
        assertTrue(code.contains("Commands.literal(\"vulture\")"));
        assertTrue(code.contains("Commands.literal(\"glider\")"));
        assertTrue(code.contains("Setor Leste: Aviário do Abutre"));
        assertTrue(code.contains("Zona 4: Abutre Hackeado - Baliza"));
        assertTrue(code.contains("Ninho de Abutre: Palha de Aço"));
    }

    @Test
    void vultureNestBlockMustBeRegisteredAndConfigured() throws IOException {
        Path blocksSource = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
        assertTrue(Files.exists(blocksSource));
        String code = Files.readString(blocksSource);
        assertTrue(code.contains("VULTURE_NEST"));

        Path nestBlockstate = Path.of("src", "main", "resources", "assets", "sandstorm", "blockstates", "vulture_nest.json");
        Path nestBlockModel = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "vulture_nest.json");
        Path nestItemModel = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "vulture_nest.json");
        Path nestItemDef = Path.of("src", "main", "resources", "assets", "sandstorm", "items", "vulture_nest.json");
        Path nestTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "vulture_nest.png");

        assertTrue(Files.exists(nestBlockstate));
        assertTrue(Files.exists(nestBlockModel));
        assertTrue(Files.exists(nestItemModel));
        assertTrue(Files.exists(nestItemDef));
        assertTrue(Files.exists(nestTexture));
        assertTrue(Files.readAllBytes(nestTexture).length > 8);
    }
}
