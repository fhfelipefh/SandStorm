package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.component.ThermalComponent;
import com.fhfelipefh.sandstorm.component.VibrationEmitterComponent;
import com.fhfelipefh.sandstorm.metrics.GameMetricsTracker;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectureTest {

    private static final List<Class<?>> COMPONENT_CLASSES = List.of(
            EnergyStorageComponent.class,
            ThermalComponent.class,
            VibrationEmitterComponent.class,
            SuitPowerComponent.class,
            com.fhfelipefh.sandstorm.component.SandstormWeatherComponent.class,
            com.fhfelipefh.sandstorm.component.BrackishWaterComponent.class,
            com.fhfelipefh.sandstorm.component.SeismicTrackerComponent.class,
            com.fhfelipefh.sandstorm.component.RecipeProcessorComponent.class
    );

    private static final List<Class<?>> METRICS_CLASSES = List.of(
            GameMetricsTracker.class
    );



    @Test
    void componentsMustBePublic() {
        for (Class<?> clazz : COMPONENT_CLASSES) {
            assertTrue(Modifier.isPublic(clazz.getModifiers()));
        }
    }

    @Test
    void componentsMustFollowNamingConvention() {
        for (Class<?> clazz : COMPONENT_CLASSES) {
            assertTrue(clazz.getSimpleName().endsWith("Component"));
        }
    }

    @Test
    void metricsMustFollowNamingConvention() {
        for (Class<?> clazz : METRICS_CLASSES) {
            assertTrue(clazz.getSimpleName().endsWith("Tracker"));
        }
    }

    @Test
    void componentsMustNotDependOnClient() {
        for (Class<?> clazz : COMPONENT_CLASSES) {
            assertDoesNotReferencePackage(clazz, "com.fhfelipefh.sandstorm.client");
            assertDoesNotReferencePackage(clazz, "net.minecraft.client");
        }
    }

    @Test
    void componentsMustNotDependOnCore() {
        for (Class<?> clazz : COMPONENT_CLASSES) {
            assertDoesNotReferencePackage(clazz, "com.fhfelipefh.sandstorm.core");
        }
    }

    @Test
    void metricsMustNotDependOnClient() {
        for (Class<?> clazz : METRICS_CLASSES) {
            assertDoesNotReferencePackage(clazz, "com.fhfelipefh.sandstorm.client");
            assertDoesNotReferencePackage(clazz, "net.minecraft.client");
        }
    }

    private void assertDoesNotReferencePackage(Class<?> clazz, String forbiddenPackage) {
        for (Field field : clazz.getDeclaredFields()) {
            assertFalse(field.getType().getName().startsWith(forbiddenPackage));
        }
        for (Method method : clazz.getDeclaredMethods()) {
            assertFalse(method.getReturnType().getName().startsWith(forbiddenPackage));
            for (Class<?> paramType : method.getParameterTypes()) {
                assertFalse(paramType.getName().startsWith(forbiddenPackage));
            }
        }
    }
}
