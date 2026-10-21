package com.fhfelipefh.sandstorm.client.handler;

import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class HeavyMachineryCameraHandler {
    public static final float MEGAZORD_STEP_PERIOD = 28.0f;
    public static final float EXCAVATOR_TREAD_PERIOD = 8.0f;
    public static final float ROTATION_LAG_FACTOR = 0.20f;
    public static final float ROTATION_SMOOTHING = 0.40f;

    private static float smoothedYaw;
    private static float smoothedPitch;
    private static float smoothedMovementWeight;
    private static boolean initialized;

    private HeavyMachineryCameraHandler() {
    }

    public static void reset() {
        initialized = false;
        smoothedMovementWeight = 0.0f;
    }

    public static Vec3 calculateMegazordBobbing(MegazordEntity megazord, float partialTick) {
        if (megazord == null) {
            return Vec3.ZERO;
        }
        Vec3 delta = megazord.getDeltaMovement();
        if (delta == null) {
            delta = Vec3.ZERO;
        }
        return calculateBobbing(delta, megazord.onGround(), megazord.tickCount, megazord.getYRot(), partialTick,
                MEGAZORD_STEP_PERIOD, 0.045f, 0.065f, 0.025f, 3.5f);
    }

    public static Vec3 calculateExcavatorBobbing(ExcavatorVehicleEntity excavator, float partialTick) {
        if (excavator == null) {
            return Vec3.ZERO;
        }
        Vec3 delta = excavator.getDeltaMovement();
        if (delta == null) {
            delta = Vec3.ZERO;
        }
        return calculateBobbing(delta, excavator.onGround(), excavator.tickCount, excavator.getYRot(), partialTick,
                EXCAVATOR_TREAD_PERIOD, 0.030f, 0.040f, 0.020f, 4.0f);
    }

    public static Vec3 calculateBobbing(Vec3 delta, boolean onGround, int tickCount, float yRot, float partialTick,
                                        float period, float scaleX, float scaleY, float scaleZ, float speedMultiplier) {
        double horizontalSpeed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        boolean isMoving = onGround && horizontalSpeed > 0.005;
        float targetWeight = isMoving ? (float) Math.min(1.0, horizontalSpeed * speedMultiplier) : 0.0f;
        smoothedMovementWeight = Mth.lerp(0.2f, smoothedMovementWeight, targetWeight);

        if (smoothedMovementWeight <= 0.0001f) {
            return Vec3.ZERO;
        }

        float theta = ((tickCount + partialTick) * (float) (Math.PI * 2.0)) / period;
        float localX = scaleX * smoothedMovementWeight * Mth.sin(theta);
        float localY = scaleY * smoothedMovementWeight * Mth.cos(2.0f * theta);
        float localZ = scaleZ * smoothedMovementWeight * Mth.sin(2.0f * theta);

        float yawRad = yRot * Mth.DEG_TO_RAD;
        double worldX = -Mth.cos(yawRad) * localX + Mth.sin(yawRad) * localZ;
        double worldY = localY;
        double worldZ = -Mth.sin(yawRad) * localX - Mth.cos(yawRad) * localZ;

        return new Vec3(worldX, worldY, worldZ);
    }

    public static float[] applyRotationalLag(float targetYaw, float targetPitch, double bobbingY) {
        if (!initialized) {
            smoothedYaw = targetYaw;
            smoothedPitch = targetPitch;
            initialized = true;
        }

        float yawDiff = Mth.wrapDegrees(targetYaw - smoothedYaw);
        float pitchDiff = targetPitch - smoothedPitch;

        smoothedYaw = Mth.wrapDegrees(smoothedYaw + yawDiff * ROTATION_SMOOTHING);
        smoothedPitch += pitchDiff * ROTATION_SMOOTHING;

        float lagYaw = yawDiff * ROTATION_LAG_FACTOR;
        float lagPitch = pitchDiff * ROTATION_LAG_FACTOR;
        float stepPitch = (float) (-bobbingY * 8.0);

        float finalYaw = targetYaw - lagYaw;
        float finalPitch = Mth.clamp(targetPitch - lagPitch + stepPitch, -89.9f, 89.9f);

        return new float[]{finalYaw, finalPitch};
    }
}
