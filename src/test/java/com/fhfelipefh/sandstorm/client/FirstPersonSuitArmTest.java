package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.renderer.FirstPersonSuitArmRenderer;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FirstPersonSuitArmTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testFirstPersonSuitArmTexturesAndParts() {
        Identifier baseTexture = FirstPersonSuitArmRenderer.getBaseTexture();
        assertNotNull(baseTexture);
        assertTrue(baseTexture.toString().contains("space_suit.png"));

        Identifier glowTexture = FirstPersonSuitArmRenderer.getGlowTexture();
        assertNotNull(glowTexture);
        assertTrue(glowTexture.toString().contains("space_suit_glow.png"));

        ModelPart rightArm = FirstPersonSuitArmRenderer.getRightArm();
        assertNotNull(rightArm);
        assertFalse(rightArm.isEmpty());

        ModelPart leftArm = FirstPersonSuitArmRenderer.getLeftArm();
        assertNotNull(leftArm);
        assertFalse(leftArm.isEmpty());
    }

    @Test
    void testFirstPersonSuitArmMixinRegistered() throws IOException {
        Path mixinConfig = Path.of("src", "client", "resources", "sandstorm.client.mixins.json");
        assertTrue(Files.exists(mixinConfig));
        String content = Files.readString(mixinConfig);
        assertTrue(content.contains("FirstPersonSuitArmMixin"));
    }
}
