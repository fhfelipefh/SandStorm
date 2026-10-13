package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RappelAnchorModel {
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart spool;

    public RappelAnchorModel(ModelPart root) {
        this.root = root;
        this.base = root.getChild("base");
        this.spool = root.getChild("spool");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.0f, 0.0f, -6.0f, 12.0f, 2.0f, 12.0f)
                .texOffs(0, 14).addBox(-5.0f, 2.0f, -2.0f, 2.0f, 5.0f, 4.0f)
                .texOffs(12, 14).addBox(3.0f, 2.0f, -2.0f, 2.0f, 5.0f, 4.0f)
                .texOffs(0, 23).addBox(-2.0f, 1.0f, -5.5f, 4.0f, 2.0f, 2.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("spool", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-3.0f, -2.0f, -2.0f, 6.0f, 4.0f, 4.0f)
                .texOffs(20, 14).addBox(-3.5f, -2.5f, -2.5f, 1.0f, 5.0f, 5.0f)
                .texOffs(20, 14).addBox(2.5f, -2.5f, -2.5f, 1.0f, 5.0f, 5.0f),
                PartPose.offset(0.0f, 4.5f, 0.0f));

        return LayerDefinition.create(mesh, 32, 32);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getBase() {
        return this.base;
    }

    public ModelPart getSpool() {
        return this.spool;
    }
}
