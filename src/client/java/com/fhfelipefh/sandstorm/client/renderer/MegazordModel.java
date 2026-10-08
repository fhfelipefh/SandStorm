package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class MegazordModel extends EntityModel<MegazordRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart flightWings;
    private final ModelPart overdriveBoosters;

    public MegazordModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.flightWings = this.body.getChild("flightWings");
        this.overdriveBoosters = this.body.getChild("overdriveBoosters");
        this.leftArm = this.body.getChild("leftArm");
        this.rightArm = this.body.getChild("rightArm");
        this.leftLeg = root.getChild("leftLeg");
        this.rightLeg = root.getChild("rightLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0f, -28.0f, -5.0f, 16.0f, 16.0f, 10.0f)
                .texOffs(54, 0).addBox(-5.0f, -26.0f, -8.0f, 10.0f, 10.0f, 3.0f)
                .texOffs(82, 0).addBox(-3.5f, -24.5f, -8.5f, 7.0f, 6.0f, 1.0f)
                .texOffs(100, 0).addBox(-4.0f, -15.0f, -7.0f, 8.0f, 4.0f, 2.0f)
                .texOffs(122, 0).addBox(-6.0f, -29.0f, -5.5f, 12.0f, 2.0f, 11.0f)
                .texOffs(170, 0).addBox(-6.0f, -27.0f, 5.0f, 12.0f, 14.0f, 2.0f),
                PartPose.offset(0.0f, 6.0f, 0.0f));

        body.addOrReplaceChild("lowerFrame", CubeListBuilder.create()
                .texOffs(170, 0).addBox(-5.0f, -12.05f, 5.0f, 10.0f, 3.0f, 2.0f),
                PartPose.ZERO);

        body.addOrReplaceChild("leftThigh", CubeListBuilder.create()
                .texOffs(18, 45).addBox(1.0f, -12.05f, -3.5f, 7.0f, 12.1f, 7.0f)
                .texOffs(172, 45).addBox(1.5f, -5.0f, -4.5f, 6.0f, 3.0f, 2.0f),
                PartPose.ZERO);

        body.addOrReplaceChild("rightThigh", CubeListBuilder.create()
                .texOffs(142, 45).addBox(-8.0f, -12.05f, -3.5f, 7.0f, 12.1f, 7.0f)
                .texOffs(172, 45).addBox(-7.5f, -5.0f, -4.5f, 6.0f, 3.0f, 2.0f),
                PartPose.ZERO);

        body.addOrReplaceChild("head",  CubeListBuilder.create()
                .texOffs(200, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                .texOffs(234, 0).addBox(-3.5f, -6.0f, -4.5f, 7.0f, 3.0f, 1.0f)
                .texOffs(0, 28).addBox(-2.0f, -11.0f, -5.0f, 4.0f, 4.0f, 1.0f)
                .texOffs(12, 28).addBox(3.0f, -13.0f, -2.0f, 1.0f, 6.0f, 3.0f)
                .texOffs(22, 28).addBox(-4.0f, -13.0f, -2.0f, 1.0f, 6.0f, 3.0f)
                .texOffs(32, 28).addBox(-2.5f, -2.0f, -4.8f, 5.0f, 2.0f, 2.0f),
                PartPose.offset(0.0f, -28.0f, 0.0f));

        body.addOrReplaceChild("flightWings", CubeListBuilder.create()
                .texOffs(42, 63).addBox(-22.0f, -25.0f, 2.0f, 14.0f, 2.0f, 12.0f)
                .texOffs(70, 63).addBox(8.0f, -25.0f, 2.0f, 14.0f, 2.0f, 12.0f)
                .texOffs(98, 63).addBox(-21.0f, -23.0f, 8.0f, 12.0f, 1.0f, 4.0f)
                .texOffs(122, 63).addBox(9.0f, -23.0f, 8.0f, 12.0f, 1.0f, 4.0f)
                .texOffs(146, 63).addBox(-23.0f, -29.0f, 11.0f, 3.0f, 6.0f, 2.0f)
                .texOffs(156, 63).addBox(20.0f, -29.0f, 11.0f, 3.0f, 6.0f, 2.0f),
                PartPose.ZERO);

        body.addOrReplaceChild("overdriveBoosters", CubeListBuilder.create()
                .texOffs(164, 63).addBox(3.0f, -26.0f, 6.0f, 6.0f, 14.0f, 6.0f)
                .texOffs(190, 63).addBox(-9.0f, -26.0f, 6.0f, 6.0f, 14.0f, 6.0f)
                .texOffs(216, 63).addBox(4.0f, -12.0f, 7.0f, 4.0f, 3.0f, 4.0f)
                .texOffs(234, 63).addBox(-8.0f, -12.0f, 7.0f, 4.0f, 3.0f, 4.0f),
                PartPose.ZERO);

        body.addOrReplaceChild("leftArm", CubeListBuilder.create()
                .texOffs(48, 28).addBox(-1.0f, -5.0f, -4.0f, 9.0f, 5.0f, 8.0f)
                .texOffs(84, 28).addBox(0.0f, 0.0f, -2.5f, 5.0f, 6.0f, 5.0f)
                .texOffs(106, 28).addBox(-0.5f, 6.0f, -3.5f, 7.0f, 8.0f, 7.0f)
                .texOffs(136, 28).addBox(1.0f, 14.0f, -2.0f, 4.0f, 3.0f, 4.0f),
                PartPose.offset(8.0f, -24.0f, 0.0f));

        body.addOrReplaceChild("rightArm", CubeListBuilder.create()
                .texOffs(154, 28).addBox(-8.0f, -5.0f, -4.0f, 9.0f, 5.0f, 8.0f)
                .texOffs(190, 28).addBox(-5.0f, 0.0f, -2.5f, 5.0f, 6.0f, 5.0f)
                .texOffs(212, 28).addBox(-6.5f, 6.0f, -3.5f, 7.0f, 8.0f, 7.0f)
                .texOffs(0, 45).addBox(-5.0f, 14.0f, -2.0f, 4.0f, 3.0f, 4.0f),
                PartPose.offset(-8.0f, -24.0f, 0.0f));

        root.addOrReplaceChild("leftLeg", CubeListBuilder.create()
                .texOffs(18, 45).addBox(-3.5f, 0.0f, -3.5f, 7.0f, 8.0f, 7.0f)
                .texOffs(172, 45).addBox(-3.0f, 7.0f, -4.5f, 6.0f, 3.0f, 2.0f)
                .texOffs(66, 45).addBox(-4.0f, 8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                .texOffs(100, 45).addBox(-4.5f, 15.0f, -6.5f, 9.0f, 3.0f, 11.0f),
                PartPose.offset(4.5f, 6.0f, 0.0f));

        root.addOrReplaceChild("rightLeg", CubeListBuilder.create()
                .texOffs(142, 45).addBox(-3.5f, 0.0f, -3.5f, 7.0f, 8.0f, 7.0f)
                .texOffs(172, 45).addBox(-3.0f, 7.0f, -4.5f, 6.0f, 3.0f, 2.0f)
                .texOffs(190, 45).addBox(-4.0f, 8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                .texOffs(0, 63).addBox(-4.5f, 15.0f, -6.5f, 9.0f, 3.0f, 11.0f),
                PartPose.offset(-4.5f, 6.0f, 0.0f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(MegazordRenderState state) {
        super.setupAnim(state);
        this.root.visible = !state.isFirstPersonPilot;
        this.flightWings.visible = state.hasFlightModule;
        this.overdriveBoosters.visible = state.hasOverdriveModule;

        float legSwing = Mth.cos(state.walkAnimationPos * 0.4f) * 0.8f * state.walkAnimationSpeed;
        this.leftLeg.xRot = legSwing;
        this.rightLeg.xRot = -legSwing;
        this.leftArm.xRot = -legSwing * 0.7f;
        this.rightArm.xRot = legSwing * 0.7f;

        if (state.hasFlightModule && state.isFlying) {
            this.leftLeg.xRot = 0.35f;
            this.rightLeg.xRot = 0.35f;
        }
        this.flightWings.xRot = 0.0f;

        if (state.hasOverdriveModule) {
            float pulse = Mth.sin(state.animationTicks * 0.6f) * 0.03f;
            this.overdriveBoosters.zRot = pulse;
        } else {
            this.overdriveBoosters.zRot = 0.0f;
        }
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getHead() {
        return this.head;
    }
}
