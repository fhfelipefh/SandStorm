package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class ExcavatorVehicleRenderer extends MobRenderer<ExcavatorVehicleEntity, ExcavatorVehicleRenderState, ExcavatorVehicleModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/excavator_vehicle/excavator_vehicle.png");

    public ExcavatorVehicleRenderer(EntityRendererProvider.Context context) {
        super(context, new ExcavatorVehicleModel(ExcavatorVehicleModel.createBodyLayer().bakeRoot()), 1.1f);
    }

    @Override
    public Identifier getTextureLocation(ExcavatorVehicleRenderState state) {
        return TEXTURE;
    }

    @Override
    public ExcavatorVehicleRenderState createRenderState() {
        return new ExcavatorVehicleRenderState();
    }

    @Override
    public void extractRenderState(ExcavatorVehicleEntity entity, ExcavatorVehicleRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isVehicle = entity.isVehicle();
        state.storedEnergy = entity.getEnergyStorage().getStoredEnergy();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
