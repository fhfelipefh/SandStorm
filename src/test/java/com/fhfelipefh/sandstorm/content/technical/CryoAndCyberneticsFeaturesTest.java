package com.fhfelipefh.sandstorm.content.technical;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.CryoCellCoolerItem;
import com.fhfelipefh.sandstorm.content.item.CryoLanceItem;
import com.fhfelipefh.sandstorm.content.item.CyborgUpgradeItem;
import com.fhfelipefh.sandstorm.content.item.GeologicalSurveyorDroneItem;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.item.SubdermalTremorSensorItem;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CryoAndCyberneticsFeaturesTest {

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

        assertNotNull(SandStormBlocks.CRYO_REVIVAL_VAT);
        assertNotNull(SandStormBlocks.MAGLEV_CONVEYOR);
        assertNotNull(SandStormItems.CRYO_LANCE);
        assertNotNull(SandStormItems.CRYO_CELL_COOLER);
        assertNotNull(SandStormItems.SUBDERMAL_TREMOR_SENSOR);
        assertNotNull(SandStormItems.GEOLOGICAL_SURVEYOR_DRONE);
    }

    @Test
    void shouldRegisterCryoAndCyberneticsItems() {
        assertNotNull(SandStormItems.CRYO_LANCE);
        assertNotNull(SandStormItems.CRYO_CELL_COOLER);
        assertNotNull(SandStormItems.SUBDERMAL_TREMOR_SENSOR);
        assertNotNull(SandStormItems.GEOLOGICAL_SURVEYOR_DRONE);

        assertEquals(28.0, CryoLanceItem.RAY_RANGE);
        assertEquals(600, CryoCellCoolerItem.MAX_DURABILITY);
        assertEquals(64.0, SubdermalTremorSensorItem.SCAN_RADIUS);
        assertEquals(48, GeologicalSurveyorDroneItem.SCAN_DEPTH);
    }

    @Test
    void shouldRegisterCryoAndCyberneticsBlocks() {
        assertNotNull(SandStormBlocks.CRYO_REVIVAL_VAT);
        assertNotNull(SandStormBlocks.MAGLEV_CONVEYOR);
        assertNotNull(SandStormBlocks.CRYO_REVIVAL_VAT_BE);
    }

    @Test
    void shouldVerifyCyborgUpgradeSubdermalTremorSensor() {
        CyborgUpgradeItem.CyborgUpgradeType type = CyborgUpgradeItem.CyborgUpgradeType.SUBDERMAL_TREMOR_SENSOR;
        assertNotNull(type);
        assertEquals(16, type.getMaskBit());
        assertEquals("subdermal_tremor_sensor", type.getId());
    }

    @Test
    void shouldContainAllTranslationKeysInLanguageFiles() throws IOException {
        String[] langFiles = {"en_us.json", "pt_br.json", "es_es.json"};
        String[] requiredKeys = {
                "item.sandstorm.cryo_lance",
                "item.sandstorm.cryo_cell_cooler",
                "block.sandstorm.cryo_revival_vat",
                "item.sandstorm.cryo_revival_vat",
                "item.sandstorm.subdermal_tremor_sensor",
                "item.sandstorm.geological_surveyor_drone",
                "block.sandstorm.maglev_conveyor",
                "item.sandstorm.maglev_conveyor"
        };

        for (String langFile : langFiles) {
            Path path = LANG_DIR.resolve(langFile);
            assertTrue(Files.exists(path), "Missing language file: " + langFile);

            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (String key : requiredKeys) {
                    assertTrue(json.has(key), "Missing key '" + key + "' in " + langFile);
                }
            }
        }
    }
}
