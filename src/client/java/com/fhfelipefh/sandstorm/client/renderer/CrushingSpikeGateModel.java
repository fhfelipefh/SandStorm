package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CrushingSpikeGateModel {
    private final ModelPart root;

    public CrushingSpikeGateModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createSpikesLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("crossbar_middle", CubeListBuilder.create()
                .texOffs(0, 6).addBox(3.0f, 7.0f, 7.0f, 10.0f, 2.0f, 2.0f), PartPose.ZERO);

        root.addOrReplaceChild("hydraulic_rod_left", CubeListBuilder.create()
                .texOffs(14, 0).addBox(5.0f, 9.0f, 7.5f, 1.0f, 3.0f, 1.0f), PartPose.ZERO);

        root.addOrReplaceChild("hydraulic_rod_right", CubeListBuilder.create()
                .texOffs(14, 0).addBox(10.0f, 9.0f, 7.5f, 1.0f, 3.0f, 1.0f), PartPose.ZERO);

        root.addOrReplaceChild("spike_bar_left", CubeListBuilder.create()
                .texOffs(10, 0).addBox(4.0f, 2.0f, 7.0f, 2.0f, 7.0f, 2.0f), PartPose.ZERO);

        root.addOrReplaceChild("spike_tip_left", CubeListBuilder.create()
                .texOffs(10, 2).addBox(4.5f, 0.0f, 7.5f, 1.0f, 2.0f, 1.0f), PartPose.ZERO);

        root.addOrReplaceChild("spike_bar_center", CubeListBuilder.create()
                .texOffs(10, 0).addBox(7.0f, 2.0f, 7.0f, 2.0f, 7.0f, 2.0f), PartPose.ZERO);

        root.addOrReplaceChild("spike_tip_center", CubeListBuilder.create()
                .texOffs(10, 2).addBox(7.5f, 0.0f, 7.5f, 1.0f, 2.0f, 1.0f), PartPose.ZERO);

        root.addOrReplaceChild("spike_bar_right", CubeListBuilder.create()
                .texOffs(10, 0).addBox(10.0f, 2.0f, 7.0f, 2.0f, 7.0f, 2.0f), PartPose.ZERO);

        root.addOrReplaceChild("spike_tip_right", CubeListBuilder.create()
                .texOffs(10, 2).addBox(10.5f, 0.0f, 7.5f, 1.0f, 2.0f, 1.0f), PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
