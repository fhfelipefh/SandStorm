package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class NaniteFabricatorMechanismsModel {
    private final ModelPart root;
    private final ModelPart mount;
    private final ModelPart manipulatorArm;
    private final ModelPart nanobot;

    public NaniteFabricatorMechanismsModel(ModelPart root) {
        this.root = root;
        this.mount = root.getChild("mount");
        this.manipulatorArm = root.getChild("manipulator_arm");
        this.nanobot = root.getChild("nanobot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("mount", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0f, -0.5f, -2.0f, 4.0f, 1.0f, 4.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("manipulator_arm", CubeListBuilder.create()
                .texOffs(16, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f)
                .texOffs(0, 8).addBox(-0.5f, 3.0f, -0.5f, 1.0f, 3.0f, 1.0f)
                .texOffs(16, 8).addBox(-1.5f, 6.0f, -1.5f, 3.0f, 1.0f, 3.0f)
                .texOffs(0, 16).addBox(-0.5f, 7.0f, -0.5f, 1.0f, 1.0f, 1.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("nanobot", CubeListBuilder.create()
                .texOffs(16, 16).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f)
                .texOffs(0, 20).addBox(-1.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f)
                .texOffs(8, 20).addBox(-0.5f, 1.0f, -0.5f, 1.0f, 1.0f, 1.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getMount() {
        return this.mount;
    }

    public ModelPart getManipulatorArm() {
        return this.manipulatorArm;
    }

    public ModelPart getNanobot() {
        return this.nanobot;
    }
}
