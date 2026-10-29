package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.CyberSteedEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class CyberSteedRenderer extends MobRenderer<CyberSteedEntity, CyberSteedRenderState, CyberSteedModel> {
    private static final Identifier TEXTURE_WILD = SandStormMod.id("textures/entity/cyber_steed/cyber_steed_wild.png");
    private static final Identifier TEXTURE_TAMED = SandStormMod.id("textures/entity/cyber_steed/cyber_steed_tamed.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/cyber_steed/cyber_steed_deactivated.png");

    public CyberSteedRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberSteedModel(CyberSteedModel.createBodyLayer().bakeRoot()), 0.75f);
    }

    @Override
    public Identifier getTextureLocation(CyberSteedRenderState state) {
        if (state.isDeactivated) {
            return TEXTURE_DEACTIVATED;
        }
        return state.isTame ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    public CyberSteedRenderState createRenderState() {
        return new CyberSteedRenderState();
    }

    @Override
    public void extractRenderState(CyberSteedEntity entity, CyberSteedRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isTame = entity.isTame();
        state.isSitting = entity.isOrderedToSit();
        state.isDeactivated = entity.isDeactivated();
        state.isCanisterExploded = entity.isCanisterExploded();
        state.hasSonicTurret = entity.hasSonicTurret();
        state.hasBatteryPack = entity.hasBatteryPack();
        state.hasSpeedOverdrive = entity.hasSpeedOverdrive();
        state.hasCargoPanniers = entity.hasCargoPanniers();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}
