package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeSerializerRegistryArchitectureTest {

    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data");
    private static final Set<String> UNREGISTERED_CUSTOM_MACHINE_TYPES = Set.of(
            "sandstorm:printer_3d",
            "sandstorm:chemical_refinery",
            "sandstorm:nanite_fabricator",
            "sandstorm:desalination_filter",
            "sandstorm:dew_condenser",
            "sandstorm:hydroponic_chamber",
            "sandstorm:atmospheric_carbon_extractor",
            "sandstorm:megastructure_constructor",
            "sandstorm:bio_regeneration_pod",
            "sandstorm:assembly_bay",
            "sandstorm:spectrometric_sifter",
            "sandstorm:synaptic_repairer"
    );

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void allDataPackRecipesMustUseValidRegisteredRecipeSerializers() throws IOException {
        assertTrue(Files.exists(DATA_DIR), "Data directory must exist");

        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(DATA_DIR)) {
            List<Path> recipeFiles = stream
                    .filter(p -> p.toString().endsWith(".json"))
                    .filter(p -> p.getParent() != null && p.getParent().getFileName().toString().equals("recipe"))
                    .toList();

            assertFalse(recipeFiles.isEmpty(), "Data pack recipe files must exist");

            for (Path recipePath : recipeFiles) {
                try (FileReader reader = new FileReader(recipePath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (!parsed.isJsonObject()) {
                        violations.add(recipePath + ": Not a valid JSON object");
                        continue;
                    }
                    JsonObject json = parsed.getAsJsonObject();
                    if (!json.has("type")) {
                        violations.add(recipePath + ": Missing 'type' field");
                        continue;
                    }
                    String type = json.get("type").getAsString();
                    Identifier typeLocation = Identifier.tryParse(type);
                    if (typeLocation == null) {
                        violations.add(recipePath + ": Invalid Identifier syntax for type: " + type);
                        continue;
                    }
                    if (!BuiltInRegistries.RECIPE_SERIALIZER.containsKey(typeLocation)) {
                        violations.add(recipePath + ": Unregistered recipe_serializer '" + type + "'. In Minecraft 1.21.4+, data pack recipes with unknown serializers cause fatal IllegalStateException during world creation!");
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found recipes with invalid or unregistered recipe_serializer types:\n" + String.join("\n", violations));
    }

    @Test
    void customMachineIdsMustNeverBeUsedAsRecipeSerializerTypesWithoutExplicitRegistration() throws IOException {
        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(DATA_DIR)) {
            List<Path> recipeFiles = stream
                    .filter(p -> p.toString().endsWith(".json"))
                    .filter(p -> p.getParent() != null && p.getParent().getFileName().toString().equals("recipe"))
                    .toList();

            for (Path recipePath : recipeFiles) {
                try (FileReader reader = new FileReader(recipePath.toFile())) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    if (json.has("type")) {
                        String type = json.get("type").getAsString();
                        if (UNREGISTERED_CUSTOM_MACHINE_TYPES.contains(type)) {
                            Identifier loc = Identifier.parse(type);
                            if (!BuiltInRegistries.RECIPE_SERIALIZER.containsKey(loc)) {
                                violations.add(recipePath + ": Uses custom machine ID '" + type + "' as recipe type without a registered RecipeSerializer!");
                            }
                        }
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found custom machine IDs illegally used as data pack recipe types:\n" + String.join("\n", violations));
    }
}
