package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SandstoneCrateModel {

    private final ModelPart root;
    private final ModelPart lid;
    private final ModelPart lock;

    public SandstoneCrateModel(ModelPart root) {
        this.root = root;
        this.lid = root.getChild("lid");
        this.lock = root.getChild("lock");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("bottom", CubeListBuilder.create()
                .texOffs(0, 19)
                .addBox(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f), PartPose.ZERO);

        root.addOrReplaceChild("lid", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f), PartPose.offset(0.0f, 9.0f, 1.0f));

        root.addOrReplaceChild("lock", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(7.0f, -2.0f, 14.01f, 2.0f, 4.0f, 1.0f), PartPose.offset(0.0f, 9.0f, 1.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    public void setupAnim(float openness) {
        float rot = -(openness * 1.5707964f);
        this.lid.xRot = rot;
        this.lock.xRot = rot;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
