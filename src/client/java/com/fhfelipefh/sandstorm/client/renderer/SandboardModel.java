package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class SandboardModel extends EntityModel<SandboardRenderState> {
    private final ModelPart root;
    private final ModelPart board;

    public SandboardModel(ModelPart root) {
        super(root);
        this.root = root;
        this.board = root.getChild("board");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("board", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -0.5f, -10.0f, 8.0f, 1.0f, 20.0f)
                .texOffs(0, 22).addBox(-3.5f, -1.0f, 10.0f, 7.0f, 1.0f, 4.0f)
                .texOffs(24, 22).addBox(-3.5f, -1.0f, -14.0f, 7.0f, 1.0f, 4.0f)
                .texOffs(0, 28).addBox(-3.0f, -1.5f, 3.0f, 6.0f, 1.0f, 2.0f)
                .texOffs(18, 28).addBox(-3.0f, -1.5f, -5.0f, 6.0f, 1.0f, 2.0f)
                .texOffs(0, 32).addBox(-3.5f, 0.5f, -9.0f, 1.0f, 1.0f, 18.0f)
                .texOffs(0, 32).addBox(2.5f, 0.5f, -9.0f, 1.0f, 1.0f, 18.0f)
                .texOffs(36, 28).addBox(-2.0f, -0.5f, -15.0f, 4.0f, 1.0f, 1.0f),
                PartPose.offset(0.0f, 23.5f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(SandboardRenderState state) {
        super.setupAnim(state);
        if (state.isRidden) {
            float hoverBob = Mth.sin(state.animationTicks * 0.2f) * 0.5f;
            this.board.y = 23.0f + hoverBob;
            this.board.zRot = Mth.sin(state.animationTicks * 0.15f) * 0.04f;
        } else {
            this.board.y = 23.5f;
            this.board.zRot = 0.0f;
        }
    }

    public ModelPart getRoot() {
        return this.root;
    }
}
