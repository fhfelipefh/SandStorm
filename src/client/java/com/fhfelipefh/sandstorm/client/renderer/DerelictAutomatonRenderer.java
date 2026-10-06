package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.DerelictAutomatonEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class DerelictAutomatonRenderer extends MobRenderer<DerelictAutomatonEntity, DerelictAutomatonRenderState, DerelictAutomatonModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/derelict_automaton/derelict_automaton.png");

    public DerelictAutomatonRenderer(EntityRendererProvider.Context context) {
        super(context, new DerelictAutomatonModel(DerelictAutomatonModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(DerelictAutomatonRenderState state) {
        return TEXTURE;
    }

    @Override
    public DerelictAutomatonRenderState createRenderState() {
        return new DerelictAutomatonRenderState();
    }

    @Override
    public void extractRenderState(DerelictAutomatonEntity entity, DerelictAutomatonRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isOvercharging = entity.isOvercharging();
        state.overchargeProgress = entity.getOverchargeProgress(partialTick);
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}
