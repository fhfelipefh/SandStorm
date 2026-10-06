package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class LaborerUnitModel extends EntityModel<LaborerUnitRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public LaborerUnitModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-5.0f, -12.0f, -4.0f, 10.0f, 12.0f, 8.0f)
                .texOffs(36, 16).addBox(-4.0f, -10.0f, 4.0f, 8.0f, 8.0f, 2.0f),
                PartPose.offset(0.0f, 10.0f, 0.0f));

        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                .texOffs(32, 0).addBox(-3.0f, -5.0f, -5.0f, 6.0f, 2.0f, 1.0f),
                PartPose.offset(0.0f, -12.0f, 0.0f));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(36, 26).addBox(-3.0f, -2.0f, -2.5f, 5.0f, 14.0f, 5.0f),
                PartPose.offset(-6.0f, -10.0f, 0.0f));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(20, 26).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                PartPose.offset(6.0f, -10.0f, 0.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 14.0f, 5.0f),
                PartPose.offset(-2.5f, 10.0f, 0.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 14.0f, 5.0f),
                PartPose.offset(2.5f, 10.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(LaborerUnitRenderState state) {
        super.setupAnim(state);

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);
        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);

        this.rightLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.2f * walkSpeed;
        this.leftLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.2f * walkSpeed;

        this.leftArm.xRot = -0.7f + Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 0.4f * walkSpeed;
        this.rightArm.xRot = -0.7f + Mth.cos(walkPos * 0.6662f) * 0.4f * walkSpeed;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
