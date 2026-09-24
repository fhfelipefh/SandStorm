package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandStormItemsTest {

    @Test
    void shouldCreateValidItemKeyWithSandStormNamespace() {
        ResourceKey<Item> key = SandStormMod.itemKey("raw_silicon");
        assertNotNull(key);
        assertEquals(Registries.ITEM, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals("raw_silicon", key.identifier().getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "space_ration",
            "space_suit_helmet",
            "space_suit_chestplate",
            "space_suit_leggings",
            "space_suit_boots",
            "brackish_water_bottle",
            "sandworm_chitin",
            "sandworm_tooth",
            "raw_silicon",
            "tool_base",
            "electric_component",
            "silicon_pickaxe",
            "silicon_wafer",
            "mineral_salt",
            "potable_water_bottle",
            "circuit_board",
            "nano_actuator",
            "anomaly_radar",
            "tech_disc",
            "scrap_metal",
            "sonic_cannon",
            "plasma_rifle",
            "vibro_crysknife",
            "atmospheric_analyzer",
            "sandworm_spawn_egg"
    })
    void shouldGenerateCorrectResourceKeysForCoreItems(String itemPath) {
        ResourceKey<Item> key = SandStormMod.itemKey(itemPath);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(itemPath, key.identifier().getPath());
    }
}
