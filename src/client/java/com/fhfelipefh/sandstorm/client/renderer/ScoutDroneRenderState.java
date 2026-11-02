package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class ScoutDroneRenderState extends LivingEntityRenderState {
    public boolean isDeactivated = false;
    public boolean isRecharging = false;
    public float animationTicks = 0.0f;
}
