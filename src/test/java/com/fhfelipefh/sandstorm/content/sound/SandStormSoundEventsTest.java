package com.fhfelipefh.sandstorm.content.sound;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormSoundEventsTest {

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
        assertNotNull(SandStormSoundEvents.TERRAFORMER_HUM);
        assertNotNull(SandStormSoundEvents.ASSEMBLY_CONSTRUCT);
        assertNotNull(SandStormSoundEvents.EXCAVATOR_ENGINE);
        assertNotNull(SandStormSoundEvents.SUIT_BATTERY_LOW);
        assertNotNull(SandStormSoundEvents.SUIT_SOLAR_CHARGE);
    }

    @Test
    void shouldHaveBothMp3AndConvertedOggFiles() {
        Path soundsDir = Path.of("src/main/resources/assets/sandstorm/sounds");
        assertTrue(Files.exists(soundsDir));

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
            Path mp3 = soundsDir.resolve(base + ".mp3");
            Path ogg = soundsDir.resolve(base + ".ogg");

            assertTrue(Files.exists(mp3), "MP3 file must be preserved: " + base);
            assertTrue(Files.exists(ogg), "OGG file must be generated: " + base);
            assertTrue(new File(ogg.toUri()).length() > 0, "OGG file must not be empty: " + base);
        }
    }

    @Test
    void shouldHaveProperSoundsJsonConfiguration() throws Exception {
        Path jsonPath = Path.of("src/main/resources/assets/sandstorm/sounds.json");
        assertTrue(Files.exists(jsonPath));

        String content = Files.readString(jsonPath);
        assertTrue(content.contains("\"weather.sandstorm.wind\""));
        assertTrue(content.contains("\"sandstorm:sandstorm_wind\""));
        assertTrue(content.contains("\"sandstorm:sandstorm_wind_2\""));
        assertTrue(content.contains("\"sandstorm:sandstorm_wind_3\""));
        assertTrue(content.contains("\"sandstorm:sandstorm_wind_4\""));
        assertTrue(content.contains("\"weather.sandstorm.wind.light\""));
        assertTrue(content.contains("\"weather.sandstorm.wind.medium\""));
        assertTrue(content.contains("\"weather.sandstorm.wind.heavy\""));
        assertTrue(content.contains("\"weather.sandstorm.wind.howl\""));
    }
}
