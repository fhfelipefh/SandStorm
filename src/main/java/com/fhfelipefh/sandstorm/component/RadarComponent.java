package com.fhfelipefh.sandstorm.component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RadarComponent {
    public record AnomalyTarget(int x, int y, int z, String anomalyType) {}

    public record ScanResult(
            AnomalyTarget target,
            double distance,
            double horizontalDistance,
            String cardinalDirection,
            double azimuthDegrees
    ) {}

    public static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double calculateHorizontalDistance(int x1, int z1, int x2, int z2) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static double calculateAzimuthDegrees(int fromX, int fromZ, int toX, int toZ) {
        double dx = toX - fromX;
        double dz = toZ - fromZ;
        if (dx == 0 && dz == 0) {
            return 0.0;
        }
        double degrees = Math.toDegrees(Math.atan2(dx, -dz));
        return (degrees % 360.0 + 360.0) % 360.0;
    }

    public static String calculateCardinalDirection(double azimuthDegrees) {
        double normalized = (azimuthDegrees % 360.0 + 360.0) % 360.0;
        if (normalized >= 337.5 || normalized < 22.5) {
            return "N";
        }
        if (normalized < 67.5) {
            return "NE";
        }
        if (normalized < 112.5) {
            return "E";
        }
        if (normalized < 157.5) {
            return "SE";
        }
        if (normalized < 202.5) {
            return "S";
        }
        if (normalized < 247.5) {
            return "SW";
        }
        if (normalized < 292.5) {
            return "W";
        }
        return "NW";
    }

    public Optional<ScanResult> findClosestAnomaly(int currentX, int currentY, int currentZ, List<AnomalyTarget> targets, double maxRange) {
        if (targets == null || targets.isEmpty()) {
            return Optional.empty();
        }

        return targets.stream()
                .map(target -> {
                    double dist = calculateDistance(currentX, currentY, currentZ, target.x(), target.y(), target.z());
                    double horizDist = calculateHorizontalDistance(currentX, currentZ, target.x(), target.z());
                    double azimuth = calculateAzimuthDegrees(currentX, currentZ, target.x(), target.z());
                    String cardinal = calculateCardinalDirection(azimuth);
                    return new ScanResult(target, dist, horizDist, cardinal, azimuth);
                })
                .filter(result -> result.horizontalDistance() <= maxRange)
                .min(Comparator.comparingDouble(ScanResult::horizontalDistance));
    }
}
