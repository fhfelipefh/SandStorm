package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlashlightFocalModelTest {

    @Test
    void shouldDetectDirectlyAheadTargetInsideCone() {
        Vec3 eyePos = new Vec3(0, 10, 0);
        Vec3 lookVec = new Vec3(0, 0, 1);
        Vec3 targetPos = new Vec3(0, 10, 10);

        boolean inside = FlashlightFocalModel.isInsideFocalCone(eyePos, lookVec, targetPos,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        assertTrue(inside);

        float intensity = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetPos,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        assertTrue(intensity > 0.5f);

        int lightLevel = FlashlightFocalModel.calculateFocalLightLevel(intensity);
        assertTrue(lightLevel >= 8 && lightLevel <= 15);
    }

    @Test
    void shouldRejectTargetBehindPlayer() {
        Vec3 eyePos = new Vec3(0, 10, 0);
        Vec3 lookVec = new Vec3(0, 0, 1);
        Vec3 targetPosBehind = new Vec3(0, 10, -5);

        boolean inside = FlashlightFocalModel.isInsideFocalCone(eyePos, lookVec, targetPosBehind,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        assertFalse(inside);

        float intensity = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetPosBehind,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        assertEquals(0.0f, intensity);

        int lightLevel = FlashlightFocalModel.calculateFocalLightLevel(intensity);
        assertEquals(0, lightLevel);

        Vec3 targetPosObliqueBehind = new Vec3(-5, 10, -5);
        assertFalse(FlashlightFocalModel.isInsideFocalCone(eyePos, lookVec, targetPosObliqueBehind,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE));
    }

    @Test
    void shouldRejectTargetOutsideConeAngle() {
        Vec3 eyePos = new Vec3(0, 10, 0);
        Vec3 lookVec = new Vec3(0, 0, 1);
        Vec3 targetWideAngle = new Vec3(10, 10, 5);

        boolean inside = FlashlightFocalModel.isInsideFocalCone(eyePos, lookVec, targetWideAngle,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        assertFalse(inside);

        float intensity = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetWideAngle,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        assertEquals(0.0f, intensity);
    }

    @Test
    void shouldRejectTargetBeyondMaxRange() {
        Vec3 eyePos = new Vec3(0, 10, 0);
        Vec3 lookVec = new Vec3(0, 0, 1);
        Vec3 targetTooFar = new Vec3(0, 10, 35);

        boolean inside = FlashlightFocalModel.isInsideFocalCone(eyePos, lookVec, targetTooFar,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        assertFalse(inside);

        float intensity = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetTooFar,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        assertEquals(0.0f, intensity);
    }

    @Test
    void shouldDecreaseIntensityMonotonicallyWithDistance() {
        Vec3 eyePos = new Vec3(0, 10, 0);
        Vec3 lookVec = new Vec3(0, 0, 1);

        Vec3 targetNear = new Vec3(0, 10, 2);
        Vec3 targetMid = new Vec3(0, 10, 12);
        Vec3 targetFar = new Vec3(0, 10, 24);

        float intensityNear = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetNear,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        float intensityMid = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetMid,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        float intensityFar = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetFar,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);

        assertTrue(intensityNear > intensityMid);
        assertTrue(intensityMid > intensityFar);
        assertTrue(intensityFar > 0.0f);
    }

    @Test
    void shouldDecreaseIntensityMonotonicallyWithAngle() {
        Vec3 eyePos = new Vec3(0, 10, 0);
        Vec3 lookVec = new Vec3(0, 0, 1);

        Vec3 targetCenter = new Vec3(0, 10, 10);
        Vec3 targetOffsetSmall = new Vec3(1.5, 10, 10);
        Vec3 targetOffsetLarge = new Vec3(4.0, 10, 10);

        float intensityCenter = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetCenter,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        float intensitySmall = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetOffsetSmall,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);
        float intensityLarge = FlashlightFocalModel.calculateFocalIntensity(eyePos, lookVec, targetOffsetLarge,
                FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.DEFAULT_MAX_RANGE);

        assertTrue(intensityCenter > intensitySmall);
        assertTrue(intensitySmall > intensityLarge);
    }

    @Test
    void shouldScaleApertureRadiusDynamically() {
        int width = 1920;
        int height = 1080;

        float nearRadius = FlashlightFocalModel.calculateFocalApertureRadius(2.0, width, height);
        float farRadius = FlashlightFocalModel.calculateFocalApertureRadius(28.0, width, height);

        assertTrue(nearRadius > 0.0f);
        assertTrue(farRadius > nearRadius);
        assertEquals(1080.0f * 0.28f, nearRadius, 0.01f);
        assertEquals(1080.0f * 0.46f, farRadius, 0.01f);
    }

    @Test
    void shouldCalculateVignetteAlphaCorrectly() {
        float apertureRadius = 300.0f;

        float centerAlpha = FlashlightFocalModel.calculateVignetteAlpha(100.0f, apertureRadius);
        assertEquals(0.0f, centerAlpha);

        float edgeAlpha = FlashlightFocalModel.calculateVignetteAlpha(250.0f, apertureRadius);
        assertTrue(edgeAlpha > 0.0f && edgeAlpha <= 0.45f);

        float outsideAlpha = FlashlightFocalModel.calculateVignetteAlpha(450.0f, apertureRadius);
        assertTrue(outsideAlpha >= 0.45f && outsideAlpha <= 0.85f);
    }

    @Test
    void shouldHandleNullOrDegenerateVectorsSafely() {
        Vec3 valid = new Vec3(0, 0, 0);

        assertFalse(FlashlightFocalModel.isInsideFocalCone(null, valid, valid, 25.0, 28.0));
        assertFalse(FlashlightFocalModel.isInsideFocalCone(valid, null, valid, 25.0, 28.0));
        assertFalse(FlashlightFocalModel.isInsideFocalCone(valid, valid, null, 25.0, 28.0));
        assertFalse(FlashlightFocalModel.isInsideFocalCone(valid, valid, valid, 25.0, 28.0));

        assertEquals(0.0f, FlashlightFocalModel.calculateFocalIntensity(null, valid, valid, 25.0, 28.0));
    }

    @Test
    void shouldReturnCorrectRangeAndConeAnglePerMode() {
        assertEquals(22.0, FlashlightFocalModel.getMaxRangeForMode(FlashlightFocalModel.MODE_LOW));
        assertEquals(35.0, FlashlightFocalModel.getMaxRangeForMode(FlashlightFocalModel.MODE_MEDIUM));
        assertEquals(50.0, FlashlightFocalModel.getMaxRangeForMode(FlashlightFocalModel.MODE_HIGH));
        assertEquals(FlashlightFocalModel.DEFAULT_MAX_RANGE, FlashlightFocalModel.getMaxRangeForMode(FlashlightFocalModel.MODE_OFF));

        assertEquals(22.0, FlashlightFocalModel.getConeAngleForMode(FlashlightFocalModel.MODE_LOW));
        assertEquals(30.0, FlashlightFocalModel.getConeAngleForMode(FlashlightFocalModel.MODE_MEDIUM));
        assertEquals(38.0, FlashlightFocalModel.getConeAngleForMode(FlashlightFocalModel.MODE_HIGH));
        assertEquals(FlashlightFocalModel.DEFAULT_CONE_HALF_ANGLE_DEGREES, FlashlightFocalModel.getConeAngleForMode(FlashlightFocalModel.MODE_OFF));
    }

    @Test
    void shouldScaleApertureRadiusAccordingToMode() {
        int width = 1920;
        int height = 1080;
        double dist = 15.0;

        float lowRadius = FlashlightFocalModel.calculateFocalApertureRadius(dist, width, height, FlashlightFocalModel.MODE_LOW);
        float medRadius = FlashlightFocalModel.calculateFocalApertureRadius(dist, width, height, FlashlightFocalModel.MODE_MEDIUM);
        float highRadius = FlashlightFocalModel.calculateFocalApertureRadius(dist, width, height, FlashlightFocalModel.MODE_HIGH);

        assertTrue(lowRadius > 0);
        assertTrue(medRadius > lowRadius);
        assertTrue(highRadius > medRadius);
    }

    @Test
    void shouldDifferentiateConeReachByMode() {
        Vec3 eyePos = new Vec3(0, 10, 0);
        Vec3 lookVec = new Vec3(0, 0, 1);
        Vec3 targetAt30 = new Vec3(0, 10, 30);

        boolean insideLow = FlashlightFocalModel.isInsideFocalCone(eyePos, lookVec, targetAt30,
                FlashlightFocalModel.getConeAngleForMode(FlashlightFocalModel.MODE_LOW),
                FlashlightFocalModel.getMaxRangeForMode(FlashlightFocalModel.MODE_LOW));
        assertFalse(insideLow);

        boolean insideMed = FlashlightFocalModel.isInsideFocalCone(eyePos, lookVec, targetAt30,
                FlashlightFocalModel.getConeAngleForMode(FlashlightFocalModel.MODE_MEDIUM),
                FlashlightFocalModel.getMaxRangeForMode(FlashlightFocalModel.MODE_MEDIUM));
        assertTrue(insideMed);

        boolean insideHigh = FlashlightFocalModel.isInsideFocalCone(eyePos, lookVec, targetAt30,
                FlashlightFocalModel.getConeAngleForMode(FlashlightFocalModel.MODE_HIGH),
                FlashlightFocalModel.getMaxRangeForMode(FlashlightFocalModel.MODE_HIGH));
        assertTrue(insideHigh);
    }
}
