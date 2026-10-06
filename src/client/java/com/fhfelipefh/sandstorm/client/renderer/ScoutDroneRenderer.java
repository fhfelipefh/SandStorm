package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.ScoutDroneEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class ScoutDroneRenderer extends MobRenderer<ScoutDroneEntity, ScoutDroneRenderState, ScoutDroneModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/scout_drone/scout_drone.png");

    public ScoutDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new ScoutDroneModel(ScoutDroneModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(ScoutDroneRenderState state) {
        return TEXTURE;
    }

    @Override
    public ScoutDroneRenderState createRenderState() {
        return new ScoutDroneRenderState();
    }
}
