package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class MegazordRenderer extends MobRenderer<MegazordEntity, MegazordRenderState, MegazordModel> {
    private static final Identifier TEXTURE_STANDARD = SandStormMod.id("textures/entity/megazord/megazord.png");
    private static final Identifier TEXTURE_AERO = SandStormMod.id("textures/entity/megazord/megazord_aero_striker.png");
    private static final Identifier TEXTURE_SUB = SandStormMod.id("textures/entity/megazord/megazord_abyssal_sub.png");
    private static final Identifier TEXTURE_APEX = SandStormMod.id("textures/entity/megazord/megazord_apex_dominator.png");

    public MegazordRenderer(EntityRendererProvider.Context context) {
        super(context, new MegazordModel(MegazordModel.createBodyLayer().bakeRoot()), 2.2f);
    }

    @Override
    protected void scale(MegazordRenderState state, PoseStack poseStack) {
        super.scale(state, poseStack);
        poseStack.scale(1.35f, 1.35f, 1.35f);
    }

    @Override
    public Identifier getTextureLocation(MegazordRenderState state) {
        return switch (state.variant) {
            case AERO_STRIKER -> TEXTURE_AERO;
            case ABYSSAL_SUB -> TEXTURE_SUB;
            case APEX_DOMINATOR -> TEXTURE_APEX;
            default -> TEXTURE_STANDARD;
        };
    }

    @Override
    public MegazordRenderState createRenderState() {
        return new MegazordRenderState();
    }

    @Override
    public void extractRenderState(MegazordEntity entity, MegazordRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.variant = entity.getVariant();
        state.hasFlightModule = entity.hasFlightModule();
        state.hasSubmersibleModule = entity.hasSubmersibleModule();
        state.hasOverdriveModule = entity.hasOverdriveModule();
        state.isFlying = entity.hasFlightModule() && !entity.onGround();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
