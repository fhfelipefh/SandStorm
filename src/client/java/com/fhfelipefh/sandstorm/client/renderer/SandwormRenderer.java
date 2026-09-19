package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.silverfish.SilverfishModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class SandwormRenderer extends MobRenderer<SandwormEntity, SandwormRenderState, SilverfishModel> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/silverfish/silverfish.png");

    public SandwormRenderer(EntityRendererProvider.Context context) {
        super(context, new SilverfishModel(context.bakeLayer(ModelLayers.SILVERFISH)), 1.2f);
    }

    @Override
    public Identifier getTextureLocation(SandwormRenderState state) {
        return TEXTURE;
    }

    @Override
    public SandwormRenderState createRenderState() {
        return new SandwormRenderState();
    }

    @Override
    public void extractRenderState(SandwormEntity entity, SandwormRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.burrowed = entity.getSandwormState() == SandwormState.BURROWED;
    }

    @Override
    protected boolean isBodyVisible(SandwormRenderState state) {
        return !state.burrowed && super.isBodyVisible(state);
    }

    @Override
    protected void scale(SandwormRenderState state, PoseStack poseStack) {
        super.scale(state, poseStack);
        poseStack.scale(3.5f, 3.5f, 3.5f);
    }
}
