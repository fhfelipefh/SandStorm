package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.renderer.NaniteFabricatorMechanismsModel;
import com.fhfelipefh.sandstorm.client.renderer.NaniteFabricatorRenderState;
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

class NaniteFabricatorMechanismsModelTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldCreateAndBakeNaniteFabricatorMechanismsModelWithAllParts() {
        LayerDefinition layer = NaniteFabricatorMechanismsModel.createBodyLayer();
        assertNotNull(layer);

        ModelPart root = layer.bakeRoot();
        assertNotNull(root);

        NaniteFabricatorMechanismsModel model = new NaniteFabricatorMechanismsModel(root);
        assertNotNull(model.getRoot());
        assertNotNull(model.getMount());
        assertNotNull(model.getManipulatorArm());
        assertNotNull(model.getNanobot());
    }

    @Test
    void shouldInitializeRenderStateWithDefaultValues() {
        NaniteFabricatorRenderState state = new NaniteFabricatorRenderState();
        assertEquals(Direction.NORTH, state.facing);
        assertEquals(0, state.progress);
        assertEquals(120, state.maxProgress);
        assertFalse(state.isProcessing);
        assertFalse(state.hasItem);
        assertNotNull(state.itemRenderState);
    }
}
