package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.client.renderer.SeismicScorpionModel;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
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

class SeismicScorpionEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildSeismicScorpionAttributes() {
        AttributeSupplier.Builder builder = SeismicScorpionEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(60.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.28, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(9.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(12.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(0.5, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
        assertEquals(36.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.STEP_HEIGHT), 0.001);
    }

    @Test
    void shouldGenerateValidModelLayerDefinition() {
        LayerDefinition layer = SeismicScorpionModel.createBodyLayer();
        assertNotNull(layer);
        assertNotNull(layer.bakeRoot());
    }

    @Test
    void shouldVerifyEntityKeyAndIdentifier() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("seismic_scorpion"));
        assertNotNull(key);
        assertEquals("seismic_scorpion", key.identifier().getPath());
        assertEquals("sandstorm", key.identifier().getNamespace());
    }

    @Test
    void shouldVerifyDartKeyAndItem() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("paralytic_toxin_dart"));
        assertNotNull(key);
        assertEquals("paralytic_toxin_dart", key.identifier().getPath());
        assertNotNull(SandStormItems.PARALYTIC_TOXIN_DART);
        assertNotNull(SandStormItems.SEISMIC_SCORPION_STINGER);
        assertNotNull(SandStormItems.SCORPION_CHITIN_PLATE);
        assertNotNull(SandStormItems.SEISMIC_SCORPION_SPAWN_EGG);
    }

    @Test
    void shouldVerifyTextureFilesExist() throws IOException {
        Path activeTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "seismic_scorpion", "seismic_scorpion.png");
        Path deactTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "seismic_scorpion", "seismic_scorpion_deactivated.png");
        Path dartTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "paralytic_toxin_dart.png");
        Path stingerTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "seismic_scorpion_stinger.png");

        assertTrue(Files.exists(activeTexture), "Active texture must exist");
        assertTrue(Files.exists(deactTexture), "Deactivated texture must exist");
        assertTrue(Files.exists(dartTexture), "Dart texture must exist");
        assertTrue(Files.exists(stingerTexture), "Stinger texture must exist");

        assertEquals(8, Files.readAllBytes(activeTexture).length > 8 ? 8 : 0);
    }
}
