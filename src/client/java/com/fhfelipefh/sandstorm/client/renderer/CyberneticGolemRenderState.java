package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class CyberneticGolemRenderState extends LivingEntityRenderState {
    public boolean isOverdrive = false;
    public float heat = 0.0f;
    public String metalTier = "iron";
    public int attackAnimTicks = 0;
    public float animationTicks = 0.0f;
}
