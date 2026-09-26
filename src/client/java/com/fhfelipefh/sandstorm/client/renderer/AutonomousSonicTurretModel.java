package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AutonomousSonicTurretModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart barrelLeft;
    private final ModelPart barrelRight;
    private final ModelPart sensorDome;

    public AutonomousSonicTurretModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.barrelLeft = this.head.getChild("barrel_left");
        this.barrelRight = this.head.getChild("barrel_right");
        this.sensorDome = this.head.getChild("sensor_dome");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0f, 0.0f, -6.0f, 10.0f, 7.0f, 12.0f),
                PartPose.ZERO);

        head.addOrReplaceChild("barrel_left", CubeListBuilder.create()
                .texOffs(0, 20).addBox(-1.5f, -2.0f, -4.0f, 3.0f, 4.0f, 4.0f),
                PartPose.offset(-2.5f, 3.5f, -6.0f));

        head.addOrReplaceChild("barrel_right", CubeListBuilder.create()
                .texOffs(16, 20).addBox(-1.5f, -2.0f, -4.0f, 3.0f, 4.0f, 4.0f),
                PartPose.offset(2.5f, 3.5f, -6.0f));

        head.addOrReplaceChild("sensor_dome", CubeListBuilder.create()
                .texOffs(0, 30).addBox(-2.0f, 0.0f, -3.0f, 4.0f, 2.0f, 6.0f),
                PartPose.offset(0.0f, 7.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getHead() {
        return this.head;
    }

    public ModelPart getBarrelLeft() {
        return this.barrelLeft;
    }

    public ModelPart getBarrelRight() {
        return this.barrelRight;
    }

    public ModelPart getSensorDome() {
        return this.sensorDome;
    }
}
