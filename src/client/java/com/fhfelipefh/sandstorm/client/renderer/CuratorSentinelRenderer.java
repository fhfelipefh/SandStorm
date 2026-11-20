package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.CuratorSentinelEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class CuratorSentinelRenderer extends MobRenderer<CuratorSentinelEntity, CuratorSentinelRenderState, CuratorSentinelModel> {

    private static final Identifier TEXTURE_NORMAL = SandStormMod.id("textures/entity/curator_sentinel/curator_sentinel.png");
    private static final Identifier TEXTURE_ALERT = SandStormMod.id("textures/entity/curator_sentinel/curator_sentinel_alert.png");

    public CuratorSentinelRenderer(EntityRendererProvider.Context context) {
        super(context, new CuratorSentinelModel(CuratorSentinelModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(CuratorSentinelRenderState state) {
        return state.isAlarmActive ? TEXTURE_ALERT : TEXTURE_NORMAL;
    }

    @Override
    public CuratorSentinelRenderState createRenderState() {
        return new CuratorSentinelRenderState();
    }

    @Override
    public void extractRenderState(CuratorSentinelEntity entity, CuratorSentinelRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isAlarmActive = entity.isAlarmActive();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}
