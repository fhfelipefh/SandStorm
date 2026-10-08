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
import net.minecraft.client.Camera;
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
    private static final Path MEGAZORD_CAMERA_MIXIN_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "MegazordCameraMixin.java"
    );
    private static final Path MEGAZORD_ARM_RENDERER_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "MegazordFirstPersonArmRenderer.java"
    );
    private static final Path SHOWCASE_TEST_JAVA = Path.of(
            "src", "test", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseWorldSetupTest.java"
    );
    private static final Path CLIENT_MIXINS_JSON = Path.of(
            "src", "client", "resources", "sandstorm.client.mixins.json"
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
    void megazordMustPlacePassengerInsideCockpit() throws Exception {
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
        assertEquals(2.68, attachment.y, 0.001, "Megazord passenger feet must be inside the chest cockpit");
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
        assertNotNull(body.getChild("leftThigh"));
        assertNotNull(body.getChild("rightThigh"));
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
    void firstPersonMegazordViewMustRenderPilotBodyInsteadOfVehicleGeometry() throws IOException {
        assertTrue(Files.exists(MEGAZORD_RENDERER_JAVA), "MegazordRenderer.java must exist");
        String rendererCode = Files.readString(MEGAZORD_RENDERER_JAVA);
        assertTrue(rendererCode.contains("isFirstPerson()"), "Megazord renderer must detect first-person camera");
        assertTrue(rendererCode.contains("getControllingPassenger()"), "Megazord renderer must identify the local pilot");
        assertTrue(rendererCode.contains("return false"), "Megazord renderer must hide the body for the local first-person pilot");

        assertTrue(Files.exists(MEGAZORD_CAMERA_MIXIN_JAVA), "MegazordCameraMixin.java must exist");
        String cameraCode = Files.readString(MEGAZORD_CAMERA_MIXIN_JAVA);
        assertTrue(cameraCode.contains("opticPosition"), "Megazord camera must use the optic position profile");
        assertTrue(cameraCode.contains("setPosition"), "Megazord camera must be positioned explicitly");
        assertTrue(cameraCode.contains("isFirstPerson()"), "Megazord camera must only move the first-person view");

        assertTrue(Files.exists(MEGAZORD_ARM_RENDERER_JAVA), "MegazordFirstPersonArmRenderer.java must exist");
        String armCode = Files.readString(MEGAZORD_ARM_RENDERER_JAVA);
        assertTrue(armCode.contains("renderRightArm"), "Megazord must provide a mechanical right arm");
        assertTrue(armCode.contains("renderLeftArm"), "Megazord must provide a mechanical left arm");
        assertTrue(armCode.contains("isFirstPerson()"), "Megazord arms must only replace the local first-person hands");

        String modelCode = Files.readString(MEGAZORD_MODEL_JAVA);
        assertTrue(modelCode.contains("lowerFrame"), "Megazord torso must have a cockpit frame reaching the leg origins");
        assertTrue(modelCode.contains("leftThigh"), "Megazord must define a left thigh between torso and lower leg");
        assertTrue(modelCode.contains("rightThigh"), "Megazord must define a right thigh between torso and lower leg");
        assertTrue(modelCode.contains("1.0f, -12.05f, -3.5f, 7.0f, 12.1f, 7.0f"), "Megazord thigh must overlap torso and lower leg to avoid a gap");
        assertFalse(modelCode.contains(".texOffs(0, 100)"), "Megazord thighs must not use the transparent texture area");
        assertFalse(modelCode.contains(".texOffs(26, 100)"), "Megazord thighs must not use the transparent texture area");
        assertTrue(modelCode.contains(".texOffs(18, 45)"), "Left thigh must use the matching leg armor texture");
        assertTrue(modelCode.contains(".texOffs(142, 45)"), "Right thigh must use the matching leg armor texture");
        assertTrue(modelCode.contains(".texOffs(172, 45).addBox(1.5f, -5.0f, -4.5f, 6.0f, 3.0f, 2.0f)"), "Left thigh must use the mirrored knee plate texture");
        assertTrue(modelCode.contains(".texOffs(172, 45).addBox(-7.5f, -5.0f, -4.5f, 6.0f, 3.0f, 2.0f)"), "Right thigh must have a front knee plate");
        assertTrue(modelCode.contains(".texOffs(172, 45).addBox(-3.0f, 7.0f, -4.5f, 6.0f, 3.0f, 2.0f)"), "Left lower leg must use the mirrored knee plate texture");
        assertTrue(modelCode.contains("14.0f, 2.0f, 12.0f"), "Flight wings must be horizontal side panels with an aircraft wing chord");
        assertTrue(modelCode.contains("this.flightWings.xRot = 0.0f"), "Flight wings must remain level on the ground and in level flight");
    }

    @Test
    void megazordCameraMixinMustTargetTheMinecraft26UpdateApi() throws IOException {
        assertTrue(Arrays.stream(Camera.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("update") && method.getParameterCount() == 1),
                "Minecraft 26.3 Camera must expose update(DeltaTracker)");
        String cameraCode = Files.readString(MEGAZORD_CAMERA_MIXIN_JAVA);
        assertTrue(cameraCode.contains("@Inject(method = \"update\""),
                "Megazord camera mixin must target Camera.update, not the removed setup method");
        assertTrue(cameraCode.contains("DeltaTracker"),
                "Megazord camera mixin must use the current camera tick API");
    }

    @Test
    void megazordPilotPoseMixinMustDisableVanillaPassengerPose() throws IOException {
        Path poseMixin = Path.of(
                "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "MegazordPilotPoseMixin.java"
        );
        assertTrue(Files.exists(poseMixin), "MegazordPilotPoseMixin.java must exist");
        String poseCode = Files.readString(poseMixin);
        assertTrue(poseCode.contains("state.isPassenger = false"),
                "Megazord pilots must render standing instead of using the seated passenger pose");
        assertTrue(poseCode.contains("state.isInvisible = true"),
                "Megazord pilot body must not clip through the external armor render");
        assertTrue(poseCode.contains("state.isInvisibleToPlayer = true"),
                "Megazord pilot must be fully hidden instead of rendered translucently");
    }

    @Test
    void localMegazordMustNotBlockPilotBlockRaycasts() throws IOException {
        Path pickabilityMixin = Path.of(
                "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "MegazordPickabilityMixin.java"
        );
        assertTrue(Files.exists(pickabilityMixin), "MegazordPickabilityMixin.java must exist");
        String pickabilityCode = Files.readString(pickabilityMixin);
        assertTrue(pickabilityCode.contains("isPickable"), "Megazord pilot mixin must control entity raycast pickability");
        assertTrue(pickabilityCode.contains("setReturnValue(false)"),
                "The local pilot vehicle must not intercept the pilot block raycast");

        Path blockRaycastMixin = Path.of(
                "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "MegazordBlockRaycastMixin.java"
        );
        assertTrue(Files.exists(blockRaycastMixin), "MegazordBlockRaycastMixin.java must exist");
        String blockRaycastCode = Files.readString(blockRaycastMixin);
        assertTrue(blockRaycastCode.contains("method = \"pick\""), "Megazord pilot must repair the Minecraft block pick result");
        assertTrue(blockRaycastCode.contains("ClipContext"), "Megazord pilot block targeting must use a block clip");

        Path itemTransformMixin = Path.of(
                "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "MegazordItemTransformMixin.java"
        );
        assertTrue(Files.exists(itemTransformMixin), "MegazordItemTransformMixin.java must exist");
        String itemTransformCode = Files.readString(itemTransformMixin);
        assertTrue(itemTransformCode.contains("submitArmWithItem"), "Megazord items must use the first-person item transform hook");
        assertTrue(itemTransformCode.contains("poseStack.translate"), "Megazord items must be offset toward mechanical hands");
    }

    @Test
    void clientMixinConfigurationMustReferenceExistingMixinSources() throws IOException {
        assertTrue(Files.exists(CLIENT_MIXINS_JSON), "Client mixin configuration must exist");
        JsonObject config = JsonParser.parseString(Files.readString(CLIENT_MIXINS_JSON)).getAsJsonObject();
        assertTrue(config.has("client"), "Client mixin configuration must declare client mixins");

        for (var entry : config.getAsJsonArray("client")) {
            String className = entry.getAsString();
            Path source = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", className + ".java");
            assertTrue(Files.exists(source), "Client mixin source must exist: " + className);
        }
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
