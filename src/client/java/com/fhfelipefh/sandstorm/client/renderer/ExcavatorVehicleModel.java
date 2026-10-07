package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ExcavatorVehicleModel extends EntityModel<ExcavatorVehicleRenderState> {
    private final ModelPart root;
    private final ModelPart chassis;
    private final ModelPart drillHead;

    public ExcavatorVehicleModel(ModelPart root) {
        super(root);
        this.root = root;
        this.chassis = root.getChild("chassis");
        this.drillHead = this.chassis.getChild("drillHead");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition chassis = root.addOrReplaceChild("chassis", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-12.0f, -8.0f, -14.0f, 24.0f, 6.0f, 28.0f)
                .texOffs(106, 0).addBox(-11.0f, -6.0f, 14.0f, 22.0f, 5.0f, 3.0f)
                .texOffs(106, 10).addBox(-8.0f, -12.0f, -15.0f, 16.0f, 8.0f, 2.0f)
                .texOffs(144, 10).addBox(-9.0f, -18.0f, -14.0f, 2.0f, 10.0f, 2.0f)
                .texOffs(144, 10).addBox(7.0f, -18.0f, -14.0f, 2.0f, 10.0f, 2.0f)
                .texOffs(0, 36).addBox(-17.0f, -4.0f, -16.0f, 5.0f, 7.0f, 32.0f)
                .texOffs(0, 36).addBox(12.0f, -4.0f, -16.0f, 5.0f, 7.0f, 32.0f)
                .texOffs(76, 36).addBox(-18.0f, -6.0f, -16.0f, 6.0f, 2.0f, 32.0f)
                .texOffs(76, 36).addBox(12.0f, -6.0f, -16.0f, 6.0f, 2.0f, 32.0f)
                .texOffs(0, 77).addBox(-8.0f, -18.0f, -8.0f, 16.0f, 10.0f, 16.0f)
                .texOffs(66, 77).addBox(-9.0f, -20.0f, -9.0f, 18.0f, 2.0f, 18.0f)
                .texOffs(0, 105).addBox(-4.0f, -12.0f, -4.0f, 8.0f, 3.0f, 8.0f)
                .texOffs(34, 105).addBox(-4.0f, -18.0f, -4.0f, 8.0f, 6.0f, 2.0f)
                .texOffs(56, 105).addBox(-7.0f, -23.0f, 7.0f, 4.0f, 3.0f, 3.0f)
                .texOffs(56, 105).addBox(3.0f, -23.0f, 7.0f, 4.0f, 3.0f, 3.0f)
                .texOffs(72, 105).addBox(-4.0f, -9.0f, 14.0f, 8.0f, 6.0f, 6.0f)
                .texOffs(102, 105).addBox(-3.0f, -8.0f, 20.0f, 6.0f, 5.0f, 10.0f)
                .texOffs(136, 105).addBox(-5.0f, -7.0f, 16.0f, 2.0f, 3.0f, 10.0f)
                .texOffs(136, 105).addBox(3.0f, -7.0f, 16.0f, 2.0f, 3.0f, 10.0f),
                PartPose.offsetAndRotation(0.0f, 22.0f, 0.0f, 0.0f, (float) Math.PI, 0.0f));

        chassis.addOrReplaceChild("drillHead", CubeListBuilder.create()
                .texOffs(0, 122).addBox(-5.0f, -5.0f, 0.0f, 10.0f, 10.0f, 8.0f)
                .texOffs(38, 122).addBox(-3.5f, -3.5f, 8.0f, 7.0f, 7.0f, 6.0f)
                .texOffs(66, 122).addBox(-2.0f, -2.0f, 14.0f, 4.0f, 4.0f, 6.0f)
                .texOffs(88, 122).addBox(-1.0f, -7.0f, 2.0f, 2.0f, 2.0f, 4.0f)
                .texOffs(88, 122).addBox(-1.0f, 5.0f, 2.0f, 2.0f, 2.0f, 4.0f)
                .texOffs(88, 122).addBox(-7.0f, -1.0f, 4.0f, 2.0f, 2.0f, 4.0f)
                .texOffs(88, 122).addBox(5.0f, -1.0f, 4.0f, 2.0f, 2.0f, 4.0f),
                PartPose.offset(0.0f, -5.5f, 30.0f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(ExcavatorVehicleRenderState state) {
        super.setupAnim(state);
        float spinRate = state.isVehicle ? 0.85f : 0.25f;
        this.drillHead.zRot = state.animationTicks * spinRate;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
