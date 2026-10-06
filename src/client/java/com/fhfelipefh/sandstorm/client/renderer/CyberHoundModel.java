package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class CyberHoundModel extends EntityModel<CyberHoundRenderState> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart upperBody;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;

    public CyberHoundModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.upperBody = root.getChild("upper_body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f)
                .texOffs(0, 10).addBox(-1.5f, -0.02f, -5.0f, 3.0f, 3.0f, 4.0f)
                .texOffs(16, 14).addBox(-3.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f)
                .texOffs(16, 14).addBox(1.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f)
                .texOffs(52, 0).addBox(-0.5f, -6.0f, -2.0f, 1.0f, 3.0f, 1.0f),
                PartPose.offset(-1.0f, 13.5f, -7.0f));

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(18, 14).addBox(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f)
                .texOffs(42, 14).addBox(-2.0f, -3.0f, -2.0f, 4.0f, 1.0f, 4.0f),
                PartPose.offsetAndRotation(0.0f, 14.0f, 2.0f, ((float) Math.PI / 2.0f), 0.0f, 0.0f));

        root.addOrReplaceChild("upper_body", CubeListBuilder.create()
                .texOffs(21, 0).addBox(-4.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f),
                PartPose.offsetAndRotation(-1.0f, 14.0f, -3.0f, ((float) Math.PI / 2.0f), 0.0f, 0.0f));

        root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(0, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f),
                PartPose.offset(-2.5f, 16.0f, 7.0f));

        root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(0, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f),
                PartPose.offset(0.5f, 16.0f, 7.0f));

        root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(0, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f),
                PartPose.offset(-2.5f, 16.0f, -4.0f));

        root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(0, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f),
                PartPose.offset(0.5f, 16.0f, -4.0f));

        root.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(9, 18).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 8.0f, 1.0f),
                PartPose.offsetAndRotation(-1.0f, 12.0f, 8.0f, 0.62831855f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CyberHoundRenderState state) {
        super.setupAnim(state);

        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);
        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);

        if (state.isSitting) {
            this.body.setPos(0.0f, 18.0f, 0.0f);
            this.body.xRot = ((float) Math.PI / 4.0f);
            this.upperBody.setPos(-1.0f, 16.0f, -2.0f);
            this.upperBody.xRot = ((float) Math.PI / 3.0f);
            this.head.setPos(-1.0f, 14.0f, -4.0f);

            this.rightHindLeg.setPos(-2.5f, 22.0f, 4.0f);
            this.rightHindLeg.xRot = -((float) Math.PI / 2.0f);
            this.leftHindLeg.setPos(0.5f, 22.0f, 4.0f);
            this.leftHindLeg.xRot = -((float) Math.PI / 2.0f);

            this.rightFrontLeg.setPos(-2.5f, 17.0f, -4.0f);
            this.rightFrontLeg.xRot = 0.0f;
            this.leftFrontLeg.setPos(0.5f, 17.0f, -4.0f);
            this.leftFrontLeg.xRot = 0.0f;

            this.tail.setPos(-1.0f, 21.0f, 6.0f);
            this.tail.xRot = -0.3f;
            this.tail.yRot = 0.0f;
        } else {
            this.body.setPos(0.0f, 14.0f, 2.0f);
            this.body.xRot = ((float) Math.PI / 2.0f);
            this.upperBody.setPos(-1.0f, 14.0f, -3.0f);
            this.upperBody.xRot = ((float) Math.PI / 2.0f);
            this.head.setPos(-1.0f, 13.5f, -7.0f);

            this.rightHindLeg.setPos(-2.5f, 16.0f, 7.0f);
            this.leftHindLeg.setPos(0.5f, 16.0f, 7.0f);
            this.rightFrontLeg.setPos(-2.5f, 16.0f, -4.0f);
            this.leftFrontLeg.setPos(0.5f, 16.0f, -4.0f);

            float walkPos = state.walkAnimationPos;
            float walkSpeed = state.walkAnimationSpeed;

            this.rightHindLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.4f * walkSpeed;
            this.leftHindLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.4f * walkSpeed;
            this.rightFrontLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.4f * walkSpeed;
            this.leftFrontLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.4f * walkSpeed;

            this.tail.setPos(-1.0f, 12.0f, 8.0f);
            this.tail.xRot = 0.62831855f;
            this.tail.yRot = state.isTame ? Mth.cos(state.animationTicks * 0.6f) * 0.45f : 0.0f;
        }
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
