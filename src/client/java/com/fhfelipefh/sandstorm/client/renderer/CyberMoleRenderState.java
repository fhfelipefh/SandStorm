package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class CyberMoleRenderState extends LivingEntityRenderState {
    public boolean isTame = false;
    public boolean isSitting = false;
    public boolean isDeactivated = false;
    public boolean isBurrowed = false;
    public float drillRotation = 0.0f;
    public float animationTicks = 0.0f;
}

