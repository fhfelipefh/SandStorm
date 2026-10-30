package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class FanghornRenderState extends LivingEntityRenderState {
    public boolean isDeactivated = false;
    public boolean isChargingHorns = false;
    public float animationTicks = 0.0f;
}

