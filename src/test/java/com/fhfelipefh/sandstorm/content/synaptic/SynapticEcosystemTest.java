package com.fhfelipefh.sandstorm.content.synaptic;

import com.fhfelipefh.sandstorm.content.item.SynapticCartridgeItem;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SynapticEcosystemTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testResourceKeys() {
        ResourceKey<Fluid> fluidKey = ResourceKey.create(Registries.FLUID, SandStormMod.id("synaptic_fluid"));
        ResourceKey<Item> bucketKey = ResourceKey.create(Registries.ITEM, SandStormMod.id("synaptic_fluid_bucket"));
        ResourceKey<Item> cartridgeKey = ResourceKey.create(Registries.ITEM, SandStormMod.id("synaptic_cartridge"));
        ResourceKey<Block> condenserKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("synaptic_condenser"));
        ResourceKey<Block> vacuumKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("xp_vacuum"));
        ResourceKey<Block> repairerKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("synaptic_repairer"));
        ResourceKey<Block> overdriveKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("neural_overdrive_module"));

        assertEquals("synaptic_fluid", fluidKey.identifier().getPath());
        assertEquals("synaptic_fluid_bucket", bucketKey.identifier().getPath());
        assertEquals("synaptic_cartridge", cartridgeKey.identifier().getPath());
        assertEquals("synaptic_condenser", condenserKey.identifier().getPath());
        assertEquals("xp_vacuum", vacuumKey.identifier().getPath());
        assertEquals("synaptic_repairer", repairerKey.identifier().getPath());
        assertEquals("neural_overdrive_module", overdriveKey.identifier().getPath());
    }

    @Test
    void testXpMathCalculations() {
        assertEquals(0, SynapticCartridgeItem.getXpForLevel(0));
        assertEquals(7, SynapticCartridgeItem.getXpForLevel(1));
        assertEquals(352, SynapticCartridgeItem.getXpForLevel(16));
        assertEquals(1395, SynapticCartridgeItem.getXpForLevel(30));

        assertEquals(0, SynapticCartridgeItem.getLevelForXp(0));
        assertEquals(1, SynapticCartridgeItem.getLevelForXp(7));
        assertEquals(16, SynapticCartridgeItem.getLevelForXp(352));
        assertEquals(30, SynapticCartridgeItem.getLevelForXp(1395));

        assertEquals(7, SynapticCartridgeItem.getXpToNextLevel(0));
        assertEquals(37, SynapticCartridgeItem.getXpToNextLevel(15));
        assertEquals(112, SynapticCartridgeItem.getXpToNextLevel(30));
    }

    @Test
    void testFluidTags() throws Exception {
        Path expTag = Path.of("src", "main", "resources", "data", "c", "tags", "fluid", "experience.json");
        Path liquidXpTag = Path.of("src", "main", "resources", "data", "c", "tags", "fluid", "liquid_xp.json");

        assertTrue(Files.exists(expTag));
        assertTrue(Files.exists(liquidXpTag));

        String expContent = Files.readString(expTag);
        String liquidXpContent = Files.readString(liquidXpTag);

        assertTrue(expContent.contains("sandstorm:synaptic_fluid"));
        assertTrue(liquidXpContent.contains("sandstorm:synaptic_fluid"));
    }

    @Test
    void testAssetIntegrity() throws Exception {
        List<String> textures = List.of(
                "src/main/resources/assets/sandstorm/textures/block/synaptic_fluid_still.png",
                "src/main/resources/assets/sandstorm/textures/block/synaptic_fluid_flow.png",
                "src/main/resources/assets/sandstorm/textures/item/synaptic_fluid_bucket.png",
                "src/main/resources/assets/sandstorm/textures/item/synaptic_cartridge.png",
                "src/main/resources/assets/sandstorm/textures/block/synaptic_condenser_top.png",
                "src/main/resources/assets/sandstorm/textures/block/synaptic_condenser_side.png",
                "src/main/resources/assets/sandstorm/textures/block/synaptic_condenser_bottom.png",
                "src/main/resources/assets/sandstorm/textures/block/xp_vacuum_top.png",
                "src/main/resources/assets/sandstorm/textures/block/xp_vacuum_side.png",
                "src/main/resources/assets/sandstorm/textures/block/xp_vacuum_bottom.png",
                "src/main/resources/assets/sandstorm/textures/block/synaptic_repairer_top.png",
                "src/main/resources/assets/sandstorm/textures/block/synaptic_repairer_side.png",
                "src/main/resources/assets/sandstorm/textures/block/synaptic_repairer_bottom.png",
                "src/main/resources/assets/sandstorm/textures/block/neural_overdrive_module_top.png",
                "src/main/resources/assets/sandstorm/textures/block/neural_overdrive_module_side.png",
                "src/main/resources/assets/sandstorm/textures/block/neural_overdrive_module_bottom.png"
        );

        for (String tex : textures) {
            Path p = Path.of(tex);
            assertTrue(Files.exists(p), "Texture must exist: " + tex);
            byte[] bytes = Files.readAllBytes(p);
            assertTrue(bytes.length > 0, "Texture must not be empty: " + tex);
            assertEquals((byte) 0x89, bytes[0]);
            assertEquals((byte) 0x50, bytes[1]);
            assertEquals((byte) 0x4E, bytes[2]);
            assertEquals((byte) 0x47, bytes[3]);
        }
    }

    @Test
    void testLootTableIntegrity() {
        List<String> blocks = List.of(
                "synaptic_condenser",
                "xp_vacuum",
                "synaptic_repairer",
                "neural_overdrive_module"
        );

        for (String b : blocks) {
            Path p = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "blocks", b + ".json");
            assertTrue(Files.exists(p), "Loot table must exist: " + p);
        }
    }

    @Test
    void testRecipeIntegrity() {
        List<String> recipes = List.of(
                "synaptic_condenser",
                "xp_vacuum",
                "synaptic_repairer",
                "neural_overdrive_module",
                "synaptic_cartridge"
        );

        for (String r : recipes) {
            try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/" + r + ".json")) {
                assertNotNull(stream, "Recipe resource stream must exist: " + r);
                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                assertEquals("minecraft:crafting_shaped", json.get("type").getAsString());
                assertEquals("sandstorm:" + r, json.getAsJsonObject("result").get("id").getAsString());
                assertEquals(1, json.getAsJsonObject("result").get("count").getAsInt());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

