package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class GrazerRenderState extends LivingEntityRenderState {
    public boolean isDeactivated = false;
    public boolean areCanistersIntact = true;
    public int rotaryTicks = 0;
    public float animationTicks = 0.0f;
}

