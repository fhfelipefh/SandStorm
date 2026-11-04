package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class CyberMoleModel extends EntityModel<CyberMoleRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart drill;
    private final ModelPart leftFrontClaw;
    private final ModelPart rightFrontClaw;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart exhaust;

    public CyberMoleModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.drill = this.head.getChild("drill");
        this.leftFrontClaw = this.body.getChild("left_front_claw");
        this.rightFrontClaw = this.body.getChild("right_front_claw");
        this.leftHindLeg = this.body.getChild("left_hind_leg");
        this.rightHindLeg = this.body.getChild("right_hind_leg");
        this.exhaust = this.body.getChild("exhaust");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0f, -4.0f, -7.0f, 10.0f, 7.0f, 14.0f)
                .texOffs(0, 21).addBox(-5.2f, -5.5f, -6.5f, 10.4f, 2.0f, 13.0f, new CubeDeformation(0.1f)),
                PartPose.offset(0.0f, 18.0f, 0.0f));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(34, 0).addBox(-3.0f, -2.5f, -6.0f, 6.0f, 5.0f, 6.0f)
                .texOffs(34, 11).addBox(-3.2f, -3.2f, -5.5f, 6.4f, 2.0f, 4.0f, new CubeDeformation(0.08f)),
                PartPose.offset(0.0f, -0.5f, -7.0f));

        head.addOrReplaceChild("drill", CubeListBuilder.create()
                .texOffs(48, 21).addBox(-2.0f, -2.0f, -3.0f, 4.0f, 4.0f, 3.0f)
                .texOffs(50, 28).addBox(-1.5f, -1.5f, -5.0f, 3.0f, 3.0f, 2.0f)
                .texOffs(52, 33).addBox(-1.0f, -1.0f, -7.0f, 2.0f, 2.0f, 2.0f),
                PartPose.offset(0.0f, 0.0f, -6.0f));

        body.addOrReplaceChild("left_front_claw", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-1.0f, -1.0f, -4.0f, 3.0f, 6.0f, 7.0f)
                .texOffs(20, 36).addBox(-0.5f, 3.0f, -7.0f, 2.0f, 2.0f, 4.0f),
                PartPose.offset(5.5f, 0.5f, -4.0f));

        body.addOrReplaceChild("right_front_claw", CubeListBuilder.create()
                .texOffs(0, 36).mirror().addBox(-2.0f, -1.0f, -4.0f, 3.0f, 6.0f, 7.0f)
                .texOffs(20, 36).mirror().addBox(-1.5f, 3.0f, -7.0f, 2.0f, 2.0f, 4.0f),
                PartPose.offset(-5.5f, 0.5f, -4.0f));

        body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(32, 36).addBox(-1.0f, -1.0f, -2.5f, 3.0f, 5.0f, 5.0f),
                PartPose.offset(5.2f, 1.5f, 4.5f));

        body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(32, 36).mirror().addBox(-2.0f, -1.0f, -2.5f, 3.0f, 5.0f, 5.0f),
                PartPose.offset(-5.2f, 1.5f, 4.5f));

        body.addOrReplaceChild("exhaust", CubeListBuilder.create()
                .texOffs(48, 37).addBox(-2.0f, -2.0f, 0.0f, 4.0f, 3.0f, 3.0f),
                PartPose.offset(0.0f, -1.0f, 7.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CyberMoleRenderState state) {
        super.setupAnim(state);

        this.body.xRot = 0.0f;
        this.body.yRot = 0.0f;
        this.head.xRot = state.xRot * 0.017453292f;
        this.head.yRot = state.yRot * 0.017453292f;

        if (state.isDeactivated) {
            this.body.y = 19.5f;
            this.head.xRot = 0.45f;
            this.leftFrontClaw.xRot = 0.2f;
            this.rightFrontClaw.xRot = 0.2f;
            this.leftHindLeg.xRot = 0.0f;
            this.rightHindLeg.xRot = 0.0f;
            this.drill.zRot = 0.0f;
            return;
        }

        if (state.isBurrowed) {
            this.body.y = 22.5f;
            this.head.xRot = -0.15f;
            this.drill.zRot = state.animationTicks * 0.8f;
            float digCycle = Mth.sin(state.animationTicks * 0.5f) * 0.45f;
            this.leftFrontClaw.xRot = 0.6f + digCycle;
            this.rightFrontClaw.xRot = 0.6f - digCycle;
            this.leftHindLeg.xRot = -0.2f;
            this.rightHindLeg.xRot = -0.2f;
            return;
        }

        if (state.isSitting) {
            this.body.y = 19.5f;
            this.head.xRot = 0.1f;
            this.leftFrontClaw.xRot = -0.3f;
            this.rightFrontClaw.xRot = -0.3f;
            this.leftHindLeg.xRot = 0.5f;
            this.rightHindLeg.xRot = 0.5f;
            this.drill.zRot = 0.0f;
            return;
        }

        this.body.y = 18.0f;
        this.drill.zRot = state.drillRotation;

        float walkSpeed = state.walkAnimationSpeed;
        float walkPos = state.walkAnimationPos;

        this.leftFrontClaw.xRot = Mth.cos(walkPos * 0.6662f) * 1.2f * walkSpeed;
        this.rightFrontClaw.xRot = Mth.cos(walkPos * 0.6662f + 3.1415927f) * 1.2f * walkSpeed;
        this.leftHindLeg.xRot = Mth.cos(walkPos * 0.6662f + 3.1415927f) * 1.2f * walkSpeed;
        this.rightHindLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.2f * walkSpeed;
    }
}

