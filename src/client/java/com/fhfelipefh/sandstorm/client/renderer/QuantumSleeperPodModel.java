package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class QuantumSleeperPodModel {
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart topCap;
    private final ModelPart spine;
    private final ModelPart cornerStruts;
    private final ModelPart leftDoor;
    private final ModelPart rightDoor;
    private final ModelPart sideGlass;

    public QuantumSleeperPodModel(ModelPart root) {
        this.root = root;
        this.base = root.getChild("base");
        this.topCap = root.getChild("top_cap");
        this.spine = root.getChild("spine");
        this.cornerStruts = root.getChild("corner_struts");
        this.leftDoor = root.getChild("left_door");
        this.rightDoor = root.getChild("right_door");
        this.sideGlass = root.getChild("side_glass");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0f, 0.0f, -8.0f, 16.0f, 2.0f, 16.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("top_cap", CubeListBuilder.create()
                .texOffs(0, 20).addBox(-8.0f, 30.0f, -8.0f, 16.0f, 2.0f, 16.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("corner_struts", CubeListBuilder.create()
                .texOffs(36, 40).addBox(-8.0f, 2.0f, -8.0f, 2.0f, 28.0f, 2.0f)
                .texOffs(36, 40).addBox(6.0f, 2.0f, -8.0f, 2.0f, 28.0f, 2.0f)
                .texOffs(36, 40).addBox(-8.0f, 2.0f, 6.0f, 2.0f, 28.0f, 2.0f)
                .texOffs(36, 40).addBox(6.0f, 2.0f, 6.0f, 2.0f, 28.0f, 2.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("spine", CubeListBuilder.create()
                .texOffs(0, 40).addBox(-6.0f, 2.02f, -7.8f, 12.0f, 27.96f, 1.75f),
                PartPose.ZERO);

        root.addOrReplaceChild("side_glass", CubeListBuilder.create()
                .texOffs(46, 40).addBox(-7.25f, 2.0f, -6.0f, 0.5f, 28.0f, 12.0f)
                .texOffs(46, 40).addBox(6.75f, 2.0f, -6.0f, 0.5f, 28.0f, 12.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("left_door", CubeListBuilder.create()
                .texOffs(74, 40).addBox(-6.0f, 2.0f, 6.5f, 6.0f, 28.0f, 0.5f),
                PartPose.ZERO);

        root.addOrReplaceChild("right_door", CubeListBuilder.create()
                .texOffs(90, 40).addBox(0.0f, 2.0f, 6.5f, 6.0f, 28.0f, 0.5f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getBase() {
        return this.base;
    }

    public ModelPart getTopCap() {
        return this.topCap;
    }

    public ModelPart getSpine() {
        return this.spine;
    }

    public ModelPart getCornerStruts() {
        return this.cornerStruts;
    }

    public ModelPart getLeftDoor() {
        return this.leftDoor;
    }

    public ModelPart getRightDoor() {
        return this.rightDoor;
    }

    public ModelPart getSideGlass() {
        return this.sideGlass;
    }
}
