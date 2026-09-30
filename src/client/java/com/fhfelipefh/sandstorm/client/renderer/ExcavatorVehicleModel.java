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
                .texOffs(0, 0).addBox(-10.0f, -10.0f, -12.0f, 20.0f, 8.0f, 24.0f)
                .texOffs(0, 34).addBox(-7.0f, -18.0f, -6.0f, 14.0f, 8.0f, 14.0f)
                .texOffs(58, 34).addBox(-4.0f, -12.0f, -2.0f, 8.0f, 2.0f, 6.0f)
                .texOffs(0, 58).addBox(-8.0f, -14.0f, -16.0f, 16.0f, 8.0f, 4.0f)
                .texOffs(0, 72).addBox(-14.0f, -4.0f, -14.0f, 4.0f, 6.0f, 28.0f)
                .texOffs(0, 72).addBox(10.0f, -4.0f, -14.0f, 4.0f, 6.0f, 28.0f)
                .texOffs(42, 58).addBox(-3.0f, -8.0f, 12.0f, 6.0f, 5.0f, 8.0f),
                PartPose.offset(0.0f, 22.0f, 0.0f));

        chassis.addOrReplaceChild("drillHead", CubeListBuilder.create()
                .texOffs(66, 72).addBox(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 8.0f)
                .texOffs(100, 72).addBox(-2.0f, -2.0f, 8.0f, 4.0f, 4.0f, 4.0f),
                PartPose.offset(0.0f, -5.5f, 20.0f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(ExcavatorVehicleRenderState state) {
        super.setupAnim(state);
        this.drillHead.zRot = state.animationTicks * 0.4f;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
