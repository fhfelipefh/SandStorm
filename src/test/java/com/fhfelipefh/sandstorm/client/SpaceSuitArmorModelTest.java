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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertSame(head, model.head);
        assertSame(root.getChild("body"), model.body);
        assertSame(root.getChild("right_arm"), model.rightArm);
        assertSame(root.getChild("left_arm"), model.leftArm);
        assertSame(root.getChild("right_leg"), model.rightLeg);
        assertSame(root.getChild("left_leg"), model.leftLeg);
    }

    @Test
    void shouldHandleVisibilityForAllEquipmentSlots() {
        LayerDefinition layer = SpaceSuitArmorModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        SpaceSuitArmorModel model = new SpaceSuitArmorModel(root);

        model.setVisibleSlot(EquipmentSlot.HEAD);
        assertTrue(model.head.visible);
        assertTrue(model.hat.visible);
        assertFalse(model.body.visible);
        assertFalse(model.rightArm.visible);
        assertFalse(model.leftArm.visible);
        assertFalse(model.rightLeg.visible);
        assertFalse(model.leftLeg.visible);

        model.setVisibleSlot(EquipmentSlot.CHEST);
        assertFalse(model.head.visible);
        assertFalse(model.hat.visible);
        assertTrue(model.body.visible);
        assertTrue(model.rightArm.visible);
        assertTrue(model.leftArm.visible);
        assertFalse(model.rightLeg.visible);
        assertFalse(model.leftLeg.visible);

        model.setVisibleSlot(EquipmentSlot.LEGS);
        assertFalse(model.head.visible);
        assertFalse(model.hat.visible);
        assertFalse(model.body.visible);
        assertFalse(model.rightArm.visible);
        assertFalse(model.leftArm.visible);
        assertTrue(model.rightLeg.visible);
        assertTrue(model.leftLeg.visible);

        model.setVisibleSlot(EquipmentSlot.FEET);
        assertFalse(model.head.visible);
        assertFalse(model.hat.visible);
        assertFalse(model.body.visible);
        assertFalse(model.rightArm.visible);
        assertFalse(model.leftArm.visible);
        assertTrue(model.rightLeg.visible);
        assertTrue(model.leftLeg.visible);
    }

    @Test
    void shouldInstantiateSpaceSuitArmorRenderer() {
        SpaceSuitArmorRenderer renderer = assertDoesNotThrow(SpaceSuitArmorRenderer::new);
        assertNotNull(renderer);
    }

    @Test
    void shouldMaintainIndependentModelsForEachArmorSlot() {
        SpaceSuitArmorRenderer renderer = new SpaceSuitArmorRenderer();
        SpaceSuitArmorModel headModel = renderer.getModelForSlot(EquipmentSlot.HEAD);
        SpaceSuitArmorModel chestModel = renderer.getModelForSlot(EquipmentSlot.CHEST);
        SpaceSuitArmorModel legsModel = renderer.getModelForSlot(EquipmentSlot.LEGS);
        SpaceSuitArmorModel feetModel = renderer.getModelForSlot(EquipmentSlot.FEET);

        assertNotNull(headModel);
        assertNotNull(chestModel);
        assertNotNull(legsModel);
        assertNotNull(feetModel);

        assertNotSame(headModel, chestModel);
        assertNotSame(headModel, legsModel);
        assertNotSame(headModel, feetModel);
        assertNotSame(chestModel, legsModel);
        assertNotSame(chestModel, feetModel);
        assertNotSame(legsModel, feetModel);
    }

    @Test
    void shouldPreserveIndividualSlotVisibilityUnderSequentialSimulation() {
        SpaceSuitArmorRenderer renderer = new SpaceSuitArmorRenderer();
        SpaceSuitArmorModel headModel = renderer.getModelForSlot(EquipmentSlot.HEAD);
        SpaceSuitArmorModel chestModel = renderer.getModelForSlot(EquipmentSlot.CHEST);
        SpaceSuitArmorModel legsModel = renderer.getModelForSlot(EquipmentSlot.LEGS);
        SpaceSuitArmorModel feetModel = renderer.getModelForSlot(EquipmentSlot.FEET);

        feetModel.setVisibleSlot(EquipmentSlot.FEET);
        legsModel.setVisibleSlot(EquipmentSlot.LEGS);
        chestModel.setVisibleSlot(EquipmentSlot.CHEST);
        headModel.setVisibleSlot(EquipmentSlot.HEAD);

        assertTrue(headModel.head.visible);
        assertTrue(headModel.hat.visible);
        assertFalse(headModel.body.visible);
        assertFalse(headModel.rightArm.visible);
        assertFalse(headModel.leftArm.visible);
        assertFalse(headModel.rightLeg.visible);
        assertFalse(headModel.leftLeg.visible);

        assertFalse(chestModel.head.visible);
        assertFalse(chestModel.hat.visible);
        assertTrue(chestModel.body.visible);
        assertTrue(chestModel.rightArm.visible);
        assertTrue(chestModel.leftArm.visible);
        assertFalse(chestModel.rightLeg.visible);
        assertFalse(chestModel.leftLeg.visible);

        assertFalse(legsModel.head.visible);
        assertFalse(legsModel.hat.visible);
        assertFalse(legsModel.body.visible);
        assertFalse(legsModel.rightArm.visible);
        assertFalse(legsModel.leftArm.visible);
        assertTrue(legsModel.rightLeg.visible);
        assertTrue(legsModel.leftLeg.visible);

        assertFalse(feetModel.head.visible);
        assertFalse(feetModel.hat.visible);
        assertFalse(feetModel.body.visible);
        assertFalse(feetModel.rightArm.visible);
        assertFalse(feetModel.leftArm.visible);
        assertTrue(feetModel.rightLeg.visible);
        assertTrue(feetModel.leftLeg.visible);
    }
}
