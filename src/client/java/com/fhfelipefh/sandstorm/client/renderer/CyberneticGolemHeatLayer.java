package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class CyberneticGolemHeatLayer extends RenderLayer<CyberneticGolemRenderState, CyberneticGolemModel> {
    private static final Identifier TEXTURE_HEAT_GLOW = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_heat_glow.png");

    public CyberneticGolemHeatLayer(RenderLayerParent<CyberneticGolemRenderState, CyberneticGolemModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, CyberneticGolemRenderState state, float f, float g) {
        if (state.heat > 0.05f) {
            int alpha = (int) (Math.min(1.0f, state.heat) * 255.0f);
            int argbColor = (alpha << 24) | 0x00FFFFFF;
            submitNodeCollector.submitModel(this.getParentModel(), state, poseStack, RenderTypes.eyes(TEXTURE_HEAT_GLOW), 0x00F000F0, OverlayTexture.NO_OVERLAY, argbColor);
        }
    }
}
