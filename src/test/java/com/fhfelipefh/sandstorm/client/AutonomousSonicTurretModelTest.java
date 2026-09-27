package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.renderer.AutonomousSonicTurretModel;
import com.fhfelipefh.sandstorm.client.renderer.AutonomousSonicTurretRenderState;
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

class AutonomousSonicTurretModelTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldCreateAndBakeAutonomousSonicTurretModelWithAllParts() {
        LayerDefinition layer = AutonomousSonicTurretModel.createBodyLayer();
        assertNotNull(layer);

        ModelPart root = layer.bakeRoot();
        assertNotNull(root);

        AutonomousSonicTurretModel model = new AutonomousSonicTurretModel(root);
        assertNotNull(model.getRoot());
        assertNotNull(model.getHead());
        assertNotNull(model.getBarrelLeft());
        assertNotNull(model.getBarrelRight());
        assertNotNull(model.getSensorDome());
    }

    @Test
    void shouldInitializeRenderStateWithDefaultValues() {
        AutonomousSonicTurretRenderState state = new AutonomousSonicTurretRenderState();
        assertEquals(Direction.NORTH, state.facing);
        assertEquals(0.0f, state.yaw);
        assertEquals(0.0f, state.pitch);
        assertFalse(state.hasTarget);
        assertEquals(0, state.shootFlashTicks);
        assertEquals(0, state.energy);
        assertEquals(0.0f, state.animationTicks);
    }
}
