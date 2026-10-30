package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class GrazerModel extends EntityModel<GrazerRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart canisters;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart tail;

    public GrazerModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.canisters = this.body.getChild("canisters");
        this.neck = root.getChild("neck");
        this.head = this.neck.getChild("head");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-4.5f, -4.0f, -8.0f, 9.0f, 8.0f, 16.0f)
                .texOffs(0, 58).addBox(-4.0f, -5.5f, -6.0f, 8.0f, 2.0f, 12.0f)
                .texOffs(42, 58).addBox(-3.5f, 3.5f, -5.0f, 7.0f, 2.0f, 10.0f),
                PartPose.offset(0.0f, 11.0f, 0.0f));

        body.addOrReplaceChild("canisters", CubeListBuilder.create()
                .texOffs(52, 32).addBox(-4.0f, -5.0f, -1.0f, 8.0f, 2.0f, 9.0f)
                .texOffs(52, 44).addBox(-3.5f, -8.5f, 0.0f, 3.0f, 5.0f, 3.0f)
                .texOffs(52, 44).addBox(0.5f, -8.5f, 0.0f, 3.0f, 5.0f, 3.0f)
                .texOffs(66, 44).addBox(-3.5f, -8.5f, 4.0f, 3.0f, 5.0f, 3.0f)
                .texOffs(66, 44).addBox(0.5f, -8.5f, 4.0f, 3.0f, 5.0f, 3.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition neck = root.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 74).addBox(-2.0f, -8.0f, -3.0f, 4.0f, 10.0f, 4.0f)
                .texOffs(18, 74).addBox(-2.5f, -7.0f, -4.5f, 5.0f, 8.0f, 2.0f),
                PartPose.offset(0.0f, 9.0f, -7.0f));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0f, -3.0f, -5.0f, 6.0f, 6.0f, 6.0f)
                .texOffs(26, 0).addBox(-2.0f, -1.0f, -10.0f, 4.0f, 3.0f, 5.0f)
                .texOffs(0, 14).addBox(-2.5f, -2.0f, -5.2f, 5.0f, 2.0f, 1.0f)
                .texOffs(14, 14).addBox(2.5f, -5.0f, -2.0f, 1.0f, 3.0f, 2.0f)
                .texOffs(14, 14).addBox(-3.5f, -5.0f, -2.0f, 1.0f, 3.0f, 2.0f),
                PartPose.offset(0.0f, -7.0f, -2.5f));

        head.addOrReplaceChild("antlers", CubeListBuilder.create()
                .texOffs(46, 0).addBox(-1.0f, -5.0f, -4.0f, 2.0f, 3.0f, 2.0f)
                .texOffs(46, 7).addBox(1.0f, -9.0f, -3.0f, 2.0f, 6.0f, 2.0f)
                .texOffs(56, 7).addBox(2.0f, -13.0f, -1.0f, 2.0f, 5.0f, 2.0f)
                .texOffs(66, 7).addBox(1.5f, -10.0f, 1.0f, 1.0f, 3.0f, 3.0f)
                .texOffs(46, 7).addBox(-3.0f, -9.0f, -3.0f, 2.0f, 6.0f, 2.0f)
                .texOffs(56, 7).addBox(-4.0f, -13.0f, -1.0f, 2.0f, 5.0f, 2.0f)
                .texOffs(66, 7).addBox(-2.5f, -10.0f, 1.0f, 1.0f, 3.0f, 3.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(0, 92).addBox(-1.5f, 0.0f, -2.0f, 3.0f, 6.0f, 4.0f)
                .texOffs(16, 92).addBox(-1.0f, 6.0f, -1.5f, 2.0f, 7.0f, 3.0f),
                PartPose.offset(-3.5f, 11.0f, -5.5f));

        root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(0, 92).addBox(-1.5f, 0.0f, -2.0f, 3.0f, 6.0f, 4.0f)
                .texOffs(16, 92).addBox(-1.0f, 6.0f, -1.5f, 2.0f, 7.0f, 3.0f),
                PartPose.offset(3.5f, 11.0f, -5.5f));

        root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(28, 92).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 7.0f, 5.0f)
                .texOffs(48, 92).addBox(-1.0f, 7.0f, -1.5f, 2.0f, 6.0f, 3.0f),
                PartPose.offset(-3.5f, 11.0f, 5.5f));

        root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(28, 92).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 7.0f, 5.0f)
                .texOffs(48, 92).addBox(-1.0f, 7.0f, -1.5f, 2.0f, 6.0f, 3.0f),
                PartPose.offset(3.5f, 11.0f, 5.5f));

        root.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(60, 92).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(0.0f, 8.0f, 8.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(GrazerRenderState state) {
        super.setupAnim(state);
        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        this.canisters.visible = state.areCanistersIntact;

        if (state.isDeactivated) {
            this.body.setPos(0.0f, 18.0f, 0.0f);
            this.neck.setPos(0.0f, 17.0f, -7.0f);
            this.neck.xRot = 0.5f;
            this.head.xRot = 0.3f;
            this.head.yRot = 0.0f;
            this.rightFrontLeg.setPos(-3.5f, 21.0f, -5.5f);
            this.rightFrontLeg.xRot = -1.3f;
            this.leftFrontLeg.setPos(3.5f, 21.0f, -5.5f);
            this.leftFrontLeg.xRot = -1.3f;
            this.rightHindLeg.setPos(-3.5f, 21.0f, 5.5f);
            this.rightHindLeg.xRot = 1.3f;
            this.leftHindLeg.setPos(3.5f, 21.0f, 5.5f);
            this.leftHindLeg.xRot = 1.3f;
            return;
        }

        this.body.setPos(0.0f, 11.0f, 0.0f);
        this.neck.setPos(0.0f, 9.0f, -7.0f);
        this.neck.xRot = -0.1f + Mth.cos(walkPos * 0.3331f) * 0.08f * walkSpeed;
        this.head.yRot = state.yRot * ((float) Math.PI / 180.0f);
        this.head.xRot = state.xRot * ((float) Math.PI / 180.0f);

        this.rightFrontLeg.setPos(-3.5f, 11.0f, -5.5f);
        this.leftFrontLeg.setPos(3.5f, 11.0f, -5.5f);
        this.rightHindLeg.setPos(-3.5f, 11.0f, 5.5f);
        this.leftHindLeg.setPos(3.5f, 11.0f, 5.5f);

        this.rightFrontLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.4f * walkSpeed;
        this.leftFrontLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.4f * walkSpeed;
        this.rightHindLeg.xRot = Mth.cos(walkPos * 0.6662f + (float) Math.PI) * 1.4f * walkSpeed;
        this.leftHindLeg.xRot = Mth.cos(walkPos * 0.6662f) * 1.4f * walkSpeed;

        this.tail.zRot = Mth.cos(state.animationTicks * 0.2f) * 0.15f;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
