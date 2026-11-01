package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class SeismicScorpionModel extends EntityModel<SeismicScorpionRenderState> {
    private final ModelPart root;
    private final ModelPart prosoma;
    private final ModelPart mesosoma;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart stinger;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leg1Left;
    private final ModelPart leg2Left;
    private final ModelPart leg3Left;
    private final ModelPart leg4Left;
    private final ModelPart leg1Right;
    private final ModelPart leg2Right;
    private final ModelPart leg3Right;
    private final ModelPart leg4Right;

    public SeismicScorpionModel(ModelPart root) {
        super(root);
        this.root = root;
        this.prosoma = root.getChild("prosoma");
        this.mesosoma = root.getChild("mesosoma");
        this.leftArm = this.prosoma.getChild("left_arm");
        this.rightArm = this.prosoma.getChild("right_arm");
        this.leg1Left = this.prosoma.getChild("leg1_left");
        this.leg2Left = this.prosoma.getChild("leg2_left");
        this.leg3Left = this.prosoma.getChild("leg3_left");
        this.leg4Left = this.prosoma.getChild("leg4_left");
        this.leg1Right = this.prosoma.getChild("leg1_right");
        this.leg2Right = this.prosoma.getChild("leg2_right");
        this.leg3Right = this.prosoma.getChild("leg3_right");
        this.leg4Right = this.prosoma.getChild("leg4_right");
        this.tail1 = this.mesosoma.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail4 = this.tail3.getChild("tail4");
        this.stinger = this.tail4.getChild("stinger");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition prosoma = root.addOrReplaceChild("prosoma", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -3.0f, -5.0f, 8.0f, 4.0f, 10.0f)
                .texOffs(0, 14).addBox(-2.5f, -2.5f, -5.3f, 5.0f, 2.0f, 1.0f),
                PartPose.offset(0.0f, 19.0f, -3.0f));

        prosoma.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(0, 31).addBox(0.0f, -1.0f, -6.0f, 2.0f, 2.0f, 6.0f)
                .texOffs(16, 31).addBox(-0.5f, -1.0f, -10.5f, 3.0f, 2.0f, 5.0f),
                PartPose.offset(4.0f, 0.0f, -4.0f));

        prosoma.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(0, 39).addBox(-2.0f, -1.0f, -6.0f, 2.0f, 2.0f, 6.0f)
                .texOffs(16, 39).addBox(-2.5f, -1.0f, -10.5f, 3.0f, 2.0f, 5.0f),
                PartPose.offset(-4.0f, 0.0f, -4.0f));

        prosoma.addOrReplaceChild("leg1_left", CubeListBuilder.create()
                .texOffs(0, 48).addBox(0.0f, -1.0f, -1.0f, 6.0f, 2.0f, 2.0f)
                .texOffs(16, 48).addBox(5.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(4.0f, 1.0f, -3.0f));

        prosoma.addOrReplaceChild("leg2_left", CubeListBuilder.create()
                .texOffs(0, 48).addBox(0.0f, -1.0f, -1.0f, 6.0f, 2.0f, 2.0f)
                .texOffs(16, 48).addBox(5.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(4.0f, 1.0f, -1.0f));

        prosoma.addOrReplaceChild("leg3_left", CubeListBuilder.create()
                .texOffs(0, 48).addBox(0.0f, -1.0f, -1.0f, 6.0f, 2.0f, 2.0f)
                .texOffs(16, 48).addBox(5.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(4.0f, 1.0f, 1.0f));

        prosoma.addOrReplaceChild("leg4_left", CubeListBuilder.create()
                .texOffs(0, 48).addBox(0.0f, -1.0f, -1.0f, 6.0f, 2.0f, 2.0f)
                .texOffs(16, 48).addBox(5.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(4.0f, 1.0f, 3.0f));

        prosoma.addOrReplaceChild("leg1_right", CubeListBuilder.create()
                .texOffs(0, 53).addBox(-6.0f, -1.0f, -1.0f, 6.0f, 2.0f, 2.0f)
                .texOffs(16, 53).addBox(-7.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(-4.0f, 1.0f, -3.0f));

        prosoma.addOrReplaceChild("leg2_right", CubeListBuilder.create()
                .texOffs(0, 53).addBox(-6.0f, -1.0f, -1.0f, 6.0f, 2.0f, 2.0f)
                .texOffs(16, 53).addBox(-7.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(-4.0f, 1.0f, -1.0f));

        prosoma.addOrReplaceChild("leg3_right", CubeListBuilder.create()
                .texOffs(0, 53).addBox(-6.0f, -1.0f, -1.0f, 6.0f, 2.0f, 2.0f)
                .texOffs(16, 53).addBox(-7.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(-4.0f, 1.0f, 1.0f));

        prosoma.addOrReplaceChild("leg4_right", CubeListBuilder.create()
                .texOffs(0, 53).addBox(-6.0f, -1.0f, -1.0f, 6.0f, 2.0f, 2.0f)
                .texOffs(16, 53).addBox(-7.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(-4.0f, 1.0f, 3.0f));

        PartDefinition mesosoma = root.addOrReplaceChild("mesosoma", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-3.5f, -2.5f, 0.0f, 7.0f, 4.0f, 8.0f),
                PartPose.offset(0.0f, 19.0f, 2.0f));

        PartDefinition tail1 = mesosoma.addOrReplaceChild("tail1", CubeListBuilder.create()
                .texOffs(24, 18).addBox(-2.0f, -2.0f, 0.0f, 4.0f, 3.0f, 5.0f),
                PartPose.offsetAndRotation(0.0f, -1.0f, 7.0f, -0.4f, 0.0f, 0.0f));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create()
                .texOffs(30, 26).addBox(-1.5f, -4.5f, -0.5f, 3.0f, 5.0f, 3.0f),
                PartPose.offsetAndRotation(0.0f, -1.0f, 4.5f, -0.6f, 0.0f, 0.0f));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create()
                .texOffs(42, 26).addBox(-1.2f, -4.0f, -1.0f, 2.4f, 4.0f, 2.6f),
                PartPose.offsetAndRotation(0.0f, -4.2f, 1.0f, -0.8f, 0.0f, 0.0f));

        PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create()
                .texOffs(30, 34).addBox(-1.0f, -1.0f, -4.0f, 2.0f, 2.6f, 4.0f),
                PartPose.offsetAndRotation(0.0f, -3.8f, 0.0f, -0.8f, 0.0f, 0.0f));

        tail4.addOrReplaceChild("stinger", CubeListBuilder.create()
                .texOffs(42, 34).addBox(-1.4f, -0.5f, -2.5f, 2.8f, 2.5f, 3.0f)
                .texOffs(54, 34).addBox(-0.5f, 1.8f, -2.0f, 1.0f, 2.0f, 1.0f),
                PartPose.offsetAndRotation(0.0f, 0.5f, -3.8f, -0.2f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(SeismicScorpionRenderState state) {
        super.setupAnim(state);
        this.root.resetPose();
        this.prosoma.resetPose();
        this.mesosoma.resetPose();
        this.tail1.resetPose();
        this.tail2.resetPose();
        this.tail3.resetPose();
        this.tail4.resetPose();
        this.stinger.resetPose();
        this.leftArm.resetPose();
        this.rightArm.resetPose();
        this.leg1Left.resetPose();
        this.leg2Left.resetPose();
        this.leg3Left.resetPose();
        this.leg4Left.resetPose();
        this.leg1Right.resetPose();
        this.leg2Right.resetPose();
        this.leg3Right.resetPose();
        this.leg4Right.resetPose();

        if (state.isDeactivated) {
            this.tail1.xRot = 0.5f;
            this.tail2.xRot = 0.4f;
            this.tail3.xRot = 0.3f;
            this.tail4.xRot = 0.2f;
            this.leftArm.yRot = 0.4f;
            this.rightArm.yRot = -0.4f;
            this.leftArm.xRot = 0.3f;
            this.rightArm.xRot = 0.3f;
            return;
        }

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        float legSwing1 = Mth.cos(walkPos * 0.8f) * 0.6f * walkSpeed;
        float legSwing2 = Mth.cos(walkPos * 0.8f + (float) Math.PI) * 0.6f * walkSpeed;

        this.leg1Left.yRot += legSwing1;
        this.leg3Left.yRot += legSwing1;
        this.leg2Right.yRot += legSwing1;
        this.leg4Right.yRot += legSwing1;

        this.leg2Left.yRot += legSwing2;
        this.leg4Left.yRot += legSwing2;
        this.leg1Right.yRot += legSwing2;
        this.leg3Right.yRot += legSwing2;

        float idle = Mth.sin(state.animationTicks * 0.08f) * 0.06f;
        this.tail2.xRot += idle;
        this.tail3.xRot += idle * 0.8f;
        this.leftArm.yRot += Mth.cos(state.animationTicks * 0.06f) * 0.04f;
        this.rightArm.yRot -= Mth.cos(state.animationTicks * 0.06f) * 0.04f;

        if (state.stingProgress > 0.0f) {
            float sting = Mth.sin(state.stingProgress * (float) Math.PI);
            this.tail1.xRot -= sting * 0.5f;
            this.tail2.xRot -= sting * 0.8f;
            this.tail3.xRot -= sting * 0.9f;
            this.tail4.xRot -= sting * 0.7f;
            this.leftArm.yRot -= sting * 0.3f;
            this.rightArm.yRot += sting * 0.3f;
        }
    }
}
