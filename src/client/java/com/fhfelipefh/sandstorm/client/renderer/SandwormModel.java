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
    private final ModelPart seg8;
    private final ModelPart seg9;
    private final ModelPart seg10;
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
        this.seg8 = this.seg7.getChild("segment_8");
        this.seg9 = this.seg8.getChild("segment_9");
        this.seg10 = this.seg9.getChild("segment_10");
        this.neck = this.seg10.getChild("neck");
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
                .texOffs(0, 0).addBox(-11.0f, -20.0f, -11.0f, 22.0f, 8.0f, 22.0f)
                .texOffs(210, 30).addBox(-14.2f, -10.0f, -6.0f, 1.5f, 8.0f, 12.0f)
                .texOffs(210, 30).addBox(12.7f, -10.0f, -6.0f, 1.5f, 8.0f, 12.0f),
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
                .texOffs(0, 20).addBox(-10.5f, -20.0f, -10.5f, 21.0f, 8.0f, 21.0f)
                .texOffs(210, 30).addBox(-13.7f, -10.0f, -5.8f, 1.4f, 8.0f, 11.6f)
                .texOffs(210, 30).addBox(12.3f, -10.0f, -5.8f, 1.4f, 8.0f, 11.6f),
                PartPose.offset(0.0f, -12.0f, -0.8f));

        PartDefinition seg2 = seg1.addOrReplaceChild("segment_2", CubeListBuilder.create()
                .texOffs(0, 40).addBox(-11.0f, -12.0f, -11.0f, 22.0f, 12.0f, 22.0f)
                .texOffs(90, 40).addBox(-12.0f, -10.0f, -12.0f, 24.0f, 10.0f, 24.0f)
                .texOffs(188, 40).addBox(-3.5f, -13.0f, -13.0f, 7.0f, 12.0f, 5.0f)
                .texOffs(0, 40).addBox(-10.0f, -20.0f, -10.0f, 20.0f, 8.0f, 20.0f)
                .texOffs(210, 30).addBox(-13.2f, -10.0f, -5.5f, 1.4f, 8.0f, 11.0f)
                .texOffs(210, 30).addBox(11.8f, -10.0f, -5.5f, 1.4f, 8.0f, 11.0f),
                PartPose.offset(0.0f, -12.0f, -0.8f));

        PartDefinition seg3 = seg2.addOrReplaceChild("segment_3", CubeListBuilder.create()
                .texOffs(0, 60).addBox(-10.5f, -12.0f, -10.5f, 21.0f, 12.0f, 21.0f)
                .texOffs(86, 60).addBox(-11.5f, -10.0f, -11.5f, 23.0f, 10.0f, 23.0f)
                .texOffs(180, 60).addBox(-3.2f, -13.0f, -12.5f, 6.4f, 12.0f, 5.0f)
                .texOffs(0, 60).addBox(-9.5f, -20.0f, -9.5f, 19.0f, 8.0f, 19.0f)
                .texOffs(210, 30).addBox(-12.7f, -10.0f, -5.2f, 1.3f, 8.0f, 10.4f)
                .texOffs(210, 30).addBox(11.4f, -10.0f, -5.2f, 1.3f, 8.0f, 10.4f),
                PartPose.offset(0.0f, -12.0f, -0.9f));

        PartDefinition seg4 = seg3.addOrReplaceChild("segment_4", CubeListBuilder.create()
                .texOffs(0, 80).addBox(-10.0f, -12.0f, -10.0f, 20.0f, 12.0f, 20.0f)
                .texOffs(82, 80).addBox(-11.0f, -10.0f, -11.0f, 22.0f, 10.0f, 22.0f)
                .texOffs(172, 80).addBox(-3.0f, -13.0f, -12.0f, 6.0f, 12.0f, 5.0f)
                .texOffs(0, 80).addBox(-9.0f, -20.0f, -9.0f, 18.0f, 8.0f, 18.0f)
                .texOffs(210, 30).addBox(-12.2f, -10.0f, -5.0f, 1.3f, 8.0f, 10.0f)
                .texOffs(210, 30).addBox(10.9f, -10.0f, -5.0f, 1.3f, 8.0f, 10.0f),
                PartPose.offset(0.0f, -12.0f, -0.9f));

        PartDefinition seg5 = seg4.addOrReplaceChild("segment_5", CubeListBuilder.create()
                .texOffs(0, 100).addBox(-9.5f, -12.0f, -9.5f, 19.0f, 12.0f, 19.0f)
                .texOffs(78, 100).addBox(-10.5f, -10.0f, -10.5f, 21.0f, 10.0f, 21.0f)
                .texOffs(164, 100).addBox(-2.8f, -13.0f, -11.5f, 5.6f, 12.0f, 4.5f)
                .texOffs(0, 100).addBox(-8.5f, -20.0f, -8.5f, 17.0f, 8.0f, 17.0f)
                .texOffs(210, 30).addBox(-11.6f, -10.0f, -4.8f, 1.2f, 8.0f, 9.6f)
                .texOffs(210, 30).addBox(10.4f, -10.0f, -4.8f, 1.2f, 8.0f, 9.6f),
                PartPose.offset(0.0f, -12.0f, -1.0f));

        PartDefinition seg6 = seg5.addOrReplaceChild("segment_6", CubeListBuilder.create()
                .texOffs(0, 120).addBox(-9.0f, -12.0f, -9.0f, 18.0f, 12.0f, 18.0f)
                .texOffs(74, 120).addBox(-10.0f, -10.0f, -10.0f, 20.0f, 10.0f, 20.0f)
                .texOffs(156, 120).addBox(-2.5f, -13.0f, -11.0f, 5.0f, 12.0f, 4.0f)
                .texOffs(0, 120).addBox(-8.0f, -20.0f, -8.0f, 16.0f, 8.0f, 16.0f)
                .texOffs(210, 30).addBox(-11.1f, -10.0f, -4.5f, 1.2f, 8.0f, 9.0f)
                .texOffs(210, 30).addBox(9.9f, -10.0f, -4.5f, 1.2f, 8.0f, 9.0f),
                PartPose.offset(0.0f, -12.0f, -1.0f));

        PartDefinition seg7 = seg6.addOrReplaceChild("segment_7", CubeListBuilder.create()
                .texOffs(0, 140).addBox(-8.8f, -12.0f, -8.8f, 17.6f, 12.0f, 17.6f)
                .texOffs(70, 140).addBox(-9.8f, -10.0f, -9.8f, 19.6f, 10.0f, 19.6f)
                .texOffs(148, 140).addBox(-2.4f, -13.0f, -10.8f, 4.8f, 12.0f, 4.0f)
                .texOffs(0, 140).addBox(-7.8f, -20.0f, -7.8f, 15.6f, 8.0f, 15.6f)
                .texOffs(210, 30).addBox(-10.8f, -10.0f, -4.4f, 1.2f, 8.0f, 8.8f)
                .texOffs(210, 30).addBox(9.6f, -10.0f, -4.4f, 1.2f, 8.0f, 8.8f),
                PartPose.offset(0.0f, -12.0f, -1.0f));

        PartDefinition seg8 = seg7.addOrReplaceChild("segment_8", CubeListBuilder.create()
                .texOffs(0, 140).addBox(-8.5f, -12.0f, -8.5f, 17.0f, 12.0f, 17.0f)
                .texOffs(70, 140).addBox(-9.5f, -10.0f, -9.5f, 19.0f, 10.0f, 19.0f)
                .texOffs(148, 140).addBox(-2.2f, -13.0f, -10.5f, 4.4f, 12.0f, 4.0f)
                .texOffs(0, 140).addBox(-7.5f, -20.0f, -7.5f, 15.0f, 8.0f, 15.0f)
                .texOffs(210, 30).addBox(-10.5f, -10.0f, -4.2f, 1.1f, 8.0f, 8.4f)
                .texOffs(210, 30).addBox(9.4f, -10.0f, -4.2f, 1.1f, 8.0f, 8.4f),
                PartPose.offset(0.0f, -12.0f, -1.1f));

        PartDefinition seg9 = seg8.addOrReplaceChild("segment_9", CubeListBuilder.create()
                .texOffs(0, 160).addBox(-8.2f, -12.0f, -8.2f, 16.4f, 12.0f, 16.4f)
                .texOffs(66, 160).addBox(-9.2f, -10.0f, -9.2f, 18.4f, 10.0f, 18.4f)
                .texOffs(140, 160).addBox(-2.1f, -13.0f, -10.2f, 4.2f, 12.0f, 4.0f)
                .texOffs(0, 160).addBox(-7.2f, -20.0f, -7.2f, 14.4f, 8.0f, 14.4f)
                .texOffs(210, 30).addBox(-10.2f, -10.0f, -4.0f, 1.1f, 8.0f, 8.0f)
                .texOffs(210, 30).addBox(9.1f, -10.0f, -4.0f, 1.1f, 8.0f, 8.0f),
                PartPose.offset(0.0f, -12.0f, -1.1f));

        PartDefinition seg10 = seg9.addOrReplaceChild("segment_10", CubeListBuilder.create()
                .texOffs(0, 160).addBox(-8.0f, -12.0f, -8.0f, 16.0f, 12.0f, 16.0f)
                .texOffs(66, 160).addBox(-9.0f, -10.0f, -9.0f, 18.0f, 10.0f, 18.0f)
                .texOffs(140, 160).addBox(-2.0f, -13.0f, -10.0f, 4.0f, 12.0f, 4.0f)
                .texOffs(0, 160).addBox(-7.0f, -20.0f, -7.0f, 14.0f, 8.0f, 14.0f)
                .texOffs(210, 30).addBox(-9.9f, -10.0f, -3.8f, 1.0f, 8.0f, 7.6f)
                .texOffs(210, 30).addBox(8.9f, -10.0f, -3.8f, 1.0f, 8.0f, 7.6f),
                PartPose.offset(0.0f, -12.0f, -1.2f));

        PartDefinition neck = seg10.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 160).addBox(-7.5f, -12.0f, -7.5f, 15.0f, 12.0f, 15.0f)
                .texOffs(66, 160).addBox(-8.5f, -10.0f, -8.5f, 17.0f, 10.0f, 17.0f)
                .texOffs(140, 160).addBox(-1.8f, -13.0f, -9.5f, 3.6f, 12.0f, 3.8f)
                .texOffs(0, 160).addBox(-6.5f, -18.0f, -6.5f, 13.0f, 6.0f, 13.0f),
                PartPose.offset(0.0f, -12.0f, -1.2f));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 198).addBox(-7.0f, -10.0f, -10.0f, 14.0f, 4.0f, 6.0f)
                .texOffs(42, 198).addBox(-7.0f, 6.0f, -10.0f, 14.0f, 4.0f, 6.0f)
                .texOffs(84, 198).addBox(-10.0f, -7.0f, -10.0f, 4.0f, 14.0f, 6.0f)
                .texOffs(126, 198).addBox(6.0f, -7.0f, -10.0f, 4.0f, 14.0f, 6.0f)
                .texOffs(0, 210).addBox(-6.0f, -6.0f, -5.0f, 12.0f, 12.0f, 1.0f)
                .texOffs(0, 210).addBox(-5.5f, -5.5f, -4.0f, 11.0f, 11.0f, 1.0f)
                .texOffs(0, 210).addBox(-5.0f, -5.0f, -3.0f, 10.0f, 10.0f, 1.0f)
                .texOffs(0, 210).addBox(-4.5f, -4.5f, -2.0f, 9.0f, 9.0f, 1.0f)
                .texOffs(0, 210).addBox(-4.0f, -4.0f, -1.0f, 8.0f, 8.0f, 1.0f)
                .texOffs(0, 210).addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 1.0f),
                PartPose.offset(0.0f, -12.0f, -1.5f));

        PartDefinition teethOuter = head.addOrReplaceChild("teeth_outer", CubeListBuilder.create()
                .texOffs(180, 198).addBox(-1.5f, -6.8f, -9.2f, 3.0f, 2.5f, 1.5f)
                .texOffs(190, 198).addBox(-1.5f, 4.3f, -9.2f, 3.0f, 2.5f, 1.5f)
                .texOffs(200, 198).addBox(-6.8f, -1.5f, -9.2f, 2.5f, 3.0f, 1.5f)
                .texOffs(210, 198).addBox(4.3f, -1.5f, -9.2f, 2.5f, 3.0f, 1.5f)
                .texOffs(220, 198).addBox(-5.5f, -5.5f, -9.2f, 2.0f, 2.0f, 1.5f)
                .texOffs(228, 198).addBox(3.5f, -5.5f, -9.2f, 2.0f, 2.0f, 1.5f)
                .texOffs(236, 198).addBox(-5.5f, 3.5f, -9.2f, 2.0f, 2.0f, 1.5f)
                .texOffs(244, 198).addBox(3.5f, 3.5f, -9.2f, 2.0f, 2.0f, 1.5f),
                PartPose.ZERO);

        PartDefinition teethMiddle = head.addOrReplaceChild("teeth_middle", CubeListBuilder.create()
                .texOffs(180, 204).addBox(-1.0f, -4.8f, -6.6f, 2.0f, 2.0f, 1.2f)
                .texOffs(188, 204).addBox(-1.0f, 2.8f, -6.6f, 2.0f, 2.0f, 1.2f)
                .texOffs(196, 204).addBox(-4.8f, -1.0f, -6.6f, 2.0f, 2.0f, 1.2f)
                .texOffs(204, 204).addBox(2.8f, -1.0f, -6.6f, 2.0f, 2.0f, 1.2f)
                .texOffs(212, 204).addBox(-3.8f, -3.8f, -6.6f, 1.8f, 1.8f, 1.2f)
                .texOffs(220, 204).addBox(2.0f, -3.8f, -6.6f, 1.8f, 1.8f, 1.2f)
                .texOffs(228, 204).addBox(-3.8f, 2.0f, -6.6f, 1.8f, 1.8f, 1.2f)
                .texOffs(236, 204).addBox(2.0f, 2.0f, -6.6f, 1.8f, 1.8f, 1.2f),
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

        float totalPitch;
        if (state.hasTarget) {
            float distNorm = Mth.clamp((state.targetDistance - 3.0f) / 20.0f, 0.0f, 1.0f);
            totalPitch = Mth.lerp(distNorm, 1.85f, 0.95f);
        } else {
            totalPitch = 0.85f;
        }

        float w0 = Mth.sin(age * 0.035f) * 0.015f;
        float w1 = Mth.sin(age * 0.035f - 0.25f) * 0.02f;
        float w2 = Mth.sin(age * 0.035f - 0.50f) * 0.02f;
        float w3 = Mth.sin(age * 0.035f - 0.75f) * 0.025f;
        float w4 = Mth.sin(age * 0.035f - 1.00f) * 0.025f;
        float w5 = Mth.sin(age * 0.035f - 1.25f) * 0.025f;
        float w6 = Mth.sin(age * 0.035f - 1.50f) * 0.025f;
        float w7 = Mth.sin(age * 0.035f - 1.75f) * 0.025f;
        float w8 = Mth.sin(age * 0.035f - 2.00f) * 0.025f;
        float w9 = Mth.sin(age * 0.035f - 2.25f) * 0.03f;
        float w10 = Mth.sin(age * 0.035f - 2.50f) * 0.03f;
        float w11 = Mth.sin(age * 0.035f - 2.75f) * 0.035f;
        float w12 = Mth.sin(age * 0.035f - 3.00f) * 0.035f;

        float relYawRad = Mth.clamp(state.targetRelativeYaw * (float) (Math.PI / 180.0), -1.1f, 1.1f);
        float sway = Mth.cos(age * 0.025f) * 0.025f;

        float rearBasePitch = (totalPitch * 0.04f) + w0 + state.groundSlopePitch * 0.5f;
        float rearSeg1Pitch = (totalPitch * 0.06f) + w1;
        float rearSeg2Pitch = (totalPitch * 0.08f) + w2;
        float rearSeg3Pitch = (totalPitch * 0.09f) + w3;
        float rearSeg4Pitch = (totalPitch * 0.10f) + w4;
        float rearSeg5Pitch = (totalPitch * 0.10f) + w5;
        float rearSeg6Pitch = (totalPitch * 0.10f) + w6;
        float rearSeg7Pitch = (totalPitch * 0.09f) + w7;
        float rearSeg8Pitch = (totalPitch * 0.08f) + w8;
        float rearSeg9Pitch = (totalPitch * 0.07f) + w9;
        float rearSeg10Pitch = (totalPitch * 0.06f) + w10;
        float rearNeckPitch = (totalPitch * 0.05f) + w11;
        float rearHeadPitch = (totalPitch * 0.04f) + w12;

        float rearBaseYaw = (relYawRad * 0.04f) + sway * 0.15f;
        float rearSeg1Yaw = (relYawRad * 0.06f) + sway * 0.3f;
        float rearSeg2Yaw = (relYawRad * 0.08f) + sway * 0.45f;
        float rearSeg3Yaw = (relYawRad * 0.10f) + sway * 0.6f;
        float rearSeg4Yaw = (relYawRad * 0.11f) + sway * 0.75f;
        float rearSeg5Yaw = (relYawRad * 0.12f) + sway * 0.9f;
        float rearSeg6Yaw = (relYawRad * 0.12f) + sway * 1.05f;
        float rearSeg7Yaw = (relYawRad * 0.11f) + sway * 1.2f;
        float rearSeg8Yaw = (relYawRad * 0.10f) + sway * 1.35f;
        float rearSeg9Yaw = (relYawRad * 0.08f) + sway * 1.5f;
        float rearSeg10Yaw = (relYawRad * 0.06f) + sway * 1.65f;
        float rearNeckYaw = (relYawRad * 0.05f) + sway * 1.8f;
        float rearHeadYaw = (relYawRad * 0.04f) + sway * 2.0f;

        float rollBase = (relYawRad * 0.03f) + state.groundSlopeRoll * 0.5f;
        float rollSeg1 = (relYawRad * 0.04f) + sway * 0.2f;
        float rollSeg2 = (relYawRad * 0.05f) + sway * 0.3f;
        float rollSeg3 = (relYawRad * 0.06f) + sway * 0.4f;
        float rollSeg4 = (relYawRad * 0.07f) + sway * 0.5f;
        float rollSeg5 = (relYawRad * 0.08f) + sway * 0.6f;
        float rollSeg6 = (relYawRad * 0.08f) + sway * 0.6f;
        float rollSeg7 = (relYawRad * 0.07f) + sway * 0.5f;
        float rollSeg8 = (relYawRad * 0.06f) + sway * 0.4f;
        float rollSeg9 = (relYawRad * 0.05f) + sway * 0.3f;
        float rollSeg10 = (relYawRad * 0.04f) + sway * 0.2f;
        float rollNeck = (relYawRad * 0.03f) + sway * 0.15f;
        float rollHead = (relYawRad * 0.02f) + sway * 0.1f;

        this.base.xRot = rearBasePitch * rearing;
        this.base.yRot = rearBaseYaw;
        this.base.zRot = rollBase;

        this.seg1.xRot = rearSeg1Pitch * rearing;
        this.seg1.yRot = rearSeg1Yaw;
        this.seg1.zRot = rollSeg1;

        this.seg2.xRot = rearSeg2Pitch * rearing;
        this.seg2.yRot = rearSeg2Yaw;
        this.seg2.zRot = rollSeg2;

        this.seg3.xRot = rearSeg3Pitch * rearing;
        this.seg3.yRot = rearSeg3Yaw;
        this.seg3.zRot = rollSeg3;

        this.seg4.xRot = rearSeg4Pitch * rearing;
        this.seg4.yRot = rearSeg4Yaw;
        this.seg4.zRot = rollSeg4;

        this.seg5.xRot = rearSeg5Pitch * rearing;
        this.seg5.yRot = rearSeg5Yaw;
        this.seg5.zRot = rollSeg5;

        this.seg6.xRot = rearSeg6Pitch * rearing;
        this.seg6.yRot = rearSeg6Yaw;
        this.seg6.zRot = rollSeg6;

        this.seg7.xRot = rearSeg7Pitch * rearing;
        this.seg7.yRot = rearSeg7Yaw;
        this.seg7.zRot = rollSeg7;

        this.seg8.xRot = rearSeg8Pitch * rearing;
        this.seg8.yRot = rearSeg8Yaw;
        this.seg8.zRot = rollSeg8;

        this.seg9.xRot = rearSeg9Pitch * rearing;
        this.seg9.yRot = rearSeg9Yaw;
        this.seg9.zRot = rollSeg9;

        this.seg10.xRot = rearSeg10Pitch * rearing;
        this.seg10.yRot = rearSeg10Yaw;
        this.seg10.zRot = rollSeg10;

        this.neck.xRot = rearNeckPitch * rearing;
        this.neck.yRot = rearNeckYaw;
        this.neck.zRot = rollNeck;

        this.head.xRot = rearHeadPitch * rearing;
        this.head.yRot = rearHeadYaw;
        this.head.zRot = rollHead;

        if (state.breaching) {
            float bp = state.breachProgress;
            float smoothBreach = Mth.sin(bp * (float) (Math.PI * 0.5));
            float uncurlWave = (1.0f - smoothBreach) * 0.35f;

            this.base.xRot = this.base.xRot * smoothBreach - uncurlWave * 0.2f;
            this.seg1.xRot = this.seg1.xRot * smoothBreach - uncurlWave * 0.3f;
            this.seg2.xRot = this.seg2.xRot * smoothBreach - uncurlWave * 0.4f;
            this.seg3.xRot = this.seg3.xRot * smoothBreach - uncurlWave * 0.5f;
            this.seg4.xRot = this.seg4.xRot * smoothBreach - uncurlWave * 0.6f;
            this.seg5.xRot = this.seg5.xRot * smoothBreach - uncurlWave * 0.7f;
            this.seg6.xRot = this.seg6.xRot * smoothBreach - uncurlWave * 0.8f;
            this.seg7.xRot = this.seg7.xRot * smoothBreach - uncurlWave * 0.9f;
            this.seg8.xRot = this.seg8.xRot * smoothBreach - uncurlWave * 1.0f;
            this.seg9.xRot = this.seg9.xRot * smoothBreach - uncurlWave * 1.0f;
            this.seg10.xRot = this.seg10.xRot * smoothBreach - uncurlWave * 0.9f;
            this.neck.xRot = this.neck.xRot * smoothBreach - uncurlWave * 0.8f;
            this.head.xRot = this.head.xRot * smoothBreach - uncurlWave * 0.6f;
        }

        if (state.submerging) {
            float sp = state.submergeProgress;
            float diveCurve = Mth.sin(sp * (float) Math.PI) * 0.55f;

            this.seg4.xRot += diveCurve * 0.10f;
            this.seg5.xRot += diveCurve * 0.15f;
            this.seg6.xRot += diveCurve * 0.20f;
            this.seg7.xRot += diveCurve * 0.25f;
            this.seg8.xRot += diveCurve * 0.30f;
            this.seg9.xRot += diveCurve * 0.35f;
            this.seg10.xRot += diveCurve * 0.38f;
            this.neck.xRot += diveCurve * 0.42f;
            this.head.xRot += diveCurve * 0.48f;
        }

        if (state.biteProgress > 0.0f) {
            float p = state.biteProgress;
            float strikeCurve;
            if (p < 0.40f) {
                strikeCurve = -Mth.sin(p / 0.40f * (float) (Math.PI * 0.5)) * 0.22f;
            } else {
                strikeCurve = Mth.sin((p - 0.40f) / 0.60f * (float) Math.PI) * 0.85f;
            }
            this.seg4.xRot += strikeCurve * 0.10f;
            this.seg5.xRot += strikeCurve * 0.14f;
            this.seg6.xRot += strikeCurve * 0.18f;
            this.seg7.xRot += strikeCurve * 0.22f;
            this.seg8.xRot += strikeCurve * 0.26f;
            this.seg9.xRot += strikeCurve * 0.30f;
            this.seg10.xRot += strikeCurve * 0.33f;
            this.neck.xRot += strikeCurve * 0.36f;
            this.head.xRot += strikeCurve * 0.40f;

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
            float sp = state.submergeProgress;
            float smoothSub = Mth.sin(sp * (float) (Math.PI * 0.5));
            this.body.y = 24.0f + (smoothSub * 56.0f) + sinkModelUnits;
        } else if (state.breaching) {
            float bp = state.breachProgress;
            float smoothBreach = Mth.sin(bp * (float) (Math.PI * 0.5));
            this.body.y = 24.0f + 48.0f * (1.0f - smoothBreach) + sinkModelUnits;
        } else {
            this.body.y = 24.0f + sinkModelUnits;
        }

        this.body.visible = !state.burrowed;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
