package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Phase15RegistryTest {

    @Test
    void shouldCreatePhase15ItemKeys() {
        ResourceKey<Item> heavyPlasma = SandStormMod.itemKey("heavy_plasma_cannon");
        assertNotNull(heavyPlasma);
        assertEquals("sandstorm", heavyPlasma.identifier().getNamespace());
        assertEquals("heavy_plasma_cannon", heavyPlasma.identifier().getPath());

        ResourceKey<Item> satellite = SandStormMod.itemKey("orbital_survey_satellite");
        assertNotNull(satellite);
        assertEquals("sandstorm", satellite.identifier().getNamespace());
        assertEquals("orbital_survey_satellite", satellite.identifier().getPath());

        ResourceKey<Item> composite = SandStormMod.itemKey("titanium_chitin_composite");
        assertNotNull(composite);
        assertEquals("sandstorm", composite.identifier().getNamespace());
        assertEquals("titanium_chitin_composite", composite.identifier().getPath());

        ResourceKey<Item> shield = SandStormMod.itemKey("kinetic_shield_generator");
        assertNotNull(shield);
        assertEquals("sandstorm", shield.identifier().getNamespace());
        assertEquals("kinetic_shield_generator", shield.identifier().getPath());
    }
}
