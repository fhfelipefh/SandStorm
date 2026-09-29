package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class ScrapSentinelRenderState extends LivingEntityRenderState {
    public boolean isDormant = true;
    public int attackAnim = 0;
    public float animationTicks = 0.0f;
}
