package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.FrostclawEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class FrostclawRenderer extends MobRenderer<FrostclawEntity, FrostclawRenderState, FrostclawModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/frostclaw/frostclaw.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/frostclaw/frostclaw_deactivated.png");

    public FrostclawRenderer(EntityRendererProvider.Context context) {
        super(context, new FrostclawModel(FrostclawModel.createBodyLayer().bakeRoot()), 1.2f);
    }

    @Override
    public Identifier getTextureLocation(FrostclawRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE_ACTIVE;
    }

    @Override
    public FrostclawRenderState createRenderState() {
        return new FrostclawRenderState();
    }

    @Override
    public void extractRenderState(FrostclawEntity entity, FrostclawRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
        state.isChestSacIntact = entity.isChestSacIntact();
        state.isSlamAttacking = entity.isSlamAttacking();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}

