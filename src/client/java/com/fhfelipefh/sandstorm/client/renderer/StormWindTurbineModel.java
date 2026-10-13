package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class StormWindTurbineModel {
    private final ModelPart root;
    private final ModelPart rotor;

    public StormWindTurbineModel(ModelPart root) {
        this.root = root;
        this.rotor = root.getChild("rotor");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition rotor = root.addOrReplaceChild("rotor", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 4.0f, 4.0f)
                .texOffs(16, 0).addBox(-1.5f, 2.0f, -0.5f, 3.0f, 12.0f, 1.0f)
                .texOffs(24, 0).addBox(-12.0f, -7.0f, -0.5f, 11.0f, 3.0f, 1.0f)
                .texOffs(32, 0).addBox(1.0f, -7.0f, -0.5f, 11.0f, 3.0f, 1.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 32);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getRotor() {
        return this.rotor;
    }
}
