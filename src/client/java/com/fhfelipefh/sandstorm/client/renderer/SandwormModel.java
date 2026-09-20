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
    private final ModelPart midBody;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart teethOuter;
    private final ModelPart teethMiddle;
    private final ModelPart teethInner;
    private final ModelPart sandSkirt;

    public SandwormModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.base = this.body.getChild("base");
        this.midBody = this.base.getChild("mid_body");
        this.neck = this.midBody.getChild("neck");
        this.head = this.neck.getChild("head");
        this.teethOuter = this.head.getChild("teeth_outer");
        this.teethMiddle = this.head.getChild("teeth_middle");
        this.teethInner = this.head.getChild("teeth_inner");
        this.sandSkirt = this.body.getChild("sand_skirt");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));

        PartDefinition sandSkirt = body.addOrReplaceChild("sand_skirt", CubeListBuilder.create()
                .texOffs(48, 80).addBox(-12.0f, -4.0f, -14.0f, 24.0f, 4.0f, 6.0f)
                .texOffs(48, 90).addBox(-12.0f, -4.0f, 8.0f, 24.0f, 4.0f, 6.0f)
                .texOffs(48, 100).addBox(-14.0f, -4.0f, -8.0f, 6.0f, 4.0f, 16.0f)
                .texOffs(48, 100).addBox(8.0f, -4.0f, -8.0f, 6.0f, 4.0f, 16.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition base = body.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0f, -14.0f, -8.0f, 16.0f, 14.0f, 16.0f)
                .texOffs(0, 30).addBox(-9.0f, -13.0f, -7.0f, 18.0f, 12.0f, 14.0f)
                .texOffs(96, 40).addBox(-3.0f, -15.0f, -9.0f, 6.0f, 14.0f, 4.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        PartDefinition midBody = base.addOrReplaceChild("mid_body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.5f, -14.0f, -7.5f, 15.0f, 14.0f, 15.0f)
                .texOffs(0, 30).addBox(-8.5f, -13.0f, -8.5f, 17.0f, 12.0f, 17.0f)
                .texOffs(96, 58).addBox(-2.5f, -15.0f, -9.5f, 5.0f, 14.0f, 4.0f),
                PartPose.offset(0.0f, -14.0f, -2.0f));

        PartDefinition neck = midBody.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 40).addBox(-7.0f, -14.0f, -7.0f, 14.0f, 14.0f, 14.0f)
                .texOffs(0, 62).addBox(-8.0f, -13.0f, -8.0f, 16.0f, 12.0f, 16.0f)
                .texOffs(96, 76).addBox(-2.0f, -15.0f, -9.0f, 4.0f, 14.0f, 4.0f),
                PartPose.offset(0.0f, -14.0f, -3.0f));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-6.0f, -8.0f, -8.0f, 12.0f, 3.0f, 4.0f)
                .texOffs(64, 7).addBox(-6.0f, 5.0f, -8.0f, 12.0f, 3.0f, 4.0f)
                .texOffs(64, 14).addBox(-8.0f, -6.0f, -8.0f, 3.0f, 12.0f, 4.0f)
                .texOffs(64, 14).addBox(5.0f, -6.0f, -8.0f, 3.0f, 12.0f, 4.0f)
                .texOffs(64, 30).addBox(-7.5f, -7.5f, -7.5f, 4.0f, 4.0f, 3.5f)
                .texOffs(64, 30).addBox(3.5f, -7.5f, -7.5f, 4.0f, 4.0f, 3.5f)
                .texOffs(64, 30).addBox(-7.5f, 3.5f, -7.5f, 4.0f, 4.0f, 3.5f)
                .texOffs(64, 30).addBox(3.5f, 3.5f, -7.5f, 4.0f, 4.0f, 3.5f)
                .texOffs(64, 40).addBox(-5.0f, -5.0f, -4.0f, 10.0f, 10.0f, 8.0f),
                PartPose.offset(0.0f, -14.0f, -2.0f));

        PartDefinition teethOuter = head.addOrReplaceChild("teeth_outer", CubeListBuilder.create()
                .texOffs(0, 80).addBox(-4.0f, -5.0f, -6.5f, 8.0f, 2.0f, 2.0f)
                .texOffs(0, 84).addBox(-4.0f, 3.0f, -6.5f, 8.0f, 2.0f, 2.0f)
                .texOffs(0, 88).addBox(-5.0f, -4.0f, -6.5f, 2.0f, 8.0f, 2.0f)
                .texOffs(0, 88).addBox(3.0f, -4.0f, -6.5f, 2.0f, 8.0f, 2.0f)
                .texOffs(0, 98).addBox(-4.5f, -4.5f, -6.5f, 2.0f, 2.0f, 2.0f)
                .texOffs(0, 98).addBox(2.5f, -4.5f, -6.5f, 2.0f, 2.0f, 2.0f)
                .texOffs(0, 98).addBox(-4.5f, 2.5f, -6.5f, 2.0f, 2.0f, 2.0f)
                .texOffs(0, 98).addBox(2.5f, 2.5f, -6.5f, 2.0f, 2.0f, 2.0f),
                PartPose.ZERO);

        PartDefinition teethMiddle = head.addOrReplaceChild("teeth_middle", CubeListBuilder.create()
                .texOffs(16, 80).addBox(-3.0f, -4.0f, -4.5f, 6.0f, 1.5f, 2.0f)
                .texOffs(16, 84).addBox(-3.0f, 2.5f, -4.5f, 6.0f, 1.5f, 2.0f)
                .texOffs(16, 88).addBox(-4.0f, -3.0f, -4.5f, 1.5f, 6.0f, 2.0f)
                .texOffs(16, 88).addBox(2.5f, -3.0f, -4.5f, 1.5f, 6.0f, 2.0f)
                .texOffs(16, 98).addBox(-3.5f, -3.5f, -4.5f, 1.5f, 1.5f, 2.0f)
                .texOffs(16, 98).addBox(2.0f, -3.5f, -4.5f, 1.5f, 1.5f, 2.0f)
                .texOffs(16, 98).addBox(-3.5f, 2.0f, -4.5f, 1.5f, 1.5f, 2.0f)
                .texOffs(16, 98).addBox(2.0f, 2.0f, -4.5f, 1.5f, 1.5f, 2.0f),
                PartPose.ZERO);

        PartDefinition teethInner = head.addOrReplaceChild("teeth_inner", CubeListBuilder.create()
                .texOffs(32, 80).addBox(-2.0f, -3.0f, -2.0f, 4.0f, 1.0f, 2.0f)
                .texOffs(32, 83).addBox(-2.0f, 2.0f, -2.0f, 4.0f, 1.0f, 2.0f)
                .texOffs(32, 86).addBox(-3.0f, -2.0f, -2.0f, 1.0f, 4.0f, 2.0f)
                .texOffs(32, 86).addBox(2.0f, -2.0f, -2.0f, 1.0f, 4.0f, 2.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(SandwormRenderState state) {
        super.setupAnim(state);

        float age = state.ageInTicks;
        float idleWave = Mth.sin(age * 0.08f) * 0.05f;

        this.base.xRot = 0.05f + idleWave;
        this.midBody.xRot = -0.15f + idleWave * 1.2f;
        this.neck.xRot = -0.25f + idleWave * 1.5f;
        this.head.xRot = -0.3f + idleWave * 2.0f;

        float sway = Mth.cos(age * 0.06f) * 0.08f;
        this.base.yRot = sway * 0.5f;
        this.midBody.yRot = sway * 0.8f;
        this.neck.yRot = sway * 1.2f;
        this.head.yRot = sway * 1.5f;

        if (state.breaching) {
            float breachAngle = -0.6f * state.breachProgress;
            this.midBody.xRot += breachAngle;
            this.neck.xRot += breachAngle * 1.2f;
            this.head.xRot += breachAngle * 1.4f;
        }

        if (state.biteProgress > 0.0f) {
            float biteLunge = Mth.sin(state.biteProgress * (float) Math.PI);
            this.head.xRot += biteLunge * 0.45f;
            this.head.yRot += biteLunge * 0.1f;
        }

        if (state.burrowed) {
            this.body.y = 24.0f + 10.0f;
        } else if (state.submerging) {
            this.body.y = 24.0f + 8.0f;
        } else if (state.breaching) {
            this.body.y = 24.0f - 6.0f * state.breachProgress;
        } else {
            this.body.y = 24.0f;
        }

        this.sandSkirt.visible = true;
        this.body.visible = true;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
