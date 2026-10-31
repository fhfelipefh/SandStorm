package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.WardenAndroidEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class WardenAndroidRenderer extends MobRenderer<WardenAndroidEntity, WardenAndroidRenderState, WardenAndroidModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/warden_android/warden_android.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/warden_android/warden_android_deactivated.png");

    public WardenAndroidRenderer(EntityRendererProvider.Context context) {
        super(context, new WardenAndroidModel(WardenAndroidModel.createBodyLayer().bakeRoot()), 0.9f);
    }

    @Override
    public Identifier getTextureLocation(WardenAndroidRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE_ACTIVE;
    }

    @Override
    public WardenAndroidRenderState createRenderState() {
        return new WardenAndroidRenderState();
    }

    @Override
    public void extractRenderState(WardenAndroidEntity entity, WardenAndroidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
        state.attackAnim = entity.getAttackAnim();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}

