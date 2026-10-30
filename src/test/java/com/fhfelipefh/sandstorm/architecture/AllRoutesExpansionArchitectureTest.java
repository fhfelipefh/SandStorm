package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AllRoutesExpansionArchitectureTest {

    private static final Path ITEMS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
    private static final Path BLOCKS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");

    @Test
    void machinesMustImplementOverridableMachineInterface() throws ClassNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        Class<?> overridableClass = Class.forName("com.fhfelipefh.sandstorm.content.entity.OverridableMachine", false, classLoader);

        List<String> machineClasses = List.of(
                "com.fhfelipefh.sandstorm.content.entity.GrazerEntity",
                "com.fhfelipefh.sandstorm.content.entity.FanghornEntity",
                "com.fhfelipefh.sandstorm.content.entity.FrostclawEntity",
                "com.fhfelipefh.sandstorm.content.entity.GlinthawkEntity",
                "com.fhfelipefh.sandstorm.content.entity.WaterwingEntity"
        );

        for (String className : machineClasses) {
            Class<?> clazz = Class.forName(className, false, classLoader);
            assertTrue(overridableClass.isAssignableFrom(clazz), className + " must implement OverridableMachine");
        }
    }

    @Test
    void allExoSuitChipsMustBeDeclaredInItems() throws IOException {
        String content = Files.readString(ITEMS_JAVA);

        List<String> requiredChips = List.of(
                "seismic_sonar_chip",
                "jetboot_module",
                "gravitational_magnet_chip",
                "osmotic_recycler_module",
                "kinetic_deflector_chip"
        );

        for (String chip : requiredChips) {
            assertTrue(content.contains("\"" + chip + "\""), "Missing chip registration for " + chip);
        }
    }

    @Test
    void expansionBlocksAndApexBossMustBeDeclared() throws IOException {
        String itemsContent = Files.readString(ITEMS_JAVA);
        String blocksContent = Files.readString(BLOCKS_JAVA);

        assertTrue(blocksContent.contains("\"hydroponic_tray\""), "Missing hydroponic_tray block");
        assertTrue(blocksContent.contains("\"seismic_resonance_catalyst\""), "Missing seismic_resonance_catalyst block");
        assertTrue(blocksContent.contains("\"xeno_wheat_crop\""), "Missing xeno_wheat_crop block");

        assertTrue(itemsContent.contains("\"apex_heart_matrix\""), "Missing apex_heart_matrix item");
        assertTrue(itemsContent.contains("\"apex_railgun_harpoon\""), "Missing apex_railgun_harpoon item");
        assertTrue(itemsContent.contains("\"apex_sandworm_spawn_egg\""), "Missing apex_sandworm_spawn_egg item");
        assertTrue(itemsContent.contains("\"neural_override_lance\""), "Missing neural_override_lance item");
    }

    @Test
    void spaceFoodsMustBeDeclaredInItems() throws IOException {
        String content = Files.readString(ITEMS_JAVA);

        List<String> requiredFoods = List.of(
                "dune_melon_slice",
                "cryo_berries",
                "thermal_nutrient_paste",
                "electrolyte_juice",
                "cryo_glazed_ration",
                "hydroponic_feast"
        );

        for (String food : requiredFoods) {
            assertTrue(content.contains("\"" + food + "\""), "Missing space food registration for " + food);
        }
    }
}
