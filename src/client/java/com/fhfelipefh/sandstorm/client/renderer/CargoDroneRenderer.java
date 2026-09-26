package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.CargoDroneEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class CargoDroneRenderer extends MobRenderer<CargoDroneEntity, CargoDroneRenderState, CargoDroneModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/cargo_drone/cargo_drone.png");

    public CargoDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new CargoDroneModel(CargoDroneModel.createBodyLayer().bakeRoot()), 0.45f);
    }

    @Override
    public Identifier getTextureLocation(CargoDroneRenderState state) {
        return TEXTURE;
    }

    @Override
    public CargoDroneRenderState createRenderState() {
        return new CargoDroneRenderState();
    }

    @Override
    public void extractRenderState(CargoDroneEntity entity, CargoDroneRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.cargoStack = entity.getCargoStack();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
