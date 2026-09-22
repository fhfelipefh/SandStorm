package com.fhfelipefh.sandstorm.content.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PlasmaBeamVisualEffect {
    public static final double DEFAULT_STEP = 0.5;

    public static Vec3 calculateImpactPoint(ServerLevel level, Player player, Vec3 origin, Vec3 direction, double maxRange) {
        Vec3 targetEnd = origin.add(direction.scale(maxRange));
        BlockHitResult hitResult = level.clip(new ClipContext(
                origin,
                targetEnd,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        if (hitResult.getType() != HitResult.Type.MISS) {
            return hitResult.getLocation();
        }
        return targetEnd;
    }

    public static void spawnPlasmaBeam(ServerLevel serverLevel, Player player, Vec3 targetPoint, double maxRange) {
        Vec3 origin = player.getEyePosition();
        Vec3 direction = player.getLookAngle().normalize();
        Vec3 end = targetPoint != null ? targetPoint : calculateImpactPoint(serverLevel, player, origin, direction, maxRange);
        double distance = origin.distanceTo(end);

        Vec3 muzzlePos = origin.add(direction.scale(0.6));
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, muzzlePos.x, muzzlePos.y, muzzlePos.z, 5, 0.05, 0.05, 0.05, 0.05);
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, muzzlePos.x, muzzlePos.y, muzzlePos.z, 2, 0.02, 0.02, 0.02, 0.01);

        for (double d = 0.8; d <= distance; d += DEFAULT_STEP) {
            Vec3 point = origin.add(direction.scale(d));
            serverLevel.sendParticles(
                    ParticleTypes.GLOW,
                    point.x, point.y, point.z,
                    1,
                    0.01, 0.01, 0.01,
                    0.0
            );
            serverLevel.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    point.x, point.y, point.z,
                    1,
                    0.02, 0.02, 0.02,
                    0.01
            );
        }

        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, end.x, end.y, end.z, 12, 0.2, 0.2, 0.2, 0.1);
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, end.x, end.y, end.z, 4, 0.1, 0.1, 0.1, 0.02);
        serverLevel.sendParticles(ParticleTypes.SMOKE, end.x, end.y, end.z, 6, 0.1, 0.1, 0.1, 0.02);
    }
}
