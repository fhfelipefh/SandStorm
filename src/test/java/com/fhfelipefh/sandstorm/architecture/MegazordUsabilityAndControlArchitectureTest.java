package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.client.renderer.AutonomousSonicTurretModel;
import com.fhfelipefh.sandstorm.client.renderer.CargoDroneModel;
import com.fhfelipefh.sandstorm.client.renderer.ExcavatorVehicleModel;
import com.fhfelipefh.sandstorm.client.renderer.MegazordModel;
import com.fhfelipefh.sandstorm.client.renderer.SandboardModel;
import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MegazordUsabilityAndControlArchitectureTest {

    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final Path TEXTURES_DIR = Path.of(
            "src", "main", "resources", "assets", "sandstorm", "textures", "entity", "megazord"
    );
    private static final Path BBMODEL_PATH = Path.of(
            "models", "blockbench", "megazord.bbmodel"
    );
    private static final Path ENTITIES_JAVA = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "SandStormEntities.java"
    );
    private static final Path MEGAZORD_MODEL_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "MegazordModel.java"
    );
    private static final Path EXCAVATOR_MODEL_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "ExcavatorVehicleModel.java"
    );
    private static final Path MACHINE_HUD_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "hud", "MachineCockpitHudOverlay.java"
    );
    private static final Path CLIENT_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java"
    );
    private static final Path DEBUG_COMMAND_JAVA = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "command", "SandstormDebugCommand.java"
    );
    private static final Path MEGAZORD_RENDERER_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "MegazordRenderer.java"
    );
    private static final Path SHOWCASE_TEST_JAVA = Path.of(
            "src", "test", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseWorldSetupTest.java"
    );
    private static final List<String> MEGAZORD_TEXTURES = List.of(
            "megazord.png",
            "megazord_aero_striker.png",
            "megazord_abyssal_sub.png",
            "megazord_apex_dominator.png"
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
    void megazordMustElevatePassengerToCockpitHeight() throws Exception {
        Unsafe unsafe = getUnsafe();
        MegazordEntity megazord = (MegazordEntity) unsafe.allocateInstance(MegazordEntity.class);

        Method method = MegazordEntity.class.getDeclaredMethod(
                "getPassengerAttachmentPoint",
                Entity.class,
                EntityDimensions.class,
                float.class
        );
        method.setAccessible(true);

        Vec3 attachment = (Vec3) method.invoke(megazord, null, null, 1.0f);
        assertTrue(attachment.y >= 3.5, "Megazord passenger seat must elevate rider at least 3.5 blocks high in cockpit");
        assertTrue(attachment.z > 0.0, "Megazord passenger seat must be aligned forward in chest cockpit");
    }

    @Test
    void megazordAttributesMustDefineStepHeightForSmoothMobility() {
        AttributeSupplier.Builder builder = MegazordEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        double stepHeight = supplier.getBaseValue(Attributes.STEP_HEIGHT);
        assertTrue(stepHeight >= 1.5, "Megazord step height must be at least 1.5 blocks for obstacle traversal");
    }

    @Test
    void megazordEntityMustImplementRidingControls() throws NoSuchMethodException {
        assertNotNull(MegazordEntity.class.getDeclaredMethod("getControllingPassenger"));
        assertNotNull(MegazordEntity.class.getDeclaredMethod("tickRidden", Player.class, Vec3.class));
        assertNotNull(MegazordEntity.class.getDeclaredMethod("getRiddenInput", Player.class, Vec3.class));
        assertNotNull(MegazordEntity.class.getDeclaredMethod("getRiddenSpeed", Player.class));
    }

    @Test
    void megazordEntityRegistrationMustSpecifyHitboxAndEyeHeight() throws IOException {
        assertTrue(Files.exists(ENTITIES_JAVA), "SandStormEntities.java must exist");
        String code = Files.readString(ENTITIES_JAVA);

        assertTrue(code.contains(".sized(3.5f, 5.0f)"), "Megazord must have 3.5x5.0 hitbox");
        assertTrue(code.contains(".eyeHeight(4.3f)"), "Megazord must have eye height 4.3 blocks for high camera");
    }

    @Test
    void megazordModelMustDefine256x256AndRichHierarchy() throws IOException {
        assertTrue(Files.exists(MEGAZORD_MODEL_JAVA), "MegazordModel.java must exist");
        String code = Files.readString(MEGAZORD_MODEL_JAVA);
        assertTrue(code.contains("256, 256"), "MegazordModel must use 256x256 texture mapping");

        LayerDefinition layer = MegazordModel.createBodyLayer();
        assertNotNull(layer);

        ModelPart root = layer.bakeRoot();
        assertNotNull(root.getChild("body"));
        assertNotNull(root.getChild("leftLeg"));
        assertNotNull(root.getChild("rightLeg"));

        ModelPart body = root.getChild("body");
        assertNotNull(body.getChild("head"));
        assertNotNull(body.getChild("leftArm"));
        assertNotNull(body.getChild("rightArm"));
        assertNotNull(body.getChild("flightWings"));
        assertNotNull(body.getChild("overdriveBoosters"));
    }

    @Test
    void allMegazordTexturesMustExistAndBeValid256x256Png() throws IOException {
        assertTrue(Files.exists(TEXTURES_DIR), "Megazord texture directory must exist");

        for (String file : MEGAZORD_TEXTURES) {
            Path path = TEXTURES_DIR.resolve(file);
            assertTrue(Files.exists(path), "Texture must exist: " + file);

            byte[] bytes = Files.readAllBytes(path);
            assertTrue(bytes.length >= 8);
            byte[] header = Arrays.copyOfRange(bytes, 0, 8);
            assertTrue(Arrays.equals(PNG_SIGNATURE, header), "Must be valid PNG format: " + file);
            assertTrue(bytes.length > 1500, "Texture must be detailed (> 1500 bytes): " + file);

            BufferedImage img = ImageIO.read(path.toFile());
            assertNotNull(img, "ImageIO must decode texture: " + file);
            assertEquals(256, img.getWidth(), "Texture width must be 256: " + file);
            assertEquals(256, img.getHeight(), "Texture height must be 256: " + file);
        }
    }

    @Test
    void megazordBlockbenchModelMustSynchronizeAllElements() throws IOException {
        assertTrue(Files.exists(BBMODEL_PATH), "megazord.bbmodel must exist");

        String json = Files.readString(BBMODEL_PATH);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(root.has("resolution"));

        JsonObject resolution = root.getAsJsonObject("resolution");
        assertEquals(256, resolution.get("width").getAsInt());
        assertEquals(256, resolution.get("height").getAsInt());

        assertTrue(root.has("elements"));
        int elements = root.getAsJsonArray("elements").size();
        assertTrue(elements >= 30, "megazord.bbmodel must have at least 30 detailed elements");
    }

    @Test
    void machineModelsMustDefineValidNonEmptyGeometry() {
        assertNotNull(CargoDroneModel.createBodyLayer().bakeRoot());
        assertNotNull(AutonomousSonicTurretModel.createBodyLayer().bakeRoot());
        assertNotNull(ExcavatorVehicleModel.createBodyLayer().bakeRoot());
        assertNotNull(SandboardModel.createBodyLayer().bakeRoot());
    }

    @Test
    void vehiclesAndMegazordsMustDisableVanillaHorseHearts() throws Exception {
        Unsafe unsafe = getUnsafe();
        ExcavatorVehicleEntity excavator = (ExcavatorVehicleEntity) unsafe.allocateInstance(ExcavatorVehicleEntity.class);
        MegazordEntity megazord = (MegazordEntity) unsafe.allocateInstance(MegazordEntity.class);

        Method excavatorHealth = ExcavatorVehicleEntity.class.getDeclaredMethod("showVehicleHealth");
        excavatorHealth.setAccessible(true);
        assertFalse((Boolean) excavatorHealth.invoke(excavator), "Excavator must disable vanilla horse hearts");

        Method megazordHealth = MegazordEntity.class.getDeclaredMethod("showVehicleHealth");
        megazordHealth.setAccessible(true);
        assertFalse((Boolean) megazordHealth.invoke(megazord), "Megazord must disable vanilla horse hearts");
    }

    @Test
    void excavatorModelMustOrientDrillForward() throws IOException {
        assertTrue(Files.exists(EXCAVATOR_MODEL_JAVA), "ExcavatorVehicleModel.java must exist");
        String code = Files.readString(EXCAVATOR_MODEL_JAVA);
        assertTrue(code.contains("Math.PI"), "Excavator chassis must be rotated Math.PI so the drill faces forward");
    }

    @Test
    void machineCockpitHudOverlayMustBePresentAndRegistered() throws IOException {
        assertTrue(Files.exists(MACHINE_HUD_JAVA), "MachineCockpitHudOverlay.java must exist");
        assertTrue(Files.exists(CLIENT_JAVA), "SandStormClient.java must exist");
        String clientCode = Files.readString(CLIENT_JAVA);
        assertTrue(clientCode.contains("MachineCockpitHudOverlay.initialize()"), "SandStormClient must initialize cockpit HUD");
    }

    @Test
    void megazordCollectionCommandMustProvideAllVariantsAndModules() throws IOException {
        assertTrue(Files.exists(DEBUG_COMMAND_JAVA), "SandstormDebugCommand.java must exist");
        String code = Files.readString(DEBUG_COMMAND_JAVA);
        assertTrue(code.contains("megazord_collection"), "A dedicated Megazord collection command must exist");
        assertTrue(code.contains("spawnMegazord(level"), "The collection command must spawn the collection");
        assertTrue(code.contains("MEGAZORD_FLIGHT_MODULE"), "The collection must provide the flight module kit");
        assertTrue(code.contains("MEGAZORD_SUBMERSIBLE_HULL"), "The collection must provide the submersible module kit");
        assertTrue(code.contains("MEGAZORD_TACTICAL_OVERDRIVE"), "The collection must provide the overdrive module kit");
    }

    @Test
    void megazordMustSupportHighJumpAndFastRiddenMovement() throws IOException {
        String code = Files.readString(Path.of(
                "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "MegazordEntity.java"
        ));
        assertTrue(code.contains("player.isJumping()"), "Megazord must respond to the rider jump input");
        assertTrue(code.contains("1.05"), "Standard Megazord jump impulse must be high");
        assertTrue(code.contains("1.85f"), "Megazord ridden speed must be significantly increased");
    }

    @Test
    void firstPersonMegazordViewMustHideTheOwnBody() throws IOException {
        assertTrue(Files.exists(MEGAZORD_RENDERER_JAVA), "MegazordRenderer.java must exist");
        String code = Files.readString(MEGAZORD_RENDERER_JAVA);
        assertTrue(code.contains("isFirstPerson()"), "Megazord renderer must detect first-person camera");
        assertTrue(code.contains("getControllingPassenger()"), "Megazord renderer must identify the local pilot");
        assertTrue(code.contains("return false"), "Megazord renderer must hide the body for the local first-person pilot");
    }

    @Test
    void showcaseWorldMustProvideMiningLayersForVehicleTesting() throws IOException {
        assertTrue(Files.exists(SHOWCASE_TEST_JAVA), "ShowcaseWorldSetupTest.java must exist");
        String testCode = Files.readString(SHOWCASE_TEST_JAVA);
        assertTrue(testCode.contains("minecraft:bedrock"), "Showcase flat world must contain bedrock layer");
        assertTrue(testCode.contains("minecraft:deepslate"), "Showcase flat world must contain deepslate layer");
        assertTrue(testCode.contains("minecraft:stone"), "Showcase flat world must contain stone layer");
        assertTrue(testCode.contains("minecraft:sandstone"), "Showcase flat world must contain sandstone layer");
        assertTrue(testCode.contains("minecraft:sand"), "Showcase flat world must contain sand layer");
        assertTrue(testCode.contains("new int[]{0, 21, 0}"), "Showcase spawn must be elevated onto sand layer at Y=21");
    }
}
