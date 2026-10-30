package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class FanghornModel extends EntityModel<FanghornRenderState> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart horns;
    private final ModelPart body;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;

    public FanghornModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.horns = this.head.getChild("horns");
        this.body = root.getChild("body");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.5f, -6.0f, -8.0f, 7.0f, 7.0f, 8.0f)
                .texOffs(0, 15).addBox(-2.5f, -3.0f, -13.0f, 5.0f, 4.0f, 5.0f)
                .texOffs(30, 0).addBox(-2.0f, -1.0f, -2.0f, 4.0f, 8.0f, 4.0f),
                PartPose.offset(0.0f, 9.0f, -8.0f));

        head.addOrReplaceChild("horns", CubeListBuilder.create()
                .texOffs(32, 16).addBox(-6.0f, -12.0f, -6.0f, 3.0f, 7.0f, 3.0f)
                .texOffs(32, 16).addBox(3.0f, -12.0f, -6.0f, 3.0f, 7.0f, 3.0f)
                .texOffs(44, 16).addBox(-7.0f, -15.0f, -12.0f, 2.0f, 3.0f, 7.0f)
                .texOffs(44, 16).addBox(5.0f, -15.0f, -12.0f, 2.0f, 3.0f, 7.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 24).addBox(-5.5f, -5.0f, -9.0f, 11.0f, 10.0f, 18.0f)
                .texOffs(0, 52).addBox(-4.5f, -7.0f, -6.0f, 9.0f, 2.0f, 10.0f)
                .texOffs(38, 30).addBox(-7.0f, -2.0f, -2.0f, 2.0f, 5.0f, 6.0f)
                .texOffs(38, 30).addBox(5.0f, -2.0f, -2.0f, 2.0f, 5.0f, 6.0f),
                PartPose.offset(0.0f, 10.0f, 0.0f));

        root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(48, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 13.0f, 3.0f),
                PartPose.offset(-3.5f, 11.0f, -6.0f));

        root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(48, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 13.0f, 3.0f),
                PartPose.offset(3.5f, 11.0f, -6.0f));

        root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(48, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 13.0f, 3.0f),
                PartPose.offset(-3.5f, 11.0f, 6.0f));

        root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(48, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 13.0f, 3.0f),
                PartPose.offset(3.5f, 11.0f, 6.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(FanghornRenderState state) {
        super.setupAnim(state);
        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        if (state.isDeactivated) {
            this.rightFrontLeg.xRot = 0.0f;
            this.leftFrontLeg.xRot = 0.0f;
            this.rightHindLeg.xRot = 0.0f;
            this.leftHindLeg.xRot = 0.0f;
            this.head.xRot = 0.6f;
            return;
        }

        if (state.isChargingHorns) {
            this.head.xRot = 0.45f;
        }

        this.rightFrontLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.4f * walkSpeed;
        this.leftFrontLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.4f * walkSpeed;
        this.rightHindLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.4f * walkSpeed;
        this.leftHindLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.4f * walkSpeed;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}

