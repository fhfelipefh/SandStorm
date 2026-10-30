package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.WaterwingEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class WaterwingRenderer extends MobRenderer<WaterwingEntity, WaterwingRenderState, WaterwingModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/waterwing/waterwing.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/waterwing/waterwing_deactivated.png");

    public WaterwingRenderer(EntityRendererProvider.Context context) {
        super(context, new WaterwingModel(WaterwingModel.createBodyLayer().bakeRoot()), 0.9f);
    }

    @Override
    public Identifier getTextureLocation(WaterwingRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE_ACTIVE;
    }

    @Override
    public WaterwingRenderState createRenderState() {
        return new WaterwingRenderState();
    }

    @Override
    public void extractRenderState(WaterwingEntity entity, WaterwingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
        state.isDiving = entity.isDiving();
        state.isTame = entity.isTame();
        state.isSitting = entity.isOrderedToSit();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}

