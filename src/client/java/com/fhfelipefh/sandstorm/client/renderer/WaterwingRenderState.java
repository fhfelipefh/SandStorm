package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class WaterwingRenderState extends LivingEntityRenderState {
    public boolean isDeactivated = false;
    public boolean isDiving = false;
    public boolean isTame = false;
    public boolean isSitting = false;
    public float animationTicks = 0.0f;
}

