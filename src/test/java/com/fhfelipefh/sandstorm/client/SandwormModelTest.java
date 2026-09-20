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

        ModelPart sandSkirt = body.getChild("sand_skirt");
        assertNotNull(sandSkirt);

        ModelPart base = body.getChild("base");
        assertNotNull(base);

        ModelPart midBody = base.getChild("mid_body");
        assertNotNull(midBody);

        ModelPart neck = midBody.getChild("neck");
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
}
