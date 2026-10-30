package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class WaterwingModel extends EntityModel<WaterwingRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart throatReservoir;
    private final ModelPart head;
    private final ModelPart beak;
    private final ModelPart tail;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public WaterwingModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.throatReservoir = this.body.getChild("throat_reservoir");
        this.head = this.body.getChild("head");
        this.beak = this.head.getChild("beak");
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
                .texOffs(0, 32).addBox(-4.5f, -4.0f, -8.0f, 9.0f, 8.0f, 16.0f)
                .texOffs(0, 58).addBox(-3.5f, 4.0f, -5.0f, 7.0f, 2.0f, 10.0f)
                .texOffs(36, 58).addBox(2.0f, -1.0f, 7.5f, 2.0f, 3.0f, 3.0f)
                .texOffs(36, 58).addBox(-4.0f, -1.0f, 7.5f, 2.0f, 3.0f, 3.0f),
                PartPose.offset(0.0f, 12.0f, 0.0f));

        body.addOrReplaceChild("throat_reservoir", CubeListBuilder.create()
                .texOffs(28, 18).addBox(-2.5f, 2.0f, -12.0f, 5.0f, 5.0f, 8.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5f, -3.0f, -6.0f, 5.0f, 5.0f, 6.0f)
                .texOffs(24, 0).addBox(-1.0f, -6.0f, -4.0f, 2.0f, 3.0f, 5.0f)
                .texOffs(0, 13).addBox(-2.0f, -2.0f, -6.15f, 4.0f, 2.0f, 1.0f),
                PartPose.offset(0.0f, -2.0f, -8.0f));

        head.addOrReplaceChild("beak", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-1.5f, -0.5f, -16.0f, 3.0f, 3.0f, 10.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        body.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(0, 72).addBox(-3.0f, -0.5f, 0.0f, 6.0f, 1.0f, 11.0f),
                PartPose.offset(0.0f, -1.0f, 8.0f));

        body.addOrReplaceChild("right_wing", CubeListBuilder.create()
                .texOffs(56, 0).addBox(-10.0f, -0.5f, -5.0f, 10.0f, 1.0f, 10.0f)
                .texOffs(56, 13).addBox(-22.0f, -0.5f, -4.0f, 12.0f, 1.0f, 8.0f),
                PartPose.offset(-4.5f, 0.0f, -3.0f));

        body.addOrReplaceChild("left_wing", CubeListBuilder.create()
                .texOffs(56, 24).addBox(0.0f, -0.5f, -5.0f, 10.0f, 1.0f, 10.0f)
                .texOffs(56, 37).addBox(10.0f, -0.5f, -4.0f, 12.0f, 1.0f, 8.0f),
                PartPose.offset(4.5f, 0.0f, -3.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 86).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f)
                .texOffs(10, 86).addBox(-2.0f, 6.5f, -4.0f, 4.0f, 1.0f, 6.0f),
                PartPose.offset(-3.5f, 15.0f, 3.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 86).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f)
                .texOffs(10, 86).addBox(-2.0f, 6.5f, -4.0f, 4.0f, 1.0f, 6.0f),
                PartPose.offset(3.5f, 15.0f, 3.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(WaterwingRenderState state) {
        super.setupAnim(state);

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);
        this.body.xRot = 0.0f;
        this.body.yRot = 0.0f;
        this.tail.yRot = 0.0f;

        if (state.isDeactivated) {
            this.rightWing.zRot = 0.95f;
            this.leftWing.zRot = -0.95f;
            this.rightWing.xRot = 0.2f;
            this.leftWing.xRot = 0.2f;
            this.head.xRot = 0.65f;
            this.body.xRot = 0.15f;
            this.rightLeg.xRot = 0.2f;
            this.leftLeg.xRot = 0.2f;
            this.throatReservoir.xRot = 0.0f;
            this.beak.xRot = 0.0f;
            return;
        }

        if (state.isDiving) {
            this.body.xRot = 0.55f;
            this.rightWing.zRot = 1.25f;
            this.leftWing.zRot = -1.25f;
            this.rightWing.xRot = 0.1f;
            this.leftWing.xRot = 0.1f;
            this.rightLeg.xRot = 1.35f;
            this.leftLeg.xRot = 1.35f;
            this.tail.yRot = Mth.sin(state.animationTicks * 0.4f) * 0.25f;
            this.throatReservoir.xRot = -0.1f;
            this.beak.xRot = -0.05f;
            return;
        }

        if (state.isSitting) {
            this.body.xRot = 0.1f;
            this.rightWing.zRot = 0.4f;
            this.leftWing.zRot = -0.4f;
            this.rightWing.xRot = 0.2f;
            this.leftWing.xRot = 0.2f;
            this.rightLeg.xRot = -1.2f;
            this.leftLeg.xRot = -1.2f;
            this.tail.xRot = -0.3f;
            this.throatReservoir.xRot = 0.0f;
            this.beak.xRot = 0.0f;
            return;
        }

        float flap = Mth.sin(state.animationTicks * 0.4f) * 0.42f;
        this.rightWing.zRot = flap;
        this.leftWing.zRot = -flap;
        this.rightWing.xRot = Mth.cos(state.animationTicks * 0.4f) * 0.08f;
        this.leftWing.xRot = Mth.cos(state.animationTicks * 0.4f) * 0.08f;
        this.tail.xRot = -0.1f + Mth.cos(state.animationTicks * 0.2f) * 0.06f;
        this.rightLeg.xRot = 0.4f;
        this.leftLeg.xRot = 0.4f;
        this.throatReservoir.xRot = 0.0f;
        this.beak.xRot = 0.0f;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
