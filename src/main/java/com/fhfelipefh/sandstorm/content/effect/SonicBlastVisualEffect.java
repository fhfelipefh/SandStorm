package com.fhfelipefh.sandstorm.content.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SonicBlastVisualEffect {

    public static final double DEFAULT_STEP = 0.75;
    public static final double SONIC_RING_INTERVAL = 3.0;

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

    public static void spawnSonicBlast(ServerLevel serverLevel, Player player, double maxRange) {
        Vec3 origin = player.getEyePosition();
        Vec3 direction = player.getLookAngle().normalize();
        Vec3 impactPoint = calculateImpactPoint(serverLevel, player, origin, direction, maxRange);
        double effectiveRange = origin.distanceTo(impactPoint);

        Vec3 muzzlePos = origin.add(direction.scale(0.5));
        serverLevel.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, muzzlePos.x, muzzlePos.y, muzzlePos.z, 1, 0.0, 0.0, 0.0, 0.0);

        double lastRingDist = 0.0;
        for (double d = 0.8; d <= effectiveRange; d += DEFAULT_STEP) {
            Vec3 point = origin.add(direction.scale(d));

            serverLevel.sendParticles(
                    ParticleTypes.SMALL_GUST,
                    point.x, point.y, point.z,
                    1,
                    direction.x * 0.1, direction.y * 0.1, direction.z * 0.1,
                    0.02
            );

            if (d % 1.5 < DEFAULT_STEP) {
                serverLevel.sendParticles(
                        ParticleTypes.GUST,
                        point.x, point.y, point.z,
                        1,
                        0.05, 0.05, 0.05,
                        0.01
                );
            }

            if (d - lastRingDist >= SONIC_RING_INTERVAL) {
                serverLevel.sendParticles(
                        ParticleTypes.SONIC_BOOM,
                        point.x, point.y, point.z,
                        1,
                        0.0, 0.0, 0.0,
                        0.0
                );
                serverLevel.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        point.x, point.y, point.z,
                        4,
                        0.3, 0.3, 0.3,
                        0.05
                );
                lastRingDist = d;
            }

            serverLevel.sendParticles(
                    ParticleTypes.CLOUD,
                    point.x, point.y, point.z,
                    1,
                    0.05, 0.05, 0.05,
                    0.01
            );
        }

        serverLevel.sendParticles(
            ParticleTypes.GUST_EMITTER_LARGE,
            impactPoint.x, impactPoint.y, impactPoint.z,
            1,
            0.0, 0.0, 0.0,
            0.0
        );
        serverLevel.sendParticles(
            ParticleTypes.GUST,
            impactPoint.x, impactPoint.y, impactPoint.z,
            3,
            0.2, 0.2, 0.2,
            0.05
        );
    }
}
