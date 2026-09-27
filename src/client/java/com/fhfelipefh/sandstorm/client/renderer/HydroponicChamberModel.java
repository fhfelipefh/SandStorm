package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class HydroponicChamberModel {
    private final ModelPart root;
    private final ModelPart nutrientTray;
    private final ModelPart uvLamp;
    private final ModelPart seedling;

    public HydroponicChamberModel(ModelPart root) {
        this.root = root;
        this.nutrientTray = root.getChild("nutrient_tray");
        this.uvLamp = root.getChild("uv_lamp");
        this.seedling = root.getChild("seedling");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("nutrient_tray", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0f, -7.0f, -3.0f, 6.0f, 2.0f, 6.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("uv_lamp", CubeListBuilder.create()
                .texOffs(0, 8).addBox(-2.5f, 4.0f, -2.5f, 5.0f, 2.0f, 5.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("seedling", CubeListBuilder.create()
                .texOffs(0, 15).addBox(-1.5f, -5.0f, -1.5f, 3.0f, 6.0f, 3.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getNutrientTray() {
        return this.nutrientTray;
    }

    public ModelPart getUvLamp() {
        return this.uvLamp;
    }

    public ModelPart getSeedling() {
        return this.seedling;
    }
}
