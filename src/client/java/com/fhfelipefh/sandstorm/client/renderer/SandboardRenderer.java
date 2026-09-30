package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.SandboardEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class SandboardRenderer extends MobRenderer<SandboardEntity, SandboardRenderState, SandboardModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/sandboard/sandboard.png");

    public SandboardRenderer(EntityRendererProvider.Context context) {
        super(context, new SandboardModel(SandboardModel.createBodyLayer().bakeRoot()), 0.35f);
    }

    @Override
    public Identifier getTextureLocation(SandboardRenderState state) {
        return TEXTURE;
    }

    @Override
    public SandboardRenderState createRenderState() {
        return new SandboardRenderState();
    }

    @Override
    public void extractRenderState(SandboardEntity entity, SandboardRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isRidden = entity.isVehicle();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
