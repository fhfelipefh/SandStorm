package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class GlinthawkModel extends EntityModel<GlinthawkRenderState> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart tail;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public GlinthawkModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -3.0f, -6.0f, 8.0f, 6.0f, 12.0f)
                .texOffs(0, 18).addBox(-2.5f, 2.0f, -4.0f, 5.0f, 3.0f, 6.0f),
                PartPose.offset(0.0f, 14.0f, 0.0f));

        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(28, 0).addBox(-2.5f, -3.0f, -5.0f, 5.0f, 5.0f, 5.0f)
                .texOffs(28, 10).addBox(-1.5f, -1.0f, -9.0f, 3.0f, 3.0f, 4.0f),
                PartPose.offset(0.0f, -1.0f, -6.0f));

        body.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-3.0f, -0.5f, 0.0f, 6.0f, 1.0f, 10.0f),
                PartPose.offset(0.0f, -1.0f, 6.0f));

        body.addOrReplaceChild("right_wing", CubeListBuilder.create()
                .texOffs(28, 18).addBox(-14.0f, -0.5f, -4.0f, 14.0f, 1.0f, 8.0f),
                PartPose.offset(-4.0f, -1.0f, -2.0f));

        body.addOrReplaceChild("left_wing", CubeListBuilder.create()
                .texOffs(28, 18).addBox(0.0f, -0.5f, -4.0f, 14.0f, 1.0f, 8.0f),
                PartPose.offset(4.0f, -1.0f, -2.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 38).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f)
                .texOffs(8, 38).addBox(-1.5f, 5.0f, -3.0f, 3.0f, 1.0f, 4.0f),
                PartPose.offset(-2.5f, 17.0f, 2.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 38).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f)
                .texOffs(8, 38).addBox(-1.5f, 5.0f, -3.0f, 3.0f, 1.0f, 4.0f),
                PartPose.offset(2.5f, 17.0f, 2.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(GlinthawkRenderState state) {
        super.setupAnim(state);

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        if (state.isDeactivated) {
            this.rightWing.zRot = 0.8f;
            this.leftWing.zRot = -0.8f;
            this.head.xRot = 0.5f;
            this.body.xRot = 0.2f;
            return;
        }

        float flap = Mth.sin(state.animationTicks * 0.45f) * 0.45f;
        this.rightWing.zRot = flap;
        this.leftWing.zRot = -flap;
        this.tail.xRot = -0.1f + Mth.cos(state.animationTicks * 0.2f) * 0.05f;

        this.rightLeg.xRot = 0.3f;
        this.leftLeg.xRot = 0.3f;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}

