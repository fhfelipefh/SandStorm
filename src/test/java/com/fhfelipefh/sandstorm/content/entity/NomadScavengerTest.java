package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.client.renderer.NomadScavengerModel;
import com.fhfelipefh.sandstorm.content.gui.NomadScavengerMenu;
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

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NomadScavengerTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildNomadScavengerAttributes() {
        AttributeSupplier.Builder builder = NomadScavengerEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(30.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.28, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(8.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(24.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
    }

    @Test
    void shouldGenerateValidModelLayerDefinition() {
        LayerDefinition layer = NomadScavengerModel.createBodyLayer();
        assertNotNull(layer);
        assertNotNull(layer.bakeRoot());
    }

    @Test
    void shouldVerifyEntityKeyAndIdentifier() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("nomad_scavenger"));
        assertNotNull(key);
        assertEquals("nomad_scavenger", key.identifier().getPath());
        assertEquals("sandstorm", key.identifier().getNamespace());
    }

    @Test
    void shouldVerifyTradeCostsAndRewards() {
        assertEquals(1, NomadScavengerMenu.getWaterCost(NomadScavengerMenu.TRADE_SCRAP_METAL));
        assertEquals(2, NomadScavengerMenu.getWaterCost(NomadScavengerMenu.TRADE_CIRCUIT_BOARD));
        assertEquals(3, NomadScavengerMenu.getWaterCost(NomadScavengerMenu.TRADE_ELECTRIC_COMPONENT));
        assertEquals(4, NomadScavengerMenu.getWaterCost(NomadScavengerMenu.TRADE_TECH_DISC));

        assertEquals(2, NomadScavengerMenu.getRewardItem(NomadScavengerMenu.TRADE_SCRAP_METAL).getCount());
        assertEquals(1, NomadScavengerMenu.getRewardItem(NomadScavengerMenu.TRADE_CIRCUIT_BOARD).getCount());
        assertEquals(1, NomadScavengerMenu.getRewardItem(NomadScavengerMenu.TRADE_ELECTRIC_COMPONENT).getCount());
        assertEquals(1, NomadScavengerMenu.getRewardItem(NomadScavengerMenu.TRADE_TECH_DISC).getCount());
    }

    @Test
    void shouldVerifyLootTableExists() {
        Path lootPath = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "entities", "nomad_scavenger.json");
        assertTrue(Files.exists(lootPath), "Nomad Scavenger loot table must exist");
    }

    @Test
    void shouldVerifyTextureExists() {
        Path texturePath = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "nomad_scavenger", "nomad_scavenger.png");
        assertTrue(Files.exists(texturePath), "Nomad Scavenger entity texture must exist");
    }
}
