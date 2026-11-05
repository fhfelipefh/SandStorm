package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PneumaticSandBlasterTest {

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
        frozenField.set(BuiltInRegistries.MENU, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());
    }

    @Test
    void shouldDefineBlasterConstants() {
        assertEquals(72000, PneumaticSandBlasterItem.MAX_USE_DURATION);
        assertEquals(6, PneumaticSandBlasterItem.MIN_CHARGE_TICKS);
        assertEquals(14, PneumaticSandBlasterItem.FULL_CHARGE_TICKS);
        assertEquals(24.0, PneumaticSandBlasterItem.MAX_BLAST_RANGE, 0.001);
        assertEquals(12, PneumaticSandBlasterItem.COOLDOWN_TICKS);
    }

    @Test
    void shouldCalculateDamageByProximity() {
        float pointBlank = PneumaticSandBlasterItem.calculateDamage(2.0, 1.0f);
        assertEquals(10.0f, pointBlank, 0.001f);

        float mediumRange = PneumaticSandBlasterItem.calculateDamage(8.0, 1.0f);
        assertEquals(6.5f, mediumRange, 0.001f);

        float longRange = PneumaticSandBlasterItem.calculateDamage(18.0, 1.0f);
        assertEquals(3.5f, longRange, 0.001f);

        float beyondRange = PneumaticSandBlasterItem.calculateDamage(28.0, 1.0f);
        assertEquals(0.0f, beyondRange, 0.001f);

        float partialChargePointBlank = PneumaticSandBlasterItem.calculateDamage(2.0, 0.5f);
        assertEquals(5.0f, partialChargePointBlank, 0.001f);
    }

    @Test
    void shouldBeRegisteredInSandStormItems() {
        assertNotNull(SandStormItems.PNEUMATIC_SAND_BLASTER);
        assertTrue(SandStormItems.PNEUMATIC_SAND_BLASTER instanceof PneumaticSandBlasterItem);
    }
}
