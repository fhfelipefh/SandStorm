package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.tracker.TransponderTrackerSavedData;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransponderTelemetryTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testItemResourceKeys() {
        ResourceKey<Item> tagKey = ResourceKey.create(Registries.ITEM, SandStormMod.id("transponder_tag"));
        ResourceKey<Item> trackerKey = ResourceKey.create(Registries.ITEM, SandStormMod.id("signal_tracker"));

        assertEquals("transponder_tag", tagKey.identifier().getPath());
        assertEquals("signal_tracker", trackerKey.identifier().getPath());
    }

    @Test
    void testDirectionArrowCalculation() {
        assertEquals("⬆", SignalTrackerItem.getDirectionArrow(0.0));
        assertEquals("⬆", SignalTrackerItem.getDirectionArrow(15.0));
        assertEquals("⬆", SignalTrackerItem.getDirectionArrow(-15.0));
        assertEquals("↗", SignalTrackerItem.getDirectionArrow(45.0));
        assertEquals("➡", SignalTrackerItem.getDirectionArrow(90.0));
        assertEquals("↘", SignalTrackerItem.getDirectionArrow(135.0));
        assertEquals("⬇", SignalTrackerItem.getDirectionArrow(180.0));
        assertEquals("⬇", SignalTrackerItem.getDirectionArrow(-180.0));
        assertEquals("↙", SignalTrackerItem.getDirectionArrow(-135.0));
        assertEquals("⬅", SignalTrackerItem.getDirectionArrow(-90.0));
        assertEquals("↖", SignalTrackerItem.getDirectionArrow(-45.0));
    }

    @Test
    void testDistanceColor() {
        assertEquals(ChatFormatting.GREEN, SignalTrackerItem.getDistanceColor(10));
        assertEquals(ChatFormatting.AQUA, SignalTrackerItem.getDistanceColor(50));
        assertEquals(ChatFormatting.YELLOW, SignalTrackerItem.getDistanceColor(100));
        assertEquals(ChatFormatting.GOLD, SignalTrackerItem.getDistanceColor(300));
        assertEquals(ChatFormatting.RED, SignalTrackerItem.getDistanceColor(1000));
    }

    @Test
    void testTransponderRecordModel() {
        UUID id = UUID.randomUUID();
        TransponderTrackerSavedData.TransponderRecord record = new TransponderTrackerSavedData.TransponderRecord(
                id,
                "Test Beacon",
                "minecraft:overworld",
                100,
                64,
                -200,
                true
        );

        assertEquals(id, record.id());
        assertEquals("Test Beacon", record.name());
        assertEquals("minecraft:overworld", record.dimension());
        assertEquals(100, record.x());
        assertEquals(64, record.y());
        assertEquals(-200, record.z());
        assertTrue(record.isBlock());
    }

    @Test
    void testAssetIntegrity() throws Exception {
        Path tagItem = Path.of("src", "main", "resources", "assets", "sandstorm", "items", "transponder_tag.json");
        Path tagModel = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "transponder_tag.json");
        Path tagTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "transponder_tag.png");

        Path trackerItem = Path.of("src", "main", "resources", "assets", "sandstorm", "items", "signal_tracker.json");
        Path trackerModel = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "signal_tracker.json");
        Path trackerTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "signal_tracker.png");

        assertTrue(Files.exists(tagItem));
        assertTrue(Files.exists(tagModel));
        assertTrue(Files.exists(tagTexture));
        assertTrue(Files.exists(trackerItem));
        assertTrue(Files.exists(trackerModel));
        assertTrue(Files.exists(trackerTexture));

        byte[] tagBytes = Files.readAllBytes(tagTexture);
        byte[] trackerBytes = Files.readAllBytes(trackerTexture);

        assertEquals((byte) 0x89, tagBytes[0]);
        assertEquals((byte) 0x50, tagBytes[1]);
        assertEquals((byte) 0x4E, tagBytes[2]);
        assertEquals((byte) 0x47, tagBytes[3]);

        assertEquals((byte) 0x89, trackerBytes[0]);
        assertEquals((byte) 0x50, trackerBytes[1]);
        assertEquals((byte) 0x4E, trackerBytes[2]);
        assertEquals((byte) 0x47, trackerBytes[3]);
    }

    @Test
    void testRecipeIntegrity() {
        try (InputStream tagStream = getClass().getResourceAsStream("/data/sandstorm/recipe/transponder_tag.json")) {
            assertNotNull(tagStream);
            JsonObject tagJson = JsonParser.parseReader(new InputStreamReader(tagStream)).getAsJsonObject();
            assertEquals("minecraft:crafting_shaped", tagJson.get("type").getAsString());
            assertEquals("sandstorm:transponder_tag", tagJson.getAsJsonObject("result").get("id").getAsString());
            assertEquals(2, tagJson.getAsJsonObject("result").get("count").getAsInt());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (InputStream trackerStream = getClass().getResourceAsStream("/data/sandstorm/recipe/signal_tracker.json")) {
            assertNotNull(trackerStream);
            JsonObject trackerJson = JsonParser.parseReader(new InputStreamReader(trackerStream)).getAsJsonObject();
            assertEquals("minecraft:crafting_shaped", trackerJson.get("type").getAsString());
            assertEquals("sandstorm:signal_tracker", trackerJson.getAsJsonObject("result").get("id").getAsString());
            assertEquals(1, trackerJson.getAsJsonObject("result").get("count").getAsInt());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
