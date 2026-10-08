package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class MegazordPilotProfile {
    public static final double COCKPIT_FLOOR_Y = 2.68;
    public static final double COCKPIT_FORWARD_Z = 0.35;
    public static final double OPTIC_FORWARD_Z = 0.35;

    private MegazordPilotProfile() {
    }

    public static Vec3 passengerAttachment(float scale) {
        return new Vec3(0.0, COCKPIT_FLOOR_Y * scale, COCKPIT_FORWARD_Z * scale);
    }

    public static Vec3 opticPosition(MegazordEntity megazord, float partialTick) {
        Vec3 eyePosition = megazord.getEyePosition(partialTick);
        float yaw = megazord.getYRot() * Mth.DEG_TO_RAD;
        double x = Mth.sin(yaw) * OPTIC_FORWARD_Z;
        double z = Mth.cos(yaw) * OPTIC_FORWARD_Z;
        return eyePosition.add(x, 0.0, z);
    }

    public static boolean isPilot(Entity entity, MegazordEntity megazord) {
        return entity != null && megazord != null && megazord.getControllingPassenger() == entity;
    }
}
