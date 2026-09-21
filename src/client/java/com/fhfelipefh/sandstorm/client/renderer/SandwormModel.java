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
    private final ModelPart seg1;
    private final ModelPart seg2;
    private final ModelPart seg3;
    private final ModelPart seg4;
    private final ModelPart seg5;
    private final ModelPart seg6;
    private final ModelPart seg7;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart teethOuter;
    private final ModelPart teethMiddle;
    private final ModelPart teethInner;
    private final ModelPart subRing1;
    private final ModelPart subRing2;
    private final ModelPart subRing3;

    public SandwormModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.base = this.body.getChild("base");
        this.seg1 = this.base.getChild("segment_1");
        this.seg2 = this.seg1.getChild("segment_2");
        this.seg3 = this.seg2.getChild("segment_3");
        this.seg4 = this.seg3.getChild("segment_4");
        this.seg5 = this.seg4.getChild("segment_5");
        this.seg6 = this.seg5.getChild("segment_6");
        this.seg7 = this.seg6.getChild("segment_7");
        this.neck = this.seg7.getChild("neck");
        this.head = this.neck.getChild("head");
        this.teethOuter = this.head.getChild("teeth_outer");
        this.teethMiddle = this.head.getChild("teeth_middle");
        this.teethInner = this.head.getChild("teeth_inner");
        this.subRing1 = this.base.getChild("sub_ring_1");
        this.subRing2 = this.subRing1.getChild("sub_ring_2");
        this.subRing3 = this.subRing2.getChild("sub_ring_3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));


        PartDefinition base = body.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-12.0f, -12.0f, -12.0f, 24.0f, 12.0f, 24.0f)
                .texOffs(98, 0).addBox(-13.0f, -10.0f, -13.0f, 26.0f, 10.0f, 26.0f)
                .texOffs(204, 0).addBox(-4.0f, -13.0f, -14.0f, 8.0f, 12.0f, 6.0f)
                .texOffs(0, 0).addBox(-11.0f, -20.0f, -11.0f, 22.0f, 8.0f, 22.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition subRing1 = base.addOrReplaceChild("sub_ring_1", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-12.5f, 0.0f, -12.5f, 25.0f, 12.0f, 25.0f)
                .texOffs(98, 0).addBox(-13.5f, 2.0f, -13.5f, 27.0f, 10.0f, 27.0f)
                .texOffs(204, 0).addBox(-4.0f, 1.0f, -14.5f, 8.0f, 12.0f, 6.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition subRing2 = subRing1.addOrReplaceChild("sub_ring_2", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-13.0f, 12.0f, -13.0f, 26.0f, 12.0f, 26.0f)
                .texOffs(98, 0).addBox(-14.0f, 14.0f, -14.0f, 28.0f, 10.0f, 28.0f)
                .texOffs(204, 0).addBox(-4.5f, 13.0f, -15.0f, 9.0f, 12.0f, 6.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition subRing3 = subRing2.addOrReplaceChild("sub_ring_3", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-13.5f, 24.0f, -13.5f, 27.0f, 14.0f, 27.0f)
                .texOffs(98, 0).addBox(-14.5f, 26.0f, -14.5f, 29.0f, 12.0f, 29.0f)
                .texOffs(204, 0).addBox(-5.0f, 25.0f, -15.5f, 10.0f, 14.0f, 6.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition seg1 = base.addOrReplaceChild("segment_1", CubeListBuilder.create()
                .texOffs(0, 20).addBox(-11.5f, -12.0f, -11.5f, 23.0f, 12.0f, 23.0f)
                .texOffs(96, 20).addBox(-12.5f, -10.0f, -12.5f, 25.0f, 10.0f, 25.0f)
                .texOffs(200, 20).addBox(-3.8f, -13.0f, -13.5f, 7.6f, 12.0f, 5.5f)

                .texOffs(0, 20).addBox(-10.5f, -20.0f, -10.5f, 21.0f, 8.0f, 21.0f),
                PartPose.offset(0.0f, -12.0f, -1.0f));

        PartDefinition seg2 = seg1.addOrReplaceChild("segment_2", CubeListBuilder.create()
                .texOffs(0, 40).addBox(-11.0f, -12.0f, -11.0f, 22.0f, 12.0f, 22.0f)
                .texOffs(90, 40).addBox(-12.0f, -10.0f, -12.0f, 24.0f, 10.0f, 24.0f)
                .texOffs(188, 40).addBox(-3.5f, -13.0f, -13.0f, 7.0f, 12.0f, 5.0f)

                .texOffs(0, 40).addBox(-10.0f, -20.0f, -10.0f, 20.0f, 8.0f, 20.0f),
                PartPose.offset(0.0f, -12.0f, -1.0f));

        PartDefinition seg3 = seg2.addOrReplaceChild("segment_3", CubeListBuilder.create()
                .texOffs(0, 60).addBox(-10.5f, -12.0f, -10.5f, 21.0f, 12.0f, 21.0f)
                .texOffs(86, 60).addBox(-11.5f, -10.0f, -11.5f, 23.0f, 10.0f, 23.0f)
                .texOffs(180, 60).addBox(-3.2f, -13.0f, -12.5f, 6.4f, 12.0f, 5.0f)

                .texOffs(0, 60).addBox(-9.5f, -20.0f, -9.5f, 19.0f, 8.0f, 19.0f),
                PartPose.offset(0.0f, -12.0f, -1.2f));

        PartDefinition seg4 = seg3.addOrReplaceChild("segment_4", CubeListBuilder.create()
                .texOffs(0, 80).addBox(-10.0f, -12.0f, -10.0f, 20.0f, 12.0f, 20.0f)
                .texOffs(82, 80).addBox(-11.0f, -10.0f, -11.0f, 22.0f, 10.0f, 22.0f)
                .texOffs(172, 80).addBox(-3.0f, -13.0f, -12.0f, 6.0f, 12.0f, 5.0f)

                .texOffs(0, 80).addBox(-9.0f, -20.0f, -9.0f, 18.0f, 8.0f, 18.0f),
                PartPose.offset(0.0f, -12.0f, -1.2f));

        PartDefinition seg5 = seg4.addOrReplaceChild("segment_5", CubeListBuilder.create()
                .texOffs(0, 100).addBox(-9.5f, -12.0f, -9.5f, 19.0f, 12.0f, 19.0f)
                .texOffs(78, 100).addBox(-10.5f, -10.0f, -10.5f, 21.0f, 10.0f, 21.0f)
                .texOffs(164, 100).addBox(-2.8f, -13.0f, -11.5f, 5.6f, 12.0f, 4.5f)

                .texOffs(0, 100).addBox(-8.5f, -20.0f, -8.5f, 17.0f, 8.0f, 17.0f),
                PartPose.offset(0.0f, -12.0f, -1.5f));

        PartDefinition seg6 = seg5.addOrReplaceChild("segment_6", CubeListBuilder.create()
                .texOffs(0, 120).addBox(-9.0f, -12.0f, -9.0f, 18.0f, 12.0f, 18.0f)
                .texOffs(74, 120).addBox(-10.0f, -10.0f, -10.0f, 20.0f, 10.0f, 20.0f)
                .texOffs(156, 120).addBox(-2.5f, -13.0f, -11.0f, 5.0f, 12.0f, 4.0f)

                .texOffs(0, 120).addBox(-8.0f, -20.0f, -8.0f, 16.0f, 8.0f, 16.0f),
                PartPose.offset(0.0f, -12.0f, -1.5f));

        PartDefinition seg7 = seg6.addOrReplaceChild("segment_7", CubeListBuilder.create()
                .texOffs(0, 140).addBox(-8.5f, -12.0f, -8.5f, 17.0f, 12.0f, 17.0f)
                .texOffs(70, 140).addBox(-9.5f, -10.0f, -9.5f, 19.0f, 10.0f, 19.0f)
                .texOffs(148, 140).addBox(-2.2f, -13.0f, -10.5f, 4.4f, 12.0f, 4.0f)

                .texOffs(0, 140).addBox(-7.5f, -20.0f, -7.5f, 15.0f, 8.0f, 15.0f),
                PartPose.offset(0.0f, -12.0f, -1.5f));

        PartDefinition neck = seg7.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 160).addBox(-8.0f, -12.0f, -8.0f, 16.0f, 12.0f, 16.0f)
                .texOffs(66, 160).addBox(-9.0f, -10.0f, -9.0f, 18.0f, 10.0f, 18.0f)
                .texOffs(140, 160).addBox(-2.0f, -13.0f, -10.0f, 4.0f, 12.0f, 4.0f),
                PartPose.offset(0.0f, -12.0f, -1.5f));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 198).addBox(-7.0f, -10.0f, -10.0f, 14.0f, 4.0f, 6.0f)
                .texOffs(42, 198).addBox(-7.0f, 6.0f, -10.0f, 14.0f, 4.0f, 6.0f)
                .texOffs(84, 198).addBox(-10.0f, -7.0f, -10.0f, 4.0f, 14.0f, 6.0f)
                .texOffs(106, 198).addBox(6.0f, -7.0f, -10.0f, 4.0f, 14.0f, 6.0f)
                .texOffs(128, 198).addBox(-9.0f, -9.0f, -9.5f, 5.0f, 5.0f, 5.0f)
                .texOffs(128, 198).addBox(4.0f, -9.0f, -9.5f, 5.0f, 5.0f, 5.0f)
                .texOffs(128, 198).addBox(-9.0f, 4.0f, -9.5f, 5.0f, 5.0f, 5.0f)
                .texOffs(128, 198).addBox(4.0f, 4.0f, -9.5f, 5.0f, 5.0f, 5.0f)
                .texOffs(0, 198).addBox(-7.0f, -7.0f, -4.0f, 14.0f, 2.5f, 9.0f)
                .texOffs(42, 198).addBox(-7.0f, 4.5f, -4.0f, 14.0f, 2.5f, 9.0f)
                .texOffs(84, 198).addBox(-7.0f, -4.5f, -4.0f, 2.5f, 9.0f, 9.0f)
                .texOffs(106, 198).addBox(4.5f, -4.5f, -4.0f, 2.5f, 9.0f, 9.0f)
                .texOffs(0, 220).addBox(-4.5f, -4.5f, -3.8f, 9.0f, 0.8f, 8.3f)
                .texOffs(0, 220).addBox(-4.5f, 3.7f, -3.8f, 9.0f, 0.8f, 8.3f)
                .texOffs(0, 220).addBox(-4.5f, -3.7f, -3.8f, 0.8f, 7.4f, 8.3f)
                .texOffs(0, 220).addBox(3.7f, -3.7f, -3.8f, 0.8f, 7.4f, 8.3f)
                .texOffs(0, 220).addBox(-4.5f, -4.5f, 4.5f, 9.0f, 9.0f, 1.0f),
                PartPose.offset(0.0f, -12.0f, -2.0f));

        PartDefinition teethOuter = head.addOrReplaceChild("teeth_outer", CubeListBuilder.create()
                .texOffs(180, 160).addBox(-1.0f, -7.0f, -8.8f, 2.0f, 3.0f, 1.4f)
                .texOffs(188, 160).addBox(-1.0f, 4.0f, -8.8f, 2.0f, 3.0f, 1.4f)
                .texOffs(196, 160).addBox(-7.0f, -1.0f, -8.8f, 3.0f, 2.0f, 1.4f)
                .texOffs(206, 160).addBox(4.0f, -1.0f, -8.8f, 3.0f, 2.0f, 1.4f)
                .texOffs(216, 160).addBox(-5.5f, -5.5f, -8.8f, 2.0f, 2.0f, 1.4f)
                .texOffs(224, 160).addBox(3.5f, -5.5f, -8.8f, 2.0f, 2.0f, 1.4f)
                .texOffs(232, 160).addBox(-5.5f, 3.5f, -8.8f, 2.0f, 2.0f, 1.4f)
                .texOffs(240, 160).addBox(3.5f, 3.5f, -8.8f, 2.0f, 2.0f, 1.4f)
                .texOffs(180, 170).addBox(-3.5f, -6.5f, -8.8f, 1.5f, 2.5f, 1.4f)
                .texOffs(188, 170).addBox(2.0f, -6.5f, -8.8f, 1.5f, 2.5f, 1.4f)
                .texOffs(196, 170).addBox(-3.5f, 4.0f, -8.8f, 1.5f, 2.5f, 1.4f)
                .texOffs(204, 170).addBox(2.0f, 4.0f, -8.8f, 1.5f, 2.5f, 1.4f)
                .texOffs(212, 170).addBox(-6.5f, -3.5f, -8.8f, 2.5f, 1.5f, 1.4f)
                .texOffs(222, 170).addBox(-6.5f, 2.0f, -8.8f, 2.5f, 1.5f, 1.4f)
                .texOffs(232, 170).addBox(4.0f, -3.5f, -8.8f, 2.5f, 1.5f, 1.4f)
                .texOffs(242, 170).addBox(4.0f, 2.0f, -8.8f, 2.5f, 1.5f, 1.4f),
                PartPose.ZERO);

        PartDefinition teethMiddle = head.addOrReplaceChild("teeth_middle", CubeListBuilder.create()
                .texOffs(180, 185).addBox(-2.5f, -4.8f, -6.5f, 1.5f, 2.0f, 1.2f)
                .texOffs(188, 185).addBox(1.0f, -4.8f, -6.5f, 1.5f, 2.0f, 1.2f)
                .texOffs(196, 185).addBox(-2.5f, 2.8f, -6.5f, 1.5f, 2.0f, 1.2f)
                .texOffs(204, 185).addBox(1.0f, 2.8f, -6.5f, 1.5f, 2.0f, 1.2f)
                .texOffs(212, 185).addBox(-4.8f, -2.5f, -6.5f, 2.0f, 1.5f, 1.2f)
                .texOffs(220, 185).addBox(-4.8f, 1.0f, -6.5f, 2.0f, 1.5f, 1.2f)
                .texOffs(228, 185).addBox(2.8f, -2.5f, -6.5f, 2.0f, 1.5f, 1.2f)
                .texOffs(236, 185).addBox(2.8f, 1.0f, -6.5f, 2.0f, 1.5f, 1.2f)
                .texOffs(180, 195).addBox(-4.0f, -4.0f, -6.5f, 1.5f, 1.5f, 1.2f)
                .texOffs(188, 195).addBox(2.5f, -4.0f, -6.5f, 1.5f, 1.5f, 1.2f)
                .texOffs(196, 195).addBox(-4.0f, 2.5f, -6.5f, 1.5f, 1.5f, 1.2f)
                .texOffs(204, 195).addBox(2.5f, 2.5f, -6.5f, 1.5f, 1.5f, 1.2f),
                PartPose.ZERO);

        PartDefinition teethInner = head.addOrReplaceChild("teeth_inner", CubeListBuilder.create()
                .texOffs(180, 210).addBox(-0.75f, -3.2f, -4.2f, 1.5f, 1.5f, 1.0f)
                .texOffs(188, 210).addBox(-0.75f, 1.7f, -4.2f, 1.5f, 1.5f, 1.0f)
                .texOffs(196, 210).addBox(-3.2f, -0.75f, -4.2f, 1.5f, 1.5f, 1.0f)
                .texOffs(204, 210).addBox(1.7f, -0.75f, -4.2f, 1.5f, 1.5f, 1.0f)
                .texOffs(212, 210).addBox(-2.5f, -2.5f, -4.2f, 1.2f, 1.2f, 1.0f)
                .texOffs(220, 210).addBox(1.3f, -2.5f, -4.2f, 1.2f, 1.2f, 1.0f)
                .texOffs(228, 210).addBox(-2.5f, 1.3f, -4.2f, 1.2f, 1.2f, 1.0f)
                .texOffs(236, 210).addBox(1.3f, 1.3f, -4.2f, 1.2f, 1.2f, 1.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(SandwormRenderState state) {
        super.setupAnim(state);

        float age = state.ageInTicks;
        float rearing = Mth.clamp(state.rearingProgress, 0.0f, 1.0f);
        float slither = 1.0f - rearing;
        float phase = state.slitherProgress;

        float slitherBasePitch = 1.40f + Mth.cos(phase) * 0.04f;
        float slitherBaseYaw = Mth.sin(phase) * 0.35f;

        float slitherSeg1Pitch = 0.02f + Mth.cos(phase - 0.5f) * 0.05f;
        float slitherSeg1Yaw = Mth.sin(phase - 0.5f) * 0.42f;

        float slitherSeg2Pitch = 0.02f + Mth.cos(phase - 1.0f) * 0.06f;
        float slitherSeg2Yaw = Mth.sin(phase - 1.0f) * 0.48f;

        float slitherSeg3Pitch = 0.02f + Mth.cos(phase - 1.5f) * 0.06f;
        float slitherSeg3Yaw = Mth.sin(phase - 1.5f) * 0.52f;

        float slitherSeg4Pitch = 0.02f + Mth.cos(phase - 2.0f) * 0.06f;
        float slitherSeg4Yaw = Mth.sin(phase - 2.0f) * 0.54f;

        float slitherSeg5Pitch = -0.02f + Mth.cos(phase - 2.5f) * 0.06f;
        float slitherSeg5Yaw = Mth.sin(phase - 2.5f) * 0.52f;

        float slitherSeg6Pitch = -0.03f + Mth.cos(phase - 3.0f) * 0.06f;
        float slitherSeg6Yaw = Mth.sin(phase - 3.0f) * 0.48f;

        float slitherSeg7Pitch = -0.04f + Mth.cos(phase - 3.5f) * 0.05f;
        float slitherSeg7Yaw = Mth.sin(phase - 3.5f) * 0.42f;

        float slitherNeckPitch = -0.15f + Mth.cos(phase - 4.0f) * 0.04f;
        float slitherNeckYaw = Mth.sin(phase - 4.0f) * 0.35f;

        float slitherHeadPitch = -0.25f;
        float slitherHeadYaw = Mth.sin(phase - 4.5f) * 0.28f;

        float totalPitch;
        if (state.hasTarget) {
            float distNorm = Mth.clamp((state.targetDistance - 3.0f) / 20.0f, 0.0f, 1.0f);
            totalPitch = Mth.lerp(distNorm, 2.05f, 1.15f);
        } else {
            totalPitch = 0.95f;
        }

        float w0 = Mth.sin(age * 0.035f) * 0.02f;
        float w1 = Mth.sin(age * 0.035f - 0.3f) * 0.025f;
        float w2 = Mth.sin(age * 0.035f - 0.6f) * 0.025f;
        float w3 = Mth.sin(age * 0.035f - 0.9f) * 0.03f;
        float w4 = Mth.sin(age * 0.035f - 1.2f) * 0.03f;
        float w5 = Mth.sin(age * 0.035f - 1.5f) * 0.03f;
        float w6 = Mth.sin(age * 0.035f - 1.8f) * 0.03f;
        float w7 = Mth.sin(age * 0.035f - 2.1f) * 0.03f;
        float w8 = Mth.sin(age * 0.035f - 2.4f) * 0.035f;
        float w9 = Mth.sin(age * 0.035f - 2.7f) * 0.04f;

        float rearBasePitch = (totalPitch * 0.06f) + w0;
        float rearSeg1Pitch = (totalPitch * 0.08f) + w1;
        float rearSeg2Pitch = (totalPitch * 0.11f) + w2;
        float rearSeg3Pitch = (totalPitch * 0.13f) + w3;
        float rearSeg4Pitch = (totalPitch * 0.14f) + w4;
        float rearSeg5Pitch = (totalPitch * 0.14f) + w5;
        float rearSeg6Pitch = (totalPitch * 0.13f) + w6;
        float rearSeg7Pitch = (totalPitch * 0.10f) + w7;
        float rearNeckPitch = (totalPitch * 0.06f) + w8;
        float rearHeadPitch = (totalPitch * 0.05f) + w9;

        float relYawRad = Mth.clamp(state.targetRelativeYaw * (float) (Math.PI / 180.0), -1.2f, 1.2f);
        float sway = Mth.cos(age * 0.025f) * 0.025f;

        float rearBaseYaw = (relYawRad * 0.06f) + sway * 0.2f;
        float rearSeg1Yaw = (relYawRad * 0.08f) + sway * 0.4f;
        float rearSeg2Yaw = (relYawRad * 0.11f) + sway * 0.6f;
        float rearSeg3Yaw = (relYawRad * 0.13f) + sway * 0.8f;
        float rearSeg4Yaw = (relYawRad * 0.14f) + sway * 1.0f;
        float rearSeg5Yaw = (relYawRad * 0.14f) + sway * 1.2f;
        float rearSeg6Yaw = (relYawRad * 0.13f) + sway * 1.4f;
        float rearSeg7Yaw = (relYawRad * 0.10f) + sway * 1.6f;
        float rearNeckYaw = (relYawRad * 0.06f) + sway * 1.8f;
        float rearHeadYaw = (relYawRad * 0.05f) + sway * 2.0f;

        this.base.xRot = Mth.lerp(slither, rearBasePitch, slitherBasePitch);
        this.base.yRot = Mth.lerp(slither, rearBaseYaw, slitherBaseYaw);

        this.seg1.xRot = Mth.lerp(slither, rearSeg1Pitch, slitherSeg1Pitch);
        this.seg1.yRot = Mth.lerp(slither, rearSeg1Yaw, slitherSeg1Yaw);

        this.seg2.xRot = Mth.lerp(slither, rearSeg2Pitch, slitherSeg2Pitch);
        this.seg2.yRot = Mth.lerp(slither, rearSeg2Yaw, slitherSeg2Yaw);

        this.seg3.xRot = Mth.lerp(slither, rearSeg3Pitch, slitherSeg3Pitch);
        this.seg3.yRot = Mth.lerp(slither, rearSeg3Yaw, slitherSeg3Yaw);

        this.seg4.xRot = Mth.lerp(slither, rearSeg4Pitch, slitherSeg4Pitch);
        this.seg4.yRot = Mth.lerp(slither, rearSeg4Yaw, slitherSeg4Yaw);

        this.seg5.xRot = Mth.lerp(slither, rearSeg5Pitch, slitherSeg5Pitch);
        this.seg5.yRot = Mth.lerp(slither, rearSeg5Yaw, slitherSeg5Yaw);

        this.seg6.xRot = Mth.lerp(slither, rearSeg6Pitch, slitherSeg6Pitch);
        this.seg6.yRot = Mth.lerp(slither, rearSeg6Yaw, slitherSeg6Yaw);

        this.seg7.xRot = Mth.lerp(slither, rearSeg7Pitch, slitherSeg7Pitch);
        this.seg7.yRot = Mth.lerp(slither, rearSeg7Yaw, slitherSeg7Yaw);

        this.neck.xRot = Mth.lerp(slither, rearNeckPitch, slitherNeckPitch);
        this.neck.yRot = Mth.lerp(slither, rearNeckYaw, slitherNeckYaw);

        this.head.xRot = Mth.lerp(slither, rearHeadPitch, slitherHeadPitch);
        this.head.yRot = Mth.lerp(slither, rearHeadYaw, slitherHeadYaw + relYawRad * 0.35f * slither);

        if (state.breaching) {
            this.base.xRot *= state.breachProgress;
            this.seg1.xRot *= state.breachProgress;
            this.seg2.xRot *= state.breachProgress;
            this.seg3.xRot *= state.breachProgress;
            this.seg4.xRot *= state.breachProgress;
            this.seg5.xRot *= state.breachProgress;
            this.seg6.xRot *= state.breachProgress;
            this.seg7.xRot *= state.breachProgress;
            this.neck.xRot *= state.breachProgress;
            this.head.xRot *= state.breachProgress;
        }

        if (state.biteProgress > 0.0f) {
            float p = state.biteProgress;
            float strikeCurve;
            if (p < 0.40f) {
                strikeCurve = -Mth.sin(p / 0.40f * (float) (Math.PI * 0.5)) * 0.22f;
            } else {
                strikeCurve = Mth.sin((p - 0.40f) / 0.60f * (float) Math.PI) * 0.85f;
            }
            this.seg3.xRot += strikeCurve * 0.10f;
            this.seg4.xRot += strikeCurve * 0.15f;
            this.seg5.xRot += strikeCurve * 0.20f;
            this.seg6.xRot += strikeCurve * 0.25f;
            this.seg7.xRot += strikeCurve * 0.28f;
            this.neck.xRot += strikeCurve * 0.32f;
            this.head.xRot += strikeCurve * 0.38f;

            float teethSnap = Mth.sin(p * (float) Math.PI);
            this.teethOuter.z = teethSnap * -2.0f;
            this.teethMiddle.z = teethSnap * -2.8f;
            this.teethInner.z = teethSnap * -3.5f;
            this.teethOuter.xRot = teethSnap * 0.18f;
            this.teethMiddle.xRot = -teethSnap * 0.18f;
            this.teethInner.xRot = teethSnap * 0.15f;
            this.teethOuter.zRot = teethSnap * 0.12f;
            this.teethMiddle.zRot = -teethSnap * 0.14f;
            this.teethInner.zRot = teethSnap * 0.16f;
        } else {
            this.teethOuter.z = Mth.sin(age * 0.06f) * 0.05f;
            this.teethMiddle.z = Mth.cos(age * 0.06f + 1.0f) * 0.05f;
            this.teethInner.z = Mth.sin(age * 0.06f + 2.0f) * 0.05f;
            this.teethOuter.xRot = Mth.cos(age * 0.04f) * 0.015f;
            this.teethMiddle.xRot = -Mth.cos(age * 0.045f + 0.8f) * 0.02f;
            this.teethInner.xRot = Mth.cos(age * 0.05f + 1.6f) * 0.025f;
            this.teethOuter.zRot = Mth.sin(age * 0.04f) * 0.035f;
            this.teethMiddle.zRot = -Mth.sin(age * 0.045f + 1.2f) * 0.04f;
            this.teethInner.zRot = Mth.sin(age * 0.05f + 2.4f) * 0.045f;
        }

        float pulse = Mth.sin(age * 0.05f) * 0.02f;
        this.subRing1.xScale = 1.0f + pulse;
        this.subRing1.zScale = 1.0f + pulse;
        this.subRing1.xRot = state.groundSlopePitch * 0.35f;
        this.subRing1.zRot = state.groundSlopeRoll * 0.35f;

        this.subRing2.xScale = 1.0f + pulse * 1.5f;
        this.subRing2.zScale = 1.0f + pulse * 1.5f;
        this.subRing2.xRot = state.groundSlopePitch * 0.70f;
        this.subRing2.zRot = state.groundSlopeRoll * 0.70f;

        this.subRing3.xScale = 1.0f + pulse * 2.0f;
        this.subRing3.zScale = 1.0f + pulse * 2.0f;
        this.subRing3.xRot = state.groundSlopePitch * 1.05f;
        this.subRing3.zRot = state.groundSlopeRoll * 1.05f;

        float sinkModelUnits = state.groundSink * (16.0f / 7.5f);
        if (state.burrowed) {
            this.body.y = 24.0f + 120.0f;
        } else if (state.submerging) {
            this.body.y = 24.0f + 24.0f + sinkModelUnits;
        } else if (state.breaching) {
            this.body.y = 24.0f + 20.0f * (1.0f - state.breachProgress) + sinkModelUnits;
        } else {
            this.body.y = 24.0f + sinkModelUnits + Mth.lerp(slither, 0.0f, 6.0f);
        }

        this.body.visible = !state.burrowed;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
