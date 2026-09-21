package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class SandwormModel extends EntityModel<SandwormRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart base;
    private final ModelPart segmentLower;
    private final ModelPart segmentMid;
    private final ModelPart segmentUpper;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart teethOuter;
    private final ModelPart teethMiddle;
    private final ModelPart teethInner;

    public SandwormModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.base = this.body.getChild("base");
        this.segmentLower = this.base.getChild("segment_lower");
        this.segmentMid = this.segmentLower.getChild("segment_mid");
        this.segmentUpper = this.segmentMid.getChild("segment_upper");
        this.neck = this.segmentUpper.getChild("neck");
        this.head = this.neck.getChild("head");
        this.teethOuter = this.head.getChild("teeth_outer");
        this.teethMiddle = this.head.getChild("teeth_middle");
        this.teethInner = this.head.getChild("teeth_inner");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));

        PartDefinition base = body.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-12.0f, -18.0f, -12.0f, 24.0f, 18.0f, 24.0f)
                .texOffs(98, 0).addBox(-13.0f, -16.0f, -13.0f, 26.0f, 14.0f, 26.0f)
                .texOffs(204, 0).addBox(-4.0f, -19.0f, -14.0f, 8.0f, 18.0f, 6.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition segLower = base.addOrReplaceChild("segment_lower", CubeListBuilder.create()
                .texOffs(0, 44).addBox(-11.0f, -18.0f, -11.0f, 22.0f, 18.0f, 22.0f)
                .texOffs(90, 44).addBox(-12.0f, -16.0f, -12.0f, 24.0f, 14.0f, 24.0f)
                .texOffs(188, 44).addBox(-3.5f, -19.0f, -13.0f, 7.0f, 18.0f, 5.0f),
                PartPose.offset(0.0f, -18.0f, -2.0f));

        PartDefinition segMid = segLower.addOrReplaceChild("segment_mid", CubeListBuilder.create()
                .texOffs(0, 86).addBox(-10.0f, -18.0f, -10.0f, 20.0f, 18.0f, 20.0f)
                .texOffs(82, 86).addBox(-11.0f, -16.0f, -11.0f, 22.0f, 14.0f, 22.0f)
                .texOffs(172, 86).addBox(-3.0f, -19.0f, -12.0f, 6.0f, 18.0f, 5.0f),
                PartPose.offset(0.0f, -18.0f, -2.5f));

        PartDefinition segUpper = segMid.addOrReplaceChild("segment_upper", CubeListBuilder.create()
                .texOffs(0, 126).addBox(-9.0f, -18.0f, -9.0f, 18.0f, 18.0f, 18.0f)
                .texOffs(74, 126).addBox(-10.0f, -16.0f, -10.0f, 20.0f, 14.0f, 20.0f)
                .texOffs(156, 126).addBox(-2.5f, -19.0f, -11.0f, 5.0f, 18.0f, 4.0f),
                PartPose.offset(0.0f, -18.0f, -2.5f));

        PartDefinition neck = segUpper.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 164).addBox(-8.0f, -16.0f, -8.0f, 16.0f, 16.0f, 16.0f)
                .texOffs(66, 164).addBox(-9.0f, -14.0f, -9.0f, 18.0f, 12.0f, 18.0f)
                .texOffs(140, 164).addBox(-2.0f, -17.0f, -10.0f, 4.0f, 16.0f, 4.0f),
                PartPose.offset(0.0f, -18.0f, -2.0f));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 198).addBox(-7.0f, -10.0f, -10.0f, 14.0f, 4.0f, 6.0f)
                .texOffs(42, 198).addBox(-7.0f, 6.0f, -10.0f, 14.0f, 4.0f, 6.0f)
                .texOffs(84, 198).addBox(-10.0f, -7.0f, -10.0f, 4.0f, 14.0f, 6.0f)
                .texOffs(106, 198).addBox(6.0f, -7.0f, -10.0f, 4.0f, 14.0f, 6.0f)
                .texOffs(128, 198).addBox(-9.0f, -9.0f, -9.5f, 5.0f, 5.0f, 5.0f)
                .texOffs(128, 198).addBox(4.0f, -9.0f, -9.5f, 5.0f, 5.0f, 5.0f)
                .texOffs(128, 198).addBox(-9.0f, 4.0f, -9.5f, 5.0f, 5.0f, 5.0f)
                .texOffs(128, 198).addBox(4.0f, 4.0f, -9.5f, 5.0f, 5.0f, 5.0f)
                .texOffs(0, 220).addBox(-7.0f, -7.0f, -6.0f, 14.0f, 14.0f, 10.0f),
                PartPose.offset(0.0f, -16.0f, -2.0f));

        PartDefinition teethOuter = head.addOrReplaceChild("teeth_outer", CubeListBuilder.create()
                .texOffs(180, 160).addBox(-1.0f, -7.0f, -8.5f, 2.0f, 3.0f, 2.0f)
                .texOffs(188, 160).addBox(-1.0f, 4.0f, -8.5f, 2.0f, 3.0f, 2.0f)
                .texOffs(196, 160).addBox(-7.0f, -1.0f, -8.5f, 3.0f, 2.0f, 2.0f)
                .texOffs(206, 160).addBox(4.0f, -1.0f, -8.5f, 3.0f, 2.0f, 2.0f)
                .texOffs(216, 160).addBox(-5.5f, -5.5f, -8.5f, 2.0f, 2.0f, 2.0f)
                .texOffs(224, 160).addBox(3.5f, -5.5f, -8.5f, 2.0f, 2.0f, 2.0f)
                .texOffs(232, 160).addBox(-5.5f, 3.5f, -8.5f, 2.0f, 2.0f, 2.0f)
                .texOffs(240, 160).addBox(3.5f, 3.5f, -8.5f, 2.0f, 2.0f, 2.0f)
                .texOffs(180, 170).addBox(-3.5f, -6.5f, -8.5f, 1.5f, 2.5f, 2.0f)
                .texOffs(188, 170).addBox(2.0f, -6.5f, -8.5f, 1.5f, 2.5f, 2.0f)
                .texOffs(196, 170).addBox(-3.5f, 4.0f, -8.5f, 1.5f, 2.5f, 2.0f)
                .texOffs(204, 170).addBox(2.0f, 4.0f, -8.5f, 1.5f, 2.5f, 2.0f)
                .texOffs(212, 170).addBox(-6.5f, -3.5f, -8.5f, 2.5f, 1.5f, 2.0f)
                .texOffs(222, 170).addBox(-6.5f, 2.0f, -8.5f, 2.5f, 1.5f, 2.0f)
                .texOffs(232, 170).addBox(4.0f, -3.5f, -8.5f, 2.5f, 1.5f, 2.0f)
                .texOffs(242, 170).addBox(4.0f, 2.0f, -8.5f, 2.5f, 1.5f, 2.0f),
                PartPose.ZERO);

        PartDefinition teethMiddle = head.addOrReplaceChild("teeth_middle", CubeListBuilder.create()
                .texOffs(180, 185).addBox(-2.5f, -4.8f, -6.0f, 1.5f, 2.0f, 1.5f)
                .texOffs(188, 185).addBox(1.0f, -4.8f, -6.0f, 1.5f, 2.0f, 1.5f)
                .texOffs(196, 185).addBox(-2.5f, 2.8f, -6.0f, 1.5f, 2.0f, 1.5f)
                .texOffs(204, 185).addBox(1.0f, 2.8f, -6.0f, 1.5f, 2.0f, 1.5f)
                .texOffs(212, 185).addBox(-4.8f, -2.5f, -6.0f, 2.0f, 1.5f, 1.5f)
                .texOffs(220, 185).addBox(-4.8f, 1.0f, -6.0f, 2.0f, 1.5f, 1.5f)
                .texOffs(228, 185).addBox(2.8f, -2.5f, -6.0f, 2.0f, 1.5f, 1.5f)
                .texOffs(236, 185).addBox(2.8f, 1.0f, -6.0f, 2.0f, 1.5f, 1.5f)
                .texOffs(180, 195).addBox(-4.0f, -4.0f, -6.0f, 1.5f, 1.5f, 1.5f)
                .texOffs(188, 195).addBox(2.5f, -4.0f, -6.0f, 1.5f, 1.5f, 1.5f)
                .texOffs(196, 195).addBox(-4.0f, 2.5f, -6.0f, 1.5f, 1.5f, 1.5f)
                .texOffs(204, 195).addBox(2.5f, 2.5f, -6.0f, 1.5f, 1.5f, 1.5f),
                PartPose.ZERO);

        PartDefinition teethInner = head.addOrReplaceChild("teeth_inner", CubeListBuilder.create()
                .texOffs(180, 210).addBox(-0.75f, -3.2f, -3.5f, 1.5f, 1.5f, 1.5f)
                .texOffs(188, 210).addBox(-0.75f, 1.7f, -3.5f, 1.5f, 1.5f, 1.5f)
                .texOffs(196, 210).addBox(-3.2f, -0.75f, -3.5f, 1.5f, 1.5f, 1.5f)
                .texOffs(204, 210).addBox(1.7f, -0.75f, -3.5f, 1.5f, 1.5f, 1.5f)
                .texOffs(212, 210).addBox(-2.5f, -2.5f, -3.5f, 1.2f, 1.2f, 1.2f)
                .texOffs(220, 210).addBox(1.3f, -2.5f, -3.5f, 1.2f, 1.2f, 1.2f)
                .texOffs(228, 210).addBox(-2.5f, 1.3f, -3.5f, 1.2f, 1.2f, 1.2f)
                .texOffs(236, 210).addBox(1.3f, 1.3f, -3.5f, 1.2f, 1.2f, 1.2f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(SandwormRenderState state) {
        super.setupAnim(state);

        float age = state.ageInTicks;
        float totalPitch;
        if (state.hasTarget) {
            float distNorm = Mth.clamp((state.targetDistance - 4.0f) / 20.0f, 0.0f, 1.0f);
            totalPitch = Mth.lerp(distNorm, 1.40f, 0.85f);
        } else {
            totalPitch = 0.70f;
        }

        float w0 = Mth.sin(age * 0.06f) * 0.03f;
        float w1 = Mth.sin(age * 0.06f - 0.4f) * 0.035f;
        float w2 = Mth.sin(age * 0.06f - 0.8f) * 0.04f;
        float w3 = Mth.sin(age * 0.06f - 1.2f) * 0.045f;
        float w4 = Mth.sin(age * 0.06f - 1.6f) * 0.05f;
        float w5 = Mth.sin(age * 0.06f - 2.0f) * 0.06f;

        this.base.xRot = (totalPitch * 0.08f) + w0;
        this.segmentLower.xRot = (totalPitch * 0.16f) + w1;
        this.segmentMid.xRot = (totalPitch * 0.22f) + w2;
        this.segmentUpper.xRot = (totalPitch * 0.24f) + w3;
        this.neck.xRot = (totalPitch * 0.18f) + w4;
        this.head.xRot = (totalPitch * 0.12f) + w5;

        float relYawRad = Mth.clamp(state.targetRelativeYaw * (float) (Math.PI / 180.0), -1.2f, 1.2f);
        float sway = Mth.cos(age * 0.04f) * 0.03f;
        this.base.yRot = (relYawRad * 0.10f) + sway * 0.3f;
        this.segmentLower.yRot = (relYawRad * 0.15f) + sway * 0.6f;
        this.segmentMid.yRot = (relYawRad * 0.22f) + sway * 0.9f;
        this.segmentUpper.yRot = (relYawRad * 0.25f) + sway * 1.2f;
        this.neck.yRot = (relYawRad * 0.18f) + sway * 1.5f;
        this.head.yRot = (relYawRad * 0.10f) + sway * 1.8f;

        if (state.breaching) {
            this.base.xRot *= state.breachProgress;
            this.segmentLower.xRot *= state.breachProgress;
            this.segmentMid.xRot *= state.breachProgress;
            this.segmentUpper.xRot *= state.breachProgress;
            this.neck.xRot *= state.breachProgress;
            this.head.xRot *= state.breachProgress;
        }

        if (state.biteProgress > 0.0f) {
            float p = state.biteProgress;
            float strikeCurve;
            if (p < 0.35f) {
                strikeCurve = -Mth.sin(p / 0.35f * (float) (Math.PI * 0.5)) * 0.20f;
            } else {
                strikeCurve = Mth.sin((p - 0.35f) / 0.65f * (float) Math.PI) * 0.65f;
            }
            this.segmentLower.xRot += strikeCurve * 0.15f;
            this.segmentMid.xRot += strikeCurve * 0.30f;
            this.segmentUpper.xRot += strikeCurve * 0.45f;
            this.neck.xRot += strikeCurve * 0.55f;
            this.head.xRot += strikeCurve * 0.65f;

            float teethSnap = Mth.sin(p * (float) Math.PI);
            this.teethOuter.z = teethSnap * -2.5f;
            this.teethMiddle.z = teethSnap * -3.5f;
            this.teethInner.z = teethSnap * -4.5f;
            this.teethOuter.xRot = teethSnap * 0.15f;
            this.teethMiddle.xRot = -teethSnap * 0.15f;
        } else {
            this.teethOuter.z = 0.0f;
            this.teethMiddle.z = 0.0f;
            this.teethInner.z = 0.0f;
            this.teethOuter.xRot = 0.0f;
            this.teethMiddle.xRot = 0.0f;
        }

        if (state.burrowed) {
            this.body.y = 24.0f + 24.0f;
        } else if (state.submerging) {
            this.body.y = 24.0f + 16.0f;
        } else if (state.breaching) {
            this.body.y = 24.0f + 18.0f * (1.0f - state.breachProgress);
        } else {
            this.body.y = 24.0f;
        }

        this.body.visible = true;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
