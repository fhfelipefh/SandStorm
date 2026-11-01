package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.SeismicScorpionEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class SeismicScorpionRenderer extends MobRenderer<SeismicScorpionEntity, SeismicScorpionRenderState, SeismicScorpionModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/seismic_scorpion/seismic_scorpion.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/seismic_scorpion/seismic_scorpion_deactivated.png");

    public SeismicScorpionRenderer(EntityRendererProvider.Context context) {
        super(context, new SeismicScorpionModel(SeismicScorpionModel.createBodyLayer().bakeRoot()), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(SeismicScorpionRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE;
    }

    @Override
    public SeismicScorpionRenderState createRenderState() {
        return new SeismicScorpionRenderState();
    }

    @Override
    public void extractRenderState(SeismicScorpionEntity entity, SeismicScorpionRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
        state.stingProgress = entity.getStingAnimationProgress(partialTick);
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}
