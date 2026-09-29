package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.ScrapSentinelEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class ScrapSentinelRenderer extends MobRenderer<ScrapSentinelEntity, ScrapSentinelRenderState, ScrapSentinelModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/scrap_sentinel/scrap_sentinel_active.png");
    private static final Identifier TEXTURE_DORMANT = SandStormMod.id("textures/entity/scrap_sentinel/scrap_sentinel_dormant.png");

    public ScrapSentinelRenderer(EntityRendererProvider.Context context) {
        super(context, new ScrapSentinelModel(ScrapSentinelModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(ScrapSentinelRenderState state) {
        return state.isDormant ? TEXTURE_DORMANT : TEXTURE_ACTIVE;
    }

    @Override
    public ScrapSentinelRenderState createRenderState() {
        return new ScrapSentinelRenderState();
    }

    @Override
    public void extractRenderState(ScrapSentinelEntity entity, ScrapSentinelRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDormant = entity.isDormant();
        state.attackAnim = entity.getAttackAnim();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
