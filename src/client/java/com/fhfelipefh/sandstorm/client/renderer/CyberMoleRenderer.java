package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.CyberMoleEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class CyberMoleRenderer extends MobRenderer<CyberMoleEntity, CyberMoleRenderState, CyberMoleModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/cyber_mole/cyber_mole.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/cyber_mole/cyber_mole_deactivated.png");

    public CyberMoleRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberMoleModel(CyberMoleModel.createBodyLayer().bakeRoot()), 0.45f);
    }

    @Override
    public Identifier getTextureLocation(CyberMoleRenderState state) {
        if (state.isDeactivated) {
            return TEXTURE_DEACTIVATED;
        }
        return TEXTURE_ACTIVE;
    }

    @Override
    public CyberMoleRenderState createRenderState() {
        return new CyberMoleRenderState();
    }

    @Override
    public void extractRenderState(CyberMoleEntity entity, CyberMoleRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isTame = entity.isTame();
        state.isSitting = entity.isOrderedToSit();
        state.isDeactivated = entity.isDeactivated();
        state.isBurrowed = entity.isBurrowed();
        state.drillRotation = entity.getDrillRotation();
        Level level = entity.level();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }
}

