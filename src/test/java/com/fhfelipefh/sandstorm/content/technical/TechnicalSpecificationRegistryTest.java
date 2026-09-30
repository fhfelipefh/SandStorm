package com.fhfelipefh.sandstorm.content.technical;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechnicalSpecificationRegistryTest {

    private static final Path LANG_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "lang");

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

        assertNotNull(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR);
        assertNotNull(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K);
    }

    @Test
    void testRegistryCompletenessAndIntegrity() {
        TechnicalSpecificationRegistry.ensureInitialized();
        assertTrue(TechnicalSpecificationRegistry.size() >= 30);

        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR.asItem()));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.QUANTUM_DISK_DRIVE.asItem()));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.ATMOSPHERIC_TERRAFORMER.asItem()));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.PRINTER_3D.asItem()));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.NANITE_FABRICATOR.asItem()));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.CHEMICAL_REFINERY.asItem()));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.AUTONOMOUS_SONIC_TURRET.asItem()));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.LITHO_PLASMA_EXTRACTOR.asItem()));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormBlocks.DEEP_CORE_DRILL.asItem()));

        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.CYBERNETIC_COMMAND_UPLINK));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_4K));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_16K));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.PLASMA_RIFLE));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.HEAVY_PLASMA_CANNON));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.SONIC_CANNON));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.VIBRO_CRYSKNIFE));
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.SANDBOARD));
    }

    @Test
    void testMegastructureConstructorSpecification() {
        TechnicalSpecEntry spec = TechnicalSpecificationRegistry.get(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR.asItem());
        assertNotNull(spec);
        assertFalse(spec.details().isEmpty());
        assertTrue(spec.details().size() >= 4);
    }

    @Test
    void testQuantumStorageCartridgeSpecification() {
        TechnicalSpecEntry spec = TechnicalSpecificationRegistry.get(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K);
        assertNotNull(spec);
        assertFalse(spec.details().isEmpty());
        assertTrue(spec.details().size() >= 2);
    }

    @Test
    void testAllTechnicalSpecificationTranslationsExistAndAreSafe() throws IOException {
        TechnicalSpecificationRegistry.ensureInitialized();
        List<String> langFiles = List.of("pt_br.json", "en_us.json", "es_es.json");

        for (String langFile : langFiles) {
            Path path = LANG_DIR.resolve(langFile);
            JsonObject json;
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                json = JsonParser.parseReader(reader).getAsJsonObject();
            }

            assertTrue(json.has("tooltip.sandstorm.technical.hint"));
            assertTrue(json.has("tooltip.sandstorm.technical.header"));

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("tooltip.sandstorm.tech.") || key.startsWith("tooltip.sandstorm.technical.")) {
                    String raw = entry.getValue().getAsString();
                    assertFalse(raw.contains("\n"), "Key " + key + " in " + langFile + " contains raw newline");
                    assertFalse(raw.contains("\r"), "Key " + key + " in " + langFile + " contains raw return");
                    String clean = raw.replaceAll("§.", "");
                    assertTrue(clean.length() <= 50, "Key " + key + " in " + langFile + " exceeds 50 chars: " + clean);
                }
            }
        }
    }

    @Test
    void testTranslatableKeysReferencedInTechnicalSpecsExistInAllLanguages() throws IOException {
        TechnicalSpecificationRegistry.ensureInitialized();
        List<String> langFiles = List.of("pt_br.json", "en_us.json", "es_es.json");

        for (String langFile : langFiles) {
            Path path = LANG_DIR.resolve(langFile);
            JsonObject json;
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                json = JsonParser.parseReader(reader).getAsJsonObject();
            }

            checkComponentTranslations(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR.asItem(), json, langFile);
            checkComponentTranslations(SandStormBlocks.QUANTUM_DISK_DRIVE.asItem(), json, langFile);
            checkComponentTranslations(SandStormBlocks.ATMOSPHERIC_TERRAFORMER.asItem(), json, langFile);
            checkComponentTranslations(SandStormItems.CYBERNETIC_COMMAND_UPLINK, json, langFile);
            checkComponentTranslations(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K, json, langFile);
        }
    }

    private void checkComponentTranslations(Item item, JsonObject json, String langFile) {
        TechnicalSpecEntry spec = TechnicalSpecificationRegistry.get(item);
        assertNotNull(spec, "Spec missing for item in " + langFile);
        for (Component c : spec.details()) {
            if (c.getContents() instanceof TranslatableContents tc) {
                assertTrue(json.has(tc.getKey()), "Missing translation key " + tc.getKey() + " in " + langFile);
            }
        }
    }
}
