package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.component.RadarComponent;
import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IonSandstormTest {

    @Test
    void ionStormThresholdShouldBeReachedAtHighIntensity() {
        SandstormWeatherComponent weather = new SandstormWeatherComponent();
        weather.startSandstorm(1000, 0.90);

        for (int i = 0; i < 200; i++) {
            weather.tick();
        }

        assertTrue(weather.isActive());
        assertTrue(weather.getIntensity() >= 0.70);
    }

    @Test
    void lowIntensitySandstormShouldNotQualifyAsIonStorm() {
        SandstormWeatherComponent weather = new SandstormWeatherComponent();
        weather.startSandstorm(1000, 0.40);

        for (int i = 0; i < 200; i++) {
            weather.tick();
        }

        assertTrue(weather.isActive());
        assertFalse(weather.getIntensity() >= 0.70);
    }

    @Test
    void radarShouldIdentifyFuelSiloAndOutpostAnomalies() {
        RadarComponent radar = new RadarComponent();

        List<RadarComponent.AnomalyTarget> targets = List.of(
                new RadarComponent.AnomalyTarget(20, 35, 0, "fuel_silo"),
                new RadarComponent.AnomalyTarget(-40, 64, 0, "outpost_terminal")
        );

        Optional<RadarComponent.ScanResult> siloResult = radar.findClosestAnomaly(0, 35, 0, targets, 30.0);
        assertTrue(siloResult.isPresent());
        assertEquals("fuel_silo", siloResult.get().target().anomalyType());
        assertEquals(20.0, siloResult.get().horizontalDistance(), 0.01);
        assertEquals("E", siloResult.get().cardinalDirection());

        Optional<RadarComponent.ScanResult> outpostResult = radar.findClosestAnomaly(-30, 64, 0, targets, 30.0);
        assertTrue(outpostResult.isPresent());
        assertEquals("outpost_terminal", outpostResult.get().target().anomalyType());
        assertEquals(10.0, outpostResult.get().horizontalDistance(), 0.01);
        assertEquals("W", outpostResult.get().cardinalDirection());
    }
}
