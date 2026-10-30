package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class WaterwingModel extends EntityModel<WaterwingRenderState> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart beak;
    private final ModelPart body;
    private final ModelPart throatReservoir;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart tail;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public WaterwingModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.throatReservoir = this.body.getChild("throat_reservoir");
        this.head = this.body.getChild("head");
        this.beak = this.head.getChild("beak");
        this.tail = this.body.getChild("tail");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0f, -4.0f, -8.0f, 10.0f, 8.0f, 16.0f)
                .texOffs(0, 24).addBox(-3.5f, 4.0f, -6.0f, 7.0f, 4.0f, 10.0f),
                PartPose.offset(0.0f, 12.0f, 0.0f));

        body.addOrReplaceChild("throat_reservoir", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-3.0f, 1.0f, -12.0f, 6.0f, 6.0f, 6.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 38).addBox(-3.0f, -4.0f, -6.0f, 6.0f, 6.0f, 6.0f),
                PartPose.offset(0.0f, -2.0f, -8.0f));

        head.addOrReplaceChild("beak", CubeListBuilder.create()
                .texOffs(24, 38).addBox(-2.0f, -1.0f, -16.0f, 4.0f, 4.0f, 10.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        body.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(34, 24).addBox(-4.0f, -1.0f, 0.0f, 8.0f, 2.0f, 12.0f),
                PartPose.offset(0.0f, -1.0f, 8.0f));

        body.addOrReplaceChild("right_wing", CubeListBuilder.create()
                .texOffs(0, 52).addBox(-18.0f, -0.5f, -6.0f, 18.0f, 1.0f, 11.0f),
                PartPose.offset(-5.0f, -2.0f, -3.0f));

        body.addOrReplaceChild("left_wing", CubeListBuilder.create()
                .texOffs(0, 52).addBox(0.0f, -0.5f, -6.0f, 18.0f, 1.0f, 11.0f),
                PartPose.offset(5.0f, -2.0f, -3.0f));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(52, 38).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 8.0f, 3.0f)
                .texOffs(48, 49).addBox(-2.5f, 7.5f, -5.0f, 5.0f, 1.0f, 6.0f),
                PartPose.offset(-3.5f, 15.0f, 3.0f));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(52, 38).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 8.0f, 3.0f)
                .texOffs(48, 49).addBox(-2.5f, 7.5f, -5.0f, 5.0f, 1.0f, 6.0f),
                PartPose.offset(3.5f, 15.0f, 3.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(WaterwingRenderState state) {
        super.setupAnim(state);

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        if (state.isDeactivated) {
            this.rightWing.zRot = 0.9f;
            this.leftWing.zRot = -0.9f;
            this.head.xRot = 0.6f;
            return;
        }

        if (state.isDiving) {
            this.body.xRot = 0.4f;
            this.rightWing.zRot = 1.3f;
            this.leftWing.zRot = -1.3f;
            this.rightLeg.xRot = 1.2f;
            this.leftLeg.xRot = 1.2f;
            return;
        }

        float flap = Mth.sin(state.animationTicks * 0.4f) * 0.4f;
        this.rightWing.zRot = flap;
        this.leftWing.zRot = -flap;

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;
        this.rightLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.2f * walkSpeed;
        this.leftLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.2f * walkSpeed;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}

