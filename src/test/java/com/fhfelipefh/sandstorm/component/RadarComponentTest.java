package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RadarComponentTest {
    private RadarComponent radar;

    @BeforeEach
    void setUp() {
        radar = new RadarComponent();
    }

    @Test
    void shouldCalculateDistanceCorrectly() {
        double dist3D = RadarComponent.calculateDistance(0, 0, 0, 3, 4, 0);
        assertEquals(5.0, dist3D, 0.001);

        double horizDist = RadarComponent.calculateHorizontalDistance(0, 0, 3, 4);
        assertEquals(5.0, horizDist, 0.001);
    }

    @Test
    void shouldCalculateAzimuthAndCardinalDirections() {
        double north = RadarComponent.calculateAzimuthDegrees(0, 0, 0, -100);
        assertEquals(0.0, north, 0.01);
        assertEquals("N", RadarComponent.calculateCardinalDirection(north));

        double east = RadarComponent.calculateAzimuthDegrees(0, 0, 100, 0);
        assertEquals(90.0, east, 0.01);
        assertEquals("E", RadarComponent.calculateCardinalDirection(east));

        double south = RadarComponent.calculateAzimuthDegrees(0, 0, 0, 100);
        assertEquals(180.0, south, 0.01);
        assertEquals("S", RadarComponent.calculateCardinalDirection(south));

        double west = RadarComponent.calculateAzimuthDegrees(0, 0, -100, 0);
        assertEquals(270.0, west, 0.01);
        assertEquals("W", RadarComponent.calculateCardinalDirection(west));

        double northEast = RadarComponent.calculateAzimuthDegrees(0, 0, 50, -50);
        assertEquals(45.0, northEast, 0.01);
        assertEquals("NE", RadarComponent.calculateCardinalDirection(northEast));
    }

    @Test
    void shouldFindClosestAnomalyWithinRange() {
        List<RadarComponent.AnomalyTarget> targets = List.of(
                new RadarComponent.AnomalyTarget(100, 30, 0, "buried_ruins"),
                new RadarComponent.AnomalyTarget(20, 45, 0, "ancient_data_core"),
                new RadarComponent.AnomalyTarget(300, 50, 0, "crashed_ship")
        );

        Optional<RadarComponent.ScanResult> result = radar.findClosestAnomaly(0, 60, 0, targets, 150.0);

        assertTrue(result.isPresent());
        assertEquals("ancient_data_core", result.get().target().anomalyType());
        assertEquals(20.0, result.get().horizontalDistance(), 0.01);
        assertEquals("E", result.get().cardinalDirection());
    }

    @Test
    void shouldReturnEmptyWhenNoAnomaliesWithinRange() {
        List<RadarComponent.AnomalyTarget> targets = List.of(
                new RadarComponent.AnomalyTarget(500, 30, 500, "buried_ruins")
        );

        Optional<RadarComponent.ScanResult> result = radar.findClosestAnomaly(0, 60, 0, targets, 100.0);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldHandleEmptyOrNullTargetList() {
        assertFalse(radar.findClosestAnomaly(0, 0, 0, null, 100.0).isPresent());
        assertFalse(radar.findClosestAnomaly(0, 0, 0, List.of(), 100.0).isPresent());
    }
}
