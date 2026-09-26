package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CargoDroneModel extends EntityModel<CargoDroneRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rotor1;
    private final ModelPart rotor2;
    private final ModelPart rotor3;
    private final ModelPart rotor4;
    private final ModelPart cargoClaw;

    public CargoDroneModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.rotor1 = this.body.getChild("rotor1");
        this.rotor2 = this.body.getChild("rotor2");
        this.rotor3 = this.body.getChild("rotor3");
        this.rotor4 = this.body.getChild("rotor4");
        this.cargoClaw = this.body.getChild("cargoClaw");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -2.0f, -4.0f, 8.0f, 4.0f, 8.0f)
                .texOffs(0, 12).addBox(-2.0f, 2.0f, -2.0f, 4.0f, 2.0f, 4.0f)
                .texOffs(0, 18).addBox(4.0f, -1.0f, 4.0f, 3.0f, 1.0f, 1.0f)
                .texOffs(0, 18).addBox(-7.0f, -1.0f, 4.0f, 3.0f, 1.0f, 1.0f)
                .texOffs(0, 18).addBox(4.0f, -1.0f, -5.0f, 3.0f, 1.0f, 1.0f)
                .texOffs(0, 18).addBox(-7.0f, -1.0f, -5.0f, 3.0f, 1.0f, 1.0f),
                PartPose.offset(0.0f, 18.0f, 0.0f));

        body.addOrReplaceChild("rotor1", CubeListBuilder.create()
                .texOffs(16, 12).addBox(-3.5f, -0.5f, -0.5f, 7.0f, 1.0f, 1.0f),
                PartPose.offset(5.5f, -1.5f, 4.5f));

        body.addOrReplaceChild("rotor2", CubeListBuilder.create()
                .texOffs(16, 12).addBox(-3.5f, -0.5f, -0.5f, 7.0f, 1.0f, 1.0f),
                PartPose.offset(-5.5f, -1.5f, 4.5f));

        body.addOrReplaceChild("rotor3", CubeListBuilder.create()
                .texOffs(16, 12).addBox(-3.5f, -0.5f, -0.5f, 7.0f, 1.0f, 1.0f),
                PartPose.offset(5.5f, -1.5f, -4.5f));

        body.addOrReplaceChild("rotor4", CubeListBuilder.create()
                .texOffs(16, 12).addBox(-3.5f, -0.5f, -0.5f, 7.0f, 1.0f, 1.0f),
                PartPose.offset(-5.5f, -1.5f, -4.5f));

        body.addOrReplaceChild("cargoClaw", CubeListBuilder.create()
                .texOffs(0, 20).addBox(-2.5f, 4.0f, -2.5f, 5.0f, 2.0f, 5.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(CargoDroneRenderState state) {
        super.setupAnim(state);
        float spin = state.animationTicks * 2.0f;
        this.rotor1.yRot = spin;
        this.rotor2.yRot = -spin;
        this.rotor3.yRot = -spin;
        this.rotor4.yRot = spin;
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getCargoClaw() {
        return this.cargoClaw;
    }
}
