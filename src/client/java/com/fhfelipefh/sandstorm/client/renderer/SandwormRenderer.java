package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormState;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class SandwormRenderer extends MobRenderer<SandwormEntity, SandwormRenderState, SandwormModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/sandworm/sandworm.png");

    public SandwormRenderer(EntityRendererProvider.Context context) {
        super(context, new SandwormModel(SandwormModel.createBodyLayer().bakeRoot()), 3.0f);
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
        SandwormState wormState = entity.getSandwormState();
        state.burrowed = wormState == SandwormState.BURROWED;
        state.breaching = wormState == SandwormState.BREACHING;
        state.surfaced = wormState == SandwormState.SURFACED_ASSAULT;
        state.submerging = wormState == SandwormState.SUBMERGING;
        state.breachProgress = entity.getBreachAnimationProgress(partialTick);
        state.biteProgress = entity.getBiteAnimationProgress(partialTick);
        state.bodyPitch = entity.getXRot();
        state.bodyYaw = entity.getYRot();
    }

    @Override
    protected boolean isBodyVisible(SandwormRenderState state) {
        return super.isBodyVisible(state);
    }

    @Override
    protected void scale(SandwormRenderState state, PoseStack poseStack) {
        super.scale(state, poseStack);
        float scaleFactor = 7.5f;
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
    }
}
