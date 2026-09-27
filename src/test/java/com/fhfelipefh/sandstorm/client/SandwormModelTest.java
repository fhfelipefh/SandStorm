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

        ModelPart seg1 = base.getChild("segment_1");
        assertNotNull(seg1);

        ModelPart seg2 = seg1.getChild("segment_2");
        assertNotNull(seg2);

        ModelPart seg3 = seg2.getChild("segment_3");
        assertNotNull(seg3);

        ModelPart seg4 = seg3.getChild("segment_4");
        assertNotNull(seg4);

        ModelPart seg5 = seg4.getChild("segment_5");
        assertNotNull(seg5);

        ModelPart seg6 = seg5.getChild("segment_6");
        assertNotNull(seg6);

        ModelPart seg7 = seg6.getChild("segment_7");
        assertNotNull(seg7);

        ModelPart seg8 = seg7.getChild("segment_8");
        assertNotNull(seg8);

        ModelPart seg9 = seg8.getChild("segment_9");
        assertNotNull(seg9);

        ModelPart seg10 = seg9.getChild("segment_10");
        assertNotNull(seg10);

        ModelPart neck = seg10.getChild("neck");
        assertNotNull(neck);

        ModelPart head = neck.getChild("head");
        assertNotNull(head);

        ModelPart teethOuter = head.getChild("teeth_outer");
        assertNotNull(teethOuter);

        ModelPart teethMiddle = head.getChild("teeth_middle");
        assertNotNull(teethMiddle);

        ModelPart teethInner = head.getChild("teeth_inner");
        assertNotNull(teethInner);

        ModelPart subRing1 = base.getChild("sub_ring_1");
        assertNotNull(subRing1);

        ModelPart subRing2 = subRing1.getChild("sub_ring_2");
        assertNotNull(subRing2);

        ModelPart subRing3 = subRing2.getChild("sub_ring_3");
        assertNotNull(subRing3);

        SandwormModel model = new SandwormModel(root);
        assertNotNull(model);
        assertNotNull(model.getRoot());

        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 20.0f;
        state.burrowed = false;
        state.breaching = true;
        state.breachProgress = 0.5f;
        state.biteProgress = 0.8f;
        state.groundSink = 1.5f;
        state.groundSlopePitch = 0.2f;
        state.groundSlopeRoll = -0.1f;

        model.setupAnim(state);
        assertTrue(head.xRot != 0.0f);
        assertTrue(body.y > 24.0f);
        assertTrue(subRing1.xRot != 0.0f);
        assertTrue(subRing2.xRot != 0.0f);
        assertTrue(subRing3.xRot != 0.0f);
    }

    @Test
    void shouldAnatomicallyBendSpineForwardTowardsTarget() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        ModelPart body = root.getChild("body");
        ModelPart base = body.getChild("base");
        ModelPart seg1 = base.getChild("segment_1");
        ModelPart seg4 = seg1.getChild("segment_2").getChild("segment_3").getChild("segment_4");
        ModelPart seg7 = seg4.getChild("segment_5").getChild("segment_6").getChild("segment_7");
        ModelPart seg10 = seg7.getChild("segment_8").getChild("segment_9").getChild("segment_10");
        ModelPart neck = seg10.getChild("neck");
        ModelPart head = neck.getChild("head");

        SandwormModel model = new SandwormModel(root);
        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 100.0f;
        state.hasTarget = true;
        state.targetDistance = 6.0f;
        state.targetRelativeYaw = 30.0f;
        state.biteProgress = 0.0f;
        state.breaching = false;
        state.rearingProgress = 1.0f;

        model.setupAnim(state);

        assertTrue(base.xRot > 0.0f);
        assertTrue(seg1.xRot > 0.0f);
        assertTrue(seg4.xRot > 0.0f);
        assertTrue(seg7.xRot > 0.0f);
        assertTrue(seg10.xRot > 0.0f);
        assertTrue(neck.xRot > 0.0f);
        assertTrue(head.xRot > 0.0f);
        assertTrue(seg4.yRot > 0.0f);
    }

    @Test
    void shouldLungeAndSnapTeethDuringBiteStrike() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        ModelPart body = root.getChild("body");
        ModelPart base = body.getChild("base");
        ModelPart seg1 = base.getChild("segment_1");
        ModelPart seg4 = seg1.getChild("segment_2").getChild("segment_3").getChild("segment_4");
        ModelPart seg7 = seg4.getChild("segment_5").getChild("segment_6").getChild("segment_7");
        ModelPart seg10 = seg7.getChild("segment_8").getChild("segment_9").getChild("segment_10");
        ModelPart neck = seg10.getChild("neck");
        ModelPart head = neck.getChild("head");
        ModelPart teethOuter = head.getChild("teeth_outer");
        ModelPart teethMiddle = head.getChild("teeth_middle");
        ModelPart teethInner = head.getChild("teeth_inner");

        SandwormModel model = new SandwormModel(root);
        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 100.0f;
        state.hasTarget = true;
        state.targetDistance = 6.0f;
        state.biteProgress = 0.65f;

        model.setupAnim(state);

        assertTrue(teethOuter.z < 0.0f);
        assertTrue(teethMiddle.z < 0.0f);
        assertTrue(teethInner.z < 0.0f);
        assertTrue(base.xRot > 0.0f);
        assertTrue(seg1.xRot > 0.0f);
        assertTrue(head.xScale < 1.0f);
        assertTrue(body.z < 0.0f);
    }

    @Test
    void shouldOpenMouthWideDuringBiteWindup() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        ModelPart body = root.getChild("body");
        ModelPart base = body.getChild("base");
        ModelPart seg1 = base.getChild("segment_1");
        ModelPart seg10 = seg1.getChild("segment_2").getChild("segment_3").getChild("segment_4")
                .getChild("segment_5").getChild("segment_6").getChild("segment_7")
                .getChild("segment_8").getChild("segment_9").getChild("segment_10");
        ModelPart neck = seg10.getChild("neck");
        ModelPart head = neck.getChild("head");
        ModelPart teethOuter = head.getChild("teeth_outer");

        SandwormModel model = new SandwormModel(root);
        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 100.0f;
        state.hasTarget = true;
        state.targetDistance = 6.0f;
        state.biteProgress = 0.20f;

        model.setupAnim(state);

        assertTrue(head.xScale > 1.0f);
        assertTrue(teethOuter.xScale > 1.0f);
        assertTrue(teethOuter.z > 0.0f);
    }

    @Test
    void shouldGentlyAnimateTeethDuringIdleState() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        ModelPart body = root.getChild("body");
        ModelPart base = body.getChild("base");
        ModelPart seg1 = base.getChild("segment_1");
        ModelPart seg4 = seg1.getChild("segment_2").getChild("segment_3").getChild("segment_4");
        ModelPart seg7 = seg4.getChild("segment_5").getChild("segment_6").getChild("segment_7");
        ModelPart seg10 = seg7.getChild("segment_8").getChild("segment_9").getChild("segment_10");
        ModelPart neck = seg10.getChild("neck");
        ModelPart head = neck.getChild("head");
        ModelPart teethOuter = head.getChild("teeth_outer");
        ModelPart teethMiddle = head.getChild("teeth_middle");
        ModelPart teethInner = head.getChild("teeth_inner");

        SandwormModel model = new SandwormModel(root);
        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 35.0f;
        state.biteProgress = 0.0f;

        model.setupAnim(state);

        assertTrue(teethOuter.zRot != 0.0f);
        assertTrue(teethMiddle.zRot != 0.0f);
        assertTrue(teethInner.zRot != 0.0f);
        assertTrue(teethOuter.z != 0.0f);
        assertTrue(teethMiddle.z != 0.0f);
        assertTrue(teethInner.z != 0.0f);
    }

    @Test
    void shouldSmoothlySubmergeAndDiveDownIntoSandWhenSubmerging() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        ModelPart body = root.getChild("body");
        ModelPart base = body.getChild("base");
        ModelPart seg1 = base.getChild("segment_1");
        ModelPart seg4 = seg1.getChild("segment_2").getChild("segment_3").getChild("segment_4");
        ModelPart seg7 = seg4.getChild("segment_5").getChild("segment_6").getChild("segment_7");
        ModelPart seg10 = seg7.getChild("segment_8").getChild("segment_9").getChild("segment_10");
        ModelPart neck = seg10.getChild("neck");
        ModelPart head = neck.getChild("head");

        SandwormModel model = new SandwormModel(root);
        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 50.0f;
        state.rearingProgress = 1.0f;
        state.submerging = true;
        state.submergeProgress = 0.5f;

        model.setupAnim(state);

        assertTrue(body.y > 24.0f);
        assertTrue(head.xRot > 0.0f);
    }

    @Test
    void shouldSmoothlyEmergeAndUncurlFromSandWhenBreaching() {
        LayerDefinition layer = SandwormModel.createBodyLayer();
        ModelPart root = layer.bakeRoot();
        ModelPart body = root.getChild("body");
        ModelPart base = body.getChild("base");
        ModelPart seg1 = base.getChild("segment_1");
        ModelPart seg4 = seg1.getChild("segment_2").getChild("segment_3").getChild("segment_4");
        ModelPart seg7 = seg4.getChild("segment_5").getChild("segment_6").getChild("segment_7");
        ModelPart seg10 = seg7.getChild("segment_8").getChild("segment_9").getChild("segment_10");
        ModelPart neck = seg10.getChild("neck");
        ModelPart head = neck.getChild("head");

        SandwormModel model = new SandwormModel(root);
        SandwormRenderState state = new SandwormRenderState();
        state.ageInTicks = 50.0f;
        state.rearingProgress = 0.5f;
        state.breaching = true;
        state.breachProgress = 0.4f;

        model.setupAnim(state);

        assertTrue(body.y > 24.0f);
        assertTrue(head.xRot != 0.0f);
    }
}
