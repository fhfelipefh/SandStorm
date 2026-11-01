package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RustingBlockArchitectureTest {
    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final List<String> FAMILIES = List.of("military_armor_plate", "hazard_stripe_block", "reinforced_vent_duct", "steel_vent_grate");
    private static final List<String> LEGACY_IDS = List.of("rusted_military_plate", "weathered_military_plate", "fractured_military_plate",
            "weathered_hazard_stripe", "corroded_hazard_stripe", "rusted_vent_duct", "damaged_vent_duct",
            "rusted_steel_vent_grate", "broken_steel_vent_grate");

    @Test
    void everyRustStageHasAModel() throws Exception {
        for (String family : FAMILIES) {
            Path stateFile = RESOURCES.resolve("assets/sandstorm/blockstates/" + family + ".json");
            JsonObject states = JsonParser.parseString(Files.readString(stateFile)).getAsJsonObject().getAsJsonObject("variants");
            for (int stage = 0; stage < 3; stage++) {
                assertTrue(states.has("rust=" + stage), family + " is missing rust stage " + stage);
            }
        }
    }

    @Test
    void legacyItemsAreHiddenFromCreativeAndRecipeViewers() throws Exception {
        String items = Files.readString(Path.of("src/main/java/com/fhfelipefh/sandstorm/content/item/SandStormItems.java"));
        JsonArray hidden = JsonParser.parseString(Files.readString(RESOURCES.resolve("data/c/tags/item/hidden_from_recipe_viewers.json")))
                .getAsJsonObject().getAsJsonArray("values");
        for (String id : LEGACY_IDS) {
            assertFalse(items.contains("entries.accept(SandStormBlocks." + id.toUpperCase() + ")"), id);
            assertTrue(hidden.asList().stream().anyMatch(value -> value.getAsString().equals("sandstorm:" + id)), id);
        }
    }

    @Test
    void militaryLabIncludesRoofAndFoundationInRustDistribution() throws Exception {
        Path dungeon = Path.of("src/main/java/com/fhfelipefh/sandstorm/content/world/dungeon");
        String clutter = Files.readString(dungeon.resolve("MilitaryLabClutterPass.java"));
        String catalog = Files.readString(dungeon.resolve("MilitaryLabCatalog.java"));
        assertTrue(clutter.contains("y <= 6"), "The rust pass must cover the roof layer");
        assertTrue(catalog.matches("(?s).*level\\.setBlock\\(foundationPos,\\s*SandStormBlocks\\.MILITARY_ARMOR_PLATE\\.defaultBlockState\\(\\)\\s*\\.setValue\\(RustingBlock\\.RUST,\\s*RustingBlock\\.selectRuinRust\\(level\\.getRandom\\(\\)\\)\\).*"),
                "The foundation must use the same weighted rust distribution");
    }
}
