package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class FrostclawRenderState extends LivingEntityRenderState {
    public boolean isDeactivated = false;
    public boolean isChestSacIntact = true;
    public boolean isSlamAttacking = false;
    public float animationTicks = 0.0f;
}

