package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.GrazerEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class GrazerRenderer extends MobRenderer<GrazerEntity, GrazerRenderState, GrazerModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/grazer/grazer.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/grazer/grazer_deactivated.png");

    public GrazerRenderer(EntityRendererProvider.Context context) {
        super(context, new GrazerModel(GrazerModel.createBodyLayer().bakeRoot()), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(GrazerRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE_ACTIVE;
    }

    @Override
    public GrazerRenderState createRenderState() {
        return new GrazerRenderState();
    }

    @Override
    public void extractRenderState(GrazerEntity entity, GrazerRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
        state.areCanistersIntact = entity.areCanistersIntact();
        state.rotaryTicks = entity.getRotaryTicks();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}

