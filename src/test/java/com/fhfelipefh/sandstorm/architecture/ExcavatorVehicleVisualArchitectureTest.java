package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.client.renderer.ExcavatorVehicleModel;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcavatorVehicleVisualArchitectureTest {

    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final Path TEXTURE_PATH = Path.of(
            "src", "main", "resources", "assets", "sandstorm", "textures", "entity", "excavator_vehicle", "excavator_vehicle.png"
    );
    private static final Path BBMODEL_PATH = Path.of(
            "models", "blockbench", "excavator_vehicle.bbmodel"
    );
    private static final Path MODEL_JAVA_PATH = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "ExcavatorVehicleModel.java"
    );
    private static final Path RENDERER_JAVA_PATH = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "ExcavatorVehicleRenderer.java"
    );
    private static final Path ENTITIES_JAVA_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "SandStormEntities.java"
    );
    private static final Path CLIENT_INIT_JAVA_PATH = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java"
    );
    private static final Path SOUND_OGG_PATH = Path.of(
            "src", "main", "resources", "assets", "sandstorm", "sounds", "excavator_engine.ogg"
    );

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void excavatorTextureMustBeValid256x256Png() throws IOException {
        assertTrue(Files.exists(TEXTURE_PATH), "Texture file must exist: " + TEXTURE_PATH);

        byte[] bytes = Files.readAllBytes(TEXTURE_PATH);
        assertTrue(bytes.length >= 8, "File must contain at least 8 bytes header");

        byte[] header = Arrays.copyOfRange(bytes, 0, 8);
        assertTrue(Arrays.equals(PNG_SIGNATURE, header), "File must be valid PNG format");
        assertTrue(bytes.length > 1500, "Texture must be rich and high resolution (> 1500 bytes)");

        BufferedImage image = ImageIO.read(TEXTURE_PATH.toFile());
        assertNotNull(image, "ImageIO must successfully decode the PNG");
        assertEquals(256, image.getWidth(), "Texture width must be exactly 256");
        assertEquals(256, image.getHeight(), "Texture height must be exactly 256");
    }

    @Test
    void excavatorModelMustDefine256x256AndLayerHierarchy() throws IOException {
        assertTrue(Files.exists(MODEL_JAVA_PATH), "ExcavatorVehicleModel.java must exist");
        String modelCode = Files.readString(MODEL_JAVA_PATH);

        assertTrue(modelCode.contains("256, 256"), "Model must specify 256x256 texture dimensions");
        assertTrue(modelCode.contains("chassis"), "Model must contain chassis part");
        assertTrue(modelCode.contains("drillHead"), "Model must contain drillHead part");

        LayerDefinition layer = ExcavatorVehicleModel.createBodyLayer();
        assertNotNull(layer, "createBodyLayer must construct valid LayerDefinition");

        ModelPart root = layer.bakeRoot();
        assertNotNull(root, "bakeRoot must bake root ModelPart");
        assertNotNull(root.getChild("chassis"), "chassis part must be present in baked tree");
        assertNotNull(root.getChild("chassis").getChild("drillHead"), "drillHead part must be child of chassis");
    }

    @Test
    void excavatorBlockbenchModelMustSynchronizeAllElements() throws IOException {
        assertTrue(Files.exists(BBMODEL_PATH), "excavator_vehicle.bbmodel must exist");

        String json = Files.readString(BBMODEL_PATH);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(root.has("resolution"), "bbmodel must contain resolution block");

        JsonObject resolution = root.getAsJsonObject("resolution");
        assertEquals(256, resolution.get("width").getAsInt(), "bbmodel resolution width must be 256");
        assertEquals(256, resolution.get("height").getAsInt(), "bbmodel resolution height must be 256");

        assertTrue(root.has("elements"), "bbmodel must contain elements array");
        int count = root.getAsJsonArray("elements").size();
        assertTrue(count >= 20, "bbmodel must contain rich detailed elements (>= 20)");
    }

    @Test
    void excavatorRendererMustBindCorrectTextureAndModel() throws IOException {
        assertTrue(Files.exists(RENDERER_JAVA_PATH), "ExcavatorVehicleRenderer.java must exist");
        String rendererCode = Files.readString(RENDERER_JAVA_PATH);

        assertTrue(rendererCode.contains("textures/entity/excavator_vehicle/excavator_vehicle.png"),
                "Renderer must reference 256x256 excavator_vehicle texture");
        assertTrue(rendererCode.contains("ExcavatorVehicleModel"),
                "Renderer must pair with ExcavatorVehicleModel");
    }

    @Test
    void excavatorAudioAndEntitiesMustBeRegistered() throws IOException {
        assertTrue(Files.exists(SOUND_OGG_PATH), "excavator_engine.ogg sound must exist");
        assertNotNull(SandStormSoundEvents.EXCAVATOR_ENGINE, "EXCAVATOR_ENGINE sound must be registered");

        assertTrue(Files.exists(ENTITIES_JAVA_PATH), "SandStormEntities.java must exist");
        String entitiesCode = Files.readString(ENTITIES_JAVA_PATH);
        assertTrue(entitiesCode.contains("EXCAVATOR_VEHICLE"),
                "EXCAVATOR_VEHICLE must be declared in SandStormEntities");
        assertTrue(entitiesCode.contains("FabricDefaultAttributeRegistry.register(EXCAVATOR_VEHICLE, ExcavatorVehicleEntity.createAttributes())"),
                "ExcavatorVehicleEntity attributes must be registered");

        assertTrue(Files.exists(CLIENT_INIT_JAVA_PATH), "SandStormClient.java must exist");
        String clientCode = Files.readString(CLIENT_INIT_JAVA_PATH);
        assertTrue(clientCode.contains("EntityRendererRegistry.register(SandStormEntities.EXCAVATOR_VEHICLE, ExcavatorVehicleRenderer::new)"),
                "ExcavatorVehicleRenderer must be registered in client initialization");
    }
}
