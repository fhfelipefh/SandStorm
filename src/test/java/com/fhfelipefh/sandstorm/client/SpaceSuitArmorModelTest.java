package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.renderer.SpaceSuitArmorModel;
import com.fhfelipefh.sandstorm.client.renderer.SpaceSuitArmorRenderer;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EquipmentSlot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpaceSuitArmorModelTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldCreateAndBakeSpaceSuitArmorModelWithAllParts() {
        LayerDefinition layer = SpaceSuitArmorModel.createBodyLayer();
        assertNotNull(layer);

        ModelPart root = layer.bakeRoot();
        assertNotNull(root);

        SpaceSuitArmorModel model = assertDoesNotThrow(() -> new SpaceSuitArmorModel(root));
        assertNotNull(model);

        ModelPart head = root.getChild("head");
        assertNotNull(head);
        assertNotNull(head.getChild("hat"));
        assertNotNull(root.getChild("body"));
        assertNotNull(root.getChild("right_arm"));
        assertNotNull(root.getChild("left_arm"));
        assertNotNull(root.getChild("right_leg"));
        assertNotNull(root.getChild("left_leg"));
    }

    @Test
    void shouldHandleVisibilityForAllEquipmentSlots() {
        LayerDefinition layer = SpaceSuitArmorModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        SpaceSuitArmorModel model = new SpaceSuitArmorModel(root);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            assertDoesNotThrow(() -> model.setVisibleSlot(slot));
        }
    }

    @Test
    void shouldInstantiateSpaceSuitArmorRenderer() {
        SpaceSuitArmorRenderer renderer = assertDoesNotThrow(SpaceSuitArmorRenderer::new);
        assertNotNull(renderer);
    }
}
