package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class WardenAndroidRenderState extends LivingEntityRenderState {
    public boolean isDeactivated = false;
    public int attackAnim = 0;
    public float animationTicks = 0.0f;
}

