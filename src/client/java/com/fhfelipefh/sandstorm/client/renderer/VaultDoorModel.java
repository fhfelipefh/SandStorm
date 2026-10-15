package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class VaultDoorModel {
    private final ModelPart root;
    private final ModelPart frame;
    private final ModelPart doorBody;
    private final ModelPart lockingBars;
    private final ModelPart wheel;

    public VaultDoorModel(ModelPart root) {
        this.root = root;
        this.frame = root.getChild("frame");
        this.doorBody = root.getChild("door_body");
        this.lockingBars = root.getChild("locking_bars");
        this.wheel = root.getChild("wheel");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("frame", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-24.0f, 0.0f, -4.0f, 48.0f, 4.0f, 8.0f)
                .texOffs(0, 12).addBox(-24.0f, 44.0f, -4.0f, 48.0f, 4.0f, 8.0f)
                .texOffs(0, 24).addBox(-24.0f, 4.0f, -4.0f, 6.0f, 40.0f, 8.0f)
                .texOffs(28, 24).addBox(18.0f, 4.0f, -4.0f, 6.0f, 40.0f, 8.0f)
                .texOffs(56, 0).addBox(16.0f, 34.0f, -6.0f, 7.0f, 8.0f, 12.0f)
                .texOffs(56, 20).addBox(16.0f, 6.0f, -6.0f, 7.0f, 8.0f, 12.0f)
                .texOffs(56, 40).addBox(17.5f, 4.0f, -2.0f, 4.0f, 40.0f, 4.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("door_body", CubeListBuilder.create()
                .texOffs(50, 0).addBox(-3.0f, -10.0f, -3.0f, 5.0f, 20.0f, 6.0f)
                .texOffs(0, 72).addBox(-37.0f, -20.0f, -3.0f, 34.0f, 40.0f, 6.0f)
                .texOffs(0, 72).addBox(-35.0f, -18.0f, -3.3f, 30.0f, 36.0f, 0.3f)
                .texOffs(0, 76).addBox(-33.0f, -16.0f, -3.6f, 26.0f, 32.0f, 0.3f)
                .texOffs(0, 80).addBox(-27.0f, -8.0f, -4.0f, 16.0f, 16.0f, 0.4f),
                PartPose.ZERO);

        root.addOrReplaceChild("locking_bars", CubeListBuilder.create()
                .texOffs(98, 0).addBox(-1.5f, 13.0f, -1.0f, 3.0f, 6.0f, 2.0f)
                .texOffs(98, 0).addBox(-1.5f, -19.0f, -1.0f, 3.0f, 6.0f, 2.0f)
                .texOffs(98, 8).addBox(-19.0f, -1.5f, -1.0f, 6.0f, 3.0f, 2.0f)
                .texOffs(98, 8).addBox(13.0f, -1.5f, -1.0f, 6.0f, 3.0f, 2.0f)
                .texOffs(110, 0).addBox(-14.0f, 10.0f, -1.0f, 4.0f, 4.0f, 2.0f)
                .texOffs(110, 0).addBox(10.0f, 10.0f, -1.0f, 4.0f, 4.0f, 2.0f)
                .texOffs(110, 0).addBox(-14.0f, -14.0f, -1.0f, 4.0f, 4.0f, 2.0f)
                .texOffs(110, 0).addBox(10.0f, -14.0f, -1.0f, 4.0f, 4.0f, 2.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("wheel", CubeListBuilder.create()
                .texOffs(96, 18).addBox(-3.0f, -3.0f, -2.0f, 6.0f, 6.0f, 2.0f)
                .texOffs(96, 26).addBox(-5.0f, 4.0f, -2.5f, 10.0f, 2.0f, 1.0f)
                .texOffs(96, 26).addBox(-5.0f, -6.0f, -2.5f, 10.0f, 2.0f, 1.0f)
                .texOffs(96, 29).addBox(-6.0f, -5.0f, -2.5f, 2.0f, 10.0f, 1.0f)
                .texOffs(96, 29).addBox(4.0f, -5.0f, -2.5f, 2.0f, 10.0f, 1.0f)
                .texOffs(102, 40).addBox(-1.0f, -8.0f, -2.2f, 2.0f, 16.0f, 1.0f)
                .texOffs(96, 57).addBox(-8.0f, -1.0f, -2.2f, 16.0f, 2.0f, 1.0f)
                .texOffs(116, 18).addBox(-1.5f, 7.0f, -3.5f, 3.0f, 3.0f, 3.0f)
                .texOffs(116, 18).addBox(-1.5f, -10.0f, -3.5f, 3.0f, 3.0f, 3.0f)
                .texOffs(116, 18).addBox(-10.0f, -1.5f, -3.5f, 3.0f, 3.0f, 3.0f)
                .texOffs(116, 18).addBox(7.0f, -1.5f, -3.5f, 3.0f, 3.0f, 3.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getFrame() {
        return this.frame;
    }

    public ModelPart getDoorBody() {
        return this.doorBody;
    }

    public ModelPart getLockingBars() {
        return this.lockingBars;
    }

    public ModelPart getWheel() {
        return this.wheel;
    }
}
