package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RappelHarnessModel extends EntityModel<RappelHarnessRenderState> {
    private final ModelPart root;
    private final ModelPart clamp;
    private final ModelPart rollerLeft;
    private final ModelPart rollerRight;

    public RappelHarnessModel(ModelPart root) {
        super(root);
        this.root = root;
        this.clamp = root.getChild("clamp");
        this.rollerLeft = this.clamp.getChild("roller_left");
        this.rollerRight = this.clamp.getChild("roller_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition clamp = root.addOrReplaceChild("clamp", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f)
                .texOffs(0, 14).addBox(-2.0f, 1.0f, 3.0f, 4.0f, 2.0f, 3.0f),
                PartPose.offset(0.0f, 18.0f, 0.0f));

        clamp.addOrReplaceChild("roller_left", CubeListBuilder.create()
                .texOffs(0, 20).addBox(-1.5f, -2.0f, -1.5f, 2.0f, 4.0f, 3.0f),
                PartPose.offset(-3.0f, 0.0f, 0.0f));

        clamp.addOrReplaceChild("roller_right", CubeListBuilder.create()
                .texOffs(12, 20).addBox(-0.5f, -2.0f, -1.5f, 2.0f, 4.0f, 3.0f),
                PartPose.offset(3.0f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(RappelHarnessRenderState state) {
        super.setupAnim(state);
        if (state.isAscending) {
            float rot = state.animationTicks * 0.8f;
            this.rollerLeft.xRot = rot;
            this.rollerRight.xRot = -rot;
        } else if (state.isDescending) {
            float rot = -state.animationTicks * 0.8f;
            this.rollerLeft.xRot = rot;
            this.rollerRight.xRot = -rot;
        } else {
            this.rollerLeft.xRot = 0.0f;
            this.rollerRight.xRot = 0.0f;
        }
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
