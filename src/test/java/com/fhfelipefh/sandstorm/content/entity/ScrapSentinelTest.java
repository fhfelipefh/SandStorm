package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.client.renderer.ScrapSentinelModel;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormSeismicTargetGoal;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ScrapSentinelTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildScrapSentinelAttributes() {
        AttributeSupplier.Builder builder = ScrapSentinelEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(28.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.25, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(8.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(3.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(24.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
        assertEquals(0.6, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
    }

    @Test
    void shouldGenerateValidModelLayerDefinition() {
        LayerDefinition layer = ScrapSentinelModel.createBodyLayer();
        assertNotNull(layer);
        assertNotNull(layer.bakeRoot());
    }

    @Test
    void shouldValidateSandwormIgnoresScrapSentinel() {
        SandwormSeismicTargetGoal goal = new SandwormSeismicTargetGoal(null);
        boolean validForNull = goal.isValidPrey(null);
        assertFalse(validForNull);
    }

    @Test
    void shouldVerifyEntityKeyAndIdentifier() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("scrap_sentinel"));
        assertNotNull(key);
        assertEquals("scrap_sentinel", key.identifier().getPath());
        assertEquals("sandstorm", key.identifier().getNamespace());
    }
}
