package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.ScavengerVultureEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class ScavengerVultureRenderer extends MobRenderer<ScavengerVultureEntity, ScavengerVultureRenderState, ScavengerVultureModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/scavenger_vulture/scavenger_vulture.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/scavenger_vulture/scavenger_vulture_deactivated.png");

    public ScavengerVultureRenderer(EntityRendererProvider.Context context) {
        super(context, new ScavengerVultureModel(ScavengerVultureModel.createBodyLayer().bakeRoot()), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(ScavengerVultureRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE_ACTIVE;
    }

    @Override
    public ScavengerVultureRenderState createRenderState() {
        return new ScavengerVultureRenderState();
    }

    @Override
    public void extractRenderState(ScavengerVultureEntity entity, ScavengerVultureRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
        state.vultureState = entity.getVultureState();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}

