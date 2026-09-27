package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Printer3DMechanismsModel {
    private final ModelPart root;
    private final ModelPart zRods;
    private final ModelPart gantryRail;
    private final ModelPart toolhead;

    public Printer3DMechanismsModel(ModelPart root) {
        this.root = root;
        this.zRods = root.getChild("z_rods");
        this.gantryRail = root.getChild("gantry_rail");
        this.toolhead = root.getChild("toolhead");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("z_rods", CubeListBuilder.create()
                .texOffs(0, 0).addBox(3.0f, 2.0f, 12.0f, 1.0f, 12.0f, 1.0f)
                .texOffs(0, 0).addBox(12.0f, 2.0f, 12.0f, 1.0f, 12.0f, 1.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("gantry_rail", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.5f, -0.5f, -0.5f, 11.0f, 1.0f, 1.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("toolhead", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5f, -1.5f, -1.5f, 3.0f, 3.0f, 3.0f)
                .texOffs(0, 7).addBox(-1.0f, 1.5f, -1.0f, 2.0f, 1.0f, 2.0f)
                .texOffs(8, 7).addBox(-0.5f, 2.5f, -0.5f, 1.0f, 1.0f, 1.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getZRods() {
        return this.zRods;
    }

    public ModelPart getGantryRail() {
        return this.gantryRail;
    }

    public ModelPart getToolhead() {
        return this.toolhead;
    }
}
