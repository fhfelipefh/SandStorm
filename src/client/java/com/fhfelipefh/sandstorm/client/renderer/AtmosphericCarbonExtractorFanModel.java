package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AtmosphericCarbonExtractorFanModel {
    private final ModelPart root;
    private final ModelPart rotor;

    public AtmosphericCarbonExtractorFanModel(ModelPart root) {
        this.root = root;
        this.rotor = root.getChild("rotor");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition rotor = root.addOrReplaceChild("rotor", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 1.0f, 3.0f)
                .texOffs(0, 8).addBox(-1.0f, 0.4f, -1.0f, 2.0f, 0.8f, 2.0f),
                PartPose.ZERO);

        for (int i = 0; i < 6; i++) {
            float yRot = (float) (i * Math.PI / 3.0);
            rotor.addOrReplaceChild("blade_" + i, CubeListBuilder.create()
                    .texOffs(0, 16).addBox(-0.8f, -0.2f, 1.4f, 1.6f, 0.4f, 4.0f),
                    PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.35f, yRot, 0.0f));
        }

        return LayerDefinition.create(mesh, 32, 32);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getRotor() {
        return this.rotor;
    }
}
