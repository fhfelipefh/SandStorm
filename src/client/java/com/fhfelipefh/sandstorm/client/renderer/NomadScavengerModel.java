package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class NomadScavengerModel extends EntityModel<NomadScavengerRenderState> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart backpack;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public NomadScavengerModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.backpack = this.body.getChild("backpack");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f)
                .texOffs(16, 32).addBox(-4.5f, 0.0f, -2.5f, 9.0f, 14.0f, 5.0f),
                PartPose.offset(0.0f, 2.0f, 1.0f));

        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                .texOffs(32, 0).addBox(-4.5f, -8.5f, -4.5f, 9.0f, 9.0f, 9.0f)
                .texOffs(0, 16).addBox(-2.5f, -3.5f, -5.5f, 5.0f, 3.0f, 2.0f)
                .texOffs(24, 0).addBox(1.5f, -6.5f, -5.0f, 2.0f, 2.0f, 1.0f),
                PartPose.offset(0.0f, 0.0f, -1.0f));

        body.addOrReplaceChild("backpack", CubeListBuilder.create()
                .texOffs(32, 18).addBox(-4.0f, 2.0f, 2.5f, 8.0f, 10.0f, 5.0f)
                .texOffs(58, 0).addBox(2.0f, -4.0f, 5.0f, 1.0f, 6.0f, 1.0f),
                PartPose.ZERO);

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(40, 33).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 3.0f)
                .texOffs(52, 33).addBox(-2.5f, 7.0f, -2.5f, 4.0f, 4.0f, 4.0f),
                PartPose.offset(-5.0f, 2.0f, 0.0f));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(40, 48).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                PartPose.offset(5.0f, 2.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
                PartPose.offset(-2.0f, 14.0f, 1.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 46).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
                PartPose.offset(2.0f, 14.0f, 1.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(NomadScavengerRenderState state) {
        super.setupAnim(state);

        this.body.xRot = state.isFleeing ? 0.38f : 0.22f;
        this.head.xRot = -0.15f + (state.xRot * ((float) Math.PI / 180F));
        this.head.yRot = state.yRot * ((float) Math.PI / 180F);

        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = state.walkAnimationSpeed;

        float swingSpeed = state.isFleeing ? 1.2f : 0.6662f;
        this.rightLeg.xRot = Mth.cos(limbSwing * swingSpeed) * 1.4f * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * swingSpeed + (float) Math.PI) * 1.4f * limbSwingAmount;
        this.rightLeg.yRot = 0.0f;
        this.leftLeg.yRot = 0.0f;

        if (state.isFleeing) {
            this.rightArm.xRot = -0.8f + Mth.sin(limbSwing * 1.2f) * 0.8f * limbSwingAmount;
            this.leftArm.xRot = -0.8f + Mth.sin(limbSwing * 1.2f + (float) Math.PI) * 0.8f * limbSwingAmount;
            this.rightArm.zRot = 0.3f;
            this.leftArm.zRot = -0.3f;
        } else {
            this.rightArm.xRot = Mth.cos(limbSwing * 0.6662f + (float) Math.PI) * 1.0f * limbSwingAmount * 0.5f;
            this.leftArm.xRot = Mth.cos(limbSwing * 0.6662f) * 1.0f * limbSwingAmount * 0.5f;
            this.rightArm.zRot = 0.05f;
            this.leftArm.zRot = -0.05f;
        }

        float breath = Mth.sin(state.animationTicks * 0.08f) * 0.03f;
        this.body.xRot += breath;
    }
}
