package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class DerelictAutomatonRenderState extends LivingEntityRenderState {
    public boolean isOvercharging = false;
    public float overchargeProgress = 0.0f;
    public float animationTicks = 0.0f;
}
