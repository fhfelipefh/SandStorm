package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.CrawlerDroneEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class CrawlerDroneRenderer extends MobRenderer<CrawlerDroneEntity, CrawlerDroneRenderState, CrawlerDroneModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/crawler_drone/crawler_drone.png");
    private static final Identifier TEXTURE_DEACTIVATED = SandStormMod.id("textures/entity/crawler_drone/crawler_drone_deactivated.png");

    public CrawlerDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new CrawlerDroneModel(CrawlerDroneModel.createBodyLayer().bakeRoot()), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(CrawlerDroneRenderState state) {
        return state.isDeactivated ? TEXTURE_DEACTIVATED : TEXTURE;
    }

    @Override
    public CrawlerDroneRenderState createRenderState() {
        return new CrawlerDroneRenderState();
    }

    @Override
    public void extractRenderState(CrawlerDroneEntity entity, CrawlerDroneRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isDeactivated = entity.isDeactivated();
    }
}
