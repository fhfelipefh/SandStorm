package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class FrostclawModel extends EntityModel<FrostclawRenderState> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart chestSac;
    private final ModelPart rightFrontArm;
    private final ModelPart leftFrontArm;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;

    public FrostclawModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.chestSac = this.body.getChild("chest_sac");
        this.rightFrontArm = root.getChild("right_front_arm");
        this.leftFrontArm = root.getChild("left_front_arm");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5f, -5.0f, -6.0f, 9.0f, 8.0f, 8.0f)
                .texOffs(0, 16).addBox(-3.0f, -2.0f, -11.0f, 6.0f, 5.0f, 5.0f)
                .texOffs(26, 0).addBox(-4.5f, -7.0f, -2.0f, 2.0f, 2.0f, 1.0f)
                .texOffs(26, 0).addBox(2.5f, -7.0f, -2.0f, 2.0f, 2.0f, 1.0f),
                PartPose.offset(0.0f, 6.0f, -12.0f));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-9.0f, -7.0f, -11.0f, 18.0f, 14.0f, 22.0f)
                .texOffs(0, 64).addBox(-11.0f, -9.0f, -8.0f, 22.0f, 4.0f, 8.0f),
                PartPose.offset(0.0f, 7.0f, 0.0f));

        body.addOrReplaceChild("chest_sac", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-5.0f, -2.0f, -12.5f, 10.0f, 8.0f, 3.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        root.addOrReplaceChild("right_front_arm", CubeListBuilder.create()
                .texOffs(64, 28).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 16.0f, 6.0f)
                .texOffs(64, 50).addBox(-3.5f, 16.0f, -5.0f, 7.0f, 3.0f, 8.0f),
                PartPose.offset(-10.0f, 5.0f, -8.0f));

        root.addOrReplaceChild("left_front_arm", CubeListBuilder.create()
                .texOffs(64, 28).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 16.0f, 6.0f)
                .texOffs(64, 50).addBox(-3.5f, 16.0f, -5.0f, 7.0f, 3.0f, 8.0f),
                PartPose.offset(10.0f, 5.0f, -8.0f));

        root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(88, 28).addBox(-3.0f, 0.0f, -3.5f, 6.0f, 14.0f, 7.0f)
                .texOffs(88, 49).addBox(-3.5f, 14.0f, -4.5f, 7.0f, 3.0f, 8.0f),
                PartPose.offset(-7.5f, 7.0f, 8.0f));

        root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(88, 28).addBox(-3.0f, 0.0f, -3.5f, 6.0f, 14.0f, 7.0f)
                .texOffs(88, 49).addBox(-3.5f, 14.0f, -4.5f, 7.0f, 3.0f, 8.0f),
                PartPose.offset(7.5f, 7.0f, 8.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(FrostclawRenderState state) {
        super.setupAnim(state);
        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        this.chestSac.visible = state.isChestSacIntact;

        if (state.isDeactivated) {
            this.rightFrontArm.xRot = 0.0f;
            this.leftFrontArm.xRot = 0.0f;
            this.rightHindLeg.xRot = 0.0f;
            this.leftHindLeg.xRot = 0.0f;
            this.head.xRot = 0.4f;
            return;
        }

        if (state.isSlamAttacking) {
            this.body.xRot = -0.5f;
            this.head.xRot = -0.3f;
            this.rightFrontArm.xRot = -1.8f;
            this.leftFrontArm.xRot = -1.8f;
            return;
        } else {
            this.body.xRot = 0.0f;
        }

        this.rightFrontArm.xRot = Mth.cos(walkPos * 0.5f) * 1.1f * walkSpeed;
        this.leftFrontArm.xRot = Mth.cos(walkPos * 0.5f + (float) Math.PI) * 1.1f * walkSpeed;
        this.rightHindLeg.xRot = Mth.cos(walkPos * 0.5f + (float) Math.PI) * 1.1f * walkSpeed;
        this.leftHindLeg.xRot = Mth.cos(walkPos * 0.5f) * 1.1f * walkSpeed;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}

