package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DesalinationFilterMechanismsModel {
    private final ModelPart root;
    private final ModelPart spiralCoil;
    private final ModelPart leftTank;
    private final ModelPart rightTank;
    private final ModelPart funnel;
    private final ModelPart pipes;

    public DesalinationFilterMechanismsModel(ModelPart root) {
        this.root = root;
        this.spiralCoil = root.getChild("spiral_coil");
        this.leftTank = root.getChild("left_tank");
        this.rightTank = root.getChild("right_tank");
        this.funnel = root.getChild("funnel");
        this.pipes = root.getChild("pipes");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("spiral_coil", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.5f, 3.0f, -0.5f, 1.0f, 9.0f, 1.0f)
                .texOffs(0, 0).addBox(-2.0f, 4.0f, -2.0f, 4.0f, 1.0f, 4.0f)
                .texOffs(0, 0).addBox(-2.0f, 6.0f, -2.0f, 4.0f, 1.0f, 4.0f)
                .texOffs(0, 0).addBox(-2.0f, 8.0f, -2.0f, 4.0f, 1.0f, 4.0f)
                .texOffs(0, 0).addBox(-2.0f, 10.0f, -2.0f, 4.0f, 1.0f, 4.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("left_tank", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-5.5f, 2.0f, -3.0f, 4.0f, 5.0f, 6.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("right_tank", CubeListBuilder.create()
                .texOffs(16, 16).addBox(1.5f, 2.0f, -3.0f, 4.0f, 4.0f, 6.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("funnel", CubeListBuilder.create()
                .texOffs(16, 0).addBox(2.0f, 6.0f, -2.0f, 3.0f, 3.0f, 4.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("pipes", CubeListBuilder.create()
                .texOffs(16, 0).addBox(-4.0f, 11.0f, -0.5f, 4.0f, 1.0f, 1.0f)
                .texOffs(16, 0).addBox(-4.0f, 7.0f, -0.5f, 1.0f, 4.0f, 1.0f)
                .texOffs(16, 0).addBox(0.0f, 10.0f, -0.5f, 3.0f, 1.0f, 1.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getSpiralCoil() {
        return this.spiralCoil;
    }

    public ModelPart getLeftTank() {
        return this.leftTank;
    }

    public ModelPart getRightTank() {
        return this.rightTank;
    }

    public ModelPart getFunnel() {
        return this.funnel;
    }

    public ModelPart getPipes() {
        return this.pipes;
    }
}
