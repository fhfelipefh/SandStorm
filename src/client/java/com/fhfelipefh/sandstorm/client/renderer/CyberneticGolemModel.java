package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class CyberneticGolemModel extends EntityModel<CyberneticGolemRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public CyberneticGolemModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 40).addBox(-9.0f, -2.0f, -6.0f, 18.0f, 12.0f, 11.0f)
                .texOffs(0, 70).addBox(-4.5f, 10.0f, -3.0f, 9.0f, 5.0f, 6.0f)
                .texOffs(30, 70).addBox(-3.0f, 2.0f, -6.5f, 6.0f, 6.0f, 1.0f),
                PartPose.offset(0.0f, -7.0f, 0.0f));

        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f)
                .texOffs(32, 0).addBox(-3.0f, -8.0f, -6.0f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -7.0f, -2.0f));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(60, 21).addBox(-13.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f)
                .texOffs(80, 21).addBox(-14.0f, -3.5f, -3.5f, 5.0f, 6.0f, 7.0f)
                .texOffs(80, 40).addBox(-13.5f, 12.0f, -3.5f, 5.0f, 4.0f, 7.0f),
                PartPose.offset(0.0f, -7.0f, 0.0f));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(60, 58).addBox(9.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f)
                .texOffs(80, 58).addBox(9.0f, -3.5f, -3.5f, 5.0f, 6.0f, 7.0f)
                .texOffs(80, 75).addBox(8.5f, 12.0f, -3.5f, 5.0f, 4.0f, 7.0f),
                PartPose.offset(0.0f, -7.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(37, 0).addBox(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f)
                .texOffs(37, 25).addBox(-4.0f, 4.0f, -3.5f, 7.0f, 4.0f, 6.0f),
                PartPose.offset(-4.0f, 11.0f, 0.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(60, 0).addBox(-2.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f)
                .texOffs(60, 25).addBox(-3.0f, 4.0f, -3.5f, 7.0f, 4.0f, 6.0f),
                PartPose.offset(4.0f, 11.0f, 0.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(CyberneticGolemRenderState state) {
        super.setupAnim(state);

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        this.rightLeg.xRot = -1.5f * Mth.triangleWave(walkPos, 13.0f) * walkSpeed;
        this.leftLeg.xRot = 1.5f * Mth.triangleWave(walkPos, 13.0f) * walkSpeed;
        this.rightLeg.yRot = 0.0f;
        this.leftLeg.yRot = 0.0f;

        this.rightArm.xRot = (-0.2f + 1.5f * Mth.triangleWave(walkPos, 13.0f)) * walkSpeed;
        this.leftArm.xRot = (-0.2f - 1.5f * Mth.triangleWave(walkPos, 13.0f)) * walkSpeed;
        this.rightArm.zRot = 0.0f;
        this.leftArm.zRot = 0.0f;
        this.body.xRot = 0.0f;
        this.body.zRot = 0.0f;

        if (state.attackAnimTicks > 0) {
            float progress = (10.0f - state.attackAnimTicks) / 10.0f;
            this.rightArm.xRot = -2.0f + Mth.sin(progress * (float) Math.PI) * 1.8f;
        }

        if (state.isOverdrive) {
            this.body.xRot = 0.35f;
            this.head.xRot -= 0.35f;
            float sprintSpeed = walkSpeed * 1.5f;
            this.rightArm.xRot = (-0.3f + 2.0f * Mth.triangleWave(walkPos * 1.8f, 13.0f)) * sprintSpeed;
            this.leftArm.xRot = (-0.3f - 2.0f * Mth.triangleWave(walkPos * 1.8f, 13.0f)) * sprintSpeed;
            float jitter = Mth.sin(state.animationTicks * 24.0f) * 0.04f;
            this.body.zRot = jitter;
            this.rightArm.zRot = 0.15f + jitter;
            this.leftArm.zRot = -0.15f - jitter;
        } else if (state.heat > 0.05f) {
            float breath = Mth.sin(state.animationTicks * 0.15f) * 0.03f * state.heat;
            this.body.xRot = breath;
        }
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
