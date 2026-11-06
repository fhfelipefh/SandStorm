package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyberSteedMountUsabilityTest {

    private static final Path STEED_SOURCE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "CyberSteedEntity.java");
    private static final Path ENTITIES_SOURCE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "SandStormEntities.java");

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testInflatedPickRadiusExpandsHitboxForSprintMounting() throws Exception {
        Method getPickRadius = CyberSteedEntity.class.getDeclaredMethod("getPickRadius");
        assertNotNull(getPickRadius);
        assertEquals(float.class, getPickRadius.getReturnType());

        String source = Files.readString(STEED_SOURCE);
        assertTrue(source.contains("public float getPickRadius()"));
        assertTrue(source.contains("return 1.25f;"));
    }

    @Test
    void testMountingFromStandbyModeAutomaticallyWakesSteed() throws IOException {
        String source = Files.readString(STEED_SOURCE);
        assertTrue(source.contains("if (!this.isVehicle())"));
        assertTrue(source.contains("if (this.isOrderedToSit())"));
        assertTrue(source.contains("this.setOrderedToSit(false)"));
        assertTrue(source.contains("player.startRiding(this)"));
    }

    @Test
    void testSecondaryUseDedicatedToStandbyAnchorToggle() throws IOException {
        String source = Files.readString(STEED_SOURCE);
        int secondaryIndex = source.indexOf("if (player.isSecondaryUseActive())");
        assertTrue(secondaryIndex >= 0);
        String block = source.substring(secondaryIndex, secondaryIndex + 300);
        assertTrue(block.contains("setOrderedToSit(!this.isOrderedToSit())"));
        assertTrue(block.contains("this.jumping = false"));
        assertTrue(block.contains("this.navigation.stop()"));
    }

    @Test
    void testFullHealthScrapMetalPermitsMounting() throws IOException {
        String source = Files.readString(STEED_SOURCE);
        int scrapIndex = source.indexOf("if (itemStack.is(SandStormItems.SCRAP_METAL) && this.getHealth() < this.getMaxHealth())");
        assertTrue(scrapIndex >= 0);

        String afterScrap = source.substring(scrapIndex, scrapIndex + 800);
        assertFalse(afterScrap.contains("return InteractionResult.CONSUME;"));
    }

    @Test
    void testMainHandInteractionPriorityPreventsOffhandConflicts() throws IOException {
        String source = Files.readString(STEED_SOURCE);
        assertTrue(source.contains("if (hand != InteractionHand.MAIN_HAND && !this.isVehicle())"));
        assertTrue(source.contains("return InteractionResult.PASS;"));
    }

    @Test
    void testApproachStabilizationSuppressesHostileTargetsAndNavigation() throws Exception {
        Method stabilizationMethod = CyberSteedEntity.class.getDeclaredMethod("checkPlayerApproachStabilization");
        assertNotNull(stabilizationMethod);

        String source = Files.readString(STEED_SOURCE);
        assertTrue(source.contains("checkPlayerApproachStabilization();"));
        assertTrue(source.contains("this.level().getNearestPlayer(this, 5.0)"));
        assertTrue(source.contains("this.setTarget(null)"));
        assertTrue(source.contains("this.getNavigation().stop()"));
    }

    @Test
    void testSizedHitboxContractInSandStormEntities() throws IOException {
        String source = Files.readString(ENTITIES_SOURCE);
        assertTrue(source.contains(".sized(1.4f, 1.6f)"));
        assertTrue(source.contains(".eyeHeight(1.5f)"));
    }

    @Test
    void testCoyoteAndJumpBufferMechanicsPersistForResponsiveRiding() {
        assertEquals(2, CyberSteedEntity.COYOTE_TIME_TICKS);
        assertEquals(2, CyberSteedEntity.JUMP_BUFFER_TICKS);
    }

    @Test
    void testSoundFeedbackOnStandbyWakeup() throws IOException {
        String source = Files.readString(STEED_SOURCE);
        assertTrue(source.contains("SandStormSoundEvents.CYBER_COMPANION_SERVO_EXPAND"));
        assertTrue(source.contains("SandStormSoundEvents.CYBER_COMPANION_SERVO_RETRACT"));
    }

    @Test
    void testCyberSteedAttributesSupportRobustMovementAndMounting() {
        AttributeSupplier supplier = CyberSteedEntity.createAttributes().build();
        assertTrue(supplier.hasAttribute(Attributes.MAX_HEALTH));
        assertTrue(supplier.hasAttribute(Attributes.MOVEMENT_SPEED));
        assertTrue(supplier.hasAttribute(Attributes.STEP_HEIGHT));
        assertEquals(1.25, supplier.getBaseValue(Attributes.STEP_HEIGHT), 0.001);
        assertTrue(supplier.hasAttribute(Attributes.SAFE_FALL_DISTANCE));
        assertEquals(12.0, supplier.getBaseValue(Attributes.SAFE_FALL_DISTANCE), 0.001);
    }
}
