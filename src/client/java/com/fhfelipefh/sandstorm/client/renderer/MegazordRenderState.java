package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.MegazordVariant;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class MegazordRenderState extends LivingEntityRenderState {
    public MegazordVariant variant = MegazordVariant.STANDARD;
    public boolean hasFlightModule;
    public boolean hasSubmersibleModule;
    public boolean hasOverdriveModule;
    public boolean isFlying;
    public float animationTicks;
}
