package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class CyborgModel extends EntityModel<CyborgRenderState> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart visor;

    public CyborgModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.visor = this.head.getChild("visor");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                .texOffs(32, 0).addBox(-4.5f, -8.5f, -4.5f, 9.0f, 9.0f, 9.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        head.addOrReplaceChild("visor", CubeListBuilder.create()
                .texOffs(0, 48).addBox(-4.0f, -6.0f, -4.6f, 8.0f, 3.0f, 1.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f)
                .texOffs(16, 32).addBox(-4.5f, 1.0f, -2.5f, 9.0f, 6.0f, 5.0f)
                .texOffs(0, 32).addBox(-1.5f, 2.0f, 2.0f, 3.0f, 9.0f, 2.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(40, 16).addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f)
                .texOffs(40, 32).addBox(-3.5f, 5.0f, -2.5f, 5.0f, 7.0f, 5.0f),
                PartPose.offset(-5.0f, 2.0f, 0.0f));

        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(32, 48).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                PartPose.offset(5.0f, 2.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                PartPose.offset(-1.9f, 12.0f, 0.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(16, 48).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                PartPose.offset(1.9f, 12.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CyborgRenderState state) {
        super.setupAnim(state);
        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        this.rightLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.4f * walkSpeed;
        this.leftLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.4f * walkSpeed;
        this.rightLeg.yRot = 0.0f;
        this.leftLeg.yRot = 0.0f;

        if (state.isWorking) {
            this.rightArm.xRot = -1.2f + Mth.sin(state.animationTicks * 0.8f) * 0.35f;
            this.rightArm.yRot = -0.2f;
            this.leftArm.xRot = -0.8f + Mth.cos(state.animationTicks * 0.8f) * 0.15f;
            this.leftArm.yRot = 0.2f;
        } else {
            this.rightArm.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 2.0f * walkSpeed * 0.5f;
            this.rightArm.yRot = 0.0f;
            this.rightArm.zRot = 0.0f;
            this.leftArm.xRot = Mth.cos(walkPos * 0.6662f) * 2.0f * walkSpeed * 0.5f;
            this.leftArm.yRot = 0.0f;
            this.leftArm.zRot = 0.0f;
        }

        float breathe = Mth.sin(state.animationTicks * 0.05f) * 0.02f;
        this.body.yRot = breathe;
        this.visor.visible = true;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
