package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.renderer.SandwormModel;
import com.fhfelipefh.sandstorm.client.renderer.SandwormRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandwormModelTest {

    @Test
    void shouldCreateAndBakeSandwormModelLayerWithAllAnatomicalParts() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        assertNotNull(layer);

        ModelPart root = layer.bakeRoot();
        assertNotNull(root);

        ModelPart body = root.getChild("body");
        assertNotNull(body);

        ModelPart base = body.getChild("base");
        assertNotNull(base);

        ModelPart segLower = base.getChild("segment_lower");
        assertNotNull(segLower);

        ModelPart segMid = segLower.getChild("segment_mid");
        assertNotNull(segMid);

        ModelPart segUpper = segMid.getChild("segment_upper");
        assertNotNull(segUpper);

        ModelPart neck = segUpper.getChild("neck");
        assertNotNull(neck);

        ModelPart head = neck.getChild("head");
        assertNotNull(head);

        ModelPart teethOuter = head.getChild("teeth_outer");
        assertNotNull(teethOuter);

        ModelPart teethMiddle = head.getChild("teeth_middle");
        assertNotNull(teethMiddle);

        ModelPart teethInner = head.getChild("teeth_inner");
        assertNotNull(teethInner);

        SandwormModel model = new SandwormModel(root);
        assertNotNull(model);
        assertNotNull(model.getRoot());

        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 20.0f;
        state.burrowed = false;
        state.breaching = true;
        state.breachProgress = 0.5f;
        state.biteProgress = 0.8f;

        model.setupAnim(state);
        assertTrue(head.xRot != 0.0f);
    }

    @Test
    void shouldAnatomicallyBendSpineForwardTowardsTarget() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        ModelPart body = root.getChild("body");
        ModelPart base = body.getChild("base");
        ModelPart segLower = base.getChild("segment_lower");
        ModelPart segMid = segLower.getChild("segment_mid");
        ModelPart segUpper = segMid.getChild("segment_upper");
        ModelPart neck = segUpper.getChild("neck");
        ModelPart head = neck.getChild("head");

        SandwormModel model = new SandwormModel(root);
        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 100.0f;
        state.hasTarget = true;
        state.targetDistance = 8.0f;
        state.targetRelativeYaw = 30.0f;
        state.biteProgress = 0.0f;
        state.breaching = false;

        model.setupAnim(state);

        assertTrue(base.xRot > 0.0f);
        assertTrue(segLower.xRot > 0.0f);
        assertTrue(segMid.xRot > 0.0f);
        assertTrue(segUpper.xRot > 0.0f);
        assertTrue(neck.xRot > 0.0f);
        assertTrue(head.xRot > 0.0f);
        assertTrue(segMid.yRot > 0.0f);
    }

    @Test
    void shouldLungeAndSnapTeethDuringBiteStrike() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        ModelPart body = root.getChild("body");
        ModelPart base = body.getChild("base");
        ModelPart segLower = base.getChild("segment_lower");
        ModelPart segMid = segLower.getChild("segment_mid");
        ModelPart segUpper = segMid.getChild("segment_upper");
        ModelPart neck = segUpper.getChild("neck");
        ModelPart head = neck.getChild("head");
        ModelPart teethOuter = head.getChild("teeth_outer");
        ModelPart teethMiddle = head.getChild("teeth_middle");
        ModelPart teethInner = head.getChild("teeth_inner");

        SandwormModel model = new SandwormModel(root);
        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 100.0f;
        state.hasTarget = true;
        state.targetDistance = 6.0f;
        state.biteProgress = 0.55f;

        model.setupAnim(state);

        assertTrue(teethOuter.z < 0.0f);
        assertTrue(teethMiddle.z < 0.0f);
        assertTrue(teethInner.z < 0.0f);
    }
}
