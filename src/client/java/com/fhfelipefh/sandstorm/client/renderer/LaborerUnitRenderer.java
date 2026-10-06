package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.LaborerUnitEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class LaborerUnitRenderer extends MobRenderer<LaborerUnitEntity, LaborerUnitRenderState, LaborerUnitModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/laborer_unit/laborer_unit.png");

    public LaborerUnitRenderer(EntityRendererProvider.Context context) {
        super(context, new LaborerUnitModel(LaborerUnitModel.createBodyLayer().bakeRoot()), 0.55f);
    }

    @Override
    public Identifier getTextureLocation(LaborerUnitRenderState state) {
        return TEXTURE;
    }

    @Override
    public LaborerUnitRenderState createRenderState() {
        return new LaborerUnitRenderState();
    }
}
