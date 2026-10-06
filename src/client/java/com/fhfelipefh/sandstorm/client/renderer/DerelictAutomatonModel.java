package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class DerelictAutomatonModel extends EntityModel<DerelictAutomatonRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public DerelictAutomatonModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-4.0f, -12.0f, -3.0f, 8.0f, 12.0f, 6.0f)
                .texOffs(28, 16).addBox(-2.0f, -8.0f, -3.5f, 4.0f, 5.0f, 1.0f)
                .texOffs(38, 16).addBox(-3.0f, -11.0f, 3.0f, 6.0f, 8.0f, 2.0f),
                PartPose.offset(0.0f, 12.0f, 0.0f));

        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                .texOffs(32, 0).addBox(-3.0f, -5.0f, -4.5f, 6.0f, 2.0f, 1.0f)
                .texOffs(46, 0).addBox(2.0f, -12.0f, -1.0f, 1.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -12.0f, 0.0f));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(28, 26).addBox(0.0f, -1.0f, -1.5f, 2.0f, 6.0f, 3.0f)
                .texOffs(38, 26).addBox(0.5f, 5.0f, -1.0f, 1.0f, 7.0f, 2.0f),
                PartPose.offset(4.5f, -10.0f, 0.0f));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(44, 26).addBox(-2.0f, -1.0f, -1.5f, 2.0f, 4.0f, 3.0f)
                .texOffs(54, 26).addBox(-1.5f, 3.0f, -0.5f, 1.0f, 4.0f, 1.0f),
                PartPose.offset(-4.5f, -10.0f, 0.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 8.0f, 3.0f)
                .texOffs(12, 36).addBox(-2.0f, 8.0f, -3.0f, 4.0f, 4.0f, 5.0f),
                PartPose.offset(2.0f, 12.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 47).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 8.0f, 3.0f)
                .texOffs(12, 47).addBox(-2.0f, 8.0f, -3.0f, 4.0f, 4.0f, 5.0f),
                PartPose.offset(-2.0f, 12.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(DerelictAutomatonRenderState state) {
        super.setupAnim(state);

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        this.leftLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.4f * walkSpeed;
        this.rightLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.4f * walkSpeed;
        this.leftLeg.yRot = 0.0f;
        this.rightLeg.yRot = 0.0f;

        this.leftArm.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 0.8f * walkSpeed;
        this.leftArm.zRot = 0.0f;
        this.rightArm.xRot = Mth.cos(walkPos * 0.6662f) * 0.5f * walkSpeed;
        this.rightArm.zRot = 0.0f;
        this.body.xRot = 0.0f;

        if (state.isOvercharging) {
            float jitter = Mth.sin(state.animationTicks * 15.0f) * 0.12f;
            this.body.xRot = jitter;
            this.head.xRot = -0.3f + jitter;
            this.leftArm.zRot = 0.45f + jitter;
            this.rightArm.zRot = -0.45f - jitter;
        }
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
