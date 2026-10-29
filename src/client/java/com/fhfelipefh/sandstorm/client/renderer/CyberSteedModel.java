package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class CyberSteedModel extends EntityModel<CyberSteedRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart fuelCanister;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart tail;
    private final ModelPart sonicTurret;
    private final ModelPart batteryPack;
    private final ModelPart speedOverdrive;
    private final ModelPart cargoPanniers;

    public CyberSteedModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.fuelCanister = root.getChild("fuel_canister");
        this.neck = root.getChild("neck");
        this.head = root.getChild("head");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.tail = root.getChild("tail");
        this.sonicTurret = root.getChild("sonic_turret");
        this.batteryPack = root.getChild("battery_pack");
        this.speedOverdrive = root.getChild("speed_overdrive");
        this.cargoPanniers = root.getChild("cargo_panniers");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-5.0f, -5.0f, -8.0f, 10.0f, 10.0f, 16.0f)
                .texOffs(0, 58).addBox(-4.0f, -6.85f, -5.5f, 8.0f, 2.0f, 5.0f)
                .texOffs(32, 58).addBox(-3.5f, 4.85f, -6.0f, 7.0f, 2.0f, 12.0f),
                PartPose.offset(0.0f, 11.0f, -1.0f));

        root.addOrReplaceChild("fuel_canister", CubeListBuilder.create()
                .texOffs(52, 32).addBox(-3.0f, -8.2f, 2.0f, 6.0f, 4.0f, 7.0f)
                .texOffs(52, 43).addBox(-2.0f, -6.65f, 9.0f, 4.0f, 2.0f, 2.0f)
                .texOffs(52, 47).addBox(-2.0f, -6.65f, 0.0f, 4.0f, 2.0f, 2.0f),
                PartPose.offset(0.0f, 11.0f, -1.0f));

        root.addOrReplaceChild("sonic_turret", CubeListBuilder.create()
                .texOffs(0, 112).addBox(-2.0f, -8.85f, -7.5f, 4.0f, 2.0f, 2.0f)
                .texOffs(12, 112).addBox(-2.5f, -9.85f, -9.5f, 1.5f, 1.5f, 3.0f)
                .texOffs(12, 112).addBox(1.0f, -9.85f, -9.5f, 1.5f, 1.5f, 3.0f),
                PartPose.offset(0.0f, 11.0f, -1.0f));

        root.addOrReplaceChild("battery_pack", CubeListBuilder.create()
                .texOffs(24, 112).addBox(3.5f, -8.0f, 1.5f, 2.0f, 4.0f, 6.0f)
                .texOffs(24, 112).addBox(-5.5f, -8.0f, 1.5f, 2.0f, 4.0f, 6.0f)
                .texOffs(40, 112).addBox(3.2f, -7.0f, 2.0f, 0.5f, 2.2f, 5.0f)
                .texOffs(40, 112).addBox(-3.7f, -7.0f, 2.0f, 0.5f, 2.2f, 5.0f),
                PartPose.offset(0.0f, 11.0f, -1.0f));

        root.addOrReplaceChild("speed_overdrive", CubeListBuilder.create()
                .texOffs(52, 112).addBox(3.5f, -3.5f, 6.5f, 2.0f, 3.0f, 4.0f)
                .texOffs(52, 112).addBox(-5.5f, -3.5f, 6.5f, 2.0f, 3.0f, 4.0f)
                .texOffs(64, 112).addBox(3.8f, -3.0f, 10.5f, 1.4f, 2.0f, 1.5f)
                .texOffs(64, 112).addBox(-5.2f, -3.0f, 10.5f, 1.4f, 2.0f, 1.5f),
                PartPose.offset(0.0f, 11.0f, -1.0f));

        root.addOrReplaceChild("cargo_panniers", CubeListBuilder.create()
                .texOffs(76, 112).addBox(5.2f, -4.5f, -4.5f, 2.5f, 6.0f, 8.0f)
                .texOffs(76, 112).addBox(-7.7f, -4.5f, -4.5f, 2.5f, 6.0f, 8.0f)
                .texOffs(98, 112).addBox(5.5f, -3.0f, -3.0f, 2.4f, 3.0f, 5.0f)
                .texOffs(98, 112).addBox(-7.9f, -3.0f, -3.0f, 2.4f, 3.0f, 5.0f),
                PartPose.offset(0.0f, 11.0f, -1.0f));

        root.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 68).addBox(-2.5f, -9.0f, -4.0f, 5.0f, 11.0f, 5.0f)
                .texOffs(20, 68).addBox(-1.5f, -8.0f, 1.0f, 3.0f, 9.0f, 2.0f),
                PartPose.offset(0.0f, 8.0f, -7.0f));

        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.5f, -3.5f, -4.0f, 7.0f, 7.0f, 8.0f)
                .texOffs(30, 0).addBox(-2.5f, -1.5f, -10.0f, 5.0f, 5.0f, 6.0f)
                .texOffs(52, 0).addBox(-3.0f, -2.5f, -6.15f, 6.0f, 2.0f, 2.0f)
                .texOffs(0, 15).addBox(-4.5f, -6.5f, -2.0f, 2.0f, 4.0f, 8.0f)
                .texOffs(20, 15).addBox(2.5f, -6.5f, -2.0f, 2.0f, 4.0f, 8.0f)
                .texOffs(40, 15).addBox(-2.0f, -4.5f, -7.0f, 4.0f, 2.0f, 5.0f),
                PartPose.offset(0.0f, 0.0f, -11.0f));

        root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(64, 68).addBox(-2.0f, -1.0f, -2.0f, 4.0f, 7.0f, 4.0f)
                .texOffs(64, 79).addBox(-1.5f, 6.0f, -1.5f, 3.0f, 5.0f, 3.0f)
                .texOffs(64, 87).addBox(-2.0f, 11.0f, -2.5f, 4.0f, 2.0f, 5.0f),
                PartPose.offset(-4.0f, 11.0f, -6.0f));

        root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(80, 68).addBox(-2.0f, -1.0f, -2.0f, 4.0f, 7.0f, 4.0f)
                .texOffs(80, 79).addBox(-1.5f, 6.0f, -1.5f, 3.0f, 5.0f, 3.0f)
                .texOffs(80, 87).addBox(-2.0f, 11.0f, -2.5f, 4.0f, 2.0f, 5.0f),
                PartPose.offset(4.0f, 11.0f, -6.0f));

        root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(96, 68).addBox(-2.5f, -1.0f, -2.5f, 5.0f, 7.0f, 5.0f)
                .texOffs(96, 80).addBox(-1.5f, 6.0f, -1.5f, 3.0f, 5.0f, 3.0f)
                .texOffs(96, 88).addBox(-2.0f, 11.0f, -2.5f, 4.0f, 2.0f, 5.0f),
                PartPose.offset(-4.0f, 11.0f, 5.0f));

        root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(112, 68).addBox(-2.5f, -1.0f, -2.5f, 5.0f, 7.0f, 5.0f)
                .texOffs(112, 80).addBox(-1.5f, 6.0f, -1.5f, 3.0f, 5.0f, 3.0f)
                .texOffs(112, 88).addBox(-2.0f, 11.0f, -2.5f, 4.0f, 2.0f, 5.0f),
                PartPose.offset(4.0f, 11.0f, 5.0f));

        root.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(0, 96).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 9.0f, 2.0f)
                .texOffs(8, 96).addBox(-1.5f, 9.0f, -0.5f, 3.0f, 4.0f, 3.0f),
                PartPose.offset(0.0f, 9.0f, 7.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(CyberSteedRenderState state) {
        super.setupAnim(state);

        this.fuelCanister.visible = !state.isCanisterExploded;
        this.sonicTurret.visible = state.hasSonicTurret && !state.isDeactivated;
        this.batteryPack.visible = state.hasBatteryPack && !state.isDeactivated;
        this.speedOverdrive.visible = state.hasSpeedOverdrive && !state.isDeactivated;
        this.cargoPanniers.visible = state.hasCargoPanniers && !state.isDeactivated;

        if (state.isDeactivated) {
            this.body.setPos(0.0f, 20.0f, -1.0f);
            this.body.xRot = 0.05f;
            this.body.zRot = 0.15f;

            this.fuelCanister.setPos(0.0f, 20.0f, -1.0f);
            this.fuelCanister.xRot = 0.05f;
            this.fuelCanister.zRot = 0.15f;

            this.sonicTurret.setPos(0.0f, 20.0f, -1.0f);
            this.sonicTurret.xRot = 0.05f;
            this.sonicTurret.zRot = 0.15f;

            this.batteryPack.setPos(0.0f, 20.0f, -1.0f);
            this.batteryPack.xRot = 0.05f;
            this.batteryPack.zRot = 0.15f;

            this.speedOverdrive.setPos(0.0f, 20.0f, -1.0f);
            this.speedOverdrive.xRot = 0.05f;
            this.speedOverdrive.zRot = 0.15f;

            this.cargoPanniers.setPos(0.0f, 20.0f, -1.0f);
            this.cargoPanniers.xRot = 0.05f;
            this.cargoPanniers.zRot = 0.15f;

            this.neck.setPos(0.0f, 19.0f, -7.0f);
            this.neck.xRot = 0.8f;
            this.neck.zRot = 0.2f;

            this.head.setPos(0.0f, 21.0f, -13.0f);
            this.head.xRot = 0.2f;
            this.head.yRot = -0.3f;
            this.head.zRot = 0.2f;

            this.rightFrontLeg.setPos(-4.0f, 22.0f, -6.0f);
            this.rightFrontLeg.xRot = 1.3f;
            this.rightFrontLeg.zRot = -0.5f;

            this.leftFrontLeg.setPos(4.0f, 22.0f, -6.0f);
            this.leftFrontLeg.xRot = 1.1f;
            this.leftFrontLeg.zRot = 0.4f;

            this.rightHindLeg.setPos(-4.0f, 22.0f, 5.0f);
            this.rightHindLeg.xRot = -1.2f;
            this.rightHindLeg.zRot = -0.6f;

            this.leftHindLeg.setPos(4.0f, 22.0f, 5.0f);
            this.leftHindLeg.xRot = -1.0f;
            this.leftHindLeg.zRot = 0.5f;

            this.tail.setPos(0.0f, 20.0f, 7.0f);
            this.tail.xRot = 0.2f;
            return;
        }

        if (state.isSitting) {
            this.body.setPos(0.0f, 16.0f, -1.0f);
            this.body.xRot = 0.0f;
            this.body.zRot = 0.0f;

            this.fuelCanister.setPos(0.0f, 16.0f, -1.0f);
            this.fuelCanister.xRot = 0.0f;
            this.fuelCanister.zRot = 0.0f;

            this.sonicTurret.setPos(0.0f, 16.0f, -1.0f);
            this.sonicTurret.xRot = 0.0f;
            this.sonicTurret.zRot = 0.0f;

            this.batteryPack.setPos(0.0f, 16.0f, -1.0f);
            this.batteryPack.xRot = 0.0f;
            this.batteryPack.zRot = 0.0f;

            this.speedOverdrive.setPos(0.0f, 16.0f, -1.0f);
            this.speedOverdrive.xRot = 0.0f;
            this.speedOverdrive.zRot = 0.0f;

            this.cargoPanniers.setPos(0.0f, 16.0f, -1.0f);
            this.cargoPanniers.xRot = 0.0f;
            this.cargoPanniers.zRot = 0.0f;

            this.neck.setPos(0.0f, 13.0f, -7.0f);
            this.neck.xRot = 0.3f;
            this.neck.zRot = 0.0f;

            this.head.setPos(0.0f, 7.0f, -11.0f);
            this.head.xRot = 0.1f;
            this.head.yRot = 0.0f;
            this.head.zRot = 0.0f;

            this.rightFrontLeg.setPos(-4.0f, 18.0f, -6.0f);
            this.rightFrontLeg.xRot = 1.0f;
            this.rightFrontLeg.zRot = -0.1f;

            this.leftFrontLeg.setPos(4.0f, 18.0f, -6.0f);
            this.leftFrontLeg.xRot = 1.0f;
            this.leftFrontLeg.zRot = 0.1f;

            this.rightHindLeg.setPos(-4.0f, 18.0f, 5.0f);
            this.rightHindLeg.xRot = -1.0f;
            this.rightHindLeg.zRot = -0.1f;

            this.leftHindLeg.setPos(4.0f, 18.0f, 5.0f);
            this.leftHindLeg.xRot = -1.0f;
            this.leftHindLeg.zRot = 0.1f;

            this.tail.setPos(0.0f, 14.0f, 7.0f);
            this.tail.xRot = 0.2f;
            return;
        }

        this.body.setPos(0.0f, 11.0f, -1.0f);
        this.body.xRot = 0.0f;
        this.body.zRot = 0.0f;

        this.fuelCanister.setPos(0.0f, 11.0f, -1.0f);
        this.fuelCanister.xRot = 0.0f;
        this.fuelCanister.zRot = 0.0f;

        this.sonicTurret.setPos(0.0f, 11.0f, -1.0f);
        this.sonicTurret.xRot = 0.0f;
        this.sonicTurret.zRot = 0.0f;

        this.batteryPack.setPos(0.0f, 11.0f, -1.0f);
        this.batteryPack.xRot = 0.0f;
        this.batteryPack.zRot = 0.0f;

        this.speedOverdrive.setPos(0.0f, 11.0f, -1.0f);
        this.speedOverdrive.xRot = 0.0f;
        this.speedOverdrive.zRot = 0.0f;

        this.cargoPanniers.setPos(0.0f, 11.0f, -1.0f);
        this.cargoPanniers.xRot = 0.0f;
        this.cargoPanniers.zRot = 0.0f;

        this.neck.setPos(0.0f, 8.0f, -7.0f);
        this.neck.xRot = 0.2f;
        this.neck.zRot = 0.0f;

        this.head.setPos(0.0f, 0.0f, -11.0f);
        this.head.xRot = 0.1f;
        this.head.yRot = 0.0f;
        this.head.zRot = 0.0f;

        this.rightFrontLeg.setPos(-4.0f, 11.0f, -6.0f);
        this.leftFrontLeg.setPos(4.0f, 11.0f, -6.0f);
        this.rightHindLeg.setPos(-4.0f, 11.0f, 5.0f);
        this.leftHindLeg.setPos(4.0f, 11.0f, 5.0f);
        this.tail.setPos(0.0f, 9.0f, 7.0f);

        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = state.walkAnimationSpeed;

        this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662f + (float) Math.PI) * 1.0f * limbSwingAmount;
        this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662f) * 1.0f * limbSwingAmount;
        this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662f) * 1.0f * limbSwingAmount;
        this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662f + (float) Math.PI) * 1.0f * limbSwingAmount;

        this.tail.zRot = Mth.cos(state.animationTicks * 0.15f) * 0.1f;
        this.tail.xRot = 0.35f + Mth.sin(state.animationTicks * 0.1f) * 0.05f;

        this.neck.xRot = 0.2f + Mth.sin(limbSwing * 0.6662f) * 0.1f * limbSwingAmount;
        this.head.xRot = 0.1f - Mth.sin(limbSwing * 0.6662f) * 0.08f * limbSwingAmount;
    }
}
