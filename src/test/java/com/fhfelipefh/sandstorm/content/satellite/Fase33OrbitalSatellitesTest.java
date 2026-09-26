package com.fhfelipefh.sandstorm.content.satellite;

import com.fhfelipefh.sandstorm.content.block.entity.OrbitalGroundStationBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.OrbitalMassDriverBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SpectralSurveyTelescopeBlockEntity;
import com.fhfelipefh.sandstorm.content.command.SandstormDebugCommand;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.serialization.DataResult;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase33OrbitalSatellitesTest {

    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldRegisterPhase33BlocksAndBlockEntities() {
        ResourceKey<Block> massDriverBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("orbital_mass_driver"));
        ResourceKey<Block> groundStationBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("orbital_ground_station"));
        ResourceKey<Block> telescopeBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("spectral_survey_telescope"));

        assertNotNull(massDriverBlock);
        assertNotNull(groundStationBlock);
        assertNotNull(telescopeBlock);

        assertEquals("orbital_mass_driver", massDriverBlock.identifier().getPath());
        assertEquals("orbital_ground_station", groundStationBlock.identifier().getPath());
        assertEquals("spectral_survey_telescope", telescopeBlock.identifier().getPath());
    }

    @Test
    void shouldRegisterPhase33SatelliteItems() {
        ResourceKey<Item> reconKey = SandStormMod.itemKey("weather_recon_satellite");
        ResourceKey<Item> solarKey = SandStormMod.itemKey("orbital_solar_reflector_satellite");
        ResourceKey<Item> sarKey = SandStormMod.itemKey("sar_geological_satellite");
        ResourceKey<Item> lanceKey = SandStormMod.itemKey("orbital_kinetic_lance_satellite");
        ResourceKey<Item> massDriverKey = SandStormMod.itemKey("orbital_mass_driver");
        ResourceKey<Item> groundStationKey = SandStormMod.itemKey("orbital_ground_station");
        ResourceKey<Item> telescopeKey = SandStormMod.itemKey("spectral_survey_telescope");

        assertNotNull(reconKey);
        assertNotNull(solarKey);
        assertNotNull(sarKey);
        assertNotNull(lanceKey);
        assertNotNull(massDriverKey);
        assertNotNull(groundStationKey);
        assertNotNull(telescopeKey);

        assertEquals("weather_recon_satellite", reconKey.identifier().getPath());
        assertEquals("orbital_solar_reflector_satellite", solarKey.identifier().getPath());
        assertEquals("sar_geological_satellite", sarKey.identifier().getPath());
        assertEquals("orbital_kinetic_lance_satellite", lanceKey.identifier().getPath());
        assertEquals("orbital_mass_driver", massDriverKey.identifier().getPath());
        assertEquals("orbital_ground_station", groundStationKey.identifier().getPath());
        assertEquals("spectral_survey_telescope", telescopeKey.identifier().getPath());
    }

    @Test
    void shouldValidateSatelliteTypeProperties() {
        SatelliteType survey = SatelliteType.fromOrdinal(0);
        SatelliteType weather = SatelliteType.fromOrdinal(1);
        SatelliteType solar = SatelliteType.fromOrdinal(2);
        SatelliteType sar = SatelliteType.fromOrdinal(3);
        SatelliteType kinetic = SatelliteType.fromOrdinal(4);
        SatelliteType fallback = SatelliteType.fromOrdinal(999);

        assertEquals(SatelliteType.SURVEY, survey);
        assertEquals(SatelliteType.WEATHER_RECON, weather);
        assertEquals(SatelliteType.SOLAR_REFLECTOR, solar);
        assertEquals(SatelliteType.SAR_GEOLOGICAL, sar);
        assertEquals(SatelliteType.KINETIC_LANCE, kinetic);
        assertEquals(SatelliteType.SURVEY, fallback);

        assertEquals("survey", survey.getId());
        assertEquals("weather_recon", weather.getId());
        assertEquals("solar_reflector", solar.getId());
        assertEquals("sar_geological", sar.getId());
        assertEquals("kinetic_lance", kinetic.getId());
    }

    @Test
    void shouldPersistAndLoadSatelliteSavedDataViaCodec() {
        SatelliteSavedData original = new SatelliteSavedData();
        assertFalse(original.isSatelliteActive());
        assertEquals(0, original.getSatelliteCount());

        original.setSatelliteActive(true);
        original.setLaunchGameTime(12000L);
        original.setWeatherReconActive(true);
        original.setSolarReflectorActive(true);
        original.setSarGeologicalActive(true);
        original.setKineticLanceActive(true);
        original.setSatelliteCount(4);

        DataResult<Tag> encodeResult = SatelliteSavedData.CODEC.encodeStart(NbtOps.INSTANCE, original);
        assertTrue(encodeResult.result().isPresent());
        Tag encodedTag = encodeResult.result().get();
        assertTrue(encodedTag instanceof CompoundTag);

        DataResult<SatelliteSavedData> parseResult = SatelliteSavedData.CODEC.parse(NbtOps.INSTANCE, encodedTag);
        assertTrue(parseResult.result().isPresent());
        SatelliteSavedData loaded = parseResult.result().get();

        assertTrue(loaded.isSatelliteActive());
        assertEquals(12000L, loaded.getLaunchGameTime());
        assertTrue(loaded.isWeatherReconActive());
        assertTrue(loaded.isSolarReflectorActive());
        assertTrue(loaded.isSarGeologicalActive());
        assertTrue(loaded.isKineticLanceActive());
        assertEquals(4, loaded.getSatelliteCount());
    }

    @Test
    void shouldValidateMachineConstants() {
        assertEquals(500000, OrbitalMassDriverBlockEntity.MAX_ENERGY);
        assertEquals(250000, OrbitalMassDriverBlockEntity.LAUNCH_COST);

        assertEquals(100000, OrbitalGroundStationBlockEntity.MAX_ENERGY);
        assertEquals(50000, SpectralSurveyTelescopeBlockEntity.MAX_ENERGY);
    }

    @Test
    void shouldProvidePhase33DebugKitAndFacility() {
        List<String> phases = SandstormDebugCommand.getSupportedPhases();
        assertTrue(phases.contains("33"));

        List<String> facilities = SandstormDebugCommand.getSupportedFacilities();
        assertTrue(facilities.contains("orbital_array"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "weather_recon_satellite",
            "orbital_solar_reflector_satellite",
            "sar_geological_satellite",
            "orbital_kinetic_lance_satellite",
            "orbital_mass_driver",
            "orbital_ground_station",
            "spectral_survey_telescope"
    })
    void shouldHaveItemModelAndDefinitionFiles(String itemName) {
        File definitionFile = new File("src/main/resources/assets/sandstorm/items/" + itemName + ".json");
        File modelFile = new File("src/main/resources/assets/sandstorm/models/item/" + itemName + ".json");

        assertTrue(definitionFile.exists(), "Item definition missing: " + itemName);
        assertTrue(definitionFile.length() > 0, "Item definition empty: " + itemName);

        assertTrue(modelFile.exists(), "Item model missing: " + itemName);
        assertTrue(modelFile.length() > 0, "Item model empty: " + itemName);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "orbital_mass_driver",
            "orbital_ground_station",
            "spectral_survey_telescope"
    })
    void shouldHaveBlockstateAndLootTable(String blockName) {
        File blockstate = new File("src/main/resources/assets/sandstorm/blockstates/" + blockName + ".json");
        File lootTable = new File("src/main/resources/data/sandstorm/loot_table/blocks/" + blockName + ".json");

        assertTrue(blockstate.exists(), "Blockstate missing: " + blockName);
        assertTrue(blockstate.length() > 0, "Blockstate empty: " + blockName);

        assertTrue(lootTable.exists(), "Loot table missing: " + blockName);
        assertTrue(lootTable.length() > 0, "Loot table empty: " + blockName);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/main/resources/assets/sandstorm/textures/item/weather_recon_satellite.png",
            "src/main/resources/assets/sandstorm/textures/item/orbital_solar_reflector_satellite.png",
            "src/main/resources/assets/sandstorm/textures/item/sar_geological_satellite.png",
            "src/main/resources/assets/sandstorm/textures/item/orbital_kinetic_lance_satellite.png",
            "src/main/resources/assets/sandstorm/textures/block/orbital_mass_driver_top.png",
            "src/main/resources/assets/sandstorm/textures/block/orbital_mass_driver_side.png",
            "src/main/resources/assets/sandstorm/textures/block/orbital_mass_driver_bottom.png",
            "src/main/resources/assets/sandstorm/textures/block/orbital_ground_station_top.png",
            "src/main/resources/assets/sandstorm/textures/block/orbital_ground_station_side.png",
            "src/main/resources/assets/sandstorm/textures/block/orbital_ground_station_front.png",
            "src/main/resources/assets/sandstorm/textures/block/orbital_ground_station_bottom.png",
            "src/main/resources/assets/sandstorm/textures/block/spectral_survey_telescope_top.png",
            "src/main/resources/assets/sandstorm/textures/block/spectral_survey_telescope_side.png",
            "src/main/resources/assets/sandstorm/textures/block/spectral_survey_telescope_bottom.png"
    })
    void shouldValidatePngSignatures(String texturePath) throws IOException {
        File file = new File(texturePath);
        assertTrue(file.exists(), "Texture does not exist: " + texturePath);
        assertTrue(file.length() > 0, "Texture is empty: " + texturePath);

        try (FileInputStream in = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = in.read(header);
            assertEquals(8, read, "Could not read 8 bytes of PNG header: " + texturePath);
            assertArrayEquals(PNG_SIGNATURE, header, "Invalid PNG signature: " + texturePath);
        }
    }
}
