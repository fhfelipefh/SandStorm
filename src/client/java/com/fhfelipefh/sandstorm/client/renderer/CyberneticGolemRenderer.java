package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.CyberneticGolemEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class CyberneticGolemRenderer extends MobRenderer<CyberneticGolemEntity, CyberneticGolemRenderState, CyberneticGolemModel> {
    private static final Identifier TEXTURE_IRON = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_iron.png");
    private static final Identifier TEXTURE_COPPER = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_copper.png");
    private static final Identifier TEXTURE_GOLD = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_gold.png");
    private static final Identifier TEXTURE_NETHERITE = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_netherite.png");
    private static final Identifier TEXTURE_COMPOSITE = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_composite.png");
    private static final Identifier TEXTURE_HEAT_GLOW = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_heat_glow.png");

    public CyberneticGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberneticGolemModel(CyberneticGolemModel.createBodyLayer().bakeRoot()), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(CyberneticGolemRenderState state) {
        if ("copper".equalsIgnoreCase(state.metalTier)) {
            return TEXTURE_COPPER;
        }
        if ("gold".equalsIgnoreCase(state.metalTier)) {
            return TEXTURE_GOLD;
        }
        if ("netherite".equalsIgnoreCase(state.metalTier)) {
            return TEXTURE_NETHERITE;
        }
        if ("composite".equalsIgnoreCase(state.metalTier)) {
            return TEXTURE_COMPOSITE;
        }
        return TEXTURE_IRON;
    }

    @Override
    public CyberneticGolemRenderState createRenderState() {
        return new CyberneticGolemRenderState();
    }

    @Override
    public void extractRenderState(CyberneticGolemEntity entity, CyberneticGolemRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isOverdrive = entity.isOverdrive();
        state.heat = entity.getHeat();
        state.metalTier = entity.getMetalTier().getId();
        state.attackAnimTicks = entity.getAttackAnimTicks();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(CyberneticGolemRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);

        if (state.heat > 0.05f) {
            int alpha = (int) (Math.min(1.0f, state.heat) * 255.0f);
            int argbColor = (alpha << 24) | 0x00FFFFFF;
            collector.submitModel(this.model, state, poseStack, RenderTypes.eyes(TEXTURE_HEAT_GLOW), 0x00F000F0, OverlayTexture.NO_OVERLAY, argbColor);
        }
    }
}
