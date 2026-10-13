package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.CyberHoundEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class CyberHoundRenderer extends MobRenderer<CyberHoundEntity, CyberHoundRenderState, CyberHoundModel> {
    private static final Identifier TEXTURE_WILD = SandStormMod.id("textures/entity/cyber_hound/cyber_hound_wild.png");
    private static final Identifier TEXTURE_TAMED = SandStormMod.id("textures/entity/cyber_hound/cyber_hound_tamed.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/cyber_hound/cyber_hound_deactivated.png");

    public CyberHoundRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberHoundModel(CyberHoundModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(CyberHoundRenderState state) {
        if (state.isDeactivated) {
            return TEXTURE_DEACTIVATED;
        }
        return state.isTame ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    public CyberHoundRenderState createRenderState() {
        return new CyberHoundRenderState();
    }

    @Override
    public void extractRenderState(CyberHoundEntity entity, CyberHoundRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isTame = entity.isTame();
        state.isSitting = entity.isOrderedToSit();
        state.isDeactivated = entity.isDeactivated();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}
