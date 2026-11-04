package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.client.renderer.CyberMoleModel;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyberMoleEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildCyberMoleAttributes() {
        AttributeSupplier.Builder builder = CyberMoleEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(45.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.28, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(16.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(14.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(0.8, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
        assertEquals(32.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.STEP_HEIGHT), 0.001);
    }

    @Test
    void shouldGenerateValidModelLayerDefinition() {
        LayerDefinition layer = CyberMoleModel.createBodyLayer();
        assertNotNull(layer);
        assertNotNull(layer.bakeRoot());
    }

    @Test
    void shouldVerifyEntityKeyAndIdentifier() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("cyber_mole"));
        assertNotNull(key);
        assertEquals("cyber_mole", key.identifier().getPath());
        assertEquals("sandstorm", key.identifier().getNamespace());
    }

    @Test
    void shouldVerifySpawnEggItemRegistered() throws IOException {
        Path itemsSource = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
        String content = Files.readString(itemsSource);
        assertTrue(content.contains("CYBER_MOLE_SPAWN_EGG"), "CYBER_MOLE_SPAWN_EGG must be registered in SandStormItems");
        assertTrue(content.contains("cyber_mole_spawn_egg"), "cyber_mole_spawn_egg ID must exist in SandStormItems");
    }

    @Test
    void shouldVerifyTextureFilesExist() throws IOException {
        Path activeTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "cyber_mole", "cyber_mole.png");
        Path deactTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "cyber_mole", "cyber_mole_deactivated.png");
        Path eggTexture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "cyber_mole_spawn_egg.png");
        Path eggItemDef = Path.of("src", "main", "resources", "assets", "sandstorm", "items", "cyber_mole_spawn_egg.json");
        Path eggModel = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "cyber_mole_spawn_egg.json");

        assertTrue(Files.exists(activeTexture), "Active texture must exist");
        assertTrue(Files.exists(deactTexture), "Deactivated texture must exist");
        assertTrue(Files.exists(eggTexture), "Spawn egg texture must exist");
        assertTrue(Files.exists(eggItemDef), "Spawn egg item definition must exist");
        assertTrue(Files.exists(eggModel), "Spawn egg item model must exist");

        assertTrue(Files.size(activeTexture) > 0, "Active texture must not be 0 bytes");
        assertTrue(Files.size(deactTexture) > 0, "Deactivated texture must not be 0 bytes");
        assertTrue(Files.size(eggTexture) > 0, "Spawn egg texture must not be 0 bytes");
    }

    @Test
    void shouldVerifyTranslationsPresentInAllLocales() throws IOException {
        List<String> locales = List.of("en_us.json", "pt_br.json", "es_es.json");
        List<String> requiredKeys = List.of(
                "entity.sandstorm.cyber_mole",
                "entity.sandstorm.cyber_mole.deactivated",
                "item.sandstorm.cyber_mole_spawn_egg",
                "message.sandstorm.cyber_mole_reprogrammed",
                "message.sandstorm.cyber_mole_already_tamed",
                "message.sandstorm.cyber_mole_firewall_active",
                "message.sandstorm.cyber_mole_needs_circuit"
        );

        for (String locale : locales) {
            Path langPath = Path.of("src", "main", "resources", "assets", "sandstorm", "lang", locale);
            assertTrue(Files.exists(langPath), "Lang file " + locale + " must exist");
            String content = Files.readString(langPath);
            for (String key : requiredKeys) {
                assertTrue(content.contains("\"" + key + "\""), "Missing key " + key + " in " + locale);
            }
        }
    }
}

