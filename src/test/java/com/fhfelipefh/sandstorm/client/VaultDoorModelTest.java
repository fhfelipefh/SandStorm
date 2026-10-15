package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.renderer.VaultDoorModel;
import com.fhfelipefh.sandstorm.client.renderer.VaultDoorRenderState;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VaultDoorModelTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @SuppressWarnings("unchecked")
    private static List<ModelPart.Cube> getCubes(ModelPart part) throws Exception {
        Field f = ModelPart.class.getDeclaredField("cubes");
        f.setAccessible(true);
        return (List<ModelPart.Cube>) f.get(part);
    }

    @Test
    void shouldCreateAndBakeVaultDoorModelWithAllParts() {
        LayerDefinition layer = VaultDoorModel.createBodyLayer();
        assertNotNull(layer);

        ModelPart root = layer.bakeRoot();
        assertNotNull(root);

        VaultDoorModel model = new VaultDoorModel(root);
        assertNotNull(model.getRoot());
        assertNotNull(model.getFrame());
        assertNotNull(model.getDoorBody());
        assertNotNull(model.getLockingBars());
        assertNotNull(model.getWheel());
        assertFalse(model.getFrame().isEmpty());
        assertFalse(model.getDoorBody().isEmpty());
        assertFalse(model.getLockingBars().isEmpty());
        assertFalse(model.getWheel().isEmpty());
    }

    @Test
    void shouldInitializeRenderStateWithDefaultValues() {
        VaultDoorRenderState state = new VaultDoorRenderState();
        assertEquals(Direction.NORTH, state.facing);
        assertFalse(state.open);
        assertEquals(0.0f, state.progress);
    }

    @Test
    void shouldGuaranteeZeroOverlappingVolumeInDoorBodyCubes() throws Exception {
        LayerDefinition layer = VaultDoorModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        VaultDoorModel model = new VaultDoorModel(root);
        List<ModelPart.Cube> cubes = getCubes(model.getDoorBody());

        for (int i = 0; i < cubes.size(); i++) {
            ModelPart.Cube a = cubes.get(i);
            for (int j = i + 1; j < cubes.size(); j++) {
                ModelPart.Cube b = cubes.get(j);

                float overlapX = Math.max(0.0f, Math.min(a.maxX, b.maxX) - Math.max(a.minX, b.minX));
                float overlapY = Math.max(0.0f, Math.min(a.maxY, b.maxY) - Math.max(a.minY, b.minY));
                float overlapZ = Math.max(0.0f, Math.min(a.maxZ, b.maxZ) - Math.max(a.minZ, b.minZ));
                float volume = overlapX * overlapY * overlapZ;

                assertEquals(0.0f, volume, 1e-4f);
            }
        }
    }

    @Test
    void shouldGuaranteeUniformRearDepthPlaneAndFrontSeparationOnDoorBody() throws Exception {
        LayerDefinition layer = VaultDoorModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        VaultDoorModel model = new VaultDoorModel(root);
        List<ModelPart.Cube> cubes = getCubes(model.getDoorBody());

        float maxZAll = -Float.MAX_VALUE;
        for (ModelPart.Cube cube : cubes) {
            maxZAll = Math.max(maxZAll, cube.maxZ);
        }
        assertEquals(3.0f, maxZAll, 1e-4f);

        for (ModelPart.Cube cube : cubes) {
            if (cube.maxZ > -3.0f) {
                assertEquals(3.0f, cube.maxZ, 1e-4f);
                assertEquals(-3.0f, cube.minZ, 1e-4f);
            } else {
                assertTrue(cube.maxZ <= -3.0f);
            }
        }
    }

    @Test
    void shouldFitDoorHingeBracketStrictlyBetweenFrameHinges() throws Exception {
        LayerDefinition layer = VaultDoorModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        VaultDoorModel model = new VaultDoorModel(root);
        List<ModelPart.Cube> cubes = getCubes(model.getDoorBody());

        ModelPart.Cube hingeBracket = cubes.get(0);
        assertTrue(hingeBracket.minY >= -10.0f);
        assertTrue(hingeBracket.maxY <= 10.0f);
        assertEquals(5.0f, hingeBracket.maxX - hingeBracket.minX, 1e-4f);
        assertEquals(6.0f, hingeBracket.maxZ - hingeBracket.minZ, 1e-4f);
    }
}
