package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.NomadScavengerEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class NomadScavengerRenderer extends MobRenderer<NomadScavengerEntity, NomadScavengerRenderState, NomadScavengerModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/nomad_scavenger/nomad_scavenger.png");

    public NomadScavengerRenderer(EntityRendererProvider.Context context) {
        super(context, new NomadScavengerModel(NomadScavengerModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(NomadScavengerRenderState state) {
        return TEXTURE;
    }

    @Override
    public NomadScavengerRenderState createRenderState() {
        return new NomadScavengerRenderState();
    }

    @Override
    public void extractRenderState(NomadScavengerEntity entity, NomadScavengerRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isFleeing = entity.isFleeing();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
