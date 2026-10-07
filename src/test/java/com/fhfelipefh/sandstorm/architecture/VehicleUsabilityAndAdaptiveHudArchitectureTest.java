package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.entity.SandboardEntity;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleUsabilityAndAdaptiveHudArchitectureTest {

    private static final List<Class<? extends LivingEntity>> VEHICLE_CLASSES = List.of(
            ExcavatorVehicleEntity.class,
            MegazordEntity.class,
            SandboardEntity.class
    );

    private static final Path HUD_DIR = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "hud"
    );

    private static final Path EXCAVATOR_MODEL_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "ExcavatorVehicleModel.java"
    );

    private static final Pattern COMPONENT_LITERAL_PATTERN = Pattern.compile(
            "Component\\.literal\\(\"([^\"]*)\"\\)"
    );

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    @Test
    void allRideableVehiclesMustOverridePassengerAttachmentPointAndMountRightSide() throws Exception {
        Unsafe unsafe = getUnsafe();

        for (Class<? extends LivingEntity> clazz : VEHICLE_CLASSES) {
            Method method = clazz.getDeclaredMethod(
                    "getPassengerAttachmentPoint",
                    Entity.class,
                    EntityDimensions.class,
                    float.class
            );
            assertNotNull(method, clazz.getSimpleName() + " must override getPassengerAttachmentPoint");
            method.setAccessible(true);

            LivingEntity instance = (LivingEntity) unsafe.allocateInstance(clazz);
            Vec3 attachment = (Vec3) method.invoke(instance, null, null, 1.0f);

            assertNotNull(attachment, clazz.getSimpleName() + " attachment point must not be null");
            assertTrue(attachment.y > 0.0, clazz.getSimpleName() + " attachment Y must be elevated above ground");
            assertTrue(Math.abs(attachment.x) < 0.001, clazz.getSimpleName() + " passenger must be centered horizontally on X axis");
        }

        MegazordEntity megazord = (MegazordEntity) unsafe.allocateInstance(MegazordEntity.class);
        Method megazordMethod = MegazordEntity.class.getDeclaredMethod("getPassengerAttachmentPoint", Entity.class, EntityDimensions.class, float.class);
        megazordMethod.setAccessible(true);
        Vec3 megazordSeat = (Vec3) megazordMethod.invoke(megazord, null, null, 1.0f);
        assertTrue(megazordSeat.y >= 3.5, "Megazord rider must be elevated to upper cockpit (Y >= 3.5)");
        assertTrue(megazordSeat.z > 0.0, "Megazord rider must face forward toward chest canopy (Z > 0)");

        ExcavatorVehicleEntity excavator = (ExcavatorVehicleEntity) unsafe.allocateInstance(ExcavatorVehicleEntity.class);
        Method excavatorMethod = ExcavatorVehicleEntity.class.getDeclaredMethod("getPassengerAttachmentPoint", Entity.class, EntityDimensions.class, float.class);
        excavatorMethod.setAccessible(true);
        Vec3 excavatorSeat = (Vec3) excavatorMethod.invoke(excavator, null, null, 1.0f);
        assertTrue(excavatorSeat.y >= 0.7 && excavatorSeat.y <= 1.2, "Excavator rider must sit inside the cab (0.7 <= Y <= 1.2)");

        SandboardEntity sandboard = (SandboardEntity) unsafe.allocateInstance(SandboardEntity.class);
        Method sandboardMethod = SandboardEntity.class.getDeclaredMethod("getPassengerAttachmentPoint", Entity.class, EntityDimensions.class, float.class);
        sandboardMethod.setAccessible(true);
        Vec3 sandboardSeat = (Vec3) sandboardMethod.invoke(sandboard, null, null, 1.0f);
        assertTrue(sandboardSeat.y >= 0.1 && sandboardSeat.y <= 0.3, "Sandboard rider must stand on board deck (0.1 <= Y <= 0.3)");
    }

    @Test
    void allRideableVehiclesMustSynchronizeRiderControls() {
        for (Class<? extends LivingEntity> clazz : VEHICLE_CLASSES) {
            assertNotNull(getMethodOrNull(clazz, "tickRidden", Player.class, Vec3.class),
                    clazz.getSimpleName() + " must implement tickRidden for steering synchronization");
            assertNotNull(getMethodOrNull(clazz, "getRiddenInput", Player.class, Vec3.class),
                    clazz.getSimpleName() + " must implement getRiddenInput for player input capture");
            assertNotNull(getMethodOrNull(clazz, "getControllingPassenger"),
                    clazz.getSimpleName() + " must implement getControllingPassenger");
        }
    }

    @Test
    void allRideableVehiclesMustDisableVanillaHorseHearts() throws Exception {
        Unsafe unsafe = getUnsafe();

        for (Class<? extends LivingEntity> clazz : VEHICLE_CLASSES) {
            Method healthMethod = clazz.getDeclaredMethod("showVehicleHealth");
            assertNotNull(healthMethod, clazz.getSimpleName() + " must override showVehicleHealth");
            healthMethod.setAccessible(true);

            LivingEntity instance = (LivingEntity) unsafe.allocateInstance(clazz);
            boolean showHealth = (Boolean) healthMethod.invoke(instance);
            assertFalse(showHealth, clazz.getSimpleName() + " must return false for showVehicleHealth to hide horse hearts");
        }
    }

    @Test
    void excavatorModelMustAlignDrillForwardWithChassisRotation() throws IOException {
        assertTrue(Files.exists(EXCAVATOR_MODEL_JAVA), "ExcavatorVehicleModel.java must exist");
        String code = Files.readString(EXCAVATOR_MODEL_JAVA);
        assertTrue(code.contains("Math.PI"), "Excavator model must apply Math.PI Y-rotation to chassis so drill faces forward");
    }

    @Test
    void allHudOverlaysMustImplementAdaptiveScreenResolutionScaling() throws IOException {
        assertTrue(Files.exists(HUD_DIR), "HUD directory must exist");

        try (Stream<Path> files = Files.walk(HUD_DIR)) {
            List<Path> hudFiles = files.filter(p -> p.toString().endsWith(".java")).toList();
            assertFalse(hudFiles.isEmpty(), "At least one HUD overlay must exist");

            for (Path hudPath : hudFiles) {
                String code = Files.readString(hudPath);
                boolean queriesHeight = code.contains("guiHeight()");
                boolean queriesWidth = code.contains("guiWidth()");
                assertTrue(queriesHeight || queriesWidth,
                        hudPath.getFileName() + " must query dynamic screen dimensions via guiWidth() or guiHeight()");

                boolean hasAdaptiveMatrixScaling = code.contains("pushMatrix()") && code.contains("scale(") && code.contains("popMatrix()");
                assertTrue(hasAdaptiveMatrixScaling,
                        hudPath.getFileName() + " must support adaptive matrix scaling for low-resolution or high GUI scales");

                boolean boundsCoordinates = code.contains("Math.max") || code.contains("Math.min") || code.contains("clamp");
                assertTrue(boundsCoordinates,
                        hudPath.getFileName() + " must bound screen coordinates against edges with Math.max/min/clamp");
            }
        }
    }

    @Test
    void hudOverlaysMustNotContainRawNewlinesOrOverflowingLines() throws IOException {
        try (Stream<Path> files = Files.walk(HUD_DIR)) {
            List<Path> hudFiles = files.filter(p -> p.toString().endsWith(".java")).toList();

            for (Path hudPath : hudFiles) {
                String code = Files.readString(hudPath);
                assertFalse(code.contains("\\n"), hudPath.getFileName() + " must not contain raw \\n escape sequences");
                assertFalse(code.contains("\\r"), hudPath.getFileName() + " must not contain raw \\r escape sequences");

                Matcher matcher = COMPONENT_LITERAL_PATTERN.matcher(code);
                while (matcher.find()) {
                    String literal = matcher.group(1);
                    String cleanText = literal.replaceAll("\\\\u00A7[0-9a-fk-or]", "");
                    assertTrue(cleanText.length() <= 50,
                            "HUD text line exceeds 50 visible chars in " + hudPath.getFileName() + ": " + cleanText);
                }
            }
        }
    }

    private Method getMethodOrNull(Class<?> clazz, String name, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
