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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VaultDoorModelTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
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
    }

    @Test
    void shouldInitializeRenderStateWithDefaultValues() {
        VaultDoorRenderState state = new VaultDoorRenderState();
        assertEquals(Direction.NORTH, state.facing);
        assertFalse(state.open);
        assertEquals(0.0f, state.progress);
    }
}
