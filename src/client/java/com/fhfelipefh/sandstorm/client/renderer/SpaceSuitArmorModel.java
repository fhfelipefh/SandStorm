package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;

public class SpaceSuitArmorModel extends HumanoidModel<HumanoidRenderState> {
    public SpaceSuitArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5f, -8.5f, -4.5f, 9.0f, 9.0f, 9.0f, new CubeDeformation(0.5f))
                .texOffs(36, 0).addBox(-3.5f, -6.5f, -5.3f, 7.0f, 4.0f, 2.0f)
                .texOffs(54, 0).addBox(-4.8f, -3.5f, -4.5f, 2.0f, 2.0f, 4.0f)
                .texOffs(54, 6).addBox(2.8f, -3.5f, -4.5f, 2.0f, 2.0f, 4.0f)
                .texOffs(0, 18).addBox(4.2f, -11.0f, -0.5f, 1.0f, 5.0f, 1.0f)
                .texOffs(36, 6).addBox(-4.5f, -0.5f, -4.5f, 9.0f, 2.0f, 9.0f),
                PartPose.ZERO);

        head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-4.5f, 0.0f, -2.5f, 9.0f, 12.0f, 5.0f, new CubeDeformation(0.6f))
                .texOffs(28, 27).addBox(-2.0f, 2.0f, -3.4f, 4.0f, 4.0f, 1.0f)
                .texOffs(38, 27).addBox(-1.0f, 1.0f, 2.6f, 2.0f, 10.0f, 1.5f)
                .texOffs(45, 27).addBox(-3.8f, 1.0f, 2.8f, 2.5f, 8.0f, 2.5f)
                .texOffs(55, 27).addBox(1.3f, 1.0f, 2.8f, 2.5f, 8.0f, 2.5f)
                .texOffs(28, 38).addBox(-4.0f, 0.0f, 1.5f, 8.0f, 2.0f, 2.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(0, 44).addBox(-3.5f, -2.5f, -2.5f, 5.0f, 12.0f, 5.0f, new CubeDeformation(0.4f))
                .texOffs(20, 44).addBox(-4.8f, -3.2f, -3.0f, 5.5f, 5.0f, 6.0f)
                .texOffs(43, 44).addBox(-3.6f, 4.0f, -2.8f, 4.5f, 6.0f, 5.5f),
                PartPose.offset(-5.0f, 2.0f, 0.0f));

        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(63, 44).addBox(-1.5f, -2.5f, -2.5f, 5.0f, 12.0f, 5.0f, new CubeDeformation(0.4f))
                .texOffs(83, 44).addBox(-0.7f, -3.2f, -3.0f, 5.5f, 5.0f, 6.0f)
                .texOffs(106, 44).addBox(-0.9f, 4.0f, -2.8f, 4.5f, 6.0f, 5.5f),
                PartPose.offset(5.0f, 2.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 61).addBox(-2.3f, 0.0f, -2.3f, 4.6f, 12.0f, 4.6f, new CubeDeformation(0.35f))
                .texOffs(19, 61).addBox(-2.8f, 1.0f, -1.0f, 1.0f, 6.0f, 2.0f)
                .texOffs(25, 61).addBox(-2.0f, 4.5f, -2.9f, 4.0f, 3.0f, 1.0f)
                .texOffs(35, 61).addBox(-2.5f, 8.0f, -3.0f, 5.0f, 4.2f, 5.5f),
                PartPose.offset(-1.9f, 12.0f, 0.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(56, 61).addBox(-2.3f, 0.0f, -2.3f, 4.6f, 12.0f, 4.6f, new CubeDeformation(0.35f))
                .texOffs(75, 61).addBox(1.8f, 1.0f, -1.0f, 1.0f, 6.0f, 2.0f)
                .texOffs(81, 61).addBox(-2.0f, 4.5f, -2.9f, 4.0f, 3.0f, 1.0f)
                .texOffs(91, 61).addBox(-2.5f, 8.0f, -3.0f, 5.0f, 4.2f, 5.5f),
                PartPose.offset(1.9f, 12.0f, 0.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    public void copyTransforms(HumanoidModel<HumanoidRenderState> context) {
        this.head.loadPose(context.head.storePose());
        this.hat.loadPose(context.hat.storePose());
        this.body.loadPose(context.body.storePose());
        this.rightArm.loadPose(context.rightArm.storePose());
        this.leftArm.loadPose(context.leftArm.storePose());
        this.rightLeg.loadPose(context.rightLeg.storePose());
        this.leftLeg.loadPose(context.leftLeg.storePose());
    }

    public void setVisibleSlot(EquipmentSlot slot) {
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.rightArm.visible = false;
        this.leftArm.visible = false;
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;

        switch (slot) {
            case HEAD -> {
                this.head.visible = true;
                this.hat.visible = true;
            }
            case CHEST -> {
                this.body.visible = true;
                this.rightArm.visible = true;
                this.leftArm.visible = true;
            }
            case LEGS -> {
                this.rightLeg.visible = true;
                this.leftLeg.visible = true;
            }
            case FEET -> {
                this.rightLeg.visible = true;
                this.leftLeg.visible = true;
            }
            default -> {}
        }
    }
}
