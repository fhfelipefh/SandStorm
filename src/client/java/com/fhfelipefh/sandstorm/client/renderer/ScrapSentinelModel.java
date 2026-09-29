package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ScrapSentinelModel extends EntityModel<ScrapSentinelRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart crest;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public ScrapSentinelModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.crest = this.body.getChild("crest");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0f, -12.0f, -4.0f, 10.0f, 12.0f, 8.0f)
                .texOffs(0, 20).addBox(-4.0f, -11.0f, -5.0f, 8.0f, 7.0f, 1.0f)
                .texOffs(20, 20).addBox(-2.0f, -9.0f, -5.5f, 4.0f, 3.0f, 1.0f)
                .texOffs(36, 0).addBox(-3.5f, -10.0f, 4.0f, 7.0f, 8.0f, 3.0f)
                .texOffs(56, 0).addBox(5.0f, -14.0f, -1.0f, 1.0f, 4.0f, 2.0f)
                .texOffs(56, 0).addBox(-6.0f, -14.0f, -1.0f, 1.0f, 4.0f, 2.0f),
                PartPose.offset(0.0f, 12.0f, 0.0f));

        body.addOrReplaceChild("crest", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-4.5f, -2.0f, -3.0f, 9.0f, 4.0f, 5.0f),
                PartPose.offset(0.0f, -12.0f, -2.0f));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(32, 20).addBox(0.0f, -1.0f, -2.0f, 3.0f, 6.0f, 4.0f)
                .texOffs(48, 20).addBox(0.0f, 5.0f, -2.0f, 3.0f, 6.0f, 4.0f)
                .texOffs(0, 38).addBox(0.5f, 11.0f, -1.5f, 2.0f, 3.0f, 3.0f),
                PartPose.offset(5.5f, -9.0f, 0.0f));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(12, 38).addBox(-3.0f, -1.0f, -2.0f, 3.0f, 6.0f, 4.0f)
                .texOffs(28, 38).addBox(-3.5f, 5.0f, -2.5f, 4.0f, 8.0f, 5.0f),
                PartPose.offset(-5.5f, -9.0f, 0.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(48, 32).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f)
                .texOffs(0, 48).addBox(-2.0f, 6.0f, -3.0f, 4.0f, 6.0f, 5.0f),
                PartPose.offset(2.5f, 12.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(48, 42).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f)
                .texOffs(20, 48).addBox(-2.0f, 6.0f, -3.0f, 4.0f, 6.0f, 5.0f),
                PartPose.offset(-2.5f, 12.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(ScrapSentinelRenderState state) {
        super.setupAnim(state);

        if (state.isDormant) {
            this.body.y = 16.0f;
            this.body.yRot = 0.0f;
            this.crest.xRot = 0.85f;
            this.leftArm.xRot = 0.75f;
            this.leftArm.yRot = 0.0f;
            this.leftArm.zRot = 0.5f;
            this.rightArm.xRot = 0.75f;
            this.rightArm.yRot = 0.0f;
            this.rightArm.zRot = -0.5f;
            this.leftLeg.y = 17.0f;
            this.leftLeg.xRot = -1.35f;
            this.leftLeg.yRot = 0.0f;
            this.rightLeg.y = 17.0f;
            this.rightLeg.xRot = -1.35f;
            this.rightLeg.yRot = 0.0f;
            return;
        }

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.body.y = 12.0f;
        this.body.yRot = state.yRot * ((float) Math.PI / 180.0f) * 0.25f;
        this.crest.xRot = -0.2f + (state.xRot * ((float) Math.PI / 180.0f) * 0.4f);

        this.leftLeg.y = 12.0f;
        this.rightLeg.y = 12.0f;
        this.leftLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.3f * walkSpeed;
        this.rightLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.3f * walkSpeed;
        this.leftLeg.yRot = 0.0f;
        this.rightLeg.yRot = 0.0f;

        this.leftArm.zRot = 0.0f;
        this.rightArm.zRot = 0.0f;
        this.leftArm.yRot = 0.0f;
        this.rightArm.yRot = 0.0f;
        this.leftArm.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 0.8f * walkSpeed;
        this.rightArm.xRot = Mth.cos(walkPos * 0.6662f) * 0.8f * walkSpeed;

        if (state.attackAnim > 0) {
            float jitter = Mth.sin(state.animationTicks * 4.0f) * 0.12f;
            this.leftArm.xRot = -1.35f + jitter;
            this.leftArm.zRot = 0.35f;
            this.rightArm.xRot = -1.35f - jitter;
            this.rightArm.zRot = -0.35f;
        }
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
