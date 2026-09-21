package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.renderer.DesalinationFilterMechanismsModel;
import com.fhfelipefh.sandstorm.client.renderer.DesalinationFilterRenderState;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DesalinationFilterMechanismsModelTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldCreateAndBakeDesalinationFilterMechanismsModelWithAllParts() {
        LayerDefinition layer = DesalinationFilterMechanismsModel.createBodyLayer();
        assertNotNull(layer);

        ModelPart root = layer.bakeRoot();
        assertNotNull(root);

        DesalinationFilterMechanismsModel model = new DesalinationFilterMechanismsModel(root);
        assertNotNull(model.getRoot());
        assertNotNull(model.getSpiralCoil());
        assertNotNull(model.getLeftTank());
        assertNotNull(model.getRightTank());
        assertNotNull(model.getFunnel());
        assertNotNull(model.getPipes());
    }

    @Test
    void shouldInitializeRenderStateWithDefaultValues() {
        DesalinationFilterRenderState state = new DesalinationFilterRenderState();
        assertEquals(Direction.NORTH, state.facing);
        assertEquals(0, state.progress);
        assertEquals(80, state.maxProgress);
        assertFalse(state.isProcessing);
        assertEquals(0, state.waterInput);
        assertEquals(0, state.waterOutput);
        assertEquals(4000, state.maxWater);
        assertFalse(state.hasCartridge);
    }
}
