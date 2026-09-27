package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class BuilderDroneModel extends EntityModel<BuilderDroneRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rotor1;
    private final ModelPart rotor2;
    private final ModelPart rotor3;
    private final ModelPart rotor4;

    public BuilderDroneModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.rotor1 = this.body.getChild("rotor1");
        this.rotor2 = this.body.getChild("rotor2");
        this.rotor3 = this.body.getChild("rotor3");
        this.rotor4 = this.body.getChild("rotor4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0f, -2.0f, -3.0f, 6.0f, 4.0f, 6.0f)
                .texOffs(0, 10).addBox(-1.5f, 2.0f, -1.5f, 3.0f, 2.0f, 3.0f)
                .texOffs(0, 15).addBox(3.0f, -1.0f, 3.0f, 3.0f, 1.0f, 1.0f)
                .texOffs(0, 15).addBox(-6.0f, -1.0f, 3.0f, 3.0f, 1.0f, 1.0f)
                .texOffs(0, 15).addBox(3.0f, -1.0f, -4.0f, 3.0f, 1.0f, 1.0f)
                .texOffs(0, 15).addBox(-6.0f, -1.0f, -4.0f, 3.0f, 1.0f, 1.0f),
                PartPose.offset(0.0f, 18.0f, 0.0f));

        body.addOrReplaceChild("rotor1", CubeListBuilder.create()
                .texOffs(16, 10).addBox(-3.0f, -0.5f, -0.5f, 6.0f, 1.0f, 1.0f),
                PartPose.offset(4.5f, -1.5f, 3.5f));

        body.addOrReplaceChild("rotor2", CubeListBuilder.create()
                .texOffs(16, 10).addBox(-3.0f, -0.5f, -0.5f, 6.0f, 1.0f, 1.0f),
                PartPose.offset(-4.5f, -1.5f, 3.5f));

        body.addOrReplaceChild("rotor3", CubeListBuilder.create()
                .texOffs(16, 10).addBox(-3.0f, -0.5f, -0.5f, 6.0f, 1.0f, 1.0f),
                PartPose.offset(4.5f, -1.5f, -3.5f));

        body.addOrReplaceChild("rotor4", CubeListBuilder.create()
                .texOffs(16, 10).addBox(-3.0f, -0.5f, -0.5f, 6.0f, 1.0f, 1.0f),
                PartPose.offset(-4.5f, -1.5f, -3.5f));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(BuilderDroneRenderState state) {
        super.setupAnim(state);
        float spin = state.animationTicks * 1.8f;
        this.rotor1.yRot = spin;
        this.rotor2.yRot = -spin;
        this.rotor3.yRot = -spin;
        this.rotor4.yRot = spin;
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
