package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.entity.CyberneticGolemEntity;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyberneticGolemVisualIntegrityArchitectureTest {

    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final Path TEXTURES_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "cybernetic_golem");
    private static final List<String> GOLEM_TEXTURES = List.of(
            "cybernetic_golem_copper.png",
            "cybernetic_golem_iron.png",
            "cybernetic_golem_gold.png",
            "cybernetic_golem_netherite.png",
            "cybernetic_golem_composite.png",
            "cybernetic_golem_heat_glow.png"
    );

    @Test
    void allGolemTexturesMustExistAndBeValid128x128Png() throws IOException {
        assertTrue(Files.exists(TEXTURES_DIR), "Directory must exist: " + TEXTURES_DIR);

        for (String fileName : GOLEM_TEXTURES) {
            Path filePath = TEXTURES_DIR.resolve(fileName);
            assertTrue(Files.exists(filePath), "Texture must exist: " + filePath);

            byte[] bytes = Files.readAllBytes(filePath);
            assertTrue(bytes.length >= 8, "File must contain at least 8 bytes: " + fileName);

            byte[] header = Arrays.copyOfRange(bytes, 0, 8);
            assertTrue(Arrays.equals(PNG_SIGNATURE, header), "File must be valid PNG: " + fileName);

            if (fileName.equals("cybernetic_golem_heat_glow.png")) {
                assertTrue(bytes.length > 200, "Heat glow texture must not be empty");
            } else {
                assertTrue(bytes.length > 2000, "Metal variant texture must be detailed and > 2000 bytes: " + fileName);
            }

            BufferedImage image = ImageIO.read(filePath.toFile());
            assertNotNull(image, "ImageIO must be able to decode: " + fileName);
            assertEquals(128, image.getWidth(), "Texture width must be exactly 128: " + fileName);
            assertEquals(128, image.getHeight(), "Texture height must be exactly 128: " + fileName);
        }
    }

    @Test
    void cyberneticGolemHeadBlockAndItemAssetsMustExist() {
        Path headBlock = Path.of("src", "main", "resources", "assets", "sandstorm", "blockstates", "cybernetic_golem_head.json");
        Path headModel = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "cybernetic_golem_head.json");
        Path headItem = Path.of("src", "main", "resources", "assets", "sandstorm", "items", "cybernetic_golem_head.json");
        Path headFrontTex = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "cybernetic_golem_head_front.png");
        Path headSideTex = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "cybernetic_golem_head_side.png");
        Path headTopTex = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "cybernetic_golem_head_top.png");
        Path headBottomTex = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "cybernetic_golem_head_bottom.png");

        assertTrue(Files.exists(headBlock), "cybernetic_golem_head.json blockstate must exist");
        assertTrue(Files.exists(headModel), "cybernetic_golem_head.json model must exist");
        assertTrue(Files.exists(headItem), "cybernetic_golem_head.json item definition must exist");
        assertTrue(Files.exists(headFrontTex), "cybernetic_golem_head_front.png must exist");
        assertTrue(Files.exists(headSideTex), "cybernetic_golem_head_side.png must exist");
        assertTrue(Files.exists(headTopTex), "cybernetic_golem_head_top.png must exist");
        assertTrue(Files.exists(headBottomTex), "cybernetic_golem_head_bottom.png must exist");
    }

    @Test
    void rendererAndModelMustReferenceAllTexturesAnd128x128Layer() throws IOException {
        Path rendererPath = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "CyberneticGolemRenderer.java");
        assertTrue(Files.exists(rendererPath), "CyberneticGolemRenderer must exist");
        String rendererContent = Files.readString(rendererPath);

        for (String textureName : GOLEM_TEXTURES) {
            assertTrue(rendererContent.contains(textureName), "Renderer must reference texture: " + textureName);
        }

        Path modelPath = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "CyberneticGolemModel.java");
        assertTrue(Files.exists(modelPath), "CyberneticGolemModel must exist");
        String modelContent = Files.readString(modelPath);
        assertTrue(modelContent.contains("128, 128"), "Model must specify 128, 128 texture mapping");
    }

    @Test
    void entityMustSupportPermanentOverdriveMethods() throws NoSuchMethodException {
        assertNotNull(CyberneticGolemEntity.class.getMethod("setPermanentOverdrive", boolean.class));
        assertNotNull(CyberneticGolemEntity.class.getMethod("isPermanentOverdrive"));
    }
}
