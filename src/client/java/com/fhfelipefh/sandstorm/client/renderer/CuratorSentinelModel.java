package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class CuratorSentinelModel extends EntityModel<CuratorSentinelRenderState> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart sensor;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public CuratorSentinelModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.sensor = this.head.getChild("sensor");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-4.0f, -12.0f, -2.5f, 8.0f, 12.0f, 5.0f)
                .texOffs(26, 16).addBox(-3.0f, -10.0f, -3.5f, 6.0f, 8.0f, 1.0f)
                .texOffs(40, 16).addBox(-2.5f, -11.0f, 2.5f, 5.0f, 9.0f, 2.0f),
                PartPose.offset(0.0f, 12.0f, 0.0f));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.5f, -7.0f, -3.5f, 7.0f, 7.0f, 7.0f)
                .texOffs(28, 0).addBox(-2.5f, -9.0f, -2.5f, 5.0f, 2.0f, 5.0f),
                PartPose.offset(0.0f, -12.0f, 0.0f));

        head.addOrReplaceChild("sensor", CubeListBuilder.create()
                .texOffs(48, 0).addBox(-2.0f, -4.5f, -4.5f, 4.0f, 2.0f, 1.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(26, 26).addBox(0.0f, -1.0f, -1.5f, 3.0f, 12.0f, 3.0f),
                PartPose.offset(4.0f, -11.0f, 0.0f));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(38, 26).addBox(-3.0f, -1.0f, -1.5f, 3.0f, 12.0f, 3.0f),
                PartPose.offset(-4.0f, -11.0f, 0.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 33).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 12.0f, 3.0f),
                PartPose.offset(2.0f, 12.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(12, 33).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 12.0f, 3.0f),
                PartPose.offset(-2.0f, 12.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CuratorSentinelRenderState state) {
        super.setupAnim(state);

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        this.leftLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.2f * walkSpeed;
        this.rightLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.2f * walkSpeed;
        this.leftLeg.yRot = 0.0f;
        this.rightLeg.yRot = 0.0f;
        this.leftLeg.zRot = 0.0f;
        this.rightLeg.zRot = 0.0f;

        if (state.isAlarmActive) {
            float alertJitter = Mth.sin(state.animationTicks * 20.0f) * 0.08f;
            this.body.xRot = 0.15f + alertJitter;
            this.leftArm.xRot = -1.2f + alertJitter;
            this.rightArm.xRot = -1.2f - alertJitter;
            this.leftArm.zRot = 0.3f;
            this.rightArm.zRot = -0.3f;
            this.sensor.xRot = alertJitter * 2.0f;
        } else {
            this.body.xRot = 0.0f;
            this.leftArm.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 0.6f * walkSpeed;
            this.rightArm.xRot = Mth.cos(walkPos * 0.6662f) * 0.6f * walkSpeed;
            this.leftArm.zRot = 0.0f;
            this.rightArm.zRot = 0.0f;

            float idleScan = Mth.sin(state.animationTicks * 0.04f) * 0.2f;
            this.head.yRot += idleScan;
            this.sensor.xRot = 0.0f;
        }
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
