package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class CrawlerDroneModel extends EntityModel<CrawlerDroneRenderState> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightMiddleHindLeg;
    private final ModelPart leftMiddleHindLeg;
    private final ModelPart rightMiddleFrontLeg;
    private final ModelPart leftMiddleFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public CrawlerDroneModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightMiddleHindLeg = root.getChild("right_middle_hind_leg");
        this.leftMiddleHindLeg = root.getChild("left_middle_hind_leg");
        this.rightMiddleFrontLeg = root.getChild("right_middle_front_leg");
        this.leftMiddleFrontLeg = root.getChild("left_middle_front_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(32, 4)
                .addBox(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f),
                PartPose.offset(0.0f, 15.0f, -3.0f));

        root.addOrReplaceChild("body0", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f),
                PartPose.offset(0.0f, 15.0f, 0.0f));

        root.addOrReplaceChild("body1", CubeListBuilder.create()
                .texOffs(0, 12)
                .addBox(-5.0f, -4.0f, -6.0f, 10.0f, 8.0f, 12.0f),
                PartPose.offset(0.0f, 15.0f, 9.0f));

        CubeListBuilder rightLegBuilder = CubeListBuilder.create().texOffs(18, 0).addBox(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        CubeListBuilder leftLegBuilder = CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);

        root.addOrReplaceChild("right_hind_leg", rightLegBuilder, PartPose.offset(-4.0f, 15.0f, 2.0f));
        root.addOrReplaceChild("left_hind_leg", leftLegBuilder, PartPose.offset(4.0f, 15.0f, 2.0f));
        root.addOrReplaceChild("right_middle_hind_leg", rightLegBuilder, PartPose.offset(-4.0f, 15.0f, 1.0f));
        root.addOrReplaceChild("left_middle_hind_leg", leftLegBuilder, PartPose.offset(4.0f, 15.0f, 1.0f));
        root.addOrReplaceChild("right_middle_front_leg", rightLegBuilder, PartPose.offset(-4.0f, 15.0f, 0.0f));
        root.addOrReplaceChild("left_middle_front_leg", leftLegBuilder, PartPose.offset(4.0f, 15.0f, 0.0f));
        root.addOrReplaceChild("right_front_leg", rightLegBuilder, PartPose.offset(-4.0f, 15.0f, -1.0f));
        root.addOrReplaceChild("left_front_leg", leftLegBuilder, PartPose.offset(4.0f, 15.0f, -1.0f));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(CrawlerDroneRenderState state) {
        super.setupAnim(state);

        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        float legSpreadZ = (float) Math.PI / 4.0f;
        this.rightHindLeg.zRot = -legSpreadZ * 0.74f;
        this.leftHindLeg.zRot = legSpreadZ * 0.74f;
        this.rightMiddleHindLeg.zRot = -legSpreadZ * 0.58f;
        this.leftMiddleHindLeg.zRot = legSpreadZ * 0.58f;
        this.rightMiddleFrontLeg.zRot = -legSpreadZ * 0.58f;
        this.leftMiddleFrontLeg.zRot = legSpreadZ * 0.58f;
        this.rightFrontLeg.zRot = -legSpreadZ * 0.74f;
        this.leftFrontLeg.zRot = legSpreadZ * 0.74f;

        this.rightHindLeg.yRot = ((float) Math.PI / 4.0f);
        this.leftHindLeg.yRot = -((float) Math.PI / 4.0f);
        this.rightMiddleHindLeg.yRot = ((float) Math.PI / 8.0f);
        this.leftMiddleHindLeg.yRot = -((float) Math.PI / 8.0f);
        this.rightMiddleFrontLeg.yRot = -((float) Math.PI / 8.0f);
        this.leftMiddleFrontLeg.yRot = ((float) Math.PI / 8.0f);
        this.rightFrontLeg.yRot = -((float) Math.PI / 4.0f);
        this.leftFrontLeg.yRot = ((float) Math.PI / 4.0f);

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;
        float speedFactor = walkPos * 0.6662f;

        float swing1 = -(Mth.cos(speedFactor * 2.0f) * 0.4f) * walkSpeed;
        float swing2 = -(Mth.cos(speedFactor * 2.0f + (float) Math.PI) * 0.4f) * walkSpeed;

        this.rightHindLeg.yRot += swing1;
        this.leftHindLeg.yRot += -swing1;
        this.rightMiddleHindLeg.yRot += swing2;
        this.leftMiddleHindLeg.yRot += -swing2;
        this.rightMiddleFrontLeg.yRot += swing1;
        this.leftMiddleFrontLeg.yRot += -swing1;
        this.rightFrontLeg.yRot += swing2;
        this.leftFrontLeg.yRot += -swing2;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
