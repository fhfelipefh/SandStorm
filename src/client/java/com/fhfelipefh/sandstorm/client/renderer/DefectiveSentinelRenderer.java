package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.DefectiveSentinelEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class DefectiveSentinelRenderer extends MobRenderer<DefectiveSentinelEntity, DefectiveSentinelRenderState, DefectiveSentinelModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/defective_sentinel/defective_sentinel.png");

    public DefectiveSentinelRenderer(EntityRendererProvider.Context context) {
        super(context, new DefectiveSentinelModel(DefectiveSentinelModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(DefectiveSentinelRenderState state) {
        return TEXTURE;
    }

    @Override
    public DefectiveSentinelRenderState createRenderState() {
        return new DefectiveSentinelRenderState();
    }

    @Override
    public void extractRenderState(DefectiveSentinelEntity entity, DefectiveSentinelRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isCharging = entity.isCharging();
        state.twitchTicks = entity.getTwitchTicks();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}
