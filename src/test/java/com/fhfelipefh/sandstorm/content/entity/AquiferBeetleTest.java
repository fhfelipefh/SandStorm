package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.client.renderer.AquiferBeetleModel;
import com.fhfelipefh.sandstorm.component.ThermalComponent;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AquiferBeetleTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildAquiferBeetleAttributes() {
        AttributeSupplier.Builder builder = AquiferBeetleEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(40.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.22, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(14.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(4.0, supplier.getBaseValue(Attributes.ARMOR_TOUGHNESS), 0.001);
        assertEquals(0.85, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
        assertEquals(24.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.STEP_HEIGHT), 0.001);
    }

    @Test
    void shouldGenerateValidModelLayerDefinition() {
        LayerDefinition layer = AquiferBeetleModel.createBodyLayer();
        assertNotNull(layer);
        assertNotNull(layer.bakeRoot());
    }

    @Test
    void shouldVerifyEntityKeyAndIdentifier() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("aquifer_beetle"));
        assertNotNull(key);
        assertEquals("aquifer_beetle", key.identifier().getPath());
        assertEquals("sandstorm", key.identifier().getNamespace());
    }

    @Test
    void shouldVerifyDropsInLootTable() throws IOException {
        Path lootPath = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "entities", "aquifer_beetle.json");
        assertTrue(Files.exists(lootPath));
        String content = Files.readString(lootPath);
        assertTrue(content.contains("sandstorm:salt_gland"));
        assertTrue(content.contains("sandstorm:thermal_carapace"));
        assertTrue(content.contains("sandstorm:mineral_salt"));
    }

    @Test
    void shouldVerifyThermalSpikeOverheating() {
        ThermalComponent thermal = new ThermalComponent(37.0, 10.0, 50.0);
        assertEquals(37.0, thermal.getCurrentTemperature(), 0.001);
        thermal.setCurrentTemperature(50.0);
        assertEquals(50.0, thermal.getCurrentTemperature(), 0.001);
        thermal.setCurrentTemperature(50.5);
        assertTrue(thermal.isOverheating());
    }
}
