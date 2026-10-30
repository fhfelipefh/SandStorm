package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.GlinthawkEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class GlinthawkRenderer extends MobRenderer<GlinthawkEntity, GlinthawkRenderState, GlinthawkModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/glinthawk/glinthawk.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/glinthawk/glinthawk_deactivated.png");

    public GlinthawkRenderer(EntityRendererProvider.Context context) {
        super(context, new GlinthawkModel(GlinthawkModel.createBodyLayer().bakeRoot()), 0.6f);
    }

    @Override
    public Identifier getTextureLocation(GlinthawkRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE_ACTIVE;
    }

    @Override
    public GlinthawkRenderState createRenderState() {
        return new GlinthawkRenderState();
    }

    @Override
    public void extractRenderState(GlinthawkEntity entity, GlinthawkRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}

