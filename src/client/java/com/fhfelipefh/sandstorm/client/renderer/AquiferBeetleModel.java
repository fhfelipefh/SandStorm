package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class AquiferBeetleModel extends EntityModel<AquiferBeetleRenderState> {
    private final ModelPart root;
    private final ModelPart carapace;
    private final ModelPart head;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart midLeftLeg;
    private final ModelPart midRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backRightLeg;

    public AquiferBeetleModel(ModelPart root) {
        super(root);
        this.root = root;
        this.carapace = root.getChild("carapace");
        this.head = root.getChild("head");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.midLeftLeg = root.getChild("mid_left_leg");
        this.midRightLeg = root.getChild("mid_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("carapace", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0f, -6.0f, -10.0f, 16.0f, 6.0f, 20.0f)
                .texOffs(0, 26).addBox(-6.0f, -8.0f, -8.0f, 12.0f, 2.0f, 16.0f)
                .texOffs(0, 44).addBox(8.0f, -4.0f, -6.0f, 2.0f, 3.0f, 12.0f)
                .texOffs(0, 44).addBox(-10.0f, -4.0f, -6.0f, 2.0f, 3.0f, 12.0f),
                PartPose.offset(0.0f, 18.0f, 0.0f));

        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 48).addBox(-4.0f, -3.0f, -6.0f, 8.0f, 5.0f, 6.0f)
                .texOffs(28, 48).addBox(-2.5f, -1.0f, -8.0f, 5.0f, 3.0f, 2.0f)
                .texOffs(42, 48).addBox(2.5f, -2.0f, -11.0f, 1.0f, 1.0f, 5.0f)
                .texOffs(42, 48).addBox(-3.5f, -2.0f, -11.0f, 1.0f, 1.0f, 5.0f),
                PartPose.offset(0.0f, 19.0f, -10.0f));

        root.addOrReplaceChild("front_left_leg", CubeListBuilder.create()
                .texOffs(36, 0).addBox(0.0f, -1.0f, -1.0f, 5.0f, 2.0f, 2.0f)
                .texOffs(36, 4).addBox(4.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(8.0f, 18.0f, -6.0f));

        root.addOrReplaceChild("front_right_leg", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-5.0f, -1.0f, -1.0f, 5.0f, 2.0f, 2.0f)
                .texOffs(36, 4).addBox(-6.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(-8.0f, 18.0f, -6.0f));

        root.addOrReplaceChild("mid_left_leg", CubeListBuilder.create()
                .texOffs(36, 0).addBox(0.0f, -1.0f, -1.0f, 5.0f, 2.0f, 2.0f)
                .texOffs(36, 4).addBox(4.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(8.0f, 18.0f, 0.0f));

        root.addOrReplaceChild("mid_right_leg", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-5.0f, -1.0f, -1.0f, 5.0f, 2.0f, 2.0f)
                .texOffs(36, 4).addBox(-6.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(-8.0f, 18.0f, 0.0f));

        root.addOrReplaceChild("back_left_leg", CubeListBuilder.create()
                .texOffs(36, 0).addBox(0.0f, -1.0f, -1.0f, 5.0f, 2.0f, 2.0f)
                .texOffs(36, 4).addBox(4.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(8.0f, 18.0f, 6.0f));

        root.addOrReplaceChild("back_right_leg", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-5.0f, -1.0f, -1.0f, 5.0f, 2.0f, 2.0f)
                .texOffs(36, 4).addBox(-6.0f, 1.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(-8.0f, 18.0f, 6.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(AquiferBeetleRenderState state) {
        super.setupAnim(state);

        if (state.isHibernating) {
            this.carapace.y = 22.0f;
            this.carapace.xRot = 0.0f;
            this.carapace.yRot = 0.0f;

            this.head.y = 22.0f;
            this.head.z = -7.0f;
            this.head.xRot = 0.4f;
            this.head.yRot = 0.0f;

            this.frontLeftLeg.y = 23.0f;
            this.frontLeftLeg.zRot = -1.2f;
            this.frontLeftLeg.xRot = 0.5f;

            this.frontRightLeg.y = 23.0f;
            this.frontRightLeg.zRot = 1.2f;
            this.frontRightLeg.xRot = 0.5f;

            this.midLeftLeg.y = 23.0f;
            this.midLeftLeg.zRot = -1.3f;
            this.midLeftLeg.xRot = 0.0f;

            this.midRightLeg.y = 23.0f;
            this.midRightLeg.zRot = 1.3f;
            this.midRightLeg.xRot = 0.0f;

            this.backLeftLeg.y = 23.0f;
            this.backLeftLeg.zRot = -1.2f;
            this.backLeftLeg.xRot = -0.5f;

            this.backRightLeg.y = 23.0f;
            this.backRightLeg.zRot = 1.2f;
            this.backRightLeg.xRot = -0.5f;
            return;
        }

        this.carapace.y = 18.0f;
        this.head.y = 19.0f;
        this.head.z = -10.0f;

        this.frontLeftLeg.y = 18.0f;
        this.frontRightLeg.y = 18.0f;
        this.midLeftLeg.y = 18.0f;
        this.midRightLeg.y = 18.0f;
        this.backLeftLeg.y = 18.0f;
        this.backRightLeg.y = 18.0f;

        this.frontLeftLeg.zRot = 0.0f;
        this.frontRightLeg.zRot = 0.0f;
        this.midLeftLeg.zRot = 0.0f;
        this.midRightLeg.zRot = 0.0f;
        this.backLeftLeg.zRot = 0.0f;
        this.backRightLeg.zRot = 0.0f;

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;
        float legSwingA = Mth.cos(walkPos * 0.7f) * 0.8f * walkSpeed;
        float legSwingB = Mth.cos(walkPos * 0.7f + (float) Math.PI) * 0.8f * walkSpeed;

        this.frontLeftLeg.xRot = legSwingA;
        this.frontRightLeg.xRot = legSwingB;
        this.midLeftLeg.xRot = legSwingB;
        this.midRightLeg.xRot = legSwingA;
        this.backLeftLeg.xRot = legSwingA;
        this.backRightLeg.xRot = legSwingB;

        this.head.yRot = Mth.sin(state.animationTicks * 0.12f) * 0.15f;

        if (state.isSteamAttacking) {
            this.carapace.xRot = -0.3f;
            this.head.xRot = -0.4f;
            this.head.yRot = Mth.sin(state.animationTicks * 1.5f) * 0.08f;
            this.frontLeftLeg.xRot = -0.5f;
            this.frontRightLeg.xRot = -0.5f;
        } else {
            this.carapace.xRot = 0.0f;
            this.head.xRot = 0.0f;
        }
    }
}
