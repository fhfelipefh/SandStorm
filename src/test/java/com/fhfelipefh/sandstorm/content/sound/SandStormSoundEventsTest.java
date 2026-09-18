package com.fhfelipefh.sandstorm.content.sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormSoundEventsTest {

    private static final Path SOUNDS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "sounds");
    private static final Path SOUNDS_JSON = Path.of("src", "main", "resources", "assets", "sandstorm", "sounds.json");

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldHaveValidSoundEventRegistrations() {
        assertNotNull(SandStormSoundEvents.WEATHER_SANDSTORM_WIND);
        assertNotNull(SandStormSoundEvents.WEATHER_SANDSTORM_WIND_LIGHT);
        assertNotNull(SandStormSoundEvents.WEATHER_SANDSTORM_WIND_MEDIUM);
        assertNotNull(SandStormSoundEvents.WEATHER_SANDSTORM_WIND_HEAVY);
        assertNotNull(SandStormSoundEvents.WEATHER_SANDSTORM_WIND_HOWL);
        assertNotNull(SandStormSoundEvents.SONIC_CANNON_BLAST);
        assertNotNull(SandStormSoundEvents.MEGAZORD_SHOCKWAVE);
        assertNotNull(SandStormSoundEvents.MEGAZORD_STEP);
        assertNotNull(SandStormSoundEvents.SANDWORM_RUMBLE);
        assertNotNull(SandStormSoundEvents.SANDWORM_EMERGE);
        assertNotNull(SandStormSoundEvents.SANDWORM_ATTACK);
        assertNotNull(SandStormSoundEvents.THUMPER_THUMP);
        assertNotNull(SandStormSoundEvents.ANOMALY_RADAR_PING);
        assertNotNull(SandStormSoundEvents.ATMOSPHERIC_ANALYZER_SCAN);
        assertNotNull(SandStormSoundEvents.PRINTER_3D_CRAFT);
        assertNotNull(SandStormSoundEvents.DESALINATION_PROCESS);
        assertNotNull(SandStormSoundEvents.NANITE_ACTIVATE);
        assertNotNull(SandStormSoundEvents.TERRAFORMER_HUM);
        assertNotNull(SandStormSoundEvents.ASSEMBLY_CONSTRUCT);
        assertNotNull(SandStormSoundEvents.CARGO_DRONE_FLIGHT);
        assertNotNull(SandStormSoundEvents.EXCAVATOR_ENGINE);
        assertNotNull(SandStormSoundEvents.SUIT_BATTERY_LOW);
        assertNotNull(SandStormSoundEvents.SUIT_SOLAR_CHARGE);
    }

    @Test
    void shouldHaveValidOggFilesWithValidVorbisHeaders() throws Exception {
        assertTrue(Files.exists(SOUNDS_DIR), "Sounds directory must exist");

        String[] soundBases = {
                "anomaly_radar_ping",
                "assembly_construct",
                "atmospheric_analyzer_scan",
                "desalination_process",
                "excavator_engine",
                "megazord_shockwave",
                "megazord_step",
                "printer_3d_craft",
                "sandstorm_wind",
                "sandstorm_wind_2",
                "sandstorm_wind_3",
                "sandstorm_wind_4",
                "sandworm_attack",
                "sandworm_emerge",
                "sandworm_rumble",
                "sonic_cannon_blast",
                "suit_battery_low",
                "suit_solar_charge",
                "terraformer_hum",
                "thumper_thump"
        };

        for (String base : soundBases) {
            Path mp3 = SOUNDS_DIR.resolve(base + ".mp3");
            Path ogg = SOUNDS_DIR.resolve(base + ".ogg");

            assertFalse(Files.exists(mp3), "Provisional MP3 should not remain in repository: " + base);
            assertTrue(Files.exists(ogg), "OGG file must exist: " + base);
            assertTrue(new File(ogg.toUri()).length() > 100, "OGG file must be substantial in size: " + base);

            try (InputStream in = Files.newInputStream(ogg)) {
                byte[] header = new byte[4];
                int read = in.read(header);
                assertEquals(4, read, "Must be able to read 4 header bytes: " + base);
                assertEquals('O', (char) header[0], "First byte must be 'O': " + base);
                assertEquals('g', (char) header[1], "Second byte must be 'g': " + base);
                assertEquals('g', (char) header[2], "Third byte must be 'g': " + base);
                assertEquals('S', (char) header[3], "Fourth byte must be 'S': " + base);
            }
        }
    }

    @Test
    void shouldHaveProperSoundsJsonConfigurationAndReferentialIntegrity() throws Exception {
        assertTrue(Files.exists(SOUNDS_JSON), "sounds.json must exist");

        try (FileReader reader = new FileReader(SOUNDS_JSON.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            assertTrue(parsed.isJsonObject(), "sounds.json must be a JSON object");
            JsonObject root = parsed.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                String eventKey = entry.getKey();
                JsonObject eventObj = entry.getValue().getAsJsonObject();

                assertTrue(eventObj.has("category"), "Event must have category: " + eventKey);
                assertTrue(eventObj.has("sounds"), "Event must have sounds array: " + eventKey);

                JsonArray soundsArr = eventObj.getAsJsonArray("sounds");
                assertTrue(soundsArr.size() > 0, "Sounds array must not be empty: " + eventKey);

                for (JsonElement soundElem : soundsArr) {
                    String soundRef = soundElem.getAsString();
                    assertTrue(soundRef.startsWith("sandstorm:"), "Sound reference must use sandstorm namespace: " + soundRef);
                    String soundFile = soundRef.substring("sandstorm:".length());
                    Path targetOgg = SOUNDS_DIR.resolve(soundFile + ".ogg");
                    assertTrue(Files.exists(targetOgg), "Target OGG file must exist for sound reference: " + soundRef + " at " + targetOgg);
                }
            }
        }
    }
}
