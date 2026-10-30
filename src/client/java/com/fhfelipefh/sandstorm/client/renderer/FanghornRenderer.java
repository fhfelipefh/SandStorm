package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.FanghornEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class FanghornRenderer extends MobRenderer<FanghornEntity, FanghornRenderState, FanghornModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/fanghorn/fanghorn.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/fanghorn/fanghorn_deactivated.png");

    public FanghornRenderer(EntityRendererProvider.Context context) {
        super(context, new FanghornModel(FanghornModel.createBodyLayer().bakeRoot()), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(FanghornRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE_ACTIVE;
    }

    @Override
    public FanghornRenderState createRenderState() {
        return new FanghornRenderState();
    }

    @Override
    public void extractRenderState(FanghornEntity entity, FanghornRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
        state.isChargingHorns = entity.isChargingHorns();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}

