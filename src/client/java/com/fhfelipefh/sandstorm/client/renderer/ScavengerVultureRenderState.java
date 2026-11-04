package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class ScavengerVultureRenderState extends LivingEntityRenderState {
    public boolean isDeactivated = false;
    public float animationTicks = 0.0f;
    public int vultureState = 0;
}

