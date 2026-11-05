package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QuantumSleeperPodModelSymmetryTest {

    @Test
    public void testDoorGeometrySymmetry() {
        LayerDefinition layer = QuantumSleeperPodModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        QuantumSleeperPodModel model = new QuantumSleeperPodModel(root);

        ModelPart leftDoor = model.getLeftDoor();
        ModelPart rightDoor = model.getRightDoor();
        ModelPart sideGlass = model.getSideGlass();

        assertNotNull(leftDoor);
        assertNotNull(rightDoor);
        assertNotNull(sideGlass);

        RandomSource random = RandomSource.create();
        ModelPart.Cube leftCube = leftDoor.getRandomCube(random);
        ModelPart.Cube rightCube = rightDoor.getRandomCube(random);

        assertEquals(-leftCube.minX, rightCube.maxX, 0.001f);
        assertEquals(-leftCube.maxX, rightCube.minX, 0.001f);
        assertEquals(leftCube.minY, rightCube.minY, 0.001f);
        assertEquals(leftCube.maxY, rightCube.maxY, 0.001f);
        assertEquals(leftCube.minZ, rightCube.minZ, 0.001f);
        assertEquals(leftCube.maxZ, rightCube.maxZ, 0.001f);

        assertEquals(0.0f, leftCube.maxX, 0.001f);
        assertEquals(0.0f, rightCube.minX, 0.001f);

        float leftWidth = leftCube.maxX - leftCube.minX;
        float rightWidth = rightCube.maxX - rightCube.minX;
        assertEquals(leftWidth, rightWidth, 0.001f);
    }

    @Test
    public void testDoorTextureSymmetryAndCentering() throws Exception {
        InputStream stream = getClass().getResourceAsStream("/assets/sandstorm/textures/entity/quantum_sleeper_pod/quantum_chamber.png");
        assertNotNull(stream);

        BufferedImage img = ImageIO.read(stream);
        assertNotNull(img);

        int leftSeamCol = 81;
        int rightSeamCol = 102;
        int leftOuterCol = 86;
        int rightOuterCol = 97;

        for (int y = 42; y <= 67; y++) {
            int leftSeamRgba = img.getRGB(leftSeamCol, y);
            int rightSeamRgba = img.getRGB(rightSeamCol, y);
            assertEquals(leftSeamRgba, rightSeamRgba, "Center seam must be identical on left (col 81) and right (col 102) at y=" + y);

            int leftOuterRgba = img.getRGB(leftOuterCol, y);
            int rightOuterRgba = img.getRGB(rightOuterCol, y);
            assertEquals(leftOuterRgba, rightOuterRgba, "Outer frame must be identical on left (col 86) and right (col 97) at y=" + y);

            for (int dist = 1; dist <= 4; dist++) {
                int leftGlassX = leftSeamCol + dist;
                int rightGlassX = rightSeamCol - dist;

                int leftRgba = img.getRGB(leftGlassX, y);
                int rightRgba = img.getRGB(rightGlassX, y);
                assertEquals(leftRgba, rightRgba, "Glass pixel must be perfectly mirrored at distance " + dist + " from seam at y=" + y);

                int alpha = (leftRgba >> 24) & 0xFF;
                int red = (leftRgba >> 16) & 0xFF;
                int green = (leftRgba >> 8) & 0xFF;
                int blue = leftRgba & 0xFF;

                if (alpha > 0 && alpha < 200) {
                    assertEquals(red, green);
                    assertEquals(green, blue);
                }
            }
        }
    }
}
