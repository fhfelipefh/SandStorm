package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.client.renderer.ScavengerVultureModel;
import com.fhfelipefh.sandstorm.component.ExoSuitChipComponent;
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

class ScavengerVultureEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildScavengerVultureAttributes() {
        AttributeSupplier.Builder builder = ScavengerVultureEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(24.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.28, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(0.50, supplier.getBaseValue(Attributes.FLYING_SPEED), 0.001);
        assertEquals(2.5, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(2.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(48.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
    }

    @Test
    void shouldGenerateValidModelLayerDefinition() {
        LayerDefinition layer = ScavengerVultureModel.createBodyLayer();
        assertNotNull(layer);
        assertNotNull(layer.bakeRoot());
    }

    @Test
    void shouldVerifyEntityKeyAndIdentifier() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("scavenger_vulture"));
        assertNotNull(key);
        assertEquals("scavenger_vulture", key.identifier().getPath());
        assertEquals("sandstorm", key.identifier().getNamespace());
    }

    @Test
    void shouldVerifyGliderChipType() {
        assertEquals("suit_glider_module", ExoSuitChipComponent.GLIDER_MODULE.getId());
        assertEquals("item.sandstorm.suit_glider_module", ExoSuitChipComponent.GLIDER_MODULE.getNameKey());
        assertEquals("tooltip.sandstorm.suit_glider_module", ExoSuitChipComponent.GLIDER_MODULE.getTooltipKey());
    }

    @Test
    void shouldVerifyTextureFilesExist() throws IOException {
        Path activeTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "scavenger_vulture", "scavenger_vulture.png");
        Path deactTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "scavenger_vulture", "scavenger_vulture_deactivated.png");
        Path featherTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "carbon_fiber_servo_feather.png");
        Path sensorTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "avionics_optical_sensor.png");
        Path gliderTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "suit_glider_module.png");
        Path eggTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "scavenger_vulture_spawn_egg.png");
        Path recipeFile = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "suit_glider_module.json");

        assertTrue(Files.exists(activeTexture), "Active texture must exist");
        assertTrue(Files.exists(deactTexture), "Deactivated texture must exist");
        assertTrue(Files.exists(featherTexture), "Feather texture must exist");
        assertTrue(Files.exists(sensorTexture), "Sensor texture must exist");
        assertTrue(Files.exists(gliderTexture), "Glider module texture must exist");
        assertTrue(Files.exists(eggTexture), "Spawn egg texture must exist");
        assertTrue(Files.exists(recipeFile), "Suit glider recipe must exist");

        assertTrue(Files.readAllBytes(activeTexture).length > 8);
    }
}

