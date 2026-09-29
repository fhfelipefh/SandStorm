package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.WirelessSolarReceiverBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewExpansionBlocksTest {

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
    }

    @Test
    void shouldRecognizeSeismicDampenerPavingAsSafe() {
        assertNotNull(SandStormBlocks.SEISMIC_DAMPENER_PAVING);
        assertTrue(SeismicSurvivalHandler.isSeismicSafeBlock(SandStormBlocks.SEISMIC_DAMPENER_PAVING.defaultBlockState()));
    }

    @Test
    void shouldVerifySaltBrickBlocksExist() {
        assertNotNull(SandStormBlocks.SALT_BRICKS);
        assertNotNull(SandStormBlocks.SALT_BRICK_STAIRS);
        assertNotNull(SandStormBlocks.SALT_BRICK_SLAB);
        assertNotNull(SandStormBlocks.SALT_BRICK_WALL);
    }

    @Test
    void shouldVerifyAdvancedRefractionLensItemExists() {
        assertNotNull(SandStormItems.ADVANCED_REFRACTION_LENS);
        assertNotNull(SandStormItems.LOOSE_WIRES);
        assertNotNull(SandStormItems.WORTHLESS_SCRAP);
    }

    @Test
    void shouldVerifyWirelessSolarReceiverLensState() {
        WirelessSolarReceiverBlockEntity receiver = new WirelessSolarReceiverBlockEntity(BlockPos.ZERO, SandStormBlocks.WIRELESS_SOLAR_RECEIVER.defaultBlockState(), 1);
        assertFalse(receiver.hasRefractionLens());
        receiver.setHasRefractionLens(true);
        assertTrue(receiver.hasRefractionLens());
    }

    @Test
    void shouldVerifyJsonAssetsExist() {
        assertTrue(Files.exists(Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "seismic_dampener_paving.png")));
        assertTrue(Files.exists(Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "salt_bricks.png")));
        assertTrue(Files.exists(Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "advanced_refraction_lens.png")));
        assertTrue(Files.exists(Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "seismic_dampener_paving.json")));
        assertTrue(Files.exists(Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "salt_bricks.json")));
        assertTrue(Files.exists(Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "advanced_refraction_lens.json")));
    }
}
