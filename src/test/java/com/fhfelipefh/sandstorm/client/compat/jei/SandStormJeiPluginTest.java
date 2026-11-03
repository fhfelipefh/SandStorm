package com.fhfelipefh.sandstorm.client.compat.jei;

import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipeRegistry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormJeiPluginTest {

    @Test
    void fabricModJsonMustRegisterJeiPluginEntrypoint() throws IOException {
        Path modJsonPath = Path.of("src", "main", "resources", "fabric.mod.json");
        try (FileReader reader = new FileReader(modJsonPath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(json.has("entrypoints"));
            JsonObject entrypoints = json.getAsJsonObject("entrypoints");
            assertTrue(entrypoints.has("jei_mod_plugin"));
            String registered = entrypoints.getAsJsonArray("jei_mod_plugin").get(0).getAsString();
            assertTrue(registered.contains("SandStormJeiPlugin"));
        }
    }

    @Test
    void allMachineCategoriesMustHavePopulatedRecipes() {
        List<String> machineIds = List.of(
                "sandstorm:printer_3d",
                "sandstorm:nanite_fabricator",
                "sandstorm:molecular_modifier",
                "sandstorm:desalination_filter",
                "sandstorm:atmospheric_carbon_extractor",
                "sandstorm:megastructure_constructor",
                "sandstorm:chemical_refinery",
                "sandstorm:hydroponic_chamber",
                "sandstorm:dew_condenser"
        );

        for (String machineId : machineIds) {
            List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes(machineId);
            assertNotNull(recipes, "Recipe list must not be null for " + machineId);
            assertFalse(recipes.isEmpty(), "Machine must register at least one recipe: " + machineId);

            for (MachineRecipe recipe : recipes) {
                assertTrue(recipe.getEnergyCost() >= 0, "Recipe must specify non-negative energy cost: " + machineId);
                assertTrue(recipe.getProcessTicks() > 0, "Recipe must specify positive process ticks: " + machineId);
            }
        }
    }

    @Test
    void pluginUidMustMatchSandStormNamespace() {
        SandStormJeiPlugin plugin = new SandStormJeiPlugin();
        assertNotNull(plugin.getPluginUid());
        assertTrue(plugin.getPluginUid().getNamespace().equals("sandstorm"));
        assertTrue(plugin.getPluginUid().getPath().equals("jei_plugin"));
    }
}
