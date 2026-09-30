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
                .texOffs(54, 0).addBox(-4.0f, -24.0f, -6.5f, 8.0f, 8.0f, 2.0f)
                .texOffs(0, 28).addBox(-4.0f, -36.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                PartPose.offset(0.0f, 6.0f, 0.0f));

        body.addOrReplaceChild("flightWings", CubeListBuilder.create()
                .texOffs(0, 73).addBox(-16.0f, -26.0f, 5.0f, 32.0f, 12.0f, 2.0f),
                PartPose.ZERO);

        body.addOrReplaceChild("overdriveBoosters", CubeListBuilder.create()
                .texOffs(0, 90).addBox(-6.0f, -24.0f, 5.0f, 12.0f, 8.0f, 4.0f),
                PartPose.ZERO);

        body.addOrReplaceChild("leftArm", CubeListBuilder.create()
                .texOffs(34, 28).addBox(0.0f, -2.0f, -3.0f, 6.0f, 14.0f, 6.0f),
                PartPose.offset(8.0f, -24.0f, 0.0f));

        body.addOrReplaceChild("rightArm", CubeListBuilder.create()
                .texOffs(60, 28).addBox(-6.0f, -2.0f, -3.0f, 6.0f, 14.0f, 6.0f),
                PartPose.offset(-8.0f, -24.0f, 0.0f));

        root.addOrReplaceChild("leftLeg", CubeListBuilder.create()
                .texOffs(0, 46).addBox(-3.5f, 0.0f, -3.5f, 7.0f, 18.0f, 7.0f),
                PartPose.offset(4.5f, 6.0f, 0.0f));

        root.addOrReplaceChild("rightLeg", CubeListBuilder.create()
                .texOffs(30, 46).addBox(-3.5f, 0.0f, -3.5f, 7.0f, 18.0f, 7.0f),
                PartPose.offset(-4.5f, 6.0f, 0.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(MegazordRenderState state) {
        super.setupAnim(state);
        this.flightWings.visible = state.hasFlightModule;
        this.overdriveBoosters.visible = state.hasOverdriveModule;

        float legSwing = Mth.cos(state.walkAnimationPos * 0.4f) * 0.8f * state.walkAnimationSpeed;
        this.leftLeg.xRot = legSwing;
        this.rightLeg.xRot = -legSwing;
        this.leftArm.xRot = -legSwing * 0.7f;
        this.rightArm.xRot = legSwing * 0.7f;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
