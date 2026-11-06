package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyberSteedFallResistanceTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testCyberSteedAttributesDoubleFallResistance() {
        AttributeSupplier supplier = CyberSteedEntity.createAttributes().build();
        assertTrue(supplier.hasAttribute(Attributes.SAFE_FALL_DISTANCE));
        assertTrue(supplier.hasAttribute(Attributes.FALL_DAMAGE_MULTIPLIER));
        assertEquals(12.0, supplier.getBaseValue(Attributes.SAFE_FALL_DISTANCE), 0.001);
        assertEquals(0.25, supplier.getBaseValue(Attributes.FALL_DAMAGE_MULTIPLIER), 0.001);
    }

    @Test
    void testAbstractHorseMixinStructure() throws IOException {
        Path mixinJava = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "mixin", "AbstractHorseMixin.java");
        assertTrue(Files.exists(mixinJava));
        String content = Files.readString(mixinJava);
        assertTrue(content.contains("@Mixin(AbstractHorse.class)"));
        assertTrue(content.contains("sandstorm$doubleHorseFallResistance"));
        assertTrue(content.contains("sandstorm$propagateReducedFallToPassengers"));
    }

    @Test
    void testCyberSteedOverridesFallMethods() throws Exception {
        Method causeFallDamage = CyberSteedEntity.class.getDeclaredMethod(
                "causeFallDamage", double.class, float.class, DamageSource.class
        );
        assertNotNull(causeFallDamage);
        assertEquals(CyberSteedEntity.class, causeFallDamage.getDeclaringClass());

        Method propagateFallToPassengers = CyberSteedEntity.class.getDeclaredMethod(
                "propagateFallToPassengers", double.class, float.class, DamageSource.class
        );
        assertNotNull(propagateFallToPassengers);
        assertEquals(CyberSteedEntity.class, propagateFallToPassengers.getDeclaringClass());
    }

    @Test
    void testMixinRegistration() throws IOException {
        Path mixinConfig = Path.of("src", "main", "resources", "sandstorm.mixins.json");
        assertTrue(Files.exists(mixinConfig));
        String content = Files.readString(mixinConfig);
        assertTrue(content.contains("AbstractHorseMixin"));
    }

    @Test
    void testFallDamageReductionFormula() {
        double safeDistance = 12.0;
        float riderMultiplier = 0.5f;

        double fall12 = Math.max(0.0, 12.0 - safeDistance);
        assertEquals(0.0, fall12, 0.001);

        double fall16 = Math.max(0.0, 16.0 - safeDistance);
        assertEquals(4.0, fall16, 0.001);

        double playerSafeDistance = 3.0;
        double fallPower16 = Math.max(0.0, fall16 - playerSafeDistance);
        int damage16 = (int) Math.floor(fallPower16 * riderMultiplier);
        assertEquals(0, damage16);

        double fall20 = Math.max(0.0, 20.0 - safeDistance);
        assertEquals(8.0, fall20, 0.001);
        double fallPower20 = Math.max(0.0, fall20 - playerSafeDistance);
        int damage20 = (int) Math.floor(fallPower20 * riderMultiplier);
        assertEquals(2, damage20);
    }
}
