package com.fhfelipefh.sandstorm.content.survival;

import net.minecraft.world.phys.Vec3;

public final class FlashlightFocalModel {
    public static final double DEFAULT_CONE_HALF_ANGLE_DEGREES = 25.0;
    public static final double DEFAULT_MAX_RANGE = 28.0;
    public static final int MIN_LIGHT_LEVEL = 0;
    public static final int MAX_LIGHT_LEVEL = 15;

    private FlashlightFocalModel() {}

    public static boolean isInsideFocalCone(Vec3 eyePos, Vec3 lookVec, Vec3 targetPos, double halfAngleDegrees, double maxRange) {
        if (eyePos == null || lookVec == null || targetPos == null) {
            return false;
        }
        Vec3 toTarget = targetPos.subtract(eyePos);
        double distance = toTarget.length();
        if (distance <= 0.001 || distance > maxRange) {
            return false;
        }
        Vec3 dirToTarget = toTarget.scale(1.0 / distance);
        Vec3 normLook = lookVec.normalize();
        double dot = normLook.dot(dirToTarget);
        double cosHalfAngle = Math.cos(Math.toRadians(halfAngleDegrees));
        return dot >= cosHalfAngle;
    }

    public static float calculateFocalIntensity(Vec3 eyePos, Vec3 lookVec, Vec3 targetPos, double halfAngleDegrees, double maxRange) {
        if (!isInsideFocalCone(eyePos, lookVec, targetPos, halfAngleDegrees, maxRange)) {
            return 0.0f;
        }
        double dist = targetPos.distanceTo(eyePos);
        double distFactor = Math.clamp(1.0 - (dist / maxRange), 0.0, 1.0);
        Vec3 toTarget = targetPos.subtract(eyePos);
        Vec3 dirToTarget = toTarget.scale(1.0 / dist);
        Vec3 normLook = lookVec.normalize();
        double dot = normLook.dot(dirToTarget);
        double cosHalfAngle = Math.cos(Math.toRadians(halfAngleDegrees));
        double angleFactor = Math.clamp((dot - cosHalfAngle) / (1.0 - cosHalfAngle), 0.0, 1.0);
        return (float) (distFactor * angleFactor);
    }

    public static int calculateFocalLightLevel(float intensity) {
        return Math.clamp(Math.round(intensity * MAX_LIGHT_LEVEL), MIN_LIGHT_LEVEL, MAX_LIGHT_LEVEL);
    }

    public static Vec3 calculateFocalTargetPoint(Vec3 eyePos, Vec3 lookVec, double distance) {
        return eyePos.add(lookVec.normalize().scale(distance));
    }

    public static float calculateFocalApertureRadius(double hitDistance, int screenWidth, int screenHeight) {
        float minDim = Math.min(screenWidth, screenHeight);
        double clampedDist = Math.clamp(hitDistance, 2.0, DEFAULT_MAX_RANGE);
        double t = (clampedDist - 2.0) / (DEFAULT_MAX_RANGE - 2.0);
        return (float) (minDim * (0.28 + t * 0.18));
    }

    public static float calculateVignetteAlpha(float distFromCenter, float apertureRadius) {
        if (distFromCenter <= apertureRadius * 0.70f) {
            return 0.0f;
        }
        if (distFromCenter <= apertureRadius) {
            float t = (distFromCenter - apertureRadius * 0.70f) / (apertureRadius * 0.30f);
            return Math.clamp(t * 0.45f, 0.0f, 0.45f);
        }
        float excess = (distFromCenter - apertureRadius) / Math.max(1.0f, apertureRadius);
        return Math.clamp(0.45f + excess * 0.40f, 0.45f, 0.85f);
    }
}
