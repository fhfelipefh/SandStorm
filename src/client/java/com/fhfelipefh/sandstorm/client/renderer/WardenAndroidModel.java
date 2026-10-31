package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class WardenAndroidModel extends EntityModel<WardenAndroidRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private final ModelPart chestCore;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public WardenAndroidModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.leftAntenna = this.head.getChild("left_antenna");
        this.rightAntenna = this.head.getChild("right_antenna");
        this.chestCore = this.body.getChild("chest_core");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-9.0f, -14.0f, -5.0f, 18.0f, 14.0f, 10.0f),
                PartPose.offset(0.0f, 8.0f, 0.0f));

        body.addOrReplaceChild("chest_core", CubeListBuilder.create()
                .texOffs(0, 24).addBox(-4.0f, -11.0f, -6.0f, 8.0f, 8.0f, 2.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 34).addBox(-8.0f, -16.0f, -5.0f, 16.0f, 16.0f, 10.0f),
                PartPose.offset(0.0f, -14.0f, 0.0f));

        head.addOrReplaceChild("left_antenna", CubeListBuilder.create()
                .texOffs(56, 0).addBox(8.0f, -24.0f, -0.5f, 8.0f, 10.0f, 1.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        head.addOrReplaceChild("right_antenna", CubeListBuilder.create()
                .texOffs(56, 10).addBox(-16.0f, -24.0f, -0.5f, 8.0f, 10.0f, 1.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-2.0f, 0.0f, -4.0f, 8.0f, 24.0f, 8.0f),
                PartPose.offset(11.0f, -13.0f, 0.0f));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(64, 32).addBox(-6.0f, 0.0f, -4.0f, 8.0f, 24.0f, 8.0f),
                PartPose.offset(-11.0f, -13.0f, 0.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 60).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 16.0f, 6.0f),
                PartPose.offset(5.5f, 8.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(24, 60).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 16.0f, 6.0f),
                PartPose.offset(-5.5f, 8.0f, 0.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(WardenAndroidRenderState state) {
        super.setupAnim(state);

        if (state.isDeactivated) {
            this.head.xRot = 0.5f;
            this.head.yRot = 0.0f;
            this.leftArm.xRot = 0.2f;
            this.rightArm.xRot = 0.2f;
            this.leftLeg.xRot = 0.0f;
            this.rightLeg.xRot = 0.0f;
            this.leftAntenna.zRot = 0.0f;
            this.rightAntenna.zRot = 0.0f;
            return;
        }

        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);
        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);

        this.leftLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662f) * 1.4f * state.walkAnimationSpeed;
        this.rightLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662f + (float) Math.PI) * 1.4f * state.walkAnimationSpeed;
        this.leftArm.xRot = Mth.cos(state.walkAnimationPos * 0.6662f + (float) Math.PI) * 1.0f * state.walkAnimationSpeed;
        this.rightArm.xRot = Mth.cos(state.walkAnimationPos * 0.6662f) * 1.0f * state.walkAnimationSpeed;

        float twitch = Mth.sin(state.animationTicks * 0.12f) * 0.15f;
        this.leftAntenna.zRot = twitch;
        this.rightAntenna.zRot = -twitch;

        if (state.attackAnim > 0) {
            this.leftArm.xRot = -1.6f;
            this.rightArm.xRot = -1.6f;
        }
    }
}

