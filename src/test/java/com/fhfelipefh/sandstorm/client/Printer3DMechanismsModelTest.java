package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.renderer.Printer3DMechanismsModel;
import com.fhfelipefh.sandstorm.client.renderer.Printer3DRenderState;
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

class Printer3DMechanismsModelTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldCreateAndBakePrinter3DMechanismsModelWithAllParts() {
        LayerDefinition layer = Printer3DMechanismsModel.createBodyLayer();
        assertNotNull(layer);

        ModelPart root = layer.bakeRoot();
        assertNotNull(root);

        Printer3DMechanismsModel model = new Printer3DMechanismsModel(root);
        assertNotNull(model.getRoot());
        assertNotNull(model.getZRods());
        assertNotNull(model.getGantryRail());
        assertNotNull(model.getToolhead());
    }

    @Test
    void shouldInitializeRenderStateWithDefaultValues() {
        Printer3DRenderState state = new Printer3DRenderState();
        assertEquals(Direction.NORTH, state.facing);
        assertEquals(0, state.progress);
        assertEquals(100, state.maxProgress);
        assertFalse(state.isProcessing);
        assertFalse(state.hasItem);
        assertNotNull(state.itemRenderState);
    }
}
