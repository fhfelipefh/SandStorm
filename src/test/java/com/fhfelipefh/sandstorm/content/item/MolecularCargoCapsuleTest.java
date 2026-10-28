package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MolecularCargoCapsuleTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    @Test
    void testItemResourceKeyAndAntiNesting() throws Exception {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, SandStormMod.id("molecular_cargo_capsule"));
        assertEquals("molecular_cargo_capsule", key.identifier().getPath());

        Unsafe unsafe = getUnsafe();
        MolecularCargoCapsuleItem capsule = (MolecularCargoCapsuleItem) unsafe.allocateInstance(MolecularCargoCapsuleItem.class);
        assertFalse(capsule.canFitInsideContainerItems());
    }

    @Test
    void testAssetIntegrity() {
        Path itemDef = Path.of("src", "main", "resources", "assets", "sandstorm", "items", "molecular_cargo_capsule.json");
        Path itemModel = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "molecular_cargo_capsule.json");
        Path texture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "molecular_cargo_capsule.png");

        assertTrue(Files.exists(itemDef));
        assertTrue(Files.exists(itemModel));
        assertTrue(Files.exists(texture));
    }

    @Test
    void testRecipeIntegrity() {
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/molecular_cargo_capsule.json")) {
            assertNotNull(stream);
            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            assertEquals("minecraft:crafting_shaped", json.get("type").getAsString());

            JsonObject result = json.getAsJsonObject("result");
            assertEquals("sandstorm:molecular_cargo_capsule", result.get("id").getAsString());
            assertEquals(1, result.get("count").getAsInt());

            JsonObject keys = json.getAsJsonObject("key");
            assertEquals("sandstorm:titanium_chitin_composite", keys.get("T").getAsString());
            assertEquals("sandstorm:flexible_biopolymer", keys.get("F").getAsString());
            assertEquals("sandstorm:storage_housing", keys.get("H").getAsString());
            assertEquals("sandstorm:sandstone_crate", keys.get("C").getAsString());
            assertEquals("sandstorm:circuit_board", keys.get("B").getAsString());
            assertEquals("sandstorm:pressure_seal", keys.get("P").getAsString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
